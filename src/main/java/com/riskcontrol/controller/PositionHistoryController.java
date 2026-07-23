package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.position.PositionHistoryPage;
import com.riskcontrol.domain.vo.position.PositionHistoryQuery;
import com.riskcontrol.service.IPositionHistoryService;
import com.riskcontrol.service.IPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

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
    @Resource
    IPositionService positionService;

    @Operation(summary = "持仓历史列表")
    @PostMapping("/pc/query-page")
    public ResultBean<IPage<PositionHistoryPage>> queryList(@RequestBody PositionHistoryQuery query) {
        return new ResultBean<>(positionHistoryService.queryPage(query));
    }

    @Operation(summary = "下载持仓列表模板")
    @GetMapping("/pc/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        setResponseToExcel(response, "持仓列表.xlsx");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("template/持仓列表.xlsx")) {
            if (is != null) {
                StreamUtils.copy(is, response.getOutputStream());
                response.getOutputStream().flush();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板文件不存在");
            }
        }
    }

    @Operation(summary = "导入持仓列表")
    @PostMapping("/pc/import")
    @ResourceMethod(btnCode = "btn-pc-position-import", level = 3)
    public ResultBean<String> importPosition(@RequestParam("file") MultipartFile file) throws IOException {
        String errorUrl = positionService.importPosition(file.getInputStream());
        if (StringUtils.isNotEmpty(errorUrl)) {
            return new ResultBean<>(-66 ,errorUrl);
        } else {
            return new ResultBean<>("");
        }

    }

    @Operation(summary = "下载错误文件")
    @GetMapping("/pc/download-error-file")
    public void downloadErrorFile(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File file = new File(tempDir, fileName);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        try (InputStream is = new java.io.FileInputStream(file)) {
            StreamUtils.copy(is, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }
}