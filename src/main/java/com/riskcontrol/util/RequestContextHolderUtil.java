package com.riskcontrol.util;

import com.riskcontrol.domain.vo.TokenUserBean;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class RequestContextHolderUtil {


    public Long getUserId(){
        Long userId = -1L;
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (null == servletRequestAttributes) {
                return userId;
            }
            HttpServletRequest request = servletRequestAttributes.getRequest();
            // 从session中获取
            Object loginUser = request.getSession().getAttribute("user");
            String token = null;
            if (loginUser == null)
            {
                // 若session中不存在，尝试从redis中获取
                String bearerToken = request.getHeader("Authorization");
                if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
                    token = bearerToken.substring(7);
                    userId = JWTUtil.getUserId(token);
                }
            }
        } catch (Exception e) {
            log.error("get userId error", e);
        }

        return userId;
    }

    public TokenUserBean getTokenUserBean() {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (null == servletRequestAttributes) {
            return new TokenUserBean();
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        // 从session中获取
        Object loginUser = request.getSession().getAttribute("user");
        String token = null;
        if (loginUser == null) {
            // 若session中不存在，尝试从redis中获取
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
            loginUser = RedisUtil.getTokenUserBeanFromRedisByToken(token);
        }

        return (TokenUserBean) loginUser;
    }
}
