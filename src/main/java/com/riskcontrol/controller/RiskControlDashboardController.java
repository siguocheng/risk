package com.riskcontrol.controller;

import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewData;
import com.riskcontrol.domain.vo.dashboard.RiskControlQuery;
import com.riskcontrol.domain.vo.dashboard.RiskControlVarModule;
import com.riskcontrol.service.IPortfolioOverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(description = "风控仪表盘", name = "风控仪表盘")
@RestController
@RequestMapping("/risk-dashboard")
public class RiskControlDashboardController {


    @Resource
    IPortfolioOverviewService portfolioOverviewService;

    @Operation(summary = "var")
    @PostMapping("/pc/query-var")
    @ResourceMethod(btnCode = "btn-pc-portfolio-overview-query-list", level = 3)
    public ResultBean<RiskControlVarModule> queryList(@RequestBody RiskControlQuery query) {
        return new ResultBean<>(portfolioOverviewService.queryVar(query));
    }

}
