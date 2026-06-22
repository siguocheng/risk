package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;
import com.riskcontrol.service.IPositionExecutionService;
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
public class PositionExecutionController extends BaseController {

    @Resource
    IPositionExecutionService positionExecutionService;

    @Operation(summary = "成交明细列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-contract-execution-query-page", level = 3)
    public ResultBean<IPage<PositionExecutionPage>> queryList(@RequestBody PositionExecutionQuery query) {
        return new ResultBean<>(positionExecutionService.queryPage(query));
    }

}
