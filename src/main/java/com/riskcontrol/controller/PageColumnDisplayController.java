package com.riskcontrol.controller;

import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.PageColumnDisplay;
import com.riskcontrol.domain.vo.PageColumnDisplayModify;
import com.riskcontrol.service.IPageColumnDisplayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 页面列表列展示控制器
 *
 * @author zpc
 * @date 2026-07-13
 */
@Tag(description = "页面列表列展示", name = "页面列表列展示")
@RestController
@RequestMapping("/page-column-display")
public class PageColumnDisplayController extends BaseController {

    @Resource
    IPageColumnDisplayService pageColumnDisplayService;

    @Operation(summary = "根据页面名称获取列展示配置")
    @PostMapping("/pc/get-by-page")
    public ResultBean<List<PageColumnDisplay>> getByPageName(@RequestBody PageColumnDisplayModify modify) {
        return new ResultBean<>(pageColumnDisplayService.getByPageName(modify.getPageName()));
    }

    @Operation(summary = "更新列展示状态")
    @PostMapping("/pc/update-display")
    public ResultBean<Void> updateDisplay(@RequestBody PageColumnDisplayModify modify) {
        pageColumnDisplayService.updateDisplay(modify.getPageName(), modify.getType(), modify.getColumnName(), modify.getIsDisplay());
        return new ResultBean<>();
    }
}
