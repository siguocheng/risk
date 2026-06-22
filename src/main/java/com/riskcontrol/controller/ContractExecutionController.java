package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionAllocateModify;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionPage;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionQuery;
import com.riskcontrol.service.IContractExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 成交明细控制器
 *
 * @author zpc
 * @date 2026-06-20
 */
@Tag(description = "成交明细", name = "成交明细")
@RestController
@RequestMapping("/contract-execution")
public class ContractExecutionController extends BaseController {

    @Resource
    IContractExecutionService contractExecutionService;

    @Operation(summary = "成交明细列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-contract-execution-query-page", level = 3)
    public ResultBean<IPage<ContractExecutionPage>> queryList(@RequestBody ContractExecutionQuery query) {
        return new ResultBean<>(contractExecutionService.queryPage(query));
    }

    @Operation(summary = "分配成交数量")
    @PostMapping("/pc/allocate")
    @ResourceMethod(btnCode = "btn-pc-contract-execution-allocate", level = 3)
    public ResultBean<Boolean> allocate(@RequestBody ContractExecutionAllocateModify request) {
        contractExecutionService.allocate(request);
        return new ResultBean<>(true);
    }
}
