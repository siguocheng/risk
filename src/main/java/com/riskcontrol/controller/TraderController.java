package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.trader.TraderDetail;
import com.riskcontrol.domain.vo.trader.TraderModify;
import com.riskcontrol.domain.vo.trader.TraderPage;
import com.riskcontrol.domain.vo.trader.TraderQuery;
import com.riskcontrol.service.ITraderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易员控制器
 *
 * @author zpc
 * @date 2026-06-18
 */
@Tag(description = "交易员", name = "交易员")
@RestController
@RequestMapping("/trader")
public class TraderController extends BaseController {

    @Resource
    ITraderService traderService;

    @Operation(summary = "交易员列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-trader-query-page", level = 3)
    public ResultBean<IPage<TraderPage>> queryList(@RequestBody TraderQuery query){
        return new ResultBean<>(traderService.queryPage(query));
    }

    @Operation(summary = "新增交易员")
    @PostMapping("/pc/create")
    @ResourceMethod(btnCode = "btn-pc-trader-create", level = 3)
    public ResultBean<Long> create(@RequestBody TraderModify modify){
        return new ResultBean<>(traderService.create(modify));
    }

    @Operation(summary = "更新交易员")
    @PostMapping("/pc/update")
    @ResourceMethod(btnCode = "btn-pc-trader-update", level = 3)
    public ResultBean<Long> update(@RequestBody TraderModify modify){
        return new ResultBean<>(traderService.update(modify));
    }

    @Operation(summary = "删除交易员")
    @PostMapping("/pc/delete")
    @ResourceMethod(btnCode = "btn-pc-trader-delete", level = 3)
    public ResultBean<Long> delete(@RequestBody TraderModify modify){
        return new ResultBean<>(traderService.delete(modify.getId()));
    }

    @Operation(summary = "交易员详情")
    @PostMapping("/pc/detail")
    @ResourceMethod(btnCode = "btn-pc-trader-detail", level = 3)
    public ResultBean<TraderDetail> detail(@RequestBody TraderModify modify){
        return new ResultBean<>(traderService.getDetail(modify.getId()));
    }
}
