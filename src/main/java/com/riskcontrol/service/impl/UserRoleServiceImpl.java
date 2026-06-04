package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.UserRoleMapper;
import com.riskcontrol.domain.UserRole;
import com.riskcontrol.service.IUserRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Override
    public void deleteByUserId(Long userId) {

        LambdaUpdateWrapper<UserRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserRole::getUserId, userId);

        this.remove(updateWrapper);
    }
}
