package com.riskcontrol.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 资源与按钮之间的关联关系
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceMethod {

    /**
     * 按钮的code
     */
    String btnCode() default "-1";

    /**
     * 按钮的code 1:不用登录就能用 2:登录后谁都能用 3:需要权限
     */
    int level() default 2;

    /**
     * 权限描述
     */
    String description() default "";
}
