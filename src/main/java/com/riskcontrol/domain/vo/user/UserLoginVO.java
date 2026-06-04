package com.riskcontrol.domain.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class UserLoginVO {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "账号")
    private String accountName;

    @Schema(description = "昵称")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "按钮权限信息")
    private Map<String, Boolean>  btnPermission;

    @Schema(description = "token")
    private String token;

    @Schema(description = "刷新token")
    private String refreshToken;

    @Schema(description = "用户等级 0管理员 1普通用户")
    private Integer level;
}
