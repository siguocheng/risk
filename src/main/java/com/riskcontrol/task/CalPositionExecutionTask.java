package com.riskcontrol.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.domain.*;
import com.riskcontrol.enums.PositionExecutionOptTypeEnum;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.enums.TradeSideEnum;
import com.riskcontrol.service.*;
import com.riskcontrol.util.DateUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalPositionExecutionTask {

    @Resource
    IPositionExecutionService positionExecutionService;

    @Resource
    IPositionService positionService;

    @Resource
    IPositionHistoryService positionHistoryService;

    @Resource
    IContractService contractService;

    @Resource
    IAccountCurrencyService accountCurrencyService;

    @Resource
    IContractMarketHistoryService contractMarketHistoryService;

    @Resource
    IPositionExecutionInOutService positionExecutionInOutService;

    @Resource
    ITradeCalendarService tradeCalendarService;

    @Resource
    ITraderService traderService;

    @Resource
    ITraderCapitalService traderCapitalService;


    @Transactional(rollbackFor = Exception.class)
    public void cal(){

        List<AccountCurrency> list = accountCurrencyService.list(); // 取得账号
        for (AccountCurrency accountCurrency : list) {

            String accountCode = accountCurrency.getAccountCode();

            // 取得账号下的交易信息
            List<PositionExecution> positionExecutionsAccountCode = this.listPositionExecution(accountCode);
            if (positionExecutionsAccountCode.isEmpty()) {
                continue;
            }

            // 根据时间进行分组
            Map<String, List<PositionExecution>> positionExecutionDateGroup = positionExecutionsAccountCode.stream()
                    .collect(Collectors.groupingBy(PositionExecution::getExecutionDate, TreeMap::new,Collectors.toList()));

            for (String date : positionExecutionDateGroup.keySet()) {
                // 当前账号下，同一天的交易信息
                List<PositionExecution> positionExecutionsDate = positionExecutionDateGroup.get(date);

                // 以合约进行分组
                Map<Integer, List<PositionExecution>> positionExecutionDateConidGroup = positionExecutionsDate.stream()
                        .collect(Collectors.groupingBy(pe -> pe.getConid()));

                for (Integer conid : positionExecutionDateConidGroup.keySet()) {
                    log.info("cal conid :{}", conid);
                    List<PositionExecution> positionExecutionsDateConid = positionExecutionDateConidGroup.get(conid);
                    positionExecutionsDateConid.sort(Comparator.comparing(PositionExecution::getTime));

                    BigDecimal buyQty = positionExecutionsDateConid.stream()
                            .filter(t -> TradeSideEnum.BOT.name().equals(t.getSide()))
                            .map(PositionExecution::getShares)
                            .filter(qty -> qty != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal sellQty = positionExecutionsDateConid.stream()
                            .filter(t -> TradeSideEnum.SLD.name().equals(t.getSide()))
                            .map(PositionExecution::getShares)
                            .filter(qty -> qty != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    boolean isDayTrade = buyQty.compareTo(sellQty) == 0;

                    log.info("cal conid :{} 日内交易:{}", conid, isDayTrade);

                    Contract contract = contractService.getByConid(conid);

                    // 如果是日内交易
                    if (isDayTrade) {
                        log.info("cal conid :{} SecType:{}", conid, contract.getSecType());
                        if (StringUtils.isEmpty(contract.getMultiplier())) {
                            contract.setMultiplier("1");
                        }
                        Boolean ret = this.handleDayTrades(positionExecutionsDateConid, accountCode, date, contract);
                        if (!ret) {
                            break;
                        }
                    } else {
                        log.info("cal conid :{} SecType:{}", conid, contract.getSecType());
                        Boolean ret;
                        if (contract.getSecType().equals(SetTypeEnum.OPT.getCode()) ) {
                            ret = this.handleNoDayTradesOpt(positionExecutionsDateConid, accountCode, date, contract);
                        } else {
                            ret = this.handleNoDayTrades(positionExecutionsDateConid, accountCode, date, conid);
                        }
                        if (!ret) {
                            break;
                        }
                    }
                }

                // 处理没有交易的持仓，计算未实现收益和当日未实现收益
                LambdaQueryWrapper<Position> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.ne(Position::getPositionDate, date);

                List<Position> positionHistoryNoTrade = positionService.list(queryWrapper);

                // 计算未实现收益和当日已实现收益
                for (Position position : positionHistoryNoTrade) {
                    position.setPositionDate(date);

                    BigDecimal marketPrice = resolveMarketClosePrice(position.getConid(), date);
                    if (marketPrice == null) {
                        continue;
                    }
                    String preTradeDate = tradeCalendarService.getPreTradeDate(date);
                    BigDecimal preMarketPrice = resolveMarketClosePrice(position.getConid(), preTradeDate);

                    if (preMarketPrice == null) {
                        continue;
                    }

                    BigDecimal multiplier = BigDecimal.ONE;
                    if (StringUtils.isNotEmpty(position.getMultiplier())) {
                        multiplier = new BigDecimal(position.getMultiplier());
                    }


                    BigDecimal calDailyUnrealizedPnl = marketPrice.subtract(preMarketPrice).multiply(position.getCalPositionQty()).multiply(multiplier);

                    BigDecimal calUnrealizedPnl = marketPrice.subtract(position.getCalAvgCost()).multiply(position.getCalPositionQty()).multiply(multiplier);
                    position.setCalDailyUnrealizedPnl(calDailyUnrealizedPnl);
                    position.setCalUnrealizedPnl(calUnrealizedPnl);

                    positionService.updateById(position);

                    PositionHistory positionHistory = new PositionHistory(position, position.getPositionDate());
                    positionHistoryService.saveOrUpdatePositionHistory(positionHistory);
                }
            }
        }

        this.handleTraderCapital();
    }

    private void handleTraderCapital(){
        String yesterday = DateUtil.localDateToString(LocalDate.now().minusDays(1));
        List<Trader> list = traderService.list();
        for (Trader trader : list) {
            TraderCapital traderCapital = new TraderCapital();
            traderCapital.setCapital(trader.getCapital());
            traderCapital.setTraderName(trader.getTraderName());
            traderCapital.setDailyDate(yesterday);

            traderCapitalService.saveOrUpdateTraderCapital(traderCapital);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean handleNoDayTrades(List<PositionExecution> trades, String accountCode, String date, int conid) {

        Boolean ret = true;

        trades.sort(Comparator.comparing(PositionExecution::getTime));

        Position position = positionService.getPositionByConid(accountCode, conid);
        initPositionCalFields(position);

        BigDecimal multiplier = BigDecimal.ONE;
        if (StringUtils.isNotEmpty(position.getMultiplier())) {
            multiplier = new BigDecimal(position.getMultiplier());
        }
        BigDecimal calDailyRealizedPnl = BigDecimal.ZERO;
        BigDecimal marketPrice = resolveMarketClosePrice(conid, date);
        if (marketPrice == null) {
            ret = false;
            return ret;
        }

        BigDecimal commissionAndFeesSum = BigDecimal.ZERO;
        for (PositionExecution trade : trades) {
            String optType = resolveOptType(position, trade);
            if (PositionExecutionOptTypeEnum.IN.name().equals(optType)) {
                handleNoDayTradesOptIn(position, trade, multiplier, marketPrice);
            } else {
                BigDecimal pnl = handleNoDayTradesOptOut(position, trade, multiplier, marketPrice);
                calDailyRealizedPnl = calDailyRealizedPnl.add(pnl);
            }
            trade.setCalMarketPrice(marketPrice);
            trade.setStatus(1);
            positionExecutionService.updateById(trade);
            if (trade.getCommissionAndFees() != null) {
                commissionAndFeesSum = commissionAndFeesSum.add(trade.getCommissionAndFees());
            }
        }

        rollupPositionDailyPnl(position, accountCode, conid, date, calDailyRealizedPnl, multiplier);
        if (position.getAccCommissionAndFees() == null) {
            position.setAccCommissionAndFees(commissionAndFeesSum);
        } else {
            position.setAccCommissionAndFees(position.getAccCommissionAndFees().add(commissionAndFeesSum));
        }
        positionService.updateById(position);

        PositionHistory positionHistory = new PositionHistory(position, date);
        positionHistoryService.saveOrUpdatePositionHistory(positionHistory);

        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean handleNoDayTradesOpt(List<PositionExecution> trades, String accountCode, String date, Contract contract) {

        Boolean ret = true;

        trades.sort(Comparator.comparing(PositionExecution::getTime));

        int conid = contract.getConid();
        BigDecimal multiplier = this.getMultiplier(contract);
        Position position = positionService.getPositionByConid(accountCode, conid);
        initPositionCalFields(position);

        BigDecimal calDailyRealizedPnl = BigDecimal.ZERO;
        BigDecimal marketPrice = this.resolveMarketClosePrice(conid, date);

        if (marketPrice == null) {
            ret = false;
            return ret;
        }

        log.info("cal conid :{} date:{} marketPrice:{}", conid, date, marketPrice);

        BigDecimal commissionAndFeesSum = BigDecimal.ZERO;
        for (PositionExecution trade : trades) {
            String optType = resolveOptType(position, trade);
            log.info("cal conid :{} PositionExecutionId:{} optType:{}", conid, trade.getId(), optType);
            if (PositionExecutionOptTypeEnum.IN.name().equals(optType)) {
                handleNoDayTradesOptIn(position, trade, multiplier, marketPrice);
            } else {
                BigDecimal pnl = handleNoDayTradesOptOut(position, trade, multiplier, marketPrice);
                calDailyRealizedPnl = calDailyRealizedPnl.add(pnl);
            }
            trade.setStatus(1);
            trade.setCalMarketPrice(marketPrice);
            positionExecutionService.updateById(trade);

            if (trade.getCommissionAndFees() != null) {
                commissionAndFeesSum = commissionAndFeesSum.add(trade.getCommissionAndFees());
            }
        }

        rollupPositionDailyPnl(position, accountCode, conid, date, calDailyRealizedPnl, multiplier);
        if (position.getAccCommissionAndFees() == null) {
            position.setAccCommissionAndFees(commissionAndFeesSum);
        } else {
            position.setAccCommissionAndFees(position.getAccCommissionAndFees().add(commissionAndFeesSum));
        }
        positionService.updateById(position);

        PositionHistory positionHistory = new PositionHistory(position, date);
        positionHistoryService.saveOrUpdatePositionHistory(positionHistory);

        return ret;
    }

    private String resolveOptType(Position position, PositionExecution trade) {
        BigDecimal calPositionQty = nvl(position.getCalPositionQty());
        String side = trade.getSide();

        if (calPositionQty.compareTo(BigDecimal.ZERO) == 0) {
            return PositionExecutionOptTypeEnum.IN.name();
        }
        if (calPositionQty.compareTo(BigDecimal.ZERO) > 0) {
            return TradeSideEnum.BOT.name().equals(side)
                    ? PositionExecutionOptTypeEnum.IN.name()
                    : PositionExecutionOptTypeEnum.OUT.name();
        }
        return TradeSideEnum.BOT.name().equals(side)
                ? PositionExecutionOptTypeEnum.OUT.name()
                : PositionExecutionOptTypeEnum.IN.name();
    }

    private void handleNoDayTradesOptIn(Position position, PositionExecution trade, BigDecimal multiplier, BigDecimal marketPrice) {
        trade.setOptType(PositionExecutionOptTypeEnum.IN.name());
        trade.setRemainQty(trade.getShares());

        BigDecimal oldQty = nvl(position.getCalPositionQty());
        BigDecimal signedQty = TradeSideEnum.BOT.name().equals(trade.getSide())
                ? trade.getShares()
                : trade.getShares().negate();
        BigDecimal newQty = oldQty.add(signedQty);
        position.setCalPositionQty(newQty);

        if (marketPrice != null) {
            BigDecimal unrealized;
            if (TradeSideEnum.BOT.name().equals(trade.getSide())) {
                unrealized = marketPrice.subtract(trade.getPrice()).multiply(multiplier).multiply(trade.getShares());
            } else {
                unrealized = trade.getPrice().subtract(marketPrice).multiply(multiplier).multiply(trade.getShares());
            }
            trade.setCalExecutionUnrealizedPnl(unrealized);
        }
    }

    private BigDecimal handleNoDayTradesOptOut(Position position, PositionExecution trade, BigDecimal multiplier, BigDecimal marketPrice) {
        BigDecimal outPrice = trade.getPrice().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : trade.getPrice();
        BigDecimal outQty = trade.getShares();
        boolean closingLong = TradeSideEnum.SLD.name().equals(trade.getSide());
        String matchInSide = closingLong ? TradeSideEnum.BOT.name() : TradeSideEnum.SLD.name();

        BigDecimal remainOutQty = outQty; // 出库数量
        BigDecimal totalPnl = BigDecimal.ZERO;
        List<PositionExecution> inLots = listInLots(position.getAccountCode(), position.getConid(), matchInSide); // 取得入库的记录
        for (PositionExecution inLot : inLots) {
            if (remainOutQty.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal lotRemainQty = nvl(inLot.getRemainQty());
            if (lotRemainQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal matchQty = remainOutQty.min(lotRemainQty);
            BigDecimal lotPnl = closingLong
                    ? outPrice.subtract(inLot.getPrice()).multiply(multiplier).multiply(matchQty)
                    : inLot.getPrice().subtract(outPrice).multiply(multiplier).multiply(matchQty);
            totalPnl = totalPnl.add(lotPnl);

            inLot.setRemainQty(lotRemainQty.subtract(matchQty));
            positionExecutionService.updateById(inLot);
            remainOutQty = remainOutQty.subtract(matchQty);

            PositionExecutionInOut positionExecutionInOut = new PositionExecutionInOut();
            positionExecutionInOut.setPositionExecutionInId(inLot.getId());
            positionExecutionInOut.setPositionExecutionOutId(trade.getId());
            positionExecutionInOut.setQty(matchQty);
            positionExecutionInOutService.saveOrUpdatePositionExecutionInOut(positionExecutionInOut);
        }

        trade.setCalExecutionRealizedPnl(nvl(trade.getCalExecutionRealizedPnl()).add(totalPnl));
        trade.setOptType(PositionExecutionOptTypeEnum.OUT.name());

        BigDecimal matchedQty = outQty.subtract(remainOutQty);
        BigDecimal calPositionQty = nvl(position.getCalPositionQty());
        if (closingLong) {
            position.setCalPositionQty(calPositionQty.subtract(matchedQty));
        } else {
            position.setCalPositionQty(calPositionQty.add(matchedQty));
        }

        if (remainOutQty.compareTo(BigDecimal.ZERO) > 0) {
            saveOverflowOpenLot(position, trade, remainOutQty, multiplier, marketPrice);
        }

        return totalPnl;
    }

    private void saveOverflowOpenLot(Position position, PositionExecution trade, BigDecimal remainQty,
                                     BigDecimal multiplier, BigDecimal marketPrice) {
        PositionExecution openLot = new PositionExecution();
        openLot.setAccountCode(trade.getAccountCode());
        openLot.setConid(trade.getConid());
        openLot.setSymbol(trade.getSymbol());
        openLot.setSide(trade.getSide());
        openLot.setPrice(trade.getPrice());
        openLot.setShares(remainQty);
        openLot.setTime(trade.getTime());
        openLot.setExecutionDate(trade.getExecutionDate());
        openLot.setStatus(1);
        openLot.setOrderId(trade.getId().intValue());
        openLot.setExecId(trade.getExecId());
        handleNoDayTradesOptIn(position, openLot, multiplier, marketPrice);
        positionExecutionService.save(openLot);
    }

    private List<PositionExecution> listInLots(String accountCode, int conid, String inSide) {
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecution::getAccountCode, accountCode);
        queryWrapper.eq(PositionExecution::getConid, conid);
        queryWrapper.eq(PositionExecution::getOptType, PositionExecutionOptTypeEnum.IN.name());
        queryWrapper.eq(PositionExecution::getSide, inSide);
        queryWrapper.gt(PositionExecution::getRemainQty, BigDecimal.ZERO);
        queryWrapper.orderByAsc(PositionExecution::getTime);
        return positionExecutionService.list(queryWrapper);
    }

    private List<PositionExecution> listRemainInLots(String accountCode, int conid) {
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecution::getAccountCode, accountCode);
        queryWrapper.eq(PositionExecution::getConid, conid);
        queryWrapper.eq(PositionExecution::getOptType, PositionExecutionOptTypeEnum.IN.name());
        queryWrapper.gt(PositionExecution::getRemainQty, BigDecimal.ZERO);
        return positionExecutionService.list(queryWrapper);
    }

    private BigDecimal sumRemainInLotsCostBasis(String accountCode, int conid, BigDecimal multiplier) {
        BigDecimal total = BigDecimal.ZERO;
        for (PositionExecution lot : listRemainInLots(accountCode, conid)) {
            total = total.add(nvl(lot.getPrice()).multiply(nvl(lot.getRemainQty())).multiply(multiplier));
        }
        return total;
    }

    private BigDecimal sumRemainInLotsQty(String accountCode, int conid) {
        BigDecimal total = BigDecimal.ZERO;
        for (PositionExecution lot : listRemainInLots(accountCode, conid)) {
            total = total.add(nvl(lot.getRemainQty()));
        }
        return total;
    }

    private BigDecimal deriveCalAvgCostFromCostBasis(BigDecimal costBasis, BigDecimal totalRemainQty, BigDecimal multiplier) {
        if (totalRemainQty.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return costBasis.divide(totalRemainQty.multiply(multiplier), 4, RoundingMode.HALF_EVEN);
    }

    private BigDecimal sumRemainInLotsUnrealized(String accountCode, int conid, BigDecimal marketPrice, BigDecimal multiplier) {
        if (marketPrice == null) {
            return BigDecimal.ZERO;
        }

        List<PositionExecution> inLots = listRemainInLots(accountCode, conid);
        BigDecimal total = BigDecimal.ZERO;
        for (PositionExecution lot : inLots) {
            BigDecimal qty = nvl(lot.getRemainQty());
            BigDecimal price = nvl(lot.getPrice());
            if (TradeSideEnum.BOT.name().equals(lot.getSide())) {
                total = total.add(marketPrice.subtract(price).multiply(multiplier).multiply(qty));
            } else {
                total = total.add(price.subtract(marketPrice).multiply(multiplier).multiply(qty));
            }
        }
        return total;
    }

    /**
     * 当日未实现盈亏：仅统计收盘仍持有的仓位，今日因市价变动新增的浮盈浮亏。
     * 昨仓今持：(todayClose - yesterdayClose) × remainQty × multiplier
     * 今买今持：(todayClose - buyPrice) × remainQty × multiplier
     */
    private BigDecimal sumDailyUnrealizedPnl(String accountCode, int conid, String date,
                                             BigDecimal todayClose, BigDecimal multiplier) {
        if (todayClose == null) {
            return BigDecimal.ZERO;
        }

        LocalDate tradeDate = parseTradeDate(date);
        String normalizedDate = tradeDate != null
                ? DateUtil.localDateToString(tradeDate, "yyyyMMdd")
                : date;

        String prevDate = tradeCalendarService.getPreTradeDate(date); // 上一个交易日的时间
        BigDecimal yesterdayClose = resolveMarketClosePrice(conid, prevDate);

        List<PositionExecution> inLots = listRemainInLots(accountCode, conid);
        BigDecimal total = BigDecimal.ZERO;
        for (PositionExecution lot : inLots) {
            BigDecimal qty = nvl(lot.getRemainQty());
            BigDecimal lotPrice = nvl(lot.getPrice());
            boolean isLong = TradeSideEnum.BOT.name().equals(lot.getSide());
            boolean openedToday = normalizedDate.equals(normalizeTradeDate(lot.getExecutionDate()));

            BigDecimal dailyPnl;
            if (openedToday) {
                dailyPnl = isLong
                        ? todayClose.subtract(lotPrice)
                        : lotPrice.subtract(todayClose);
            } else if (yesterdayClose != null) {
                dailyPnl = isLong
                        ? todayClose.subtract(yesterdayClose)
                        : yesterdayClose.subtract(todayClose);
            } else {
                continue;
            }
            total = total.add(dailyPnl.multiply(multiplier).multiply(qty));
        }
        return total;
    }

    private void rollupPositionDailyPnl(Position position, String accountCode, int conid, String date,
                                        BigDecimal calDailyRealizedPnl, BigDecimal multiplier) {
        if (date.equals(position.getPositionDate())) {
            position.setCalDailyRealizedPnl(nvl(position.getCalDailyRealizedPnl()).add(calDailyRealizedPnl));
        } else {
            position.setCalDailyRealizedPnl(calDailyRealizedPnl);
            position.setPositionDate(date);
        }
        position.setCalRealizedPnl(nvl(position.getCalRealizedPnl()).add(calDailyRealizedPnl));

        BigDecimal todayClose = resolveMarketClosePrice(conid, date);
        position.setCalDailyUnrealizedPnl(sumDailyUnrealizedPnl(accountCode, conid, date, todayClose, multiplier));
        position.setCalUnrealizedPnl(sumRemainInLotsUnrealized(accountCode, conid, todayClose, multiplier));

        BigDecimal costBasis = sumRemainInLotsCostBasis(accountCode, conid, multiplier);
        BigDecimal totalRemainQty = sumRemainInLotsQty(accountCode, conid);
        position.setCalCostBasis(costBasis);
        position.setCalAvgCost(deriveCalAvgCostFromCostBasis(costBasis, totalRemainQty, multiplier));
    }

    private BigDecimal resolveMarketClosePrice(int conid, String date) {
        BigDecimal price = contractMarketHistoryService.getMarketPriceByConidAndDate(conid, date);
        return price;
    }

    private LocalDate parseTradeDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        String trimmed = date.trim();
        if (trimmed.length() >= 10 && trimmed.charAt(4) == '-') {
            return DateUtil.stringToLocalDate(trimmed.substring(0, 10), "yyyy-MM-dd");
        }
        if (trimmed.length() >= 8) {
            return DateUtil.stringToLocalDate(trimmed.substring(0, 8), "yyyyMMdd");
        }
        return null;
    }

    private String normalizeTradeDate(String date) {
        LocalDate localDate = parseTradeDate(date);
        if (localDate != null) {
            return DateUtil.localDateToString(localDate, "yyyyMMdd");
        }
        return date;
    }

    private BigDecimal getMultiplier(Contract contract) {
        String multiplier = contract.getMultiplier();
        return new BigDecimal(multiplier);
    }

    private void initPositionCalFields(Position position) {
        if (position.getCalPositionQty() == null) {
            position.setCalPositionQty(BigDecimal.ZERO);
        }
        if (position.getCalAvgCost() == null) {
            position.setCalAvgCost(BigDecimal.ZERO);
        }
        if (position.getCalCostBasis() == null) {
            position.setCalCostBasis(BigDecimal.ZERO);
        }
        if (position.getCalRealizedPnl() == null) {
            position.setCalRealizedPnl(BigDecimal.ZERO);
        }
        if (position.getCalUnrealizedPnl() == null) {
            position.setCalUnrealizedPnl(BigDecimal.ZERO);
        }
        if (position.getCalDailyRealizedPnl() == null) {
            position.setCalDailyRealizedPnl(BigDecimal.ZERO);
        }
        if (position.getCalDailyUnrealizedPnl() == null) {
            position.setCalDailyUnrealizedPnl(BigDecimal.ZERO);
        }
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean handleDayTrades(List<PositionExecution> intradayTrades, String accountCode, String date, Contract contract) {
        Boolean ret = true;
        intradayTrades.sort(Comparator.comparing(PositionExecution::getTime));

        int conid = contract.getConid();
        Position position = positionService.getPositionByConid(accountCode, conid);
        initPositionCalFields(position);

        BigDecimal marketPrice = resolveMarketClosePrice(conid, date);
        String preTradeDate = tradeCalendarService.getPreTradeDate(date);
        BigDecimal preMarketPrice = resolveMarketClosePrice(conid, preTradeDate);

        if (marketPrice != null) {

        } else {
            ret = false;
            return ret;
        }

        BigDecimal multiplier = getMultiplier(contract);

        List<PositionExecution> buyQueue = new ArrayList<>();
        List<PositionExecution> sellQueue = new ArrayList<>();
        for (PositionExecution trade : intradayTrades) {
            if (trade.getRemainQty() == null || trade.getRemainQty().compareTo(BigDecimal.ZERO) == 0) {
                trade.setRemainQty(trade.getShares());
            }
            if (TradeSideEnum.BOT.name().equals(trade.getSide())) {
                buyQueue.add(trade);
            } else {
                sellQueue.add(trade);
            }
        }

        BigDecimal calDailyRealizedPnl = BigDecimal.ZERO;
        for (PositionExecution sellTrade : sellQueue) {
            BigDecimal remainSldQty = nvl(sellTrade.getRemainQty());
            BigDecimal sldPrice = sellTrade.getPrice();
            BigDecimal sellRealizedPnl = BigDecimal.ZERO;

            Iterator<PositionExecution> buyIterator = buyQueue.iterator();
            while (buyIterator.hasNext() && remainSldQty.compareTo(BigDecimal.ZERO) > 0) {
                PositionExecution buyTrade = buyIterator.next();
                BigDecimal remainBotQty = nvl(buyTrade.getRemainQty());
                if (remainBotQty.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                BigDecimal matchQty = remainSldQty.min(remainBotQty);
                BigDecimal pnl = sldPrice.subtract(buyTrade.getPrice()).multiply(multiplier).multiply(matchQty);
                sellRealizedPnl = sellRealizedPnl.add(pnl);
                calDailyRealizedPnl = calDailyRealizedPnl.add(pnl);

                buyTrade.setRemainQty(remainBotQty.subtract(matchQty));
                buyTrade.setOptType(PositionExecutionOptTypeEnum.IN.name());
                if (buyTrade.getRemainQty().compareTo(BigDecimal.ZERO) == 0) {
                    buyIterator.remove();
                }

                remainSldQty = remainSldQty.subtract(matchQty);

                PositionExecutionInOut positionExecutionInOut = new PositionExecutionInOut();
                positionExecutionInOut.setPositionExecutionInId(buyTrade.getId());
                positionExecutionInOut.setPositionExecutionOutId(sellTrade.getId());
                positionExecutionInOut.setQty(matchQty);
                positionExecutionInOutService.saveOrUpdatePositionExecutionInOut(positionExecutionInOut);
            }

            sellTrade.setRemainQty(remainSldQty);
            sellTrade.setCalExecutionRealizedPnl(sellRealizedPnl);
            sellTrade.setOptType(PositionExecutionOptTypeEnum.OUT.name());
        }

        BigDecimal commissionAndFeesSum = BigDecimal.ZERO;
        for (PositionExecution trade : intradayTrades) {
            trade.setCalMarketPrice(marketPrice);
            trade.setStatus(1);
            positionExecutionService.updateById(trade);
            if (trade.getCommissionAndFees() != null) {
                commissionAndFeesSum = commissionAndFeesSum.add(trade.getCommissionAndFees());
            }
        }

        if (date.equals(position.getPositionDate())) {
            position.setCalDailyRealizedPnl(nvl(position.getCalDailyRealizedPnl()).add(calDailyRealizedPnl));
        } else {
            position.setCalDailyRealizedPnl(calDailyRealizedPnl);
            position.setPositionDate(date);
        }
        position.setCalRealizedPnl(nvl(position.getCalRealizedPnl()).add(calDailyRealizedPnl));
        position.setCalDailyUnrealizedPnl(marketPrice.subtract(preMarketPrice).multiply(position.getCalPositionQty()).multiply(multiplier));
        position.setCalUnrealizedPnl(marketPrice.subtract(position.getCalAvgCost()).multiply(position.getCalPositionQty()).multiply(multiplier));

        if (position.getAccCommissionAndFees() == null) {
            position.setAccCommissionAndFees(commissionAndFeesSum);
        } else {
            position.setAccCommissionAndFees(position.getAccCommissionAndFees().add(commissionAndFeesSum));
        }

        positionService.updateById(position);

        PositionHistory positionHistory = new PositionHistory(position, date);
        positionHistoryService.saveOrUpdatePositionHistory(positionHistory);

        return ret;
    }

    private void updatePosition(String accountCode, Integer conid, String date, BigDecimal realizedPnl) {
        Position position = positionService.getPositionByConid(accountCode, conid);
        if (position != null) {
            BigDecimal currentCalRealizedPnl = position.getCalRealizedPnl() != null ? position.getCalRealizedPnl() : BigDecimal.ZERO;
            position.setCalRealizedPnl(currentCalRealizedPnl.add(realizedPnl));

            String pnlDailyDate = position.getPositionDate();
            BigDecimal currentCalDailyRealizedPnl = position.getCalDailyRealizedPnl() != null ? position.getCalDailyRealizedPnl() : BigDecimal.ZERO;

            if (date.equals(pnlDailyDate)) {
                position.setCalDailyRealizedPnl(currentCalDailyRealizedPnl.add(realizedPnl));
            } else {
                position.setCalDailyRealizedPnl(realizedPnl);
                position.setPositionDate(date);
            }

            positionService.updateById(position);
        }
    }



    private List<PositionExecution> listPositionExecution(String accountCode){
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecution::getStatus, 0);
        queryWrapper.eq(PositionExecution::getAccountCode, accountCode);

        queryWrapper.orderByAsc(PositionExecution::getTime);

        return positionExecutionService.list(queryWrapper);
    }
}
