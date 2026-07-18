package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.constant.Constant;
import com.riskcontrol.dao.PositionRelationHistoryMapper;
import com.riskcontrol.dao.PositionRelationMapper;
import com.riskcontrol.domain.*;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.*;
import com.riskcontrol.domain.vo.dashboard.PressureTestVo;
import com.riskcontrol.domain.vo.dashboard.RiskControlQuery;
import com.riskcontrol.domain.vo.dashboard.RiskControlVarModule;
import com.riskcontrol.domain.vo.dashboard.TraderRiskMetricsVo;
import com.riskcontrol.domain.vo.dashboard.VarVo;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.service.*;
import com.riskcontrol.util.DateUtil;
import com.riskcontrol.util.TwrCalculator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioOverviewServiceImpl implements IPortfolioOverviewService {

    private final PositionRelationMapper positionRelationMapper;

    private final IPositionRelationHistoryService positionRelationHistoryService;

    private final PositionRelationHistoryMapper positionRelationHistoryMapper;

    private final IContractMarketHistoryService contractMarketHistoryService;

    private final IContractService contractService;

    private final ITraderService traderService;

    private final ISystemConfigService systemConfigService;

    private final ITraderCapitalService traderCapitalService;

    private final IAccountSummaryService accountSummaryService;

    private final IPositionHistoryService positionHistoryService;


    @Override
    public PortfolioOverviewData queryPortfolioOverview(PortfolioOverviewBo portfolioOverviewBo) {

        PortfolioOverviewData viewData = new PortfolioOverviewData();

        // 整合图表的信息
        List<ChartVo> chartList = this.getChartList(portfolioOverviewBo);
        BigDecimal rate = BigDecimal.ZERO; // 指数收益率
        if (chartList.size() > 0) {
            BigDecimal firstValue = chartList.get(0).getBenchmarks().get(0).getValue1();
            BigDecimal lastValue = chartList.get(chartList.size() - 1).getBenchmarks().get(0).getValue1();
            rate = lastValue.subtract(firstValue).divide(firstValue, 2, RoundingMode.HALF_UP);

            portfolioOverviewBo.setReferenceIndexRate(rate);
        }

        Map<String, BigDecimal> growthRateMap = this.calGrowthRate(portfolioOverviewBo); // 增长率

        // 整合列表数据
        List<PortfolioOverviewVo> portfolioOverviewList = this.getPortfolioOverviewList(portfolioOverviewBo, growthRateMap);

        if (portfolioOverviewList.size() > 0) {
            BigDecimal profitAmount = BigDecimal.ZERO; // 总的收益额
            BigDecimal yearCapital = BigDecimal.ZERO; // 总的本金
            for (PortfolioOverviewVo portfolioOverviewVo : portfolioOverviewList) {
                profitAmount = profitAmount.add(portfolioOverviewVo.getPnl());
                yearCapital = yearCapital.add(portfolioOverviewVo.getYearCapital());
            }
//            BigDecimal growthRate = profitAmount.divide(yearCapital, 4, RoundingMode.HALF_UP); // 增长率
            BigDecimal growthRate = growthRateMap.get("ALL"); // 增长率

            viewData.setProfitAmount(profitAmount);
            viewData.setGrowthRate(growthRate);
            viewData.setExcessReturn(growthRate.subtract(rate));
        }

        viewData.setChartList(chartList);
        viewData.setPortfolioOverviewList(portfolioOverviewList);
        return viewData;
    }

    private BigDecimal getTraderCapital(String trader, String dailyDate){
        BigDecimal capital = traderCapitalService.getCapitalByTraderDate(trader, dailyDate);
        if (capital == null) {
            capital = traderService.getDetailByTrader(trader).getCapital();
        }
        return capital;
    }

    private Map<String, BigDecimal> calGrowthRate(PortfolioOverviewBo portfolioOverviewBo){

        Map<String, BigDecimal> rateMap = new HashMap<>();

        LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(PositionRelationHistory::getDailyDate);
        List<PositionRelationHistory> historyList = positionRelationHistoryService.list(queryWrapper);

        Map<String, List<PositionRelationHistory>> historyListDateMap = historyList.stream().collect(Collectors.groupingBy(PositionRelationHistory::getDailyDate));

        List<TraderDailyDateMarketValueVo> valueList = new ArrayList<>();
        for (String dailyDate : historyListDateMap.keySet()) {
            List<PositionRelationHistory> positionRelationHistories = historyListDateMap.get(dailyDate);

            Map<String, List<PositionRelationHistory>> historyListDateTradeMap = positionRelationHistories.stream().collect(Collectors.groupingBy(PositionRelationHistory::getTraderName));

            for (String trader : historyListDateTradeMap.keySet()) {

                // 取得当前时间交易员的本金
                BigDecimal capital = this.getTraderCapital(trader, dailyDate);

                List<PositionRelationHistory> positionRelationHistoriesTrader = historyListDateTradeMap.get(trader);

                BigDecimal totalCostValue = BigDecimal.ZERO;
                BigDecimal totalMarketValue = BigDecimal.ZERO;

                for (PositionRelationHistory positionRelationHistory : positionRelationHistoriesTrader) {
                    Contract contract = contractService.getByConid(positionRelationHistory.getConid());

                    BigDecimal multiplier = BigDecimal.ONE;

                    if (StringUtils.isNotEmpty(contract.getMultiplier())) {
                        multiplier = new BigDecimal(contract.getMultiplier());
                    }

                    if (positionRelationHistory.getAvgCost() != null && positionRelationHistory.getPositionQty() != null) {
                        BigDecimal costValue = positionRelationHistory.getAvgCost().multiply(positionRelationHistory.getPositionQty()).multiply(multiplier);
                        totalCostValue = totalCostValue.add(costValue);
                    }
                    if (positionRelationHistory.getMarketPrice() != null && positionRelationHistory.getPositionQty() != null) {
                        BigDecimal marketValue = positionRelationHistory.getMarketPrice().multiply(positionRelationHistory.getPositionQty()).multiply(multiplier);
                        totalMarketValue = totalMarketValue.add(marketValue);
                    }
                }
                BigDecimal dailyValue = capital.subtract(totalCostValue).add(totalMarketValue);

                TraderDailyDateMarketValueVo traderDailyDateMarketValueVo = new TraderDailyDateMarketValueVo(trader, dailyDate, dailyValue);
                valueList.add(traderDailyDateMarketValueVo);
            }
        }

        Map<String, List<TraderDailyDateMarketValueVo>> valueListTrader = valueList.stream().collect(Collectors.groupingBy(TraderDailyDateMarketValueVo::getTraderName));

        for (String traderName : valueListTrader.keySet()) {
            List<TraderDailyDateMarketValueVo> traderDailyDateMarketValueVos = valueListTrader.get(traderName);
            traderDailyDateMarketValueVos.sort(Comparator.comparing(TraderDailyDateMarketValueVo::getDailyDate));

            if (traderDailyDateMarketValueVos.size() == 1) {
                continue;
            }
            List<PeriodSegment> periodSegmentTrader = new ArrayList<>();
            for (int i = 0; i < traderDailyDateMarketValueVos.size(); i++) {
                if (i == traderDailyDateMarketValueVos.size() -1) {

                } else {
                    TraderDailyDateMarketValueVo curValue = traderDailyDateMarketValueVos.get(i);
                    TraderDailyDateMarketValueVo postValue = traderDailyDateMarketValueVos.get(i+1);

                    PeriodSegment periodSegment = new PeriodSegment(curValue.getMarketValue(), postValue.getMarketValue(), BigDecimal.ZERO);
                    periodSegmentTrader.add(periodSegment);
                }
            }
            BigDecimal traderRate = TwrCalculator.calculateTotalTwr(periodSegmentTrader);

            rateMap.put(traderName, traderRate);
        }
        
        Map<String, List<TraderDailyDateMarketValueVo>> valueListDailyDate = valueList.stream().collect(Collectors.groupingBy(TraderDailyDateMarketValueVo::getDailyDate, TreeMap::new, Collectors.toList()));;

        List<PeriodSegment> periodSegmentAll = new ArrayList<>();
        BigDecimal preValue = null;
        for (String dailyDate : valueListDailyDate.keySet()) {
            List<TraderDailyDateMarketValueVo> traderDailyDateMarketValueVos = valueListDailyDate.get(dailyDate);
            BigDecimal curValue = BigDecimal.ZERO;
            for (TraderDailyDateMarketValueVo traderDailyDateMarketValueVo : traderDailyDateMarketValueVos) {
                BigDecimal marketValue = traderDailyDateMarketValueVo.getMarketValue();
                curValue = curValue.add(marketValue);
            }

            if (preValue == null) {
                preValue = curValue;
            } else {
                PeriodSegment periodSegment = new PeriodSegment(preValue, curValue, BigDecimal.ZERO);
                periodSegmentAll.add(periodSegment);
            }
        }

        BigDecimal allRate = TwrCalculator.calculateTotalTwr(periodSegmentAll);

        rateMap.put("ALL", allRate);

        return rateMap;
    }


    private void handleStartEndDate(PortfolioOverviewBo portfolioOverviewBo){
        if (portfolioOverviewBo.getDateType() != null) {
            if (portfolioOverviewBo.getDateType() == 1 || portfolioOverviewBo.getDateType() == 7) {
                portfolioOverviewBo.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                portfolioOverviewBo.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(30)));
            } else if (portfolioOverviewBo.getDateType() == 11) {
                portfolioOverviewBo.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                portfolioOverviewBo.setStartDate(DateUtil.localDateToString(LocalDate.now().with(TemporalAdjusters.firstDayOfYear())));
            } else if (portfolioOverviewBo.getDateType() == 365) {
                portfolioOverviewBo.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                portfolioOverviewBo.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(365)));
            } else if (portfolioOverviewBo.getDateType() == 30) {
                portfolioOverviewBo.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                portfolioOverviewBo.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(180)));
            } else if (portfolioOverviewBo.getDateType() == 365) {
                portfolioOverviewBo.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                portfolioOverviewBo.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(365)));
            }
        }
    }

    private List<ChartVo> getChartList(PortfolioOverviewBo portfolioOverviewBo){

        this.handleStartEndDate(portfolioOverviewBo);

        // 取得账号的历史收益
        List<DailyProfitVo> dailyProfitList = this.getDailyProfitList(portfolioOverviewBo);

        // 取得指数的历史收益情况
        Map<String, List<DailyReturnRateVo>> dailyReturnRateMap = this.getReferenceIndexDailyHistory(portfolioOverviewBo);

        List<ChartVo> chartList = new ArrayList<>();
        for (DailyProfitVo dailyProfitVo : dailyProfitList) {

            String dailyDate = dailyProfitVo.getDailyDate();

            List<DailyReturnRateVo> contractMarketHistories = dailyReturnRateMap.get(dailyDate);

            // 市场数据没有，就不计算收益
            if (contractMarketHistories == null) {
                continue;
            }
            ChartVo chartVo = new ChartVo();

            chartVo.setDate(dailyDate);
            chartVo.setNav(dailyProfitVo.getTotalPnl());
            chartVo.setPortReturn(dailyProfitVo.getTotalPnlRate());

            List<Benchmark> benchmarks = new ArrayList<>();
            for (DailyReturnRateVo dailyReturnRateVo : contractMarketHistories) {
                Benchmark benchmark = new Benchmark();
                benchmark.setKey(dailyReturnRateVo.getSymbol());
                benchmark.setName(dailyReturnRateVo.getSymbol());
                benchmark.setValue(dailyReturnRateVo.getDailyReturnRate());
                benchmark.setValue1(dailyReturnRateVo.getPriceClose());
//                benchmark.setValue(dailyReturnRateVo.getPriceClose());

                benchmarks.add(benchmark);
            }
            chartVo.setBenchmarks(benchmarks);

            chartList.add(chartVo);
        }
        return chartList;
    }

    private List<PortfolioOverviewVo> getPortfolioOverviewList(PortfolioOverviewBo portfolioOverviewBo, Map<String, BigDecimal> growthRateMap){

//        if (portfolioOverviewBo.getDateType() != null) {
//            portfolioOverviewBo.setDailyDate(DateUtil.localDateToString(LocalDate.now()));
//        }

        this.handleStartEndDate(portfolioOverviewBo);

        List<PortfolioOverviewVo> portfolioOverviewList = new ArrayList<>();

        List<PortfolioOverviewDetail> portfolioOverviewDetails = positionRelationMapper.listPortfolioOverviewDetail(portfolioOverviewBo);

        // 以交易员进行分组
        Map<String, List<PortfolioOverviewDetail>> portfolioOverviewDetailMap = portfolioOverviewDetails.stream()
                .collect(Collectors.groupingBy(PortfolioOverviewDetail::getTraderName));

        for (String s : portfolioOverviewDetailMap.keySet()) {
            List<PortfolioOverviewDetail> details = portfolioOverviewDetailMap.get(s);
            PortfolioOverviewVo data = new PortfolioOverviewVo();
            data.setTraderName(s); // 交易员
            BigDecimal yearCapital = details.get(0).getCapital();
            data.setYearCapital(yearCapital);


            BigDecimal grossPositionValue = BigDecimal.ZERO; // 总市值
            BigDecimal deltaExposure = BigDecimal.ZERO; // delta敞口
            BigDecimal availableFunds = BigDecimal.ZERO; // 现金
            BigDecimal realizedPnl = BigDecimal.ZERO; // 已实现盈亏
            BigDecimal unrealizedPnl = BigDecimal.ZERO; // 未实现盈亏
            BigDecimal sumPositionCost = BigDecimal.ZERO; // 总持仓成本
            BigDecimal sumCost = BigDecimal.ZERO; // 佣金及各项费用

            for (PortfolioOverviewDetail portfolioOverviewDetail : details) {

                if (portfolioOverviewDetail.getPositionQty() == null) {
                    continue;
                }
                // 单一资产市值
                BigDecimal singlePositionValue = portfolioOverviewDetail.getMarketPrice().multiply(portfolioOverviewDetail.getPositionQty());
                grossPositionValue = grossPositionValue.add(singlePositionValue);

                // delta敞口
                BigDecimal deltaValue = singlePositionValue;
                // 期权的时候
                if (portfolioOverviewDetail.getSecType().equals(SetTypeEnum.OPT.getCode())) {
                    deltaValue = this.calDelta(portfolioOverviewDetail);
                }
                deltaExposure = deltaExposure.add(deltaValue);

                // 单一资成本
                BigDecimal singleCost = portfolioOverviewDetail.getAvgCost().multiply(portfolioOverviewDetail.getPositionQty());
                // 总成本
                sumPositionCost = sumPositionCost.add(singleCost);

                realizedPnl = realizedPnl.add(portfolioOverviewDetail.getRealizedPnl()); // 未实现盈亏
                unrealizedPnl = unrealizedPnl.add(portfolioOverviewDetail.getUnrealizedPnl()); // 实现盈亏
                sumCost = sumCost.add(portfolioOverviewDetail.getCommissionAndFees());
            }

            // 现金=投入本金-每一个持仓的总成本价-佣金及各项费用
            availableFunds = data.getYearCapital().subtract(sumPositionCost);

            data.setGrossPositionValue(grossPositionValue);
            data.setDeltaExposure(deltaExposure);
            data.setAvailableFunds(availableFunds);
            data.setSumGrossPositionValue(data.getGrossPositionValue().add(data.getAvailableFunds()));
            data.setRealizedPnl(realizedPnl);
            data.setUnrealizedPnl(unrealizedPnl);
            data.setPnl(data.getRealizedPnl().add(data.getUnrealizedPnl()));
            data.setCost(sumCost);
//            data.setGrowthRate(grossPositionValue.subtract(yearCapital).divide(yearCapital, 4, RoundingMode.HALF_UP));
            data.setGrowthRate(growthRateMap.get(s));
            data.setDeltaGrowthRate(deltaExposure.subtract(yearCapital).divide(yearCapital, 4, RoundingMode.HALF_UP));
            data.setExcessReturn(portfolioOverviewBo.getReferenceIndexRate().subtract(data.getGrowthRate()));

            portfolioOverviewList.add(data);
        }
        return portfolioOverviewList;
    }

    private List<DailyProfitVo> getDailyProfitList(PortfolioOverviewBo portfolioOverviewBo){
        // 取得持仓历史收益情况
        List<PositionRelationHistory> positionRelationHistories = positionRelationHistoryMapper.sumPnlByDate(portfolioOverviewBo.getStartDate(), portfolioOverviewBo.getEndDate());

        // 按日期分组，计算每天的综合收益
//        Map<String, List<PositionRelationHistory>> historyMap = positionRelationHistories.stream()
//                .collect(Collectors.groupingBy(PositionRelationHistory::getDailyDate));

        List<DailyProfitVo> dailyProfitList = new ArrayList<>();

        DailyProfitVo previousHistory = null;
        for (PositionRelationHistory positionRelationHistory : positionRelationHistories) {
            BigDecimal unrealizedPnl = positionRelationHistory.getUnrealizedPnl();
            BigDecimal realizedPnl = positionRelationHistory.getRealizedPnl();

            DailyProfitVo dailyProfitVo = new DailyProfitVo();
            dailyProfitVo.setDailyDate(positionRelationHistory.getDailyDate());
            dailyProfitVo.setUnrealizedPnl(unrealizedPnl);
            dailyProfitVo.setRealizedPnl(realizedPnl);
            dailyProfitVo.setTotalPnl(unrealizedPnl.add(realizedPnl));

            dailyProfitList.add(dailyProfitVo);
            // 计算日收益率 = (当日收盘价 - 前一日收盘价) / 前一日收盘价
            if (previousHistory != null && previousHistory.getTotalPnl() != null
                    && dailyProfitVo.getTotalPnl() != null
                    && previousHistory.getTotalPnl().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal dailyReturnRate = dailyProfitVo.getTotalPnl()
                        .subtract(previousHistory.getTotalPnl())
                        .divide(previousHistory.getTotalPnl(), 4, BigDecimal.ROUND_HALF_UP);

                dailyProfitVo.setTotalPnlRate(dailyReturnRate);
            } else {
                dailyProfitVo.setTotalPnlRate(BigDecimal.ZERO);
            }
            previousHistory = dailyProfitVo;
        }

        // 按日期排序
        dailyProfitList.sort((a, b) -> a.getDailyDate().compareTo(b.getDailyDate()));
        return dailyProfitList;
    }

    private Map<String, List<DailyReturnRateVo>> getReferenceIndexDailyHistory(PortfolioOverviewBo portfolioOverviewBo){
        // 对标指数，默认是SPX
        List<Integer> referenceIndexConids = portfolioOverviewBo.getReferenceIndexConids();
        if (referenceIndexConids == null || referenceIndexConids.size() == 0) {
            referenceIndexConids = new ArrayList<>();
            referenceIndexConids.add(719582);
        }
        portfolioOverviewBo.setReferenceIndexConids(referenceIndexConids);
        // 取得对标指数的历史数据
        List<ContractMarketHistory> list = contractMarketHistoryService.listContractMarketHistoryByConidAndDate(referenceIndexConids, portfolioOverviewBo.getStartDate(), portfolioOverviewBo.getEndDate());

        // 按conid分组
        Map<Integer, List<ContractMarketHistory>> conidHistoryMap = list.stream()
                .collect(Collectors.groupingBy(ContractMarketHistory::getConid));

        // 计算每只股票每天的收益率
        List<DailyReturnRateVo> dailyReturnRateList = new ArrayList<>();

        for (Map.Entry<Integer, List<ContractMarketHistory>> entry : conidHistoryMap.entrySet()) {
            Integer conid = entry.getKey();
            List<ContractMarketHistory> histories = entry.getValue();

            // 按日期排序
            histories.sort((a, b) -> a.getDailyDate().compareTo(b.getDailyDate()));

            ContractMarketHistory previousHistory = null;
            for (ContractMarketHistory history : histories) {
                DailyReturnRateVo vo = new DailyReturnRateVo();
                vo.setConid(conid);
                vo.setSymbol(history.getSymbol());
                vo.setDailyDate(history.getDailyDate());
                vo.setPriceClose(history.getPriceClose());

                // 计算日收益率 = (当日收盘价 - 前一日收盘价) / 前一日收盘价
                if (previousHistory != null && previousHistory.getPriceClose() != null
                        && history.getPriceClose() != null
                        && previousHistory.getPriceClose().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal dailyReturnRate = history.getPriceClose()
                            .subtract(previousHistory.getPriceClose())
                            .divide(previousHistory.getPriceClose(), 4, BigDecimal.ROUND_HALF_UP);

                    BigDecimal dailyReturn = history.getPriceClose().subtract(previousHistory.getPriceClose());
                    vo.setDailyReturnRate(dailyReturnRate);
                    vo.setDailyReturn(dailyReturn);
                } else {
                    vo.setDailyReturnRate(BigDecimal.ZERO);
                    vo.setDailyReturn(BigDecimal.ZERO);
                }

                dailyReturnRateList.add(vo);
                previousHistory = history;
            }
        }

        // 按日期分组，
        Map<String, List<DailyReturnRateVo>> dailyReturnRateMap = dailyReturnRateList.stream()
                .collect(Collectors.groupingBy(DailyReturnRateVo::getDailyDate));

        return dailyReturnRateMap;
    }

    @Override
    public RiskControlVarModule queryVar(RiskControlQuery query) {

        RiskControlVarModule result = new RiskControlVarModule();

        result.setVix(this.getLastVix());
        VarVo var = new VarVo ();
        var.setAmount(new BigDecimal("0"));
        var.setConfidence(0.99);
        var.setDay(3);
        var.setTotalAssetRatio(new BigDecimal("0"));

        PressureTestVo pressureTestVo1 = new PressureTestVo();
        pressureTestVo1.setScene("2020格斯场景");
        pressureTestVo1.setAmount(new BigDecimal("0"));

        PressureTestVo pressureTestVo2 = new PressureTestVo();
        pressureTestVo2.setScene("加息暴跌场景");
        pressureTestVo2.setAmount(new BigDecimal("0"));

        List<PressureTestVo> pressureTestVo = new ArrayList<>();
        pressureTestVo.add(pressureTestVo1);
        pressureTestVo.add(pressureTestVo2);


        result.setVar(var);
        result.setEs(new BigDecimal("0"));
        result.setIv(new BigDecimal("0"));

        // 计算最大回撤
        BigDecimal maxDrawdown = this.calculateMaxDrawdownAccount(query.getAccountCodes());
        result.setMaxDrawdown(maxDrawdown);

        // 计算保证金使用率
        List<BigDecimal> marginRatios = new ArrayList<>();
        BigDecimal marginRatioSum = BigDecimal.ZERO;
        List<AccountSummary> accountSummarys = accountSummaryService.queryAccountSummary(query.getAccountCodes());

        for (AccountSummary accountSummary : accountSummarys) {
            if (accountSummary != null && accountSummary.getCushion() != null) {
                marginRatios.add(accountSummary.getCushion());
                marginRatioSum = marginRatioSum.add(accountSummary.getCushion());
            }
        }

        if (!marginRatios.isEmpty()) {
            result.setMarginRatio(BigDecimal.ONE.subtract(marginRatioSum.divide(new BigDecimal(marginRatios.size()), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
        }

        return result;
    }

    @Override
    public List<TraderRiskMetricsVo> getTraderRiskMetrics(RiskControlQuery query) {
        List<TraderRiskMetricsVo> result = new ArrayList<>();

        String riskFreeRateStr = systemConfigService.getValueByKey(Constant.risk_free_rate);
        BigDecimal riskFreeRateAnnual = BigDecimal.ZERO;
        if (riskFreeRateStr != null && !riskFreeRateStr.isEmpty()) {
            try {
                riskFreeRateAnnual = new BigDecimal(riskFreeRateStr);
            } catch (NumberFormatException e) {
                riskFreeRateAnnual = BigDecimal.ZERO;
            }
        }
        BigDecimal riskFreeRateDaily = riskFreeRateAnnual.divide(new BigDecimal(Constant.trade_day), 10, RoundingMode.HALF_UP);

        List<Trader> traders = traderService.listByTraders(query.getTradeNames());

        for (Trader trader : traders) {

            String traderName = trader.getTraderName();

            TraderRiskMetricsVo metrics = new TraderRiskMetricsVo();
            metrics.setTraderName(traderName);

            BigDecimal capital = trader.getCapital();

            LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PositionRelationHistory::getTraderName, traderName);
            List<PositionRelationHistory> list = positionRelationHistoryService.list(queryWrapper);

            Map<String, List<PositionRelationHistory>> historyByDateMap = list.stream()
                    .collect(Collectors.groupingBy(PositionRelationHistory::getDailyDate));

            List<Map.Entry<String, List<PositionRelationHistory>>> sortedEntries = new ArrayList<>(historyByDateMap.entrySet());
            sortedEntries.sort(Map.Entry.comparingByKey());

            List<BigDecimal> dailyValues = new ArrayList<>();
            for (Map.Entry<String, List<PositionRelationHistory>> entry : sortedEntries) {
                List<PositionRelationHistory> dayHistories = entry.getValue();
                BigDecimal totalCostValue = BigDecimal.ZERO;
                BigDecimal totalMarketValue = BigDecimal.ZERO;
                for (PositionRelationHistory history : dayHistories) {

                    Contract contract = contractService.getByConid(history.getConid());

                    BigDecimal multiplier = BigDecimal.ONE;

                    if (StringUtils.isNotEmpty(contract.getMultiplier())) {
                        multiplier = new BigDecimal(contract.getMultiplier());
                    }

                    if (history.getAvgCost() != null && history.getPositionQty() != null) {
                        BigDecimal costValue = history.getAvgCost().multiply(history.getPositionQty()).multiply(multiplier);
                        totalCostValue = totalCostValue.add(costValue);
                    }
                    if (history.getMarketPrice() != null && history.getPositionQty() != null) {
                        BigDecimal marketValue = history.getMarketPrice().multiply(history.getPositionQty()).multiply(multiplier);
                        totalMarketValue = totalMarketValue.add(marketValue);
                    }
                }
                BigDecimal dailyValue = capital.subtract(totalCostValue).add(totalMarketValue);
                dailyValues.add(dailyValue);
            }

            List<BigDecimal> dailyReturns = calculateDailyReturns(dailyValues);

            BigDecimal sharpeRatio = calculateSharpeRatio(dailyReturns, riskFreeRateDaily);
            metrics.setSharpeRatio(sharpeRatio);

            BigDecimal sortinoRatio = calculateSortinoRatio(dailyReturns, riskFreeRateDaily);
            metrics.setSortinoRatio(sortinoRatio);

            BigDecimal calmarRatio = calculateCalmarRatio(dailyValues);
            metrics.setCalmarRatio(calmarRatio);

            BigDecimal winLossRatio = calculateWinLossRatio(dailyReturns);
            metrics.setWinLossRatio(winLossRatio);

            metrics.setRiskRatio(null);
            metrics.setVolatilityPremium(null);
            result.add(metrics);
        }

        return result;
    }

    private List<BigDecimal> calculateDailyReturns(List<BigDecimal> dailyValues) {
        List<BigDecimal> dailyReturns = new ArrayList<>();
        for (int i = 1; i < dailyValues.size(); i++) {
            BigDecimal prevValue = dailyValues.get(i - 1);
            BigDecimal currValue = dailyValues.get(i);
            if (prevValue != null && currValue != null && prevValue.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal dailyReturn = currValue.subtract(prevValue).divide(prevValue, 10, RoundingMode.HALF_UP);
                dailyReturns.add(dailyReturn);
            }
        }
        return dailyReturns;
    }

    private BigDecimal calculateSharpeRatio(List<BigDecimal> dailyReturns, BigDecimal riskFreeRateDaily) {
        if (dailyReturns == null || dailyReturns.size() < 2) {
            return null;
        }

        BigDecimal meanReturn = BigDecimal.ZERO;
        for (BigDecimal returnValue : dailyReturns) {
            meanReturn = meanReturn.add(returnValue);
        }
        meanReturn = meanReturn.divide(new BigDecimal(dailyReturns.size()), 10, RoundingMode.HALF_UP);

        BigDecimal variance = BigDecimal.ZERO;
        for (BigDecimal returnValue : dailyReturns) {
            BigDecimal diff = returnValue.subtract(meanReturn);
            variance = variance.add(diff.multiply(diff));
        }
        variance = variance.divide(new BigDecimal(dailyReturns.size() - 1), 10, RoundingMode.HALF_UP);

        if (variance.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        double stdDev = Math.sqrt(variance.doubleValue());
        BigDecimal stdDevBigDecimal = new BigDecimal(stdDev);

        if (stdDevBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal excessReturn = meanReturn.subtract(riskFreeRateDaily);
        BigDecimal sharpeRatioDaily = excessReturn.divide(stdDevBigDecimal, 10, RoundingMode.HALF_UP);

        double sqrt252 = Math.sqrt(Constant.trade_day);
        BigDecimal annualizedSharpe = sharpeRatioDaily.multiply(new BigDecimal(sqrt252));

        return annualizedSharpe.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSortinoRatio(List<BigDecimal> dailyReturns, BigDecimal riskFreeRateDaily) {
        if (dailyReturns == null || dailyReturns.size() < 2) {
            return null;
        }

        BigDecimal meanReturn = BigDecimal.ZERO;
        for (BigDecimal returnValue : dailyReturns) {
            meanReturn = meanReturn.add(returnValue);
        }
        meanReturn = meanReturn.divide(new BigDecimal(dailyReturns.size()), 10, RoundingMode.HALF_UP);

        BigDecimal downsideSum = BigDecimal.ZERO;
        int downsideCount = 0;
        for (BigDecimal returnValue : dailyReturns) {
            BigDecimal excess = returnValue.subtract(riskFreeRateDaily);
            if (excess.compareTo(BigDecimal.ZERO) < 0) {
                downsideSum = downsideSum.add(excess.multiply(excess));
                downsideCount++;
            }
        }

        if (downsideCount == 0) {
            return null;
        }

        BigDecimal downsideVariance = downsideSum.divide(new BigDecimal(downsideCount), 10, RoundingMode.HALF_UP);

        if (downsideVariance.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        double downsideDeviation = Math.sqrt(downsideVariance.doubleValue());
        BigDecimal downsideDeviationBigDecimal = new BigDecimal(downsideDeviation);

        if (downsideDeviationBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal excessReturn = meanReturn.subtract(riskFreeRateDaily);
        BigDecimal sortinoRatioDaily = excessReturn.divide(downsideDeviationBigDecimal, 10, RoundingMode.HALF_UP);

        double sqrt252 = Math.sqrt(Constant.trade_day);
        BigDecimal annualizedSortino = sortinoRatioDaily.multiply(new BigDecimal(sqrt252));

        return annualizedSortino.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCalmarRatio(List<BigDecimal> dailyValues) {
        if (dailyValues == null || dailyValues.size() < 2) {
            return null;
        }

        BigDecimal maxDrawdown = calculateMaxDrawdown(dailyValues);
        if (maxDrawdown == null || maxDrawdown.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal firstValue = dailyValues.get(0);
        BigDecimal lastValue = dailyValues.get(dailyValues.size() - 1);

        if (firstValue == null || lastValue == null || firstValue.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        int tradingDays = dailyValues.size();
        BigDecimal totalReturn = lastValue.subtract(firstValue).divide(firstValue, 10, RoundingMode.HALF_UP);

        BigDecimal annualizedReturn;
        if (tradingDays == 0) {
            return null;
        }

        double growthFactor = lastValue.divide(firstValue, 10, RoundingMode.HALF_UP).doubleValue();
        double annualizedGrowth = Math.pow(growthFactor, Constant.trade_day / tradingDays);
        annualizedReturn = new BigDecimal(annualizedGrowth - 1);

        BigDecimal calmarRatio = annualizedReturn.divide(maxDrawdown, 10, RoundingMode.HALF_UP);

        return calmarRatio.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMaxDrawdown(List<BigDecimal> dailyValues) {
        if (dailyValues == null || dailyValues.size() < 2) {
            return null;
        }

        BigDecimal peak = dailyValues.get(0);
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        for (int i = 1; i < dailyValues.size(); i++) {
            BigDecimal currentValue = dailyValues.get(i);
            if (currentValue == null) {
                continue;
            }

            if (currentValue.compareTo(peak) > 0) {
                peak = currentValue;
            }

            if (peak != null && peak.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal drawdown = peak.subtract(currentValue).divide(peak, 10, RoundingMode.HALF_UP);
                if (drawdown.compareTo(maxDrawdown) > 0) {
                    maxDrawdown = drawdown;
                }
            }
        }

        return maxDrawdown.compareTo(BigDecimal.ZERO) > 0 ? maxDrawdown : null;
    }

    private BigDecimal calculateWinLossRatio(List<BigDecimal> dailyReturns) {
        if (dailyReturns == null || dailyReturns.size() < 1) {
            return null;
        }

        int winningDays = 0;
        int losingDays = 0;

        for (BigDecimal returnValue : dailyReturns) {
            if (returnValue == null) {
                continue;
            }
            int compareResult = returnValue.compareTo(BigDecimal.ZERO);
            if (compareResult > 0) {
                winningDays++;
            } else if (compareResult < 0) {
                losingDays++;
            }
        }

        if (losingDays == 0) {
            if (winningDays == 0) {
                return null;
            }
            return new BigDecimal("999.99");
        }

        BigDecimal winLossRatio = new BigDecimal(winningDays).divide(new BigDecimal(losingDays), 4, RoundingMode.HALF_UP);

        return winLossRatio;
    }

    private BigDecimal getLastVix(){
        LambdaQueryWrapper<ContractMarketHistory> queryWrapper = new LambdaQueryWrapper<>();
        String vixConid = systemConfigService.getValueByKey(Constant.vix_conid);
        queryWrapper.eq(ContractMarketHistory::getConid, vixConid);
        queryWrapper.orderByDesc(ContractMarketHistory::getDailyDate);
        queryWrapper.last("LIMIT 1");

        ContractMarketHistory contractMarketHistory = contractMarketHistoryService.getOne(queryWrapper);
        BigDecimal vixPriceClose = contractMarketHistory != null ? contractMarketHistory.getPriceClose() : BigDecimal.ZERO;
        return vixPriceClose;
    }

    private BigDecimal calDelta(PortfolioOverviewDetail portfolioOverviewDetail){
        BigDecimal deltaExposure = BigDecimal.ZERO;

        if (portfolioOverviewDetail.getDelta() != null && portfolioOverviewDetail.getMultiplier() != null && portfolioOverviewDetail.getMarketPrice() != null) {
            deltaExposure = portfolioOverviewDetail.getDelta().multiply(portfolioOverviewDetail.getMultiplier()).multiply(portfolioOverviewDetail.getMarketPrice());
        }

        return deltaExposure;
    }

    private BigDecimal getOrDefault(BigDecimal value, BigDecimal defaultValue) {
        return value != null ? value : defaultValue;
    }

    private BigDecimal calculateMaxDrawdownAccount(List<String> accountCodes) {
        if (CollectionUtils.isEmpty(accountCodes)) {
            return BigDecimal.ZERO;
        }

        LambdaQueryWrapper<PositionHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PositionHistory::getAccountCode, accountCodes);
        queryWrapper.orderByAsc(PositionHistory::getPositionDate);
        List<PositionHistory> positionHistoryList = positionHistoryService.list(queryWrapper);

        if (positionHistoryList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<String, BigDecimal> dailyMarketValueMap = new TreeMap<>();
        for (PositionHistory history : positionHistoryList) {
            String date = history.getPositionDate();
            BigDecimal marketValue = getOrDefault(history.getMarketValue(), BigDecimal.ZERO);
            dailyMarketValueMap.merge(date, marketValue, BigDecimal::add);
        }

        List<BigDecimal> values = new ArrayList<>(dailyMarketValueMap.values());
        if (values.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal peak = values.get(0);
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        for (BigDecimal value : values) {
            if (value.compareTo(peak) > 0) {
                peak = value;
            }
            if (peak.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal drawdown = peak.subtract(value).divide(peak, 4, RoundingMode.HALF_UP);
                if (drawdown.compareTo(maxDrawdown) > 0) {
                    maxDrawdown = drawdown;
                }
            }
        }

        return maxDrawdown;
    }
}
