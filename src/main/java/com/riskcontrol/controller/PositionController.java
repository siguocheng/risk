package com.riskcontrol.controller;

import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.service.IPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 持仓列表控制器
 *
 * @author zpc
 * @date 2026-06-10
 */
@Tag(name = "持仓列表管理")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/position")
public class PositionController extends BaseController {

    private final IPositionService positionService;

    @Operation(summary = "维护持仓分配记录")
    @PostMapping("/pc/allocate")
    @ResourceMethod(btnCode = "btn-pc-position-allocate", level = 3)
    public ResultBean<Boolean> allocatePosition(@RequestBody PositionAllocateRequest request) {
        return new ResultBean<>(positionService.allocatePosition(request));
    }
}

