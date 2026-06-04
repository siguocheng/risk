package com.riskcontrol.init;

import com.riskcontrol.service.IPermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
public class PermissionInit implements ApplicationRunner {

    @Value(value = "${init.permission}")
    private Boolean register;

    @Resource
    private IPermissionService permissionService;

    @Override
    public void run(ApplicationArguments args) {
        if (register) {
            permissionService.init();
            log.info("permission表初始化");
        }
    }
}
