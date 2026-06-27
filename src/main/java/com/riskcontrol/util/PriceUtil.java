package com.riskcontrol.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtil {

    // 保留4位小数，可按需修改
    private static final int SCALE = 4;

    /**
     * 计算涨跌额
     * @param curClose 当日收盘价
     * @param preClose 前收盘价
     * @return 涨跌额
     */
    public static BigDecimal calcChange(BigDecimal curClose, BigDecimal preClose) {
        if (preClose == null || curClose == null || preClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return curClose.subtract(preClose).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 计算涨跌幅 %
     * @param curClose 当日收盘价
     * @param preClose 前收盘价
     * @return 涨跌幅（如 +2.5 代表涨2.5%，-1.2代表跌1.2%）
     */
    public static BigDecimal calcChangeRate(BigDecimal curClose, BigDecimal preClose) {
        if (preClose == null || curClose == null || preClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal change = curClose.subtract(preClose);
        // (change / preClose) * 100
        return change.divide(preClose, SCALE + 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
}
