package com.riskcontrol.util;

import java.math.BigDecimal;

public class IbValueUtil {

    // Long空标记
    public static final long IB_LONG_MIN = Long.MIN_VALUE;
    // Double空标记1：无值极小值
    public static final double IB_DOUBLE_EMPTY_MIN = -9223372036854775808.0;
    // Double空标记2：无值极大值 Double.MAX_VALUE
    public static final double IB_DOUBLE_EMPTY_MAX = Double.MAX_VALUE;
    // BigDecimal对应两个空标记
    public static final BigDecimal IB_DEC_EMPTY_MIN = new BigDecimal("-9223372036854775808");
    public static final BigDecimal IB_DEC_EMPTY_MAX = new BigDecimal("1.7976931348623157E308");

    // int空标记 Integer.MAX_VALUE
    public static final int IB_INT_EMPTY = Integer.MAX_VALUE;

    // int 清洗：Integer.MAX_VALUE 转为 null
    public static Integer trimInt(Integer val) {
        if (val == null || val == IB_INT_EMPTY) {
            return null;
        }
        return val;
    }

    // Long清洗
    public static Long trimLong(Long val) {
        if (val == null || val == IB_LONG_MIN) return null;
        return val;
    }

    /**
     * 统一清洗IB原始Double字段（兼容极小/极大两种空占位）
     */
    public static BigDecimal trimDouble(Double val) {
        if (val == null) return null;
        if (val == IB_DOUBLE_EMPTY_MIN || val == IB_DOUBLE_EMPTY_MAX) {
            return null;
        }
        return BigDecimal.valueOf(val);
    }

    /**
     * 清洗BigDecimal字段
     */
    public static BigDecimal trimBigDec(BigDecimal val) {
        if (val == null) return null;
        if (val.compareTo(IB_DEC_EMPTY_MIN) == 0
                || val.compareTo(IB_DEC_EMPTY_MAX) == 0) {
            return null;
        }
        return val;
    }
}
