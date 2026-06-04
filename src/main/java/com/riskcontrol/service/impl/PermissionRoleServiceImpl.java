package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PermissionRoleMapper;
import com.riskcontrol.domain.PermissionRole;
import com.riskcontrol.service.IPermissionRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Service
public class PermissionRoleServiceImpl extends ServiceImpl<PermissionRoleMapper, PermissionRole> implements IPermissionRoleService {

    @Override
    public void deleteByRoleId(Long roleId) {
        LambdaUpdateWrapper<PermissionRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PermissionRole::getRoleId, roleId);

        this.remove(updateWrapper);
    }

    @Override
    public List<Long> listPermissionIdByRoleId(Long roleId) {
        LambdaQueryWrapper<PermissionRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PermissionRole::getPermissionId);
        queryWrapper.eq(PermissionRole::getRoleId, roleId);

        List<PermissionRole> list = this.list(queryWrapper);

        List<Long> permissionIds = list.stream().map(PermissionRole::getPermissionId).collect(Collectors.toList());

        return permissionIds;
    }
}
