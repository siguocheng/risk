package com.riskcontrol.controller;

import com.riskcontrol.util.RequestContextHolderUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

public class BaseController {

    @Resource
    RequestContextHolderUtil requestContextHolderUtil;

    protected void setResponseToExcel(HttpServletResponse response, String fileName) {
        response.setHeader("Content-type", "textml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
    }

    public Long getUserId(){
        return requestContextHolderUtil.getUserId();
    }
}
