package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PermissionRole;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface IPermissionRoleService extends IService<PermissionRole> {

    void deleteByRoleId(Long roleId);

    List<Long> listPermissionIdByRoleId(Long roleId);
}
