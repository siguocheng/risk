package com.riskcontrol.domain.vo.user;

import com.riskcontrol.constant.UserLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserLogin {

    @Schema(description = "账号")
    private String userName;

    @Schema(description = "密码")
    private String password;


    // 登录方式 用户类型0 管理员 1普通用户 2第三方用户
    private Integer level = UserLevelEnum.NORMAL.value;
}
