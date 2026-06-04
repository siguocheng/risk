package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.vo.user.UserPage;
import com.riskcontrol.domain.vo.user.UserQuery;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
public interface UserMapper extends BaseMapper<User> {

    IPage<UserPage> queryPage(@Param("query") UserQuery query, @Param("page") Page<?> page);

	Integer updatePassword(User user);
}
