package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskcontrol.domain.Permission;
import com.riskcontrol.domain.vo.permisssion.PermissionTableVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    void deleteAll();

    List<String> getAllPermissionCode();

    List<String> getPermissionCodeByUserId(Long userId);

    List<PermissionTableVO> getPermissions(Integer platformType);
}
