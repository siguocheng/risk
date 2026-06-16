package com.riskcontrol.util;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;

public class RiskMetricsUtil {

    // 标准正态分布分位数 固定常量
    public static final double Z_95 = -1.645;
    public static final double Z_99 = -2.326;

    // 标准正态分布分位数 固定常量
    public static final double Z_ALPHA_95 = 0.05;
    public static final double Z_ALPHA_99 = 0.01;

    // 蒙特卡洛模拟次数
    private static final int MC_SIM_TIMES = 100000;

    // ---------- 历史模拟 CVaR ----------
    // alpha=0.05 对应 95% 置信
    public static double calcHistoryCVaR(double[] prices, double marketValue, double alpha) {

        // 计算对数日收益率（VaR专用）
        double[] logReturns = calcLogReturn(prices);

        if (logReturns.length == 0) return 0.0;

        double cvarRet = calcHistoryCVaRPercent(logReturns, alpha); // 尾部平均收益率
        return Math.abs(cvarRet * marketValue);
    }

    // ---------- 历史模拟 CVaR ----------
    // alpha=0.05 对应 95% 置信
    public static double calcHistoryCVaRPercent(double[] returns, double alpha) {
        if (returns.length == 0) return 0.0;

        double[] copy = Arrays.copyOf(returns, returns.length);
        Arrays.sort(copy); // 从小到大排序（负收益在前面）

        // 取最差 alpha% 的数据
        int tailCount = (int) Math.ceil(copy.length * alpha);
        if (tailCount <= 0) tailCount = 1;

        double sumTail = 0.0;
        for (int i = 0; i < tailCount; i++) {
            sumTail += copy[i];
        }
        double cvarRet = sumTail / tailCount; // 尾部平均收益率
        return cvarRet;
    }


    // ---------- 蒙特卡洛 CVaR ----------
    public static double calcMonteCarloCVaR(double[] prices, double marketValue, double alpha) {

        // 计算对数日收益率（VaR专用）
        double[] logReturns = calcLogReturn(prices);

        if (logReturns.length == 0) return 0.0;

        double cvarRet = calcMonteCarloCVaRPercent(logReturns, alpha);
        return Math.abs(cvarRet * marketValue);
    }

    // ---------- 蒙特卡洛 CVaR ----------
    public static double calcMonteCarloCVaRPercent(double[] returns, double alpha) {
        if (returns.length == 0) return 0.0;

        double mu = mean(returns);
        double sigma = stdDev(returns);
        Random random = new Random();

        double[] simRet = new double[MC_SIM_TIMES];
        for (int i = 0; i < MC_SIM_TIMES; i++) {
            simRet[i] = mu + random.nextGaussian() * sigma;
        }

        Arrays.sort(simRet);
        int tailCount = (int) Math.ceil(MC_SIM_TIMES * alpha);
        double sumTail = 0.0;
        for (int i = 0; i < tailCount; i++) {
            sumTail += simRet[i];
        }
        double cvarRet = sumTail / tailCount;
        return cvarRet;
    }


    // ---------- 参数法 CVaR（正态分布假设） ----------
    // 公式：CVaR = -(μ + φ(z)/α * σ) * MV
    // φ 是标准正态密度，z 是分位数（如 Z_95=-1.645）
    public static double calcParamCVaR(double[] prices, double marketValue, double zQuantile, double alpha) {

        // 计算对数日收益率（VaR专用）
        double[] logReturns = calcLogReturn(prices);

        if (logReturns.length == 0) return 0.0;

        double cvarRet = calcParamCVaRPercent(logReturns, zQuantile, alpha);
        return cvarRet * marketValue;
    }

    // ---------- 参数法 CVaR（正态分布假设） ----------
    // 公式：CVaR = -(μ + φ(z)/α * σ) * MV
    // φ 是标准正态密度，z 是分位数（如 Z_95=-1.645）
    public static double calcParamCVaRPercent(double[] returns, double zQuantile, double alpha) {
        if (returns.length == 0) return 0.0;

        double mu = mean(returns);
        double sigma = stdDev(returns);

        // 标准正态密度 φ(z)
        double phi = Math.exp(-0.5 * zQuantile * zQuantile) / Math.sqrt(2 * Math.PI);

        double cvarRet = -(mu + phi / alpha * sigma);
        return cvarRet;
    }

    /**
     * 蒙特卡洛模拟 VaR
     * @param prices 每日价格
     * @param marketValue 持仓市值
     * @param alpha 显著性水平 0.05(95%) / 0.01(99%)
     */
    public static double calcMonteCarloVar(double[] prices, double marketValue, double alpha) {

        // 计算对数日收益率（VaR专用）
        double[] logReturns = calcLogReturn(prices);

        if (logReturns.length == 0) return 0.0;
        double quantileRet = calcMonteCarloVarPercent(logReturns, alpha);
        return Math.abs(quantileRet * marketValue);
    }

