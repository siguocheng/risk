package com.riskcontrol.util;

import com.riskcontrol.domain.PeriodSegment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TwrCalculator {

    // 保留小数位数
    private static final int SCALE = 10;
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;
    private static final MathContext MATH_CONTEXT = new MathContext(20);

    /**
     * 计算总时间加权收益率（小数形式，如0.0301代表3.01%）
     * @param segmentList 所有分段区间
     * @return TWR 小数
     */
    public static BigDecimal calculateTotalTwr(List<PeriodSegment> segmentList) {
        if (segmentList == null || segmentList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 累计复利因子：初始=1
        BigDecimal compoundFactor = BigDecimal.ONE;

        for (PeriodSegment seg : segmentList) {
            BigDecimal start = seg.getStartAsset();
            BigDecimal end = seg.getEndAsset();
            BigDecimal cf = seg.getCashFlow();

            // 子区间收益 ri = (end - start - cf) / start
            BigDecimal numerator = end.subtract(start).subtract(cf);
            BigDecimal ri = numerator.divide(start, SCALE, ROUND);

            // 复利因子相乘 (1+r1)*(1+r2)...
            BigDecimal onePlusRi = BigDecimal.ONE.add(ri);
            compoundFactor = compoundFactor.multiply(onePlusRi, MATH_CONTEXT);
        }
        // TWR = 复利因子 - 1
        return compoundFactor.subtract(BigDecimal.ONE).setScale(SCALE, ROUND);
    }

    /**
     * 转为百分比，保留4位小数
     */
    public static BigDecimal toPercent(BigDecimal twr) {
        return twr.multiply(new BigDecimal("100")).setScale(4, ROUND);
    }

    /**
     * 年化时间加权收益率
     * @param totalTwr 区间总TWR(小数)
     * @param totalDays 区间总天数
     * @return 年化TWR(小数)
     */
    public static BigDecimal calculateAnnualTwr(BigDecimal totalTwr, int totalDays) {
        if (totalDays <= 0) {
            throw new IllegalArgumentException("天数必须大于0");
        }
        BigDecimal base = BigDecimal.ONE.add(totalTwr);
        // 指数 = 365 / totalDays
        BigDecimal exponent = new BigDecimal("365").divide(new BigDecimal(totalDays), SCALE, ROUND);
        // (1+TWR)^(365/天数) -1
        BigDecimal annualFactor = pow(base, exponent);
        return annualFactor.subtract(BigDecimal.ONE).setScale(SCALE, ROUND);
    }

    /**
     * BigDecimal 高精度幂运算 a^b
     */
    private static BigDecimal pow(BigDecimal a, BigDecimal b) {
        return new BigDecimal(Math.exp(Math.log(a.doubleValue()) * b.doubleValue()), MATH_CONTEXT);
    }

    // 测试示例
    public static void main(String[] args) {
        // 你的案例：
        // 第1段：期初1000，期末1020，无现金流
        // 加仓1000后第2段：期初2020，期末2040，无现金流
        List<PeriodSegment> segments = new ArrayList<>();
        segments.add(new PeriodSegment(
                new BigDecimal("1000"),
                new BigDecimal("1020"),
                BigDecimal.ZERO
        ));
        segments.add(new PeriodSegment(
                new BigDecimal("2020"),
                new BigDecimal("2040"),
                BigDecimal.ZERO
        ));

        TwrCalculator calculator = new TwrCalculator();
        BigDecimal twrDecimal = calculator.calculateTotalTwr(segments);
        BigDecimal twrPercent = calculator.toPercent(twrDecimal);

        System.out.println("总时间加权收益率(小数)：" + twrDecimal);
        System.out.println("总时间加权收益率(百分比)：" + twrPercent + "%");

        // 假设总持有2天，计算年化
        BigDecimal annualTwr = calculator.calculateAnnualTwr(twrDecimal, 2);
        System.out.println("年化TWR(百分比)：" + calculator.toPercent(annualTwr) + "%");
    }
}
