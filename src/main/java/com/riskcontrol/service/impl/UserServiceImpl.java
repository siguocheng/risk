package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.constant.UserLevelEnum;
import com.riskcontrol.dao.RoleMapper;
import com.riskcontrol.dao.UserMapper;
import com.riskcontrol.dao.UserRoleMapper;
import com.riskcontrol.domain.Role;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.UserRole;
import com.riskcontrol.domain.vo.TokenUserBean;
import com.riskcontrol.domain.vo.user.*;
import com.riskcontrol.exception.BusinessException;
import com.riskcontrol.service.IPermissionResourceService;
import com.riskcontrol.service.IPermissionService;
import com.riskcontrol.service.IUserRoleService;
import com.riskcontrol.service.IUserService;
import com.riskcontrol.util.EncryptionUtil;
import com.riskcontrol.util.JWTUtil;
import com.riskcontrol.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    IUserRoleService userRoleService;

    @Resource
    IPermissionResourceService permissionResourceService;

    @Resource
    IPermissionService permissionService;

    @Resource
    @Lazy
    RedisUtil redisUtil;

    @Resource
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public IPage<UserPage> queryPage(UserQuery query) {

        IPage<UserPage> pageList = this.baseMapper.queryPage(query, new Page<>(query.getPageNum(), query.getPageSize()));

        return pageList;
    }

    @Override
    public Long create(UserModify update) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        User user = new User();
        BeanUtils.copyProperties(update, user);
        user.setLevel(UserLevelEnum.NORMAL.value);
        String password = EncryptionUtil.hashPassword(update.getPassword());
        user.setPassword(password);
        this.save(user);

        UserRole ur = new UserRole();
        ur.setUserId(user.getId());
        ur.setRoleId(update.getRoleId());
        userRoleService.save(ur);

        return user.getId();
    }

    @Override
    public Long update(UserModify update) {

        User user = new User();
        BeanUtils.copyProperties(update, user);
        this.updateById(user);

        userRoleService.deleteByUserId(user.getId());

        UserRole ur = new UserRole();
        ur.setUserId(user.getId());
        ur.setRoleId(update.getRoleId());
        userRoleService.save(ur);

        return user.getId();
    }

    @Override
    public Long delete(Long id) {

        this.removeById(id);

        userRoleService.deleteByUserId(id);
        return id;
    }

    @Override
    public User getUserByAccountName(String accountName, Integer level) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccountName, accountName);
        if (level != null) {
            queryWrapper.eq(User::getLevel, level);
        }
        return this.getOne(queryWrapper);
    }

    @Override
    public UserLoginVO login(UserLogin userLogin) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        if(userLogin.getLevel() == null){
            throw new BusinessException("请选择登录方式");
        }

        UserLoginVO result = new UserLoginVO();

        String username = userLogin.getUserName();
        String password = userLogin.getPassword();
        User user = this.getUserByAccountName(username, null);

        if (user == null) {
            if(userLogin.getLevel().equals(UserLevelEnum.API.value)){
                List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                        .eq(Role::getName, "全功能"));
                if(CollectionUtils.isEmpty(roles)){
                    throw new BusinessException("初始adfs角色权限未创建");
                }
                Role role = roles.get(0);

                User resultUser = new User(username, username, UserLevelEnum.API.value);
                userMapper.insert(resultUser);

                UserRole userRole = new UserRole(resultUser.getId(), role.getId());
                userRoleMapper.insert(userRole);
                user = resultUser;

            }else {
                throw new BusinessException("用户名或密码错误");
            }
        }

        //三方用户密码由三方验证
        if(!user.getLevel().equals(UserLevelEnum.API.value)){
            String newPassword = EncryptionUtil.hashPassword(password);

            // 校验密码
            if (!newPassword.equals(user.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }
        }



        Map<String, Boolean> btnPermission = permissionService.getPermissionCodeMapByUserId(user.getId());
        List<String> permissionUrls = permissionResourceService.getPermissionResourceByUserId(user.getId());

        TokenUserBean tokenUser = TokenUserBean.generateTokenUserBean(user, permissionUrls, false);

        String token = JWTUtil.createToken(tokenUser);

        String refreshToken = JWTUtil.createToken(tokenUser);

        BeanUtils.copyProperties(user, result);
        result.setUserId(user.getId());
        result.setToken(token);
        result.setRefreshToken(refreshToken);
        result.setBtnPermission(btnPermission);

        // 把tokenUserBean保存到redis，一开始的想法是把该bean通过JWT加密，生成token，但是发现token实在太长了
        redisUtil.saveTokenUserBean(tokenUser, token);
        //refreshToken
        redisUtil.saveRefreshToken(tokenUser, refreshToken);

        return result;

    }

    @Override
    public UserLoginVO loginOfAzure(String token) {
        String username = JWTUtil.getUsername(token);
        User user = this.getUserByAccountName(username, null);
        if(user == null){
            throw new BusinessException("用户不存在");
        }
        Map<String, Boolean> btnPermission = permissionService.getPermissionCodeMapByUserId(user.getId());
        UserLoginVO result = new UserLoginVO();

        BeanUtils.copyProperties(user, result);
        result.setUserId(user.getId());
        result.setToken(token);
        result.setBtnPermission(btnPermission);

        return result;
    }

    @Override
    public User queryUserInfo(Long userId) {
       return userMapper.selectById(userId);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest("123456".getBytes());
        System.out.println(Base64.getEncoder().encodeToString(hash));
    }

	@Override
	public Integer updatePassword(User user) {
		return userMapper.updatePassword(user);
	}
}
