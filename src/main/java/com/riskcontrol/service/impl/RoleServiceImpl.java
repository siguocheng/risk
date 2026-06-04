package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.RoleMapper;
import com.riskcontrol.domain.PermissionRole;
import com.riskcontrol.domain.Role;
import com.riskcontrol.domain.vo.role.RoleModify;
import com.riskcontrol.domain.vo.role.RolePage;
import com.riskcontrol.domain.vo.role.RoleQuery;
import com.riskcontrol.service.IPermissionRoleService;
import com.riskcontrol.service.IRoleService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    IPermissionRoleService permissionRoleService;

    @Override
    public IPage<RolePage> queryPage(RoleQuery query) {

        IPage<RolePage> pageList = this.baseMapper.queryPage(query, new Page<>(query.getPageNum(), query.getPageSize()));

        for (RolePage record : pageList.getRecords()) {
            record.setPermissionIdList(permissionRoleService.listPermissionIdByRoleId(record.getId()));
        }

        return pageList;
    }

    @Override
    public Long create(RoleModify update) {
        Role role = new Role();
        BeanUtils.copyProperties(update, role);

        role.setIsDefaultRole(false);
        this.save(role);

        List<PermissionRole> permissionRoleList = new ArrayList<>();
        for (Long permissionId : update.getPermissionIdList()) {
            PermissionRole pr = new PermissionRole();
            pr.setRoleId(role.getId());
            pr.setPermissionId(permissionId);
            permissionRoleList.add(pr);
        }

        permissionRoleService.saveBatch(permissionRoleList);

        return role.getId();
    }

    @Override
    public Long update(RoleModify update) {

        Role role = new Role();
        BeanUtils.copyProperties(update, role);
        this.updateById(role);

        permissionRoleService.deleteByRoleId(role.getId());

        List<PermissionRole> permissionRoleList = new ArrayList<>();
        for (Long permissionId : update.getPermissionIdList()) {
            PermissionRole pr = new PermissionRole();
            pr.setRoleId(role.getId());
            pr.setPermissionId(permissionId);
            permissionRoleList.add(pr);
        }

        permissionRoleService.saveBatch(permissionRoleList);

        return role.getId();
    }

    @Override
    public Long delete(Long id) {

        this.removeById(id);

        permissionRoleService.deleteByRoleId(id);

        return id;
    }

    @Override
    public List<Role> queryList(RoleQuery query) {

        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        return this.list(queryWrapper);
    }
}
