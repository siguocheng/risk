package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyModify;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyPage;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyQuery;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategysModify;
import com.riskcontrol.service.IInvestmentStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 投资策略控制器
 *
 * @author zpc
 * @date 2026-06-19
 */
@Tag(description = "投资策略", name = "投资策略")
@RestController
@RequestMapping("/investment-strategy")
public class InvestmentStrategyController extends BaseController {

    @Resource
    IInvestmentStrategyService investmentStrategyService;

    @Operation(summary = "投资策略列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-investment-strategy-query-page", level = 3)
    public ResultBean<IPage<InvestmentStrategyPage>> queryList(@RequestBody InvestmentStrategyQuery query) {
        return new ResultBean<>(investmentStrategyService.queryPage(query));
    }

    @Operation(summary = "更新投资策略")
    @PostMapping("/pc/update")
    @ResourceMethod(btnCode = "btn-pc-investment-strategy-update", level = 3)
    public ResultBean<Integer> update(@RequestBody InvestmentStrategysModify modify) {
        return new ResultBean<>(investmentStrategyService.update(modify));
    }


//    @Operation(summary = "新增投资策略")
//    @PostMapping("/pc/create")
//    @ResourceMethod(btnCode = "btn-pc-investment-strategy-create", level = 3)
//    public ResultBean<Long> create(@RequestBody InvestmentStrategyModify modify) {
//        return new ResultBean<>(investmentStrategyService.create(modify));
//    }
//
//
//
//    @Operation(summary = "删除投资策略")
//    @PostMapping("/pc/delete")
//    @ResourceMethod(btnCode = "btn-pc-investment-strategy-delete", level = 3)
//    public ResultBean<Long> delete(@RequestBody InvestmentStrategyModify modify) {
//        return new ResultBean<>(investmentStrategyService.delete(modify.getId()));
//    }
}
