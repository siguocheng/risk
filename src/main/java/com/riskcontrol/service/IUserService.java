package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.vo.user.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface IUserService extends IService<User> {

    IPage<UserPage> queryPage(UserQuery query);

    Long create(UserModify update) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    Long update(UserModify update);

    Long delete(Long id);

    User getUserByAccountName(String accountName, Integer level);

    UserLoginVO login(UserLogin userLogin) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    UserLoginVO loginOfAzure(String token);

    User queryUserInfo(Long userId);

	Integer updatePassword(User user);
}
