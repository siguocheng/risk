package com.riskcontrol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {

    CORPORATION("CORPORATION", "公司法人账户"),
    INDIVIDUAL("INDIVIDUAL", "个人账户"),
    JOINT("JOINT", "联名账户"), // 联名账户（多人共同持有，夫妻共用居多）
    TRUST("TRUST", "信托账户");


    // 两个String属性
    private final String key;
    private final String value;

    // 根据key反向查找枚举（工具方法）
    public static AccountTypeEnum getByKey(String key) {
        for (AccountTypeEnum e : values()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        return null;
    }

    // 根据key反向查找枚举（工具方法）
    public static String getValueByKey(String key) {
        for (AccountTypeEnum e : values()) {
            if (e.getKey().equals(key)) {
                return e.value;
            }
        }
        return "";
    }
}
