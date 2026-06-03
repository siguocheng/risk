package com.riskcontrol.controller.codetemplate;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.Log;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.domain.bo.codetemplate.CodeTemplateBo;
import com.riskcontrol.controller.BaseController;
import com.riskcontrol.enums.BusinessType;
import com.riskcontrol.service.ICodeTemplateService;
import com.riskcontrol.domain.vo.codetemplate.CodeTemplateVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 代码模板
 *
 * @author fallrain
 * @date 2026-04-07
 */
@Tag(name = "代码模板管理")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/code-template")
public class CodeTemplateController extends BaseController {

    private final ICodeTemplateService codeTemplateService;


    @PostMapping("/pc/query-page")
    @Operation(summary = "获取操作日志分页列表")
    @ResourceMethod(btnCode = "btn-pc-code-template-query-page", level = 3)
    public IPage<CodeTemplateVo> list(HttpServletRequest request, @RequestBody CodeTemplateBo bo) {
        return codeTemplateService.queryPageList(bo);
    }

    @PostMapping("/pc/export-excel")
    @Operation(summary = "导出代码模板列表")
    @ResourceMethod(btnCode = "btn-pc-code-template-export-excel", level = 3)
    public void export(HttpServletRequest request, CodeTemplateBo bo, HttpServletResponse response) {
        List<CodeTemplateVo> list = codeTemplateService.queryList(bo);
        try (OutputStream outputStream = response.getOutputStream()) {
            setResponseToExcel(response, URLEncoder.encode("代码模板.xlsx", "UTF-8"));
            EasyExcel.write(outputStream, CodeTemplateVo.class).sheet().doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出失败");
        }
    }

    @GetMapping("/pc/query-detail")
    @Operation(summary = "获取代码模板详细信息")
    @ResourceMethod(btnCode = "btn-pc-code-template-query-detail", level = 3)
    public CodeTemplateVo getInfo(HttpServletRequest request, @RequestParam("id") Long id) {
        return codeTemplateService.queryById(id);
    }

    @Log(title = "代码模板", businessType = BusinessType.INSERT)
    @PostMapping("/pc/create")
    @Operation(summary = "新增代码模板")
    @ResourceMethod(btnCode = "btn-pc-code-template-create", level = 3)
    public Boolean add(HttpServletRequest request, @RequestBody CodeTemplateBo bo) {
        return codeTemplateService.insertByBo(bo);
    }

    @Log(title = "代码模板", businessType = BusinessType.UPDATE)
    @PostMapping("/pc/update")
    @Operation(summary = "修改代码模板")
    @ResourceMethod(btnCode = "btn-pc-code-template-update", level = 3)
    public Boolean edit(HttpServletRequest request, @RequestBody CodeTemplateBo bo) {

        return codeTemplateService.updateByBo(bo);
    }

    @Log(title = "代码模板", businessType = BusinessType.BATCH_DELETE)
    @PostMapping("/pc/remove-batch")
    @Operation(summary = "删除代码模板")
    @ResourceMethod(btnCode = "btn-pc-code-template-remove-batch", level = 3)
    public Boolean remove(HttpServletRequest request, @RequestBody CodeTemplateBo bo) {

        return codeTemplateService.deleteWithValidByIds(bo, true);
    }
}
