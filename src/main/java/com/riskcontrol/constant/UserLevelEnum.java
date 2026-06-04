package com.riskcontrol.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserLevelEnum {

    ADMIN(0, "管理员")
    ,NORMAL(1, "普通用户")
    ,API(2, "第三方用户")
    ;

    public Integer value;
    public String text;
}
