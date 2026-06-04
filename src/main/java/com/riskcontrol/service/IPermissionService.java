package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Permission;
import com.riskcontrol.domain.vo.permisssion.PermissionTableVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface IPermissionService extends IService<Permission> {

    void init();

    List<PermissionTableVO> listPermission(Integer platformType);

    Map<String, Boolean> getPermissionCodeMapByUserId(Long userId);
}
