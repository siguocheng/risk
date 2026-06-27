package com.riskcontrol.enums;

public enum SetTypeEnum {

    /**
     * 股票 Stock
     */
    STK("STK", "股票", "权益现货"),
    /**
     * 期权 Option
     */
    OPT("OPT", "个股期权", "权益衍生品"),
    /**
     * 债券 Bond
     */
    BOND("BOND", "债券", "固收资产"),
    /**
     * 加密货币 Cryptocurrency
     */
    CRYPTO("CRYPTO", "加密货币", "数字虚拟资产"),
    /**
     * 期货 Futures
     */
    FUT("FUT", "期货", "标准化远期衍生品"),
    /**
     * 期货期权 Futures Option
     */
    FOP("FOP", "期货期权", "期货标的期权"),
    /**
     * 现金/现金等价物 Cash
     */
    CASH("CASH", "现金及等价物", "流动性资金"),
    /**
     * 权证 Warrant
     */
    WAR("WAR", "认股权证", "股本权证/备兑权证");

    /** 编码（数据库存储、接口传输用） */
    private final String code;
    /** 中文名称 */
    private final String name;
    /** 资产大类描述 */
    private final String desc;

    SetTypeEnum(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    // getter
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code反向匹配枚举（接口参数、数据库查询常用）
     * @param code 资产编码 STK/FUT...
     * @return AssetType
     */
    public static SetTypeEnum getByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (SetTypeEnum value : SetTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
