package com.riskcontrol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum GenericTickListEnum {

    // ===================== 一、期权 Greeks & 波动率（期权链必用） =====================
    MODEL_OPTION_COMPUTATION(46, "模型Delta/Gamma/Vega/Theta/IV（最推荐）"),
    OPTION_VOLUME(100, "期权成交量"),
    OPEN_INTEREST(101, "未平仓持仓量 OI"),
    HISTORICAL_VOLATILITY(104, "底层标的历史波动率 HV"),
    CLOSE_IMPLIED_VOLATILITY(106, "收盘隐含波动率"),

    // ===================== 二、风控 / 计价类（Riskfolio、PnL） =====================
    MARK_PRICE(221, "盯市结算价，损益计算基准"),
    RT_VOLUME(233, "实时成交量、成交笔数、成交时间戳"),
    SHORTABLE(236, "可融券数量（做空判断）"),

    // ===================== 三、基本面 / 统计类 =====================
    FUNDAMENTALS(47, "基本面数据（PE、EPS、分红等）"),
    MISC_STATS(165, "52周高低、平均成交量、市值"),

    // ===================== 四、竞价 / 盘口深度 =====================
    AUCTION_VALUES(225, "开盘 / 收盘竞价不平衡量、竞价价格"),

    // ===================== 五、利率 / 债券专用 =====================
    IB_REFERENCE_RATE(381, "IB 基准利率"),
    BOND_ANALYTICS(125, "债券久期、凸性、收益率");

    /** TWS GenericTick 数字ID */
    private final int tickId;
    /** 中文描述 */
    private final String desc;

    public int getTickId() {
        return tickId;
    }

    public String getDesc() {
        return desc;
    }

    // ===================== 工具方法 =====================

    /**
     * 将多个枚举拼接为逗号分隔字符串，直接用于 reqMktData genericTickList
     * @param enums 多个指标枚举
     * @return "46,100,101,221" 格式字符串
     */
    public static String joinTickIds(GenericTickListEnum... enums) {
        return Arrays.stream(enums)
                .map(e -> String.valueOf(e.getTickId()))
                .collect(Collectors.joining(","));
    }

    /**
     * 批量枚举转数字ID列表
     */
    public static List<Integer> toIdList(GenericTickListEnum... enums) {
        return Arrays.stream(enums)
                .map(GenericTickListEnum::getTickId)
                .collect(Collectors.toList());
    }


}
