package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Role;
import com.riskcontrol.domain.vo.role.RoleModify;
import com.riskcontrol.domain.vo.role.RolePage;
import com.riskcontrol.domain.vo.role.RoleQuery;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface IRoleService extends IService<Role> {

    IPage<RolePage> queryPage(RoleQuery query);

    Long create(RoleModify update);

    Long update(RoleModify update);

    Long delete(Long id);

    List<Role> queryList(RoleQuery query);
}
