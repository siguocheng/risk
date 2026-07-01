package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.dao.PositionRelationHistoryMapper;
import com.riskcontrol.dao.PositionRelationMapper;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.domain.PositionRelationHistory;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.*;
import com.riskcontrol.domain.vo.dashboard.PressureTestVo;
import com.riskcontrol.domain.vo.dashboard.RiskControlQuery;
import com.riskcontrol.domain.vo.dashboard.RiskControlVarModule;
import com.riskcontrol.domain.vo.dashboard.VarVo;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.service.*;
import com.riskcontrol.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioOverviewServiceImpl implements IPortfolioOverviewService {

    private final PositionRelationMapper positionRelationMapper;

    private final IPositionRelationHistoryService positionRelationHistoryService;

    private final PositionRelationHistoryMapper positionRelationHistoryMapper;

    private final IContractMarketHistoryService contractMarketHistoryService;

    private final IContractService contractService;


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

        // 整合列表数据
        List<PortfolioOverviewVo> portfolioOverviewList = this.getPortfolioOverviewList(portfolioOverviewBo);

        if (portfolioOverviewList.size() > 0) {
            BigDecimal profitAmount = BigDecimal.ZERO; // 总的收益额
            BigDecimal yearCapital = BigDecimal.ZERO; // 总的本金
            for (PortfolioOverviewVo portfolioOverviewVo : portfolioOverviewList) {
                profitAmount = profitAmount.add(portfolioOverviewVo.getPnl());
                yearCapital = yearCapital.add(portfolioOverviewVo.getYearCapital());
            }
            BigDecimal growthRate = profitAmount.divide(yearCapital, 4, RoundingMode.HALF_UP); // 增长率

            viewData.setProfitAmount(profitAmount);
            viewData.setGrowthRate(growthRate);
            viewData.setExcessReturn(growthRate.subtract(rate));
        }

        viewData.setChartList(chartList);
        viewData.setPortfolioOverviewList(portfolioOverviewList);
        return viewData;
    }

    private List<ChartVo> getChartList(PortfolioOverviewBo portfolioOverviewBo){
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

    private List<PortfolioOverviewVo> getPortfolioOverviewList(PortfolioOverviewBo portfolioOverviewBo){

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
            data.setGrowthRate(grossPositionValue.subtract(yearCapital).divide(yearCapital, 4, RoundingMode.HALF_UP));
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
        var.setAmount(new BigDecimal("666.66"));
        var.setConfidence(0.99);
        var.setDay(3);
        var.setTotalAssetRatio(new BigDecimal("0.032"));

        PressureTestVo pressureTestVo1 = new PressureTestVo();
        pressureTestVo1.setScene("2020格斯场景");
        pressureTestVo1.setAmount(new BigDecimal("92000000"));

        PressureTestVo pressureTestVo2 = new PressureTestVo();
        pressureTestVo2.setScene("加息暴跌场景");
        pressureTestVo2.setAmount(new BigDecimal("75000000"));

        List<PressureTestVo> pressureTestVo = new ArrayList<>();
        pressureTestVo.add(pressureTestVo1);
        pressureTestVo.add(pressureTestVo2);


        result.setVar(var);
        result.setEs(new BigDecimal("7777"));
        result.setMaxDrawdown(new BigDecimal("0.187"));
        result.setIv(new BigDecimal("0.243"));
        result.setMarginRatio(new BigDecimal("0.78"));

        return result;
    }

    private BigDecimal getLastVix(){
        LambdaQueryWrapper<ContractMarketHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractMarketHistory::getConid, 34426421);
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
}
