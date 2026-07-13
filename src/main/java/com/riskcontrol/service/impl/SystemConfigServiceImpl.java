package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.SystemConfigMapper;
import com.riskcontrol.domain.SystemConfig;
import com.riskcontrol.service.ISystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统配置Service业务层处理
 *
 * @author zpc
 * @date 2026-07-13
 */
@Slf4j
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements ISystemConfigService {

    @Override
    public String getValueByKey(String itemKey) {
        SystemConfig config = this.getOne(new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getItemKey, itemKey));
        return config != null ? config.getItemValue() : null;
    }

    @Override
    @Transactional
    public void updateByKey(String itemKey, String itemValue) {
        SystemConfig config = this.getOne(new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getItemKey, itemKey));
        if (config != null) {
            config.setItemValue(itemValue);
            this.updateById(config);
        }
    }
}
