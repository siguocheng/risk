package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PermissionResource;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface IPermissionResourceService extends IService<PermissionResource> {

    void init(List<PermissionResource> resourceList);

    List<String> getPermissionResourceByUserId(Long userId);
}
