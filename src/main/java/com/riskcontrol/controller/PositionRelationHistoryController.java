package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationHistoryPage;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationHistoryQuery;
import com.riskcontrol.service.IPositionRelationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 策略和交易员和账号和持仓之间的关系历史控制器
 *
 * @author zpc
 * @date 2026-07-11
 */
@Tag(description = "持仓关系历史", name = "持仓关系历史")
@RestController
@RequestMapping("/position-relation-history")
public class PositionRelationHistoryController extends BaseController {

    @Resource
    IPositionRelationHistoryService positionRelationHistoryService;

    @Operation(summary = "持仓关系历史列表")
    @PostMapping("/pc/query-page")
    public ResultBean<IPage<PositionRelationHistoryPage>> queryList(@RequestBody PositionRelationHistoryQuery query) {
        return new ResultBean<>(positionRelationHistoryService.queryPage(query));
    }
}