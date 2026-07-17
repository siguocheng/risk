package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;
import com.riskcontrol.service.IPositionExecutionService;
import com.riskcontrol.service.IPositionService;
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
@RequestMapping("/position-execution")
public class PositionExecutionController extends BaseController {

    @Resource
    IPositionExecutionService positionExecutionService;
    @Resource
    IPositionService positionService;

    @Operation(summary = "成交明细列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-contract-execution-query-page", level = 3)
    public ResultBean<IPage<PositionExecutionPage>> queryList(@RequestBody PositionExecutionQuery query) {
        return new ResultBean<>(positionExecutionService.queryPage(query));
    }

    @Operation(summary = "维护持仓分配记录")
    @PostMapping("/pc/allocate")
    @ResourceMethod(btnCode = "btn-pc-position-allocate", level = 3)
    public ResultBean<Boolean> allocatePosition(@RequestBody PositionAllocateRequest request) {
        return new ResultBean<>(positionService.allocatePosition(request));
    }

}
