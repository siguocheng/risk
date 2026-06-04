package com.riskcontrol.domain.vo;

import com.riskcontrol.constant.UserLevelEnum;
import com.riskcontrol.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户登录信息对象
 *
 * @author Charlie
 * @date 2018/9/4 14:55
 */
@Data
public class TokenUserBean implements Serializable {

    private static final long serialVersionUID = -971197134784032573L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户登录名
     */
    private String userName;

    /**
     * 组织Id
     */
    private Long groupId;

    /**
     * 是否部门负责人
     */
    private Boolean isOrgLeader;
    /**
     * 权限url
     */
    private List<String> permissionUrls;


    /**
     * 是否管理员角色，拦截器判断权限的时候，如果该用户是管理员，那么直接放行
     */
	private Boolean isDefaultRole;

    /**
     * 是否修改过权限
     * <p>
     * default: false
     */
	private Boolean isChangedPermission;

    public static TokenUserBean generateTokenUserBean(User user, List<String> permissionUrls, Boolean isChangedPermission) {
        TokenUserBean tokenUser = new TokenUserBean();
        tokenUser.setUserId(user.getId());
        tokenUser.setUserName(user.getName());
        tokenUser.setIsDefaultRole(UserLevelEnum.ADMIN.value.equals(user.getLevel()));
        tokenUser.setPermissionUrls(permissionUrls);
        tokenUser.setIsChangedPermission(isChangedPermission);
        return tokenUser;
    }


}
