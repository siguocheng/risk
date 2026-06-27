package com.riskcontrol.service.impl;

import com.riskcontrol.dao.PositionRelationMapper;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewDetail;
import com.riskcontrol.domain.vo.PortfolioOverviewVo;
import com.riskcontrol.enums.SetTypeEnum;
import com.riskcontrol.service.IPortfolioOverviewService;
import com.riskcontrol.service.IPositionRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioOverviewServiceImpl implements IPortfolioOverviewService {

    private final PositionRelationMapper positionRelationMapper;

    @Override
    public List<PortfolioOverviewVo> queryPortfolioOverview(PortfolioOverviewBo portfolioOverviewBo) {

        List<PortfolioOverviewVo> result = new ArrayList<>();

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

            for (PortfolioOverviewDetail portfolioOverviewDetail : details) {

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
                // 现金=投入本金-每一个持仓的成本价
                availableFunds = availableFunds.add(data.getYearCapital().subtract(singleCost));

                realizedPnl = realizedPnl.add(portfolioOverviewDetail.getRealizedPnl()); // 未实现盈亏
                unrealizedPnl = unrealizedPnl.add(portfolioOverviewDetail.getRealizedPnl()); // 实现盈亏
            }

            data.setGrossPositionValue(grossPositionValue);
            data.setDeltaExposure(deltaExposure);
            data.setAvailableFunds(availableFunds);
            data.setSumGrossPositionValue(data.getGrossPositionValue().add(data.getAvailableFunds()));
            data.setRealizedPnl(realizedPnl);
            data.setUnrealizedPnl(unrealizedPnl);
            data.setPnl(data.getRealizedPnl().add(data.getUnrealizedPnl()));
        }

        if (portfolioOverviewBo.getStartDate() == null && portfolioOverviewBo.getEndDate() == null) {

        }


        return result;
    }

    private BigDecimal calDelta(PortfolioOverviewDetail portfolioOverviewDetail){
        BigDecimal deltaExposure = BigDecimal.ZERO;

        return deltaExposure;
    }
}
