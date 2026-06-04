package com.riskcontrol.constant;

import java.math.BigDecimal;

public class BaseConstant {

    /**
     * 后台响应数据对象key
     */
    public static final String RESPONSE_CODE = "code";
    public static final String RESPONSE_MESSAGE = "msg";
    public static final String RESPONSE_DATA = "data";

    public static final Integer ADD_TYPE = 1;
    public static final Integer UPDATE_TYPE = 2;


    public static final Integer TOKEN_LOGIN_ERROR_CODE = -1;

    public static final Integer PERMISSION_CHANGED = 11;


    public static final String token = "token";
    public static final String SUBSCRIPTION_ID = "subscriptionId";

    /**
     * 默认节拍
     */
    public static final BigDecimal DEFAULT_PRODUCTION_CYCLE = BigDecimal.valueOf(5);

    /**
     * 删除标识，未删除
     */
    public static final Boolean NORMAL = Boolean.FALSE;

    public static final String GMT_ZERO = "GMT+00:00";

    public static final String GMT_LOCAL = "GMT+08:00";

    /**
     * 月查询
     */
    public static final Long MONTH = 2L;


    /**
     *企业微信
     */
    public static final Integer  TYPE_WECHAT_ENTERPRISE= 1;

    /**
     * 飞书
     */
    public static final Integer  TYPE_FEI_SHU= 2;
}
