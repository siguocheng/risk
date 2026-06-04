package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskcontrol.domain.PermissionRole;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface PermissionRoleMapper extends BaseMapper<PermissionRole> {

    void deleteNoExist();


}
