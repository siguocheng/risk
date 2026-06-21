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
@Tag(description = "综合关系", name = "综合关系")
@RestController
@RequestMapping("/composite-relation")
public class PositionRelationController extends BaseController {

    @Resource
    IPositionRelationService compositeRelationService;

    @Operation(summary = "综合关系列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-composite-relation-query-page", level = 3)
    public ResultBean<IPage<PositionRelationPage>> queryList(@RequestBody PositionRelationQuery query) {
        return new ResultBean<>(compositeRelationService.queryPage(query));
    }

    @Operation(summary = "新增综合关系")
    @PostMapping("/pc/create")
    @ResourceMethod(btnCode = "btn-pc-composite-relation-create", level = 3)
    public ResultBean<Long> create(@RequestBody PositionRelationModify modify) {
        return new ResultBean<>(compositeRelationService.create(modify));
    }

    @Operation(summary = "更新综合关系")
    @PostMapping("/pc/update")
    @ResourceMethod(btnCode = "btn-pc-composite-relation-update", level = 3)
    public ResultBean<Long> update(@RequestBody PositionRelationModify modify) {
        return new ResultBean<>(compositeRelationService.update(modify));
    }

    @Operation(summary = "删除综合关系")
    @PostMapping("/pc/delete")
    @ResourceMethod(btnCode = "btn-pc-composite-relation-delete", level = 3)
    public ResultBean<Long> delete(@RequestBody PositionRelationModify modify) {
        return new ResultBean<>(compositeRelationService.delete(modify.getId()));
    }
}
