package com.riskcontrol.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.domain.*;
import com.riskcontrol.enums.PositionExecutionOptTypeEnum;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.enums.TradeSideEnum;
import com.riskcontrol.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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


    public void cal(){

        List<AccountCurrency> list = accountCurrencyService.list(); // 取得账号
        for (AccountCurrency accountCurrency : list) {

            String accountCode = accountCurrency.getAccountCode();

            List<PositionExecution> positionExecutionsAccountCode = this.listPositionExecution(accountCode);
            if (positionExecutionsAccountCode.isEmpty()) {
                continue;
            }

            Map<String, List<PositionExecution>> positionExecutionDateGroup = positionExecutionsAccountCode.stream()
                    .collect(Collectors.groupingBy(pe -> pe.getDate()));

            for (String date : positionExecutionDateGroup.keySet()) {
                List<PositionExecution> positionExecutionsDate = positionExecutionDateGroup.get(date);

                Map<Integer, List<PositionExecution>> positionExecutionDateConidGroup = positionExecutionsDate.stream()
                        .collect(Collectors.groupingBy(pe -> pe.getConid()));

                Boolean isDayTrade = false;
                for (Integer conid : positionExecutionDateConidGroup.keySet()) {
                    List<PositionExecution> positionExecutionsDateConid = positionExecutionDateConidGroup.get(conid);

                    BigDecimal buyQty = positionExecutionsDateConid.stream()
                            .filter(t -> TradeSideEnum.BOT.name().equals(t.getSide()) && t.getPrice().compareTo(BigDecimal.ZERO) == 1)
                            .map(PositionExecution::getShares)
                            .filter(qty -> qty != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal sellQty = positionExecutionsDateConid.stream()
                            .filter(t -> TradeSideEnum.SLD.name().equals(t.getSide()) && t.getPrice().compareTo(BigDecimal.ZERO) == 1)
                            .map(PositionExecution::getShares)
                            .filter(qty -> qty != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    if (buyQty.compareTo(sellQty) == 0) {
                        isDayTrade = true;
                    }

                    Contract contract = contractService.getByConid(conid);

                    // 如果是日内交易
                    if (isDayTrade){
                        contract.setMultiplier("1");
                        this.handleDayTrades(positionExecutionsDateConid, accountCode, date, contract);
                    } else {
                        if (contract.getSecType().equals(SetTypeEnum.OPT.getCode())) {
                            this.handleNoDayTradesOpt(positionExecutionsDateConid, accountCode, date, contract);
                        } else {
                            this.handleNoDayTrades(positionExecutionsDateConid, accountCode, date, conid);
                        }
                    }
                }
            }
        }
    }

    private void handleNoDayTrades(List<PositionExecution> trades, String accountCode, String date, int conid) {
        trades.sort(Comparator.comparing(PositionExecution::getTime));

        Position position = positionService.getPositionByConid(accountCode, conid);
        for (PositionExecution execution : trades) {
            this.handleNoDayTradesPosition(position, execution);
        }

        BigDecimal marketPrice = contractMarketHistoryService.getMarketPriceByConidAndDate(conid, date);

        BigDecimal calUnrealizedPnl = marketPrice.subtract(position.getCalAvgCost()).multiply(position.getPositionQty());

        position.setCalDailyUnrealizedPnl(calUnrealizedPnl.subtract(position.getCalUnrealizedPnl()));
        position.setCalUnrealizedPnl(calUnrealizedPnl);
        positionService.updateById(position);

        PositionHistory positionHistory = new PositionHistory(position, date);
        positionHistoryService.saveOrUpdatePositionHistory(positionHistory);
    }

    private void handleNoDayTradesPosition(Position position, PositionExecution execution){
        BigDecimal positionQty = position.getPositionQty();
        String side = execution.getSide();
        BigDecimal executionQty = execution.getShares();
        // 持仓是负数
        if (positionQty.compareTo(BigDecimal.ZERO) == -1) {
            if (TradeSideEnum.BOT.name().equals(side)) {
                BigDecimal hesunQty = executionQty;

                LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(PositionExecution::getAccountCode, position.getAccountCode());
                queryWrapper.eq(PositionExecution::getConid, position.getConid());
                queryWrapper.gt(PositionExecution::getRemainQty, BigDecimal.ZERO);
                queryWrapper.orderByAsc(PositionExecution::getTime);
                List<PositionExecution> list = positionExecutionService.list(queryWrapper);

                BigDecimal pnl = BigDecimal.ZERO;
                for (PositionExecution positionExecution : list) {
                    BigDecimal remainQty = positionExecution.getRemainQty();

                    if (hesunQty.compareTo(remainQty) == 1) {
                        pnl = pnl.add(positionExecution.getPrice().subtract(execution.getPrice()).multiply(remainQty));
                        positionExecution.setRemainQty(BigDecimal.ZERO);

                        execution.setCalExecutionRealizedPnl(execution.getCalExecutionRealizedPnl().add(pnl));

                        hesunQty = hesunQty.subtract(remainQty);
                    } else {
                        pnl = pnl.add(positionExecution.getPrice().subtract(execution.getPrice()).multiply(hesunQty));
                        positionExecution.setRemainQty(remainQty.subtract(hesunQty));

                        execution.setCalExecutionRealizedPnl(execution.getCalExecutionRealizedPnl().add(pnl));
                        positionExecutionService.updateById(positionExecution);
                        break;
                    }
                }
                position.setCalRealizedPnl(position.getCalRealizedPnl().add(pnl));
                position.setPositionQty(positionQty.add(hesunQty));

                if (hesunQty.compareTo(BigDecimal.ZERO) == -1) {
                    this.handleNoDayTradesPosition(position, execution);
                }
            } else if (TradeSideEnum.SLD.name().equals(side)) {
                executionQty = executionQty.negate();
                BigDecimal avgCost = position.getCalAvgCost().multiply(position.getCalPositionQty()).add(execution.getPrice().multiply(executionQty)).divide(position.getCalPositionQty().add(executionQty));

                position.setCalAvgCost(avgCost); // 持仓成本
                position.setCalPositionQty(position.getPositionQty().add(executionQty)); // 持仓数量
                execution.setRemainQty(execution.getShares()); // 入库的数量

                positionExecutionService.updateById(execution);
            }
        } else if (positionQty.compareTo(BigDecimal.ZERO) == 1) {
            if (TradeSideEnum.BOT.name().equals(side)) {
                executionQty = executionQty.negate();
                BigDecimal avgCost = position.getCalAvgCost().multiply(position.getCalPositionQty()).add(execution.getPrice().multiply(executionQty)).divide(position.getCalPositionQty().add(executionQty));

                position.setCalAvgCost(avgCost); // 持仓成本
                position.setCalPositionQty(position.getPositionQty().add(executionQty)); // 持仓数量
                execution.setRemainQty(execution.getShares()); // 入库的数量

                positionExecutionService.updateById(execution);
            } else if (TradeSideEnum.SLD.name().equals(side)) {
                BigDecimal hesunQty = executionQty;

                LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(PositionExecution::getAccountCode, position.getAccountCode());
                queryWrapper.eq(PositionExecution::getConid, position.getConid());
                queryWrapper.gt(PositionExecution::getRemainQty, BigDecimal.ZERO);
                queryWrapper.orderByAsc(PositionExecution::getTime);
                List<PositionExecution> list = positionExecutionService.list(queryWrapper);

                BigDecimal pnl = BigDecimal.ZERO;
                for (PositionExecution positionExecution : list) {
                    BigDecimal remainQty = positionExecution.getRemainQty();

                    if (hesunQty.compareTo(remainQty) == 1) {
                        pnl = pnl.add(positionExecution.getPrice().subtract(execution.getPrice()).multiply(remainQty));
                        positionExecution.setRemainQty(BigDecimal.ZERO);

                        execution.setCalExecutionRealizedPnl(execution.getCalExecutionRealizedPnl().add(pnl));

                        hesunQty = hesunQty.subtract(remainQty);
                    } else {
                        pnl = pnl.add(positionExecution.getPrice().subtract(execution.getPrice()).multiply(hesunQty));
                        positionExecution.setRemainQty(remainQty.subtract(hesunQty));

                        execution.setCalExecutionRealizedPnl(execution.getCalExecutionRealizedPnl().add(pnl));
                        positionExecutionService.updateById(positionExecution);
                        break;
                    }
                }
                position.setCalRealizedPnl(position.getCalRealizedPnl().add(pnl));
                position.setPositionQty(positionQty.add(hesunQty));

                if (hesunQty.compareTo(BigDecimal.ZERO) == -1) {
                    this.handleNoDayTradesPosition(position, execution);
                }
            }
        } else {
            if (TradeSideEnum.BOT.name().equals(side)) {

            } else if (TradeSideEnum.SLD.name().equals(side)) {
                executionQty = executionQty.negate();
            }
            position.setCalPositionQty(executionQty);
            position.setCalAvgCost(execution.getPrice());
        }
    }

    private void handleNoDayTradesOpt(List<PositionExecution> trades, String accountCode, String date, Contract contract) {

        for (PositionExecution trade : trades) {

        }
    }

    private void handleDayTrades(List<PositionExecution> intradayTrades, String accountCode, String date, Contract contract) {
        intradayTrades.sort(Comparator.comparing(PositionExecution::getTime));

        int conid = contract.getConid();
        Position position = positionService.getPositionByConid(accountCode, conid);

        String multiplier = contract.getMultiplier(); // 一张期权对应多少股票

        Map<Long, PositionExecution> buyQueue = new LinkedHashMap<>();
        Map<Long, PositionExecution> sellQueue = new LinkedHashMap<>();

        for (PositionExecution trade : intradayTrades) {
            // 买
            if (TradeSideEnum.BOT.name().equals(trade.getSide())) {
                buyQueue.put(trade.getId(), trade);
            } else {
                sellQueue.put(trade.getId(), trade);
            }
        }

        BigDecimal calDailyRealizedPnl = BigDecimal.ZERO;
        for (Long sldId : sellQueue.keySet()) {
            PositionExecution positionExecutionSld = sellQueue.get(sldId);

            BigDecimal sldRemainQty = positionExecutionSld.getRemainQty();
            BigDecimal sldPrice = positionExecutionSld.getPrice();
            BigDecimal calExecutionUnrealizedPnl = positionExecutionSld.getCalExecutionUnrealizedPnl() == null ? BigDecimal.ZERO : positionExecutionSld.getCalExecutionUnrealizedPnl();

            for (Long botId : buyQueue.keySet()) {
                PositionExecution positionExecutionBot = buyQueue.get(botId);

                BigDecimal botRemainQty = positionExecutionBot.getRemainQty();
                BigDecimal botPrice = positionExecutionBot.getPrice();

                BigDecimal hesun = sldRemainQty.subtract(botRemainQty);

                BigDecimal pnl;
                // 买入数量比卖出数量多
                if (hesun.compareTo(BigDecimal.ZERO) == 1) {
                    pnl = sldPrice.subtract(botPrice).multiply(new BigDecimal(multiplier)).multiply(botRemainQty.negate());

                    positionExecutionSld.setRemainQty(hesun);
                    positionExecutionSld.setCalExecutionRealizedPnl(calExecutionUnrealizedPnl.add(pnl));

                    positionExecutionBot.setRemainQty(BigDecimal.ZERO);
                    positionExecutionBot.setOptType(PositionExecutionOptTypeEnum.IN.name());
                    buyQueue.remove(botId);

                    positionExecutionService.updateById(positionExecutionBot);
                } else if (hesun.compareTo(BigDecimal.ZERO) == -1) {
                    pnl = sldPrice.subtract(botPrice).multiply(new BigDecimal(multiplier)).multiply(sldRemainQty.negate());

                    positionExecutionSld.setRemainQty(BigDecimal.ZERO);
                    positionExecutionSld.setCalExecutionRealizedPnl(calExecutionUnrealizedPnl.add(pnl));

                    positionExecutionBot.setRemainQty(hesun);
                    positionExecutionBot.setOptType(PositionExecutionOptTypeEnum.IN.name());
                    break;
                } else {
                    pnl = sldPrice.subtract(botPrice).multiply(new BigDecimal(multiplier)).multiply(sldRemainQty.negate());

                    positionExecutionSld.setCalExecutionRealizedPnl(calExecutionUnrealizedPnl.add(pnl));
                    positionExecutionSld.setRemainQty(BigDecimal.ZERO);

                    positionExecutionBot.setRemainQty(BigDecimal.ZERO);
                    positionExecutionBot.setOptType(PositionExecutionOptTypeEnum.IN.name());
                    buyQueue.remove(botId);

                    positionExecutionService.updateById(positionExecutionBot);
                    break;
                }

                calDailyRealizedPnl = calDailyRealizedPnl.add(pnl);
            }

            positionExecutionSld.setOptType(PositionExecutionOptTypeEnum.OUT.name());
            positionExecutionService.updateById(positionExecutionSld);
        }

        position.setCalDailyRealizedPnl(calDailyRealizedPnl);
        position.setCalRealizedPnl(position.getCalRealizedPnl().add(calDailyRealizedPnl));

        BigDecimal marketPrice = contractMarketHistoryService.getMarketPriceByConidAndDate(conid, date);

        BigDecimal calUnrealizedPnl = marketPrice.subtract(position.getCalAvgCost()).multiply(position.getPositionQty());
        position.setCalDailyUnrealizedPnl(calUnrealizedPnl.subtract(position.getCalUnrealizedPnl()));
        position.setCalUnrealizedPnl(calUnrealizedPnl);

        positionService.updateById(position);

        if (position.getPnlDailyDate().equals(date)) {
            position.setPnlDailyDate(date);
            PositionHistory positionHistory = new PositionHistory(position, date);
            positionHistoryService.saveOrUpdatePositionHistory(positionHistory);
        }
    }

    private void updatePosition(String accountCode, Integer conid, String date, BigDecimal realizedPnl) {
        Position position = positionService.getPositionByConid(accountCode, conid);
        if (position != null) {
            BigDecimal currentCalRealizedPnl = position.getCalRealizedPnl() != null ? position.getCalRealizedPnl() : BigDecimal.ZERO;
            position.setCalRealizedPnl(currentCalRealizedPnl.add(realizedPnl));

            String pnlDailyDate = position.getPnlDailyDate();
            BigDecimal currentCalDailyRealizedPnl = position.getCalDailyRealizedPnl() != null ? position.getCalDailyRealizedPnl() : BigDecimal.ZERO;

            if (date.equals(pnlDailyDate)) {
                position.setCalDailyRealizedPnl(currentCalDailyRealizedPnl.add(realizedPnl));
            } else {
                position.setCalDailyRealizedPnl(realizedPnl);
                position.setPnlDailyDate(date);
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
