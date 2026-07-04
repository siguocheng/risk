package com.riskcontrol.enums;

public enum PositionExecutionOptTypeEnum {

    IN("入库"),
    OUT("出库");

    // 中文描述
    private final String desc;

    PositionExecutionOptTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static PositionExecutionOptTypeEnum getByCode(String code) {
        for (PositionExecutionOptTypeEnum side : PositionExecutionOptTypeEnum.values()) {
            if (side.name().equals(code)) {
                return side;
            }
        }
        throw new IllegalArgumentException("无效交易操作类型：" + code);
    }
}
