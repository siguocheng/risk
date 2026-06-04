package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.riskcontrol.domain.Role;
import com.riskcontrol.domain.vo.role.RolePage;
import com.riskcontrol.domain.vo.role.RoleQuery;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface RoleMapper extends BaseMapper<Role> {

    IPage<RolePage> queryPage(@Param("query") RoleQuery query, @Param("page") Page<?> page);
}
