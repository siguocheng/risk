package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskcontrol.domain.PermissionResource;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface PermissionResourceMapper extends BaseMapper<PermissionResource> {

    void deleteAll();

    List<String> getPermissionResourceByUserId(Long userId);
}
