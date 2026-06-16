package com.riskcontrol.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 历史模拟法 组合 VaR / ES 计算（对标 IBKR 风险价值）
 */
public class PortfolioVaR {

    // 置信水平 99% (IBKR 默认)
    private static final double CONFIDENCE = 0.99;
    // 回看历史天数
    private static final int LOOK_BACK_DAYS = 250;
    // 保留小数位数
    private static final int SCALE = 2;

    /**
     * 标的持仓：代码 -> 持仓市值(HKD)
     */
    static class Holding {
        String symbol;
        BigDecimal marketValue;

        public Holding(String symbol, BigDecimal marketValue) {
            this.symbol = symbol;
            this.marketValue = marketValue;
        }
    }

    /**
     * 模拟收益率数据：key=标的代码, value=每日收益率列表
     * 实际项目替换为：从行情API/IBKR拉取真实价格再算收益率
     */
    public static Map<String, List<Double>> mockReturnsData(List<Holding> holdings) {
        Random rand = new Random(42); // 固定随机种子，结果可复现
        Map<String, List<Double>> returnsMap = new HashMap<>();
        for (Holding h : holdings) {
            List<Double> retList = new ArrayList<>();
            // 模拟 250 个交易日日收益率（近似美股波动率）
            for (int i = 0; i < LOOK_BACK_DAYS; i++) {
                // 均值0，波动率 ~1.8%
                double ret = rand.nextGaussian() * 0.018;
                retList.add(ret);
            }
            returnsMap.put(h.symbol, retList);
        }
        return returnsMap;
    }

    /**
     * 计算组合每日盈亏
     */
    public static List<BigDecimal> calcPortfolioPnl(List<Holding> holdings, Map<String, List<Double>> returnsMap) {
        List<BigDecimal> pnlList = new ArrayList<>();

        for (int day = 0; day < LOOK_BACK_DAYS; day++) {
            BigDecimal dailyPnl = BigDecimal.ZERO;
            for (Holding h : holdings) {
                Double ret = returnsMap.get(h.symbol).get(day);
                BigDecimal retBd = BigDecimal.valueOf(ret);
                // 单笔盈亏 = 市值 * 收益率
                BigDecimal singlePnl = h.marketValue.multiply(retBd);
                dailyPnl = dailyPnl.add(singlePnl);
            }
            pnlList.add(dailyPnl.setScale(SCALE, RoundingMode.HALF_UP));
        }
        return pnlList;
    }

    /**
     * 计算 VaR & ES
     * @param pnlList 每日盈亏列表
     * @return [VaR, ES]
     */
    public static BigDecimal[] calcVaREs(List<BigDecimal> pnlList) {
        // 1. 排序：从小到大（亏损在前，盈利在后）
        List<BigDecimal> sortedPnl = pnlList.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        int total = sortedPnl.size();
        // 99%置信：取前 1% 最差样本下标
        int varIndex = (int) Math.floor((1 - CONFIDENCE) * total);

        // VaR：最差分位数对应的损失（取负，转为正数）
        BigDecimal var = sortedPnl.get(varIndex).negate().setScale(SCALE, RoundingMode.HALF_UP);

        // ES：尾部所有亏损的平均值
        BigDecimal sumTail = BigDecimal.ZERO;
        for (int i = 0; i < varIndex; i++) {
            sumTail = sumTail.add(sortedPnl.get(i));
        }
        BigDecimal es = sumTail.divide(BigDecimal.valueOf(varIndex), SCALE + 2, RoundingMode.HALF_UP)
                .negate()
                .setScale(SCALE, RoundingMode.HALF_UP);

        return new BigDecimal[]{var, es};
    }

    public static void main(String[] args) {
        // ========== 1. 你的持仓（港币市值，对应截图组合） ==========
        List<Holding> holdings = new ArrayList<>();
        holdings.add(new Holding("AAPL", new BigDecimal("144010")));
//        holdings.add(new Holding("TSLA", new BigDecimal("4053.00")));
//        holdings.add(new Holding("ES-SPY", new BigDecimal("371611.00")));

        // ========== 2. 获取历史收益率（模拟数据，生产替换为真实行情） ==========
        Map<String, List<Double>> returns = mockReturnsData(holdings);

        // ========== 3. 计算每日组合盈亏 ==========
        List<BigDecimal> pnlList = calcPortfolioPnl(holdings, returns);

        // ========== 4. 计算 VaR / ES ==========
        BigDecimal[] res = calcVaREs(pnlList);
        BigDecimal var = res[0];
        BigDecimal es = res[1];

        // 组合总市值
        BigDecimal totalValue = holdings.stream()
                .map(h -> h.marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 输出结果
        System.out.println("===== 历史模拟法 VaR / ES (99% 置信 1日) =====");
        System.out.println("组合总市值(HKD)：" + totalValue);
        System.out.println("VaR (风险价值) ：" + var + " HKD");
        System.out.println("ES (预期损失)  ：" + es + " HKD");
    }
}
