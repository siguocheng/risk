package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.UserRole;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface IUserRoleService extends IService<UserRole> {

    void deleteByUserId(Long userId);
}
