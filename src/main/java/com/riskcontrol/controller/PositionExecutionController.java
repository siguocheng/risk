package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;
import com.riskcontrol.service.IPositionExecutionService;
import com.riskcontrol.service.IPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
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
 * 成交明细控制器
 *
 * @author zpc
 * @date 2026-06-20
 */
@Tag(description = "成交明细", name = "成交明细")
@RestController
@RequestMapping("/position-execution")
public class PositionExecutionController extends BaseController {

    @Resource
    IPositionExecutionService positionExecutionService;
    @Resource
    IPositionService positionService;

    @Operation(summary = "成交明细列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-contract-execution-query-page", level = 3)
    public ResultBean<IPage<PositionExecutionPage>> queryList(@RequestBody PositionExecutionQuery query) {
        return new ResultBean<>(positionExecutionService.queryPage(query));
    }

    @Operation(summary = "维护持仓分配记录")
    @PostMapping("/pc/allocate")
    @ResourceMethod(btnCode = "btn-pc-position-allocate", level = 3)
    public ResultBean<Boolean> allocatePosition(@RequestBody PositionAllocateRequest request) {
        return new ResultBean<>(positionService.allocatePosition(request));
    }

    @Operation(summary = "下载交易数据模板")
    @GetMapping("/pc/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        setResponseToExcel(response, "交易数据.xlsx");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("template/交易数据.xlsx")) {
            if (is != null) {
                StreamUtils.copy(is, response.getOutputStream());
                response.getOutputStream().flush();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板文件不存在");
            }
        }
    }

    @Operation(summary = "导入交易数据")
    @PostMapping("/pc/import")
    @ResourceMethod(btnCode = "btn-pc-position-execution-import", level = 3)
    public ResultBean<String> importPositionExecution(@RequestParam("file") MultipartFile file) throws IOException {
        String errorUrl = positionExecutionService.importPositionExecution(file.getInputStream());
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
