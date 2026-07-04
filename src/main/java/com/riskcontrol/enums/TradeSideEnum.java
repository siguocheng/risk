package com.riskcontrol.enums;

/**
 * 交易方向枚举
 * BOT 买入
 * SLD 卖出
 */
public enum TradeSideEnum {

    /** 买入 */
    BOT("买"),
    /** 卖出 */
    SLD("卖");

    // 中文描述
    private final String desc;

    TradeSideEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static TradeSideEnum getByCode(String code) {
        for (TradeSideEnum side : TradeSideEnum.values()) {
            if (side.name().equals(code)) {
                return side;
            }
        }
        throw new IllegalArgumentException("无效交易方向编码：" + code);
    }
}
