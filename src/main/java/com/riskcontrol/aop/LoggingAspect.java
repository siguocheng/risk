package com.riskcontrol.aop;

import com.alibaba.fastjson2.JSONObject;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // 拦截所有Controller方法
    @Around("execution(* com.riskcontrol.controller..*.*(..)) && !execution(* com.riskcontrol.controller..*.download*(..)) && !execution(* com.riskcontrol.controller..*.export*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uuid = UUID.randomUUID().toString();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
//            log.info("{}:=== HTTP Request ===", uuid);
            log.info("{}:URL: {} {}", uuid, request.getMethod(), request.getRequestURL());
//            log.info("IP: {}", request.getRemoteAddr());
//            log.info("Class: {}", joinPoint.getSignature().getDeclaringTypeName());
//            log.info("Method: {}", joinPoint.getSignature().getName());
//            log.info("{}:Args: {}", uuid, JSONObject.toJSONString(joinPoint.getArgs()));
            log.info("{}:Args: {}", uuid, getArgsLog(joinPoint.getArgs()));
        }

        ResultBean<?> result = null;
        Long executionTime = null;
        try {
            // 执行方法
            result = (ResultBean<?>)joinPoint.proceed();

            return result;
        } catch (Throwable e) {
            log.error("Exception in method: {}", joinPoint.getSignature().getName(), e);
            result = handlerException(joinPoint, e);
        } finally {
            log.info("{}:Result: {}", uuid, JSONObject.toJSONString(result));
            log.info("{}:Execution Time: {} ms", uuid, executionTime);
            executionTime = System.currentTimeMillis() - startTime;
        }
        // 记录响应
//        log.info("=== HTTP Response ===", uuid);
//        log.info("=== Request Completed ===\n");
        return result;
    }

    private ResultBean<?> handlerException(ProceedingJoinPoint pjp, Throwable e) {
        ResultBean<?> result = new ResultBean();

        // 已知异常【注意：已知异常不要打印堆栈，否则会干扰日志】
        // 校验出错，参数非法
        if (e instanceof BusinessException || e instanceof IllegalArgumentException) {
            result.setMsg(e.getMessage());
            result.setCode(ResultBean.CHECK_FAIL);
        } else {
//			logger.error(pjp.getSignature() + " error ", e);

            result.setMsg("接口调用异常");
            result.setCode(ResultBean.UNKNOWN_EXCEPTION);
            result.setErrorMsg(e.getMessage());
        }

        return result;
    }

    // 在 LoggingAspect 类中添加工具方法，处理参数序列化
    private String getArgsLog(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        // 对每个参数进行处理，特殊处理 MultipartFile
        Object[] safeArgs = Arrays.stream(args)
                .map(arg -> {
                    // 如果是 MultipartFile 类型，只保留文件名和大小
                    if (arg instanceof org.springframework.web.multipart.MultipartFile) {
                        org.springframework.web.multipart.MultipartFile file = (org.springframework.web.multipart.MultipartFile) arg;
                        JSONObject fileInfo = new JSONObject();
                        fileInfo.put("fileName", file.getOriginalFilename());
                        fileInfo.put("size", file.getSize() + " bytes");
                        fileInfo.put("contentType", file.getContentType());
                        return fileInfo;
                    } else if (arg instanceof HttpServletRequest){
                        return "";
                    }
                    // 其他类型正常序列化
                    return arg;
                })
                .toArray();
        return JSONObject.toJSONString(safeArgs);
    }
}