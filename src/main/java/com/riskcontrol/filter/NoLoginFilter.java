package com.riskcontrol.filter;


import com.alibaba.fastjson2.JSONObject;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.TokenUserBean;
import com.riskcontrol.service.IPermissionService;
import com.riskcontrol.util.JWTUtil;
import com.riskcontrol.util.RedisUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.annotation.WebFilter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Order(1)
@WebFilter(filterName = "noLoginFilter", urlPatterns = "/*")
@Slf4j
public class NoLoginFilter implements Filter {

    @Resource
    private IPermissionService permissionService;

    public static final List<String> LEVEL_1_URLS = new ArrayList<>(30);
    public static final List<String> LEVEL_2_URLS = new ArrayList<>(300);

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String url = httpServletRequest.getRequestURI();

        String token = extractToken(httpServletRequest);
        if (isIgnore(url)) {
            chain.doFilter(httpServletRequest, response);
            return;
        } else {
            // 是第三方接口调用
            if (StringUtils.isEmpty(token)) {
                exceptionResponse(ResultBean.UN_LOGIN, "登录过期!", response);
                return;
            }
            // 校验1 token中是否有用户
            if (!JWTUtil.verify(token)) {
                exceptionResponse(ResultBean.UN_LOGIN, "登录凭证无效!", response);
                return;
            }
            // 校验2 redis中是否有tokenUserBean
            TokenUserBean tokenUserBean = RedisUtil.getTokenUserBeanFromRedisByToken(token);
            if (tokenUserBean == null) {
                exceptionResponse(ResultBean.UN_LOGIN, "登录缓存过期!", response);
                return;
            }
            // 校验3 检查权限是否被修改
            Boolean isChangedPermission = tokenUserBean.getIsChangedPermission();
            if (isChangedPermission != null && isChangedPermission) {
                respondWithPermissionChanged(response, tokenUserBean);
                return;
            }

            // 校验4 是否有权限
            if (!havePermission(tokenUserBean, url)) {
                exceptionResponse(-1, "抱歉，您无权限!", response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean havePermission(TokenUserBean tokenUserBean, String url) {

        if (Boolean.TRUE.equals(tokenUserBean.getIsDefaultRole())) {
            return true;
        }

        if (url.startsWith("/select")){
            return true;
        }

        // 登录后，无需权限
        for (String specialUri : LEVEL_2_URLS) {
            if (url.equals(specialUri)) {
                return true;
            }
        }

        for (String permission : tokenUserBean.getPermissionUrls()) {
            if (url.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    private void respondWithPermissionChanged(ServletResponse response, TokenUserBean tokenUserBean) throws Exception {

        String message = "您的权限发生了改变!";
        // 权限相关数据
        Map<String, Boolean> map = permissionService.getPermissionCodeMapByUserId(tokenUserBean.getUserId());
        String result = JSONObject.toJSONString(new ResultBean<>(ResultBean.PERMISSION_CHANGED, map, message));
        response.getWriter().write(result);
    }

    private void exceptionResponse(int code, String message, ServletResponse response) throws Exception {
        if (StringUtils.isEmpty(message)) {
            message = "";
        }
        String content = new String(message.getBytes(), StandardCharsets.UTF_8);
        response.getWriter().write(JSONObject.toJSONString(new ResultBean<>(code,content)));
    }

    private boolean isIgnore(String target) {

        if ((target.startsWith("/swagger")
                || target.startsWith("/doc.html")
                || target.startsWith("/webjars")
                || target.startsWith("/v2/api-docs")
                || target.contains("downloadTemplete")
                || target.contains("export")
                || target.contains("warehouse-info")
                || target.contains("inventory-order-record")
                || target.contains("select")
        		)) {
            //Swagger 路径放行
            return true;
        } else if (target.startsWith("/druid")) {
            return true;
        }

        for (String s : LEVEL_1_URLS) {
            if (s.equals(target)) {
                return true;
            }
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
