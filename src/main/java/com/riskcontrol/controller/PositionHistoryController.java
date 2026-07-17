package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.position.PositionHistoryPage;
import com.riskcontrol.domain.vo.position.PositionHistoryQuery;
import com.riskcontrol.service.IPositionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 持仓历史控制器
 *
 * @author zpc
 * @date 2026-07-17
 */
@Tag(description = "持仓历史", name = "持仓历史")
@RestController
@RequestMapping("/position-history")
public class PositionHistoryController extends BaseController {

    @Resource
    IPositionHistoryService positionHistoryService;

    @Operation(summary = "持仓历史列表")
    @PostMapping("/pc/query-page")
    public ResultBean<IPage<PositionHistoryPage>> queryList(@RequestBody PositionHistoryQuery query) {
        return new ResultBean<>(positionHistoryService.queryPage(query));
    }
}