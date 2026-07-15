package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.SystemConfig;

/**
 * 系统配置Service接口
 *
 * @author zpc
 * @date 2026-07-13
 */
public interface ISystemConfigService extends IService<SystemConfig> {

    String getValueByKey(String itemKey);

    void saveOrUpdateByKey(String itemKey, String itemValue);
}
