package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationModify;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationPage;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationQuery;
import com.riskcontrol.service.IPositionRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 综合关系控制器
 *
 * @author zpc
 * @date 2026-06-19
 */
@Tag(description = "分配后的持仓", name = "分配后的持仓")
@RestController
@RequestMapping("/position-relation")
public class PositionRelationController extends BaseController {

    @Resource
    IPositionRelationService positionRelationService;

    @Operation(summary = "分配后的持仓的列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-position-relation-query-page", level = 3)
    public ResultBean<IPage<PositionRelationPage>> queryList(@RequestBody PositionRelationQuery query) {
        return new ResultBean<>(positionRelationService.queryPage(query));
    }
}
