package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PermissionResourceMapper;
import com.riskcontrol.domain.PermissionResource;
import com.riskcontrol.service.IPermissionResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Service
public class PermissionResourceServiceImpl extends ServiceImpl<PermissionResourceMapper, PermissionResource> implements IPermissionResourceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init(List<PermissionResource> resourceList) {
        this.baseMapper.deleteAll();

        this.saveBatch(resourceList);
    }

    @Override
    public List<String> getPermissionResourceByUserId(Long userId) {
        return this.baseMapper.getPermissionResourceByUserId(userId);
    }

}
