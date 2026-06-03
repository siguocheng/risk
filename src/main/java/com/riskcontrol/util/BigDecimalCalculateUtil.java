package com.riskcontrol.util;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

public class BigDecimalCalculateUtil {

    public static BigDecimal formatNumber(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return NumberUtil.toBigDecimal(NumberUtil.toStr(value));
    }
}
