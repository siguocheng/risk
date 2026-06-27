package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.dao.PositionRelationMapper;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.domain.PositionRelationHistory;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.*;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.service.*;
import com.riskcontrol.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private final IContractMarketHistoryService contractMarketHistoryService;

    private final IContractService contractService;

    @Override
    public PortfolioOverviewData queryPortfolioOverview(PortfolioOverviewBo portfolioOverviewBo) {

        PortfolioOverviewData viewData = new PortfolioOverviewData();

        List<PortfolioOverviewVo> portfolioOverviewList = new ArrayList<>();

        List<PortfolioOverviewDetail> portfolioOverviewDetails = positionRelationMapper.listPortfolioOverviewDetail(portfolioOverviewBo);

        // 以交易员进行分组
        Map<String, List<PortfolioOverviewDetail>> portfolioOverviewDetailMap = portfolioOverviewDetails.stream()
                .collect(Collectors.groupingBy(PortfolioOverviewDetail::getTraderName));

        for (String s : portfolioOverviewDetailMap.keySet()) {
            List<PortfolioOverviewDetail> details = portfolioOverviewDetailMap.get(s);
            PortfolioOverviewVo data = new PortfolioOverviewVo();
            data.setTraderName(s); // 交易员
            data.setYearCapital(details.get(0).getCapital());


            BigDecimal grossPositionValue = BigDecimal.ZERO; // 总市值
            BigDecimal deltaExposure = BigDecimal.ZERO; // delta敞口
            BigDecimal availableFunds = BigDecimal.ZERO; // 现金
            BigDecimal realizedPnl = BigDecimal.ZERO; // 已实现盈亏
            BigDecimal unrealizedPnl = BigDecimal.ZERO; // 未实现盈亏
            BigDecimal sumCost = BigDecimal.ZERO; // 未实现盈亏

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
                sumCost = sumCost.add(singleCost);

                realizedPnl = realizedPnl.add(portfolioOverviewDetail.getRealizedPnl()); // 未实现盈亏
                unrealizedPnl = unrealizedPnl.add(portfolioOverviewDetail.getUnrealizedPnl()); // 实现盈亏
            }

            // 现金=投入本金-每一个持仓的总成本价
            availableFunds = data.getYearCapital().subtract(sumCost);

            data.setGrossPositionValue(grossPositionValue);
            data.setDeltaExposure(deltaExposure);
            data.setAvailableFunds(availableFunds);
            data.setSumGrossPositionValue(data.getGrossPositionValue().add(data.getAvailableFunds()));
            data.setRealizedPnl(realizedPnl);
            data.setUnrealizedPnl(unrealizedPnl);
            data.setPnl(data.getRealizedPnl().add(data.getUnrealizedPnl()));

            portfolioOverviewList.add(data);
        }

        List<Integer> referenceIndexConids = portfolioOverviewBo.getReferenceIndexConids();

        LambdaQueryWrapper<ContractMarketHistory> queryWrapperMarket = new LambdaQueryWrapper<>();
        queryWrapperMarket.in(ContractMarketHistory::getConid, referenceIndexConids);
        queryWrapperMarket.ge(ContractMarketHistory::getDailyDate, portfolioOverviewBo.getStartDate());
        queryWrapperMarket.le(ContractMarketHistory::getDailyDate, portfolioOverviewBo.getEndDate());

        List<ContractMarketHistory> list = contractMarketHistoryService.list(queryWrapperMarket);

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
                            .divide(previousHistory.getPriceClose(), 8, BigDecimal.ROUND_HALF_UP);

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

        // 取得历史收益情况
        List<PositionRelationHistory> positionRelationHistories = positionRelationHistoryService.listByDateRange(portfolioOverviewBo);

        // 按日期分组，计算每天的综合收益
        Map<String, List<PositionRelationHistory>> historyMap = positionRelationHistories.stream()
                .collect(Collectors.groupingBy(PositionRelationHistory::getDailyDate));

        List<DailyProfitVo> dailyProfitList = new ArrayList<>();

        for (String dailyDate : historyMap.keySet()) {
            List<PositionRelationHistory> histories = historyMap.get(dailyDate);

            BigDecimal totalUnrealizedPnl = BigDecimal.ZERO;
            BigDecimal totalRealizedPnl = BigDecimal.ZERO;

            for (PositionRelationHistory history : histories) {
                if (history.getUnrealizedPnl() != null) {
                    totalUnrealizedPnl = totalUnrealizedPnl.add(history.getUnrealizedPnl());
                }
                if (history.getRealizedPnl() != null) {
                    totalRealizedPnl = totalRealizedPnl.add(history.getRealizedPnl());
                }
            }

            DailyProfitVo dailyProfitVo = new DailyProfitVo();
            dailyProfitVo.setDailyDate(dailyDate);
            dailyProfitVo.setUnrealizedPnl(totalUnrealizedPnl);
            dailyProfitVo.setRealizedPnl(totalRealizedPnl);
            dailyProfitVo.setTotalPnl(totalUnrealizedPnl.add(totalRealizedPnl));

            dailyProfitList.add(dailyProfitVo);
        }

        // 按日期排序
        dailyProfitList.sort((a, b) -> b.getDailyDate().compareTo(a.getDailyDate()));

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

            List<Benchmark> benchmarks = new ArrayList<>();
            for (DailyReturnRateVo dailyReturnRateVo : contractMarketHistories) {
                Benchmark benchmark = new Benchmark();
                benchmark.setKey(dailyReturnRateVo.getSymbol());
                benchmark.setName(dailyReturnRateVo.getSymbol());
                benchmark.setValue(dailyReturnRateVo.getPriceClose());

                benchmarks.add(benchmark);
            }
            chartVo.setBenchmarks(benchmarks);

            chartList.add(chartVo);
        }

        viewData.setChartList(chartList);
        viewData.setPortfolioOverviewList(portfolioOverviewList);
        return viewData;
    }

    private BigDecimal calDelta(PortfolioOverviewDetail portfolioOverviewDetail){
        BigDecimal deltaExposure = BigDecimal.ZERO;

        return deltaExposure;
    }
}
