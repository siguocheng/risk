package com.riskcontrol.controller;

import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.dashboard.AssetSecTypeRatio;
import com.riskcontrol.domain.vo.dashboard.DailyProfitQuery;
import com.riskcontrol.domain.vo.dashboard.DailyProfitTop10;
import com.riskcontrol.domain.vo.dashboard.RiskControlQuery;
import com.riskcontrol.domain.vo.dashboard.RiskControlVarModule;
import com.riskcontrol.domain.vo.dashboard.TraderRiskMetricsVo;
import com.riskcontrol.service.IPositionRelationHistoryService;
import com.riskcontrol.service.IPortfolioOverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(description = "风控仪表盘", name = "风控仪表盘")
@RestController
@RequestMapping("/risk-dashboard")
public class RiskControlDashboardController {


    @Resource
    IPortfolioOverviewService portfolioOverviewService;

    @Resource
    IPositionRelationHistoryService positionRelationHistoryService;

    @Operation(summary = "var")
    @PostMapping("/pc/query-var")
    @ResourceMethod(btnCode = "btn-pc-portfolio-overview-query-list", level = 3)
    public ResultBean<RiskControlVarModule> queryList(@RequestBody RiskControlQuery query) {
        return new ResultBean<>(portfolioOverviewService.queryVar(query));
    }

    @Operation(summary = "获取收益前10的资产")
    @PostMapping("/pc/query-top10-profit")
    public ResultBean<List<DailyProfitTop10>> queryTop10Profit(@RequestBody RiskControlQuery query) {
        return new ResultBean<>(positionRelationHistoryService.getTop10Profit(query));
    }

    @Operation(summary = "获取资产类型占比")
    @PostMapping("/pc/query-asset-ratio")
    public ResultBean<List<AssetSecTypeRatio>> queryAssetRatio(@RequestBody RiskControlQuery query) {
        return new ResultBean<>(positionRelationHistoryService.getAssetSecTypeRatio(query));
    }

    @Operation(summary = "获取交易员风险指标(夏普比率、索提诺比率、卡玛比率、盈亏比、风险占比、波动率溢价)")
    @PostMapping("/pc/query-trader-risk-metrics")
    public ResultBean<List<TraderRiskMetricsVo>> queryTraderRiskMetrics(@RequestBody RiskControlQuery query) {
        return new ResultBean<>(portfolioOverviewService.getTraderRiskMetrics(query));
    }

}