    /**
     * 蒙特卡洛模拟 VaR
     * @param returns 对数收益率
     * @param alpha 显著性水平 0.05(95%) / 0.01(99%)
     */
    public static double calcMonteCarloVarPercent(double[] returns, double alpha) {
        if (returns.length == 0) return 0.0;
        double mu = mean(returns);
        double sigma = stdDev(returns);
        Random random = new Random();
        double[] simRet = new double[MC_SIM_TIMES];

        for (int i = 0; i < MC_SIM_TIMES; i++) {
            simRet[i] = mu + random.nextGaussian() * sigma;
        }
        Arrays.sort(simRet);
        int index = (int) Math.floor(MC_SIM_TIMES * alpha);
        double quantileRet = simRet[index];
        return quantileRet;
    }

    /**
     * 历史模拟法结合市值 VaR
     * @param prices 每日价格
     * @param marketValue 持仓市值
     * @param alpha 显著性水平 0.05(95%) / 0.01(99%)
     */
    public static double calcHistoryVar(double[] prices, double marketValue, double alpha) {

        // 计算对数日收益率（VaR专用）
        double[] logReturns = calcLogReturn(prices);

        if (logReturns.length == 0) return 0.0;
        double quantileRet = calcHistoryVarPercent(logReturns, alpha);
        return Math.abs(quantileRet * marketValue);
    }

    /**
     * 历史模拟法 VaR
     * @param returns 对数收益率
     * @param alpha 显著性水平 0.05(95%) / 0.01(99%)
     */
    public static double calcHistoryVarPercent(double[] returns, double alpha) {

        if (returns.length == 0) return 0.0;
        double[] copy = Arrays.copyOf(returns, returns.length);
        Arrays.sort(copy);
        int index = (int) Math.floor(copy.length * alpha);
        double quantileRet = copy[index];
        return quantileRet;
    }

    /**
     * 参数法 VaR (方差-协方差) 结合市值
     * @param prices 价格
     * @param marketValue 持仓市值
     * @param zQuantile 正态分位数 Z_95 Z_99
     * @return 单日绝对亏损VaR（金额）
     */
    public static double calcParamVar(double[] prices, double marketValue, double zQuantile) {

        // 计算对数日收益率（VaR专用）
        double[] logReturns = calcLogReturn(prices);

        double var = calcParamVarPercent(logReturns, zQuantile);
        double varValue = Math.abs(var * marketValue);

        return varValue;
    }

    /**
     * 参数法 VaR (方差-协方差)
     * @param returns 对数收益率数组
     * @param zQuantile 正态分位数
     * @return 单日绝对亏损VaR（金额）
     */
    public static double calcParamVarPercent(double[] returns, double zQuantile) {
        if (returns.length == 0) return 0.0;
        double mu = mean(returns);
        double sigma = stdDev(returns);
        double retVar = mu + zQuantile * sigma;

        return retVar;
//        return Math.abs(retVar * marketValue);
    }

    // ===================== 基础统计方法 =====================
    private static double mean(double[] arr) {
        return DoubleStream.of(arr).average().orElse(0.0);
    }

    private static double stdDev(double[] arr) {
        double avg = mean(arr);
        double sum = 0.0;
        for (double v : arr) {
            sum += Math.pow(v - avg, 2);
        }
        return arr.length <= 1 ? 0 : Math.sqrt(sum / (arr.length - 1));
    }

    /**
     * 3. 计算对数日收益率（VaR专用）
     */
    public static double[] calcLogReturn(double[] prices) {
        if (prices == null || prices.length < 2) {
            return new double[0];
        }
        int len = prices.length - 1;
        double[] logReturns = new double[len];
        for (int i = 0; i < len; i++) {
            double prev = prices[i];
            double curr = prices[i + 1];
            // 防除0、负数价格（异常行情）
            if (prev <= 0 || curr <= 0) {
                logReturns[i] = 0.0;
                continue;
            }
            logReturns[i] = Math.log(curr / prev);
        }
        return logReturns;
    }

    /**
     * 单日指标 转为 T日持有期指标（正态假设通用）
     * @param oneDayValue 1日 VaR / CVaR
     * @param holdDays 持有天数 T
     * @return T日风险值
     */
    public static double convertToTDay(double oneDayValue, int holdDays) {
        if (holdDays <= 1) {
            return oneDayValue;
        }
        return oneDayValue * Math.sqrt(holdDays);
    }

    public static void main(String[] args) {

    }
}
