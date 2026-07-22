package com.riskcontrol.controller;

import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.domain.vo.position.PositionPage;
import com.riskcontrol.domain.vo.position.PositionQuery;
import com.riskcontrol.service.IPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

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

    @Operation(summary = "持仓列表分页查询")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-position-query-page", level = 3)
    public ResultBean<IPage<PositionPage>> queryList(@RequestBody PositionQuery query) {
        return new ResultBean<>(positionService.queryPage(query));
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
        return new ResultBean<>(errorUrl);
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

