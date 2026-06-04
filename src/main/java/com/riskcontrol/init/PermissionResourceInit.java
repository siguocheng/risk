package com.riskcontrol.init;

import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.domain.PermissionResource;
import com.riskcontrol.filter.NoLoginFilter;
import com.riskcontrol.service.IPermissionResourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author huan.dong
 * @since 2023/10/30
 */
@Slf4j
@Component
public class PermissionResourceInit implements ApplicationContextAware, ApplicationRunner {

    @Value(value = "${init.permissionResource}")
    private Boolean register;

    private WebApplicationContext webApplicationContext;

    @Resource
    private IPermissionResourceService permissionResourceService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.webApplicationContext = (WebApplicationContext) applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        RequestMappingHandlerMapping mappingHandler = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> mapping = mappingHandler.getHandlerMethods();
        List<PermissionResource> resourceList = new ArrayList<>();
        mapping.forEach((k, v) -> {
            assert k.getPatternsCondition() != null;
            k.getPathPatternsCondition().getPatterns().forEach(urlPattern -> {
                ResourceMethod resourceMethod = v.getMethodAnnotation(ResourceMethod.class);
                if (null == resourceMethod) {
                    return;
                }
                switch (resourceMethod.level()) {
                    case 1 :
                        NoLoginFilter.LEVEL_1_URLS.add(urlPattern.toString());
                        break;
                    case 2 :
                        NoLoginFilter.LEVEL_2_URLS.add(urlPattern.toString());
                        break;
                    case 3 :
                        resourceList.add(new PermissionResource(resourceMethod.btnCode(), urlPattern.toString()));
                        break;
                }
            });
        });

        if (register) {
            permissionResourceService.init(resourceList);
            log.info("permission_resource表初始化");
        }
    }
}
