package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewVo;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionPage;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionQuery;
import com.riskcontrol.service.IPortfolioOverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(description = "组合总览", name = "组合总览")
@RestController
@RequestMapping("/portfolio-overview")
public class PortfolioOverviewController {

    @Resource
    IPortfolioOverviewService portfolioOverviewService;

    @Operation(summary = "列表")
    @PostMapping("/pc/query-list")
    @ResourceMethod(btnCode = "btn-pc-portfolio-overview-query-list", level = 3)
    public ResultBean<List<PortfolioOverviewVo>> queryList(@RequestBody PortfolioOverviewBo query) {
        return new ResultBean<>(portfolioOverviewService.queryPortfolioOverview(query));
    }

}
