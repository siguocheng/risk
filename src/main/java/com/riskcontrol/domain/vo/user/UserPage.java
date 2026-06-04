package com.riskcontrol.domain.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserPage {

    @Schema(description = "用户id")
    private Long id;

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

    @Schema(description = "角色名称")
    private String roleName;
}
