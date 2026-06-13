package com.riskcontrol.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskcontrol.domain.vo.ibkr.BarData;

import java.util.*;
import java.util.stream.DoubleStream;

/**
 * 基于 TWS API 历史K线 计算 AAPL VaR
 * 包含：数据排序、对数收益率、参数法/历史模拟/蒙特卡洛VaR
 */
public class TwsAaplVarCalculator {
    // 标准正态分布分位数 固定常量
    public static final double Z_95 = -1.645;
    public static final double Z_99 = -2.326;
    // 蒙特卡洛模拟次数
    private static final int MC_SIM_TIMES = 100000;
    // JSON解析器
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 1. 将TWS原始JSON数组 转为 有序K线列表（按时间升序）
     * @param jsonStr TWS返回的K线JSON数组字符串
     * @return 按交易日从旧到新排序的K线集合
     */
    public static List<BarData> parseAndSortBars(String jsonStr) {
        try {
            List<BarData> barList = OBJECT_MAPPER.readValue(jsonStr, new TypeReference<List<BarData>>() {});
            // 按日期 yyyyMMdd 升序排序
            barList.sort(Comparator.comparing(BarData::getTime));
            return barList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 2. 从有序K线提取收盘价数组
     */
    public static double[] extractClosePrices(List<BarData> barList) {
        if (barList == null || barList.size() < 2) {
            return new double[0];
        }
        double[] prices = new double[barList.size()];
        for (int i = 0; i < barList.size(); i++) {
            prices[i] = barList.get(i).getClose();
        }
        return prices;
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

    // ===================== 三大VaR算法 =====================
    /**
     * 参数法 VaR (方差-协方差)
     * @param returns 对数收益率数组
     * @param marketValue 持仓市值
     * @param zQuantile 正态分位数
     * @return 单日绝对亏损VaR（金额）
     */
    public static double calcParamVar(double[] returns, double marketValue, double zQuantile) {
        if (returns.length == 0) return 0.0;
        double mu = mean(returns);
        double sigma = stdDev(returns);
        double retVar = mu + zQuantile * sigma;
        return Math.abs(retVar * marketValue);
    }

    /**
     * 历史模拟法 VaR
     * @param returns 对数收益率
     * @param marketValue 持仓市值
     * @param alpha 显著性水平 0.05(95%) / 0.01(99%)
     */
    public static double calcHistoryVar(double[] returns, double marketValue, double alpha) {
        if (returns.length == 0) return 0.0;
        double[] copy = Arrays.copyOf(returns, returns.length);
        Arrays.sort(copy);
        int index = (int) Math.floor(copy.length * alpha);
        double quantileRet = copy[index];
        return Math.abs(quantileRet * marketValue);
    }

    /**
     * 蒙特卡洛模拟 VaR
     */
    public static double calcMonteCarloVar(double[] returns, double marketValue, double alpha) {
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
        return Math.abs(quantileRet * marketValue);
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

    public static void calculateSingleStockVar(String stockName, String barJson, double marketValue) {
        List<BarData> barList = parseAndSortBars(barJson);
        double[] prices = extractClosePrices(barList);
        double[] logReturns = calcLogReturn(prices);

        if (logReturns.length == 0) {
            System.out.printf("【%s】数据不足，无法计算VaR%n%n", stockName);
            return;
        }

        System.out.println("==================== " + stockName + " VaR 计算结果 ====================");
        // 参数法
        double param95 = calcParamVar(logReturns, marketValue, Z_95);
        double param99 = calcParamVar(logReturns, marketValue, Z_99);
        System.out.printf("参数法 | 95%%置信 1日VaR: %.2f 美元%n", param95);
        System.out.printf("参数法 | 99%%置信 1日VaR: %.2f 美元%n", param99);

        // 历史模拟法
        double hist95 = calcHistoryVar(logReturns, marketValue, 0.05);
        double hist99 = calcHistoryVar(logReturns, marketValue, 0.01);
        System.out.printf("历史模拟 | 95%%置信 1日VaR: %.2f 美元%n", hist95);
        System.out.printf("历史模拟 | 99%%置信 1日VaR: %.2f 美元%n", hist99);

        // 蒙特卡洛
        double mc95 = calcMonteCarloVar(logReturns, marketValue, 0.05);
        double mc99 = calcMonteCarloVar(logReturns, marketValue, 0.01);
        System.out.printf("蒙特卡洛 | 95%%置信 1日VaR: %.2f 美元%n", mc95);
        System.out.printf("蒙特卡洛 | 99%%置信 1日VaR: %.2f 美元%n%n", mc99);

        // 历史模拟 CVaR
        double histCVaR95 = calcHistoryCVaR(logReturns, marketValue, 0.05);
        double histCVaR99 = calcHistoryCVaR(logReturns, marketValue, 0.01);
        System.out.printf("历史模拟 | 95%%置信 1日CVaR: %.2f 美元%n", histCVaR95);
        System.out.printf("历史模拟 | 99%%置信 1日CVaR: %.2f 美元%n", histCVaR99);

        // 参数法 CVaR
        double paramCVaR95 = calcParamCVaR(logReturns, marketValue, Z_95, 0.05);
        double paramCVaR99 = calcParamCVaR(logReturns, marketValue, Z_99, 0.01);
        System.out.printf("参数法   | 95%%置信 1日CVaR: %.2f 美元%n", paramCVaR95);
        System.out.printf("参数法   | 99%%置信 1日CVaR: %.2f 美元%n", paramCVaR99);

        // 蒙特卡洛 CVaR
        double mcCVaR95 = calcMonteCarloCVaR(logReturns, marketValue, 0.05);
        double mcCVaR99 = calcMonteCarloCVaR(logReturns, marketValue, 0.01);
        System.out.printf("蒙特卡洛 | 95%%置信 1日CVaR: %.2f 美元%n", mcCVaR95);
        System.out.printf("蒙特卡洛 | 99%%置信 1日CVaR: %.2f 美元%n%n", mcCVaR99);

        // 计算 10日
        int holdDay = 10;
        double tenDayVar = convertToTDay(param95, holdDay);
        double tenDayCvar = convertToTDay(histCVaR95, holdDay);

        System.out.printf("【%d日持有期】95%%置信 VaR: %.2f 美元%n", holdDay, tenDayVar);
        System.out.printf("【%d日持有期】95%%置信 CVaR: %.2f 美元%n", holdDay, tenDayCvar);
    }

    // ===================== 入口测试 =====================
    public static void main(String[] args) {
        // 模拟 TWS API 返回的多条K线JSON（和你给出格式一致）
        String twsJson = "[\n" +
                "{\"close\":199.2,\"count\":138452,\"high\":199.68,\"low\":197.36,\"open\":199.08,\"time\":\"20250612\",\"volume\":25791782,\"wap\":198.0},\n" +
                "{\"close\":196.45,\"count\":166954,\"high\":200.37,\"low\":195.7,\"open\":199.83,\"time\":\"20250613\",\"volume\":31401929,\"wap\":197.0},\n" +
                "{\"close\":198.42,\"count\":129953,\"high\":198.69,\"low\":196.56,\"open\":197.28,\"time\":\"20250616\",\"volume\":24685057,\"wap\":197.0},\n" +
                "{\"close\":195.64,\"count\":113265,\"high\":198.39,\"low\":195.21,\"open\":197.2,\"time\":\"20250617\",\"volume\":21653955,\"wap\":196.0},\n" +
                "{\"close\":196.58,\"count\":133386,\"high\":197.57,\"low\":195.07,\"open\":195.92,\"time\":\"20250618\",\"volume\":25443792,\"wap\":196.0},\n" +
                "{\"close\":201.0,\"count\":189685,\"high\":201.7,\"low\":196.85,\"open\":198.12,\"time\":\"20250620\",\"volume\":46559234,\"wap\":199.0},\n" +
                "{\"close\":201.5,\"count\":149047,\"high\":202.3,\"low\":198.96,\"open\":201.59,\"time\":\"20250623\",\"volume\":27831103,\"wap\":201.0},\n" +
                "{\"close\":200.3,\"count\":181054,\"high\":203.44,\"low\":200.2,\"open\":202.59,\"time\":\"20250624\",\"volume\":33599593,\"wap\":201.0},\n" +
                "{\"close\":201.56,\"count\":123316,\"high\":203.67,\"low\":200.62,\"open\":201.42,\"time\":\"20250625\",\"volume\":23480840,\"wap\":201.0},\n" +
                "{\"close\":201.0,\"count\":164389,\"high\":202.64,\"low\":199.46,\"open\":201.44,\"time\":\"20250626\",\"volume\":31504289,\"wap\":200.0},\n" +
                "{\"close\":201.08,\"count\":175456,\"high\":203.22,\"low\":200.0,\"open\":201.92,\"time\":\"20250627\",\"volume\":32705704,\"wap\":201.0},\n" +
                "{\"close\":205.17,\"count\":269267,\"high\":207.39,\"low\":199.26,\"open\":201.99,\"time\":\"20250630\",\"volume\":54102691,\"wap\":202.0},\n" +
                "{\"close\":207.82,\"count\":281984,\"high\":210.19,\"low\":206.14,\"open\":206.72,\"time\":\"20250701\",\"volume\":51670413,\"wap\":208.0},\n" +
                "{\"close\":212.44,\"count\":244406,\"high\":213.34,\"low\":208.14,\"open\":208.99,\"time\":\"20250702\",\"volume\":47709793,\"wap\":211.0},\n" +
                "{\"close\":213.55,\"count\":113163,\"high\":214.65,\"low\":211.81,\"open\":212.07,\"time\":\"20250703\",\"volume\":21913671,\"wap\":213.0},\n" +
                "{\"close\":209.95,\"count\":166410,\"high\":216.23,\"low\":208.8,\"open\":212.72,\"time\":\"20250707\",\"volume\":30597625,\"wap\":211.0},\n" +
                "{\"close\":210.01,\"count\":132108,\"high\":211.43,\"low\":208.45,\"open\":210.1,\"time\":\"20250708\",\"volume\":24616407,\"wap\":209.0},\n" +
                "{\"close\":211.14,\"count\":160231,\"high\":211.33,\"low\":207.22,\"open\":209.53,\"time\":\"20250709\",\"volume\":30623245,\"wap\":209.0},\n" +
                "{\"close\":212.41,\"count\":150287,\"high\":213.48,\"low\":210.03,\"open\":210.44,\"time\":\"20250710\",\"volume\":28382734,\"wap\":212.0},\n" +
                "{\"close\":211.16,\"count\":113647,\"high\":212.13,\"low\":209.86,\"open\":210.43,\"time\":\"20250711\",\"volume\":21558284,\"wap\":210.0},\n" +
                "{\"close\":208.62,\"count\":131717,\"high\":210.91,\"low\":207.54,\"open\":209.93,\"time\":\"20250714\",\"volume\":23931631,\"wap\":208.0},\n" +
                "{\"close\":209.11,\"count\":129179,\"high\":211.89,\"low\":208.92,\"open\":209.22,\"time\":\"20250715\",\"volume\":23834647,\"wap\":210.0},\n" +
                "{\"close\":210.16,\"count\":144275,\"high\":212.4,\"low\":208.64,\"open\":210.3,\"time\":\"20250716\",\"volume\":26057225,\"wap\":210.0},\n" +
                "{\"close\":210.02,\"count\":162302,\"high\":211.8,\"low\":209.59,\"open\":210.72,\"time\":\"20250717\",\"volume\":28831926,\"wap\":210.0},\n" +
                "{\"close\":211.18,\"count\":125657,\"high\":211.79,\"low\":209.7,\"open\":210.66,\"time\":\"20250718\",\"volume\":26709715,\"wap\":210.0},\n" +
                "{\"close\":212.48,\"count\":176902,\"high\":215.78,\"low\":211.63,\"open\":212.14,\"time\":\"20250721\",\"volume\":31875276,\"wap\":213.0},\n" +
                "{\"close\":214.4,\"count\":158969,\"high\":214.95,\"low\":212.23,\"open\":213.29,\"time\":\"20250722\",\"volume\":28434741,\"wap\":213.0},\n" +
                "{\"close\":214.15,\"count\":153766,\"high\":215.15,\"low\":212.41,\"open\":215.09,\"time\":\"20250723\",\"volume\":28031489,\"wap\":213.0},\n" +
                "{\"close\":213.76,\"count\":161832,\"high\":215.69,\"low\":213.53,\"open\":213.9,\"time\":\"20250724\",\"volume\":27690949,\"wap\":214.0},\n" +
                "{\"close\":213.88,\"count\":125441,\"high\":215.24,\"low\":213.4,\"open\":214.64,\"time\":\"20250725\",\"volume\":22435977,\"wap\":214.0},\n" +
                "{\"close\":214.05,\"count\":112116,\"high\":214.85,\"low\":213.06,\"open\":214.0,\"time\":\"20250728\",\"volume\":20979637,\"wap\":214.0},\n" +
                "{\"close\":211.27,\"count\":148398,\"high\":214.81,\"low\":210.89,\"open\":214.16,\"time\":\"20250729\",\"volume\":28215124,\"wap\":212.0},\n" +
                "{\"close\":209.05,\"count\":119200,\"high\":212.39,\"low\":207.72,\"open\":211.9,\"time\":\"20250730\",\"volume\":21796233,\"wap\":209.0},\n" +
                "{\"close\":207.57,\"count\":199850,\"high\":209.84,\"low\":207.16,\"open\":208.49,\"time\":\"20250731\",\"volume\":35339307,\"wap\":208.0},\n" +
                "{\"close\":202.38,\"count\":315486,\"high\":213.58,\"low\":201.5,\"open\":210.95,\"time\":\"20250801\",\"volume\":61194902,\"wap\":204.0},\n" +
                "{\"close\":203.35,\"count\":220627,\"high\":207.88,\"low\":201.67,\"open\":204.46,\"time\":\"20250804\",\"volume\":41575582,\"wap\":204.0},\n" +
                "{\"close\":202.92,\"count\":136912,\"high\":205.34,\"low\":202.16,\"open\":203.4,\"time\":\"20250805\",\"volume\":26016426,\"wap\":203.0},\n" +
                "{\"close\":213.25,\"count\":349117,\"high\":215.38,\"low\":205.59,\"open\":205.63,\"time\":\"20250806\",\"volume\":68984952,\"wap\":212.0},\n" +
                "{\"close\":220.03,\"count\":307620,\"high\":220.85,\"low\":216.58,\"open\":218.89,\"time\":\"20250807\",\"volume\":60566435,\"wap\":219.0},\n" +
                "{\"close\":229.35,\"count\":386930,\"high\":231.0,\"low\":219.25,\"open\":220.86,\"time\":\"20250808\",\"volume\":81281357,\"wap\":227.0},\n" +
                "{\"close\":227.18,\"count\":189188,\"high\":229.56,\"low\":224.76,\"open\":227.93,\"time\":\"20250811\",\"volume\":37446748,\"wap\":227.0},\n" +
                "{\"close\":229.65,\"count\":163583,\"high\":230.8,\"low\":227.07,\"open\":228.01,\"time\":\"20250812\",\"volume\":31556177,\"wap\":229.0},\n" +
                "{\"close\":233.33,\"count\":230717,\"high\":235.0,\"low\":230.43,\"open\":231.05,\"time\":\"20250813\",\"volume\":43477560,\"wap\":232.0},\n" +
                "{\"close\":232.78,\"count\":154145,\"high\":235.06,\"low\":230.85,\"open\":234.01,\"time\":\"20250814\",\"volume\":29338129,\"wap\":232.0},\n" +
                "{\"close\":231.59,\"count\":139256,\"high\":234.28,\"low\":229.36,\"open\":234.0,\"time\":\"20250815\",\"volume\":27976278,\"wap\":231.0},\n" +
                "{\"close\":230.89,\"count\":111551,\"high\":233.12,\"low\":230.11,\"open\":231.77,\"time\":\"20250818\",\"volume\":21419078,\"wap\":231.0},\n" +
                "{\"close\":230.56,\"count\":118075,\"high\":232.87,\"low\":229.35,\"open\":231.28,\"time\":\"20250819\",\"volume\":21164727,\"wap\":230.0},\n" +
                "{\"close\":226.01,\"count\":131052,\"high\":230.47,\"low\":225.77,\"open\":229.97,\"time\":\"20250820\",\"volume\":23402459,\"wap\":226.0},\n" +
                "{\"close\":224.9,\"count\":106966,\"high\":226.52,\"low\":223.78,\"open\":226.27,\"time\":\"20250821\",\"volume\":18942470,\"wap\":225.0},\n" +
                "{\"close\":227.76,\"count\":127639,\"high\":229.09,\"low\":225.41,\"open\":226.17,\"time\":\"20250822\",\"volume\":23198421,\"wap\":227.0},\n" +
                "{\"close\":227.16,\"count\":103099,\"high\":229.3,\"low\":226.23,\"open\":226.48,\"time\":\"20250825\",\"volume\":18561203,\"wap\":228.0},\n" +
                "{\"close\":229.31,\"count\":105343,\"high\":229.49,\"low\":224.69,\"open\":226.86,\"time\":\"20250826\",\"volume\":19918324,\"wap\":227.0},\n" +
                "{\"close\":230.49,\"count\":94042,\"high\":230.9,\"low\":228.26,\"open\":228.69,\"time\":\"20250827\",\"volume\":18181994,\"wap\":229.0},\n" +
                "{\"close\":232.56,\"count\":114518,\"high\":233.41,\"low\":229.33,\"open\":230.82,\"time\":\"20250828\",\"volume\":20721721,\"wap\":231.0},\n" +
                "{\"close\":232.14,\"count\":118808,\"high\":233.38,\"low\":231.37,\"open\":232.55,\"time\":\"20250829\",\"volume\":21074167,\"wap\":232.0},\n" +
                "{\"close\":229.72,\"count\":130757,\"high\":230.85,\"low\":226.97,\"open\":229.25,\"time\":\"20250902\",\"volume\":22560705,\"wap\":228.0},\n" +
                "{\"close\":238.47,\"count\":224131,\"high\":238.85,\"low\":234.36,\"open\":237.21,\"time\":\"20250903\",\"volume\":41876214,\"wap\":236.0},\n" +
                "{\"close\":239.78,\"count\":154685,\"high\":239.9,\"low\":236.74,\"open\":238.45,\"time\":\"20250904\",\"volume\":27325798,\"wap\":238.0},\n" +
                "{\"close\":239.69,\"count\":186676,\"high\":241.32,\"low\":238.49,\"open\":240.0,\"time\":\"20250905\",\"volume\":32231886,\"wap\":239.0},\n" +
                "{\"close\":237.88,\"count\":157867,\"high\":240.15,\"low\":236.34,\"open\":239.3,\"time\":\"20250908\",\"volume\":28309913,\"wap\":238.0},\n" +
                "{\"close\":234.35,\"count\":233664,\"high\":238.79,\"low\":233.36,\"open\":237.0,\"time\":\"20250909\",\"volume\":45145566,\"wap\":235.0},\n" +
                "{\"close\":226.79,\"count\":274926,\"high\":232.42,\"low\":225.95,\"open\":231.97,\"time\":\"20250910\",\"volume\":51765395,\"wap\":227.0},\n" +
                "{\"close\":230.03,\"count\":156008,\"high\":230.45,\"low\":226.65,\"open\":226.84,\"time\":\"20250911\",\"volume\":29374467,\"wap\":229.0},\n" +
                "{\"close\":234.07,\"count\":181832,\"high\":234.51,\"low\":229.02,\"open\":229.3,\"time\":\"20250912\",\"volume\":32971785,\"wap\":233.0},\n" +
                "{\"close\":236.7,\"count\":155076,\"high\":238.19,\"low\":235.03,\"open\":237.0,\"time\":\"20250915\",\"volume\":26998887,\"wap\":236.0},\n" +
                "{\"close\":238.15,\"count\":215217,\"high\":241.22,\"low\":236.32,\"open\":237.12,\"time\":\"20250916\",\"volume\":37098569,\"wap\":239.0},\n" +
                "{\"close\":238.99,\"count\":147420,\"high\":240.1,\"low\":237.73,\"open\":238.97,\"time\":\"20250917\",\"volume\":25145998,\"wap\":239.0},\n" +
                "{\"close\":237.88,\"count\":148710,\"high\":241.2,\"low\":236.65,\"open\":239.96,\"time\":\"20250918\",\"volume\":24828491,\"wap\":238.0},\n" +
                "{\"close\":245.5,\"count\":311854,\"high\":246.27,\"low\":240.21,\"open\":241.2,\"time\":\"20250919\",\"volume\":65360751,\"wap\":243.0},\n" +
                "{\"close\":256.08,\"count\":369271,\"high\":256.64,\"low\":248.12,\"open\":248.3,\"time\":\"20250922\",\"volume\":67929923,\"wap\":253.0},\n" +
                "{\"close\":254.43,\"count\":191424,\"high\":257.34,\"low\":253.58,\"open\":255.83,\"time\":\"20250923\",\"volume\":33620295,\"wap\":255.0},\n" +
                "{\"close\":252.31,\"count\":121926,\"high\":255.58,\"low\":251.04,\"open\":255.25,\"time\":\"20250924\",\"volume\":21753461,\"wap\":252.0},\n" +
                "{\"close\":256.87,\"count\":181676,\"high\":257.17,\"low\":251.71,\"open\":253.35,\"time\":\"20250925\",\"volume\":33679965,\"wap\":254.0},\n" +
                "{\"close\":255.46,\"count\":149165,\"high\":257.6,\"low\":253.78,\"open\":254.18,\"time\":\"20250926\",\"volume\":24877099,\"wap\":255.0},\n" +
                "{\"close\":254.43,\"count\":123370,\"high\":255.0,\"low\":253.01,\"open\":254.56,\"time\":\"20250929\",\"volume\":21788282,\"wap\":253.0},\n" +
                "{\"close\":254.63,\"count\":110332,\"high\":255.92,\"low\":253.11,\"open\":254.89,\"time\":\"20250930\",\"volume\":18925926,\"wap\":254.0},\n" +
                "{\"close\":255.45,\"count\":145147,\"high\":258.79,\"low\":254.93,\"open\":255.04,\"time\":\"20251001\",\"volume\":25515504,\"wap\":256.0},\n" +
                "{\"close\":257.13,\"count\":126065,\"high\":258.18,\"low\":254.15,\"open\":256.59,\"time\":\"20251002\",\"volume\":24287379,\"wap\":256.0},\n" +
                "{\"close\":258.02,\"count\":166518,\"high\":259.24,\"low\":253.95,\"open\":254.67,\"time\":\"20251003\",\"volume\":30080734,\"wap\":257.0},\n" +
                "{\"close\":256.69,\"count\":131146,\"high\":259.07,\"low\":255.05,\"open\":257.99,\"time\":\"20251006\",\"volume\":23399815,\"wap\":256.0},\n" +
                "{\"close\":256.48,\"count\":106785,\"high\":257.4,\"low\":255.43,\"open\":256.81,\"time\":\"20251007\",\"volume\":18973959,\"wap\":256.0},\n" +
                "{\"close\":258.06,\"count\":99018,\"high\":258.52,\"low\":256.11,\"open\":256.52,\"time\":\"20251008\",\"volume\":18897335,\"wap\":257.0},\n" +
                "{\"close\":254.04,\"count\":118664,\"high\":258.0,\"low\":253.14,\"open\":257.81,\"time\":\"20251009\",\"volume\":21470272,\"wap\":254.0},\n" +
                "{\"close\":245.27,\"count\":200132,\"high\":256.38,\"low\":244.65,\"open\":254.94,\"time\":\"20251010\",\"volume\":34365839,\"wap\":249.0},\n" +
                "{\"close\":247.66,\"count\":105768,\"high\":249.69,\"low\":245.56,\"open\":249.31,\"time\":\"20251013\",\"volume\":18746543,\"wap\":247.0},\n" +
                "{\"close\":247.77,\"count\":100507,\"high\":248.85,\"low\":244.7,\"open\":246.6,\"time\":\"20251014\",\"volume\":17603499,\"wap\":247.0},\n" +
                "{\"close\":249.34,\"count\":96331,\"high\":251.82,\"low\":247.47,\"open\":249.49,\"time\":\"20251015\",\"volume\":17213646,\"wap\":249.0},\n" +
                "{\"close\":247.45,\"count\":132811,\"high\":249.04,\"low\":245.13,\"open\":248.28,\"time\":\"20251016\",\"volume\":23517994,\"wap\":247.0},\n" +
                "{\"close\":252.29,\"count\":125831,\"high\":253.38,\"low\":247.27,\"open\":248.08,\"time\":\"20251017\",\"volume\":26060775,\"wap\":250.0},\n" +
                "{\"close\":262.24,\"count\":265957,\"high\":264.38,\"low\":255.63,\"open\":255.89,\"time\":\"20251020\",\"volume\":53856089,\"wap\":261.0},\n" +
                "{\"close\":262.77,\"count\":140549,\"high\":265.29,\"low\":261.83,\"open\":261.88,\"time\":\"20251021\",\"volume\":28223471,\"wap\":263.0},\n" +
                "{\"close\":258.45,\"count\":135366,\"high\":262.85,\"low\":255.43,\"open\":262.61,\"time\":\"20251022\",\"volume\":25366395,\"wap\":258.0},\n" +
                "{\"close\":259.58,\"count\":90076,\"high\":260.62,\"low\":258.01,\"open\":259.89,\"time\":\"20251023\",\"volume\":16292061,\"wap\":259.0},\n" +
                "{\"close\":262.82,\"count\":131207,\"high\":264.13,\"low\":259.18,\"open\":261.16,\"time\":\"20251024\",\"volume\":23085665,\"wap\":262.0},\n" +
                "{\"close\":268.81,\"count\":127754,\"high\":269.12,\"low\":264.65,\"open\":264.88,\"time\":\"20251027\",\"volume\":24139520,\"wap\":266.0},\n" +
                "{\"close\":269.0,\"count\":125626,\"high\":269.89,\"low\":268.15,\"open\":269.01,\"time\":\"20251028\",\"volume\":25261165,\"wap\":269.0},\n" +
                "{\"close\":269.7,\"count\":131019,\"high\":271.41,\"low\":267.11,\"open\":269.37,\"time\":\"20251029\",\"volume\":26967994,\"wap\":269.0},\n" +
                "{\"close\":271.4,\"count\":174263,\"high\":274.14,\"low\":268.48,\"open\":271.99,\"time\":\"20251030\",\"volume\":31595250,\"wap\":271.0},\n" +
                "{\"close\":270.37,\"count\":203583,\"high\":277.32,\"low\":269.16,\"open\":276.99,\"time\":\"20251031\",\"volume\":40570747,\"wap\":271.0},\n" +
                "{\"close\":269.05,\"count\":121417,\"high\":270.85,\"low\":266.25,\"open\":270.48,\"time\":\"20251103\",\"volume\":22813649,\"wap\":267.0},\n" +
                "{\"close\":270.04,\"count\":125544,\"high\":271.49,\"low\":267.61,\"open\":268.32,\"time\":\"20251104\",\"volume\":24536614,\"wap\":269.0},\n" +
                "{\"close\":270.14,\"count\":97149,\"high\":271.7,\"low\":266.93,\"open\":268.61,\"time\":\"20251105\",\"volume\":18353179,\"wap\":269.0},\n" +
                "{\"close\":269.77,\"count\":132515,\"high\":273.4,\"low\":267.89,\"open\":267.89,\"time\":\"20251106\",\"volume\":25440332,\"wap\":271.0},\n" +
                "{\"close\":268.47,\"count\":152184,\"high\":272.29,\"low\":266.77,\"open\":269.8,\"time\":\"20251107\",\"volume\":26176590,\"wap\":269.0},\n" +
                "{\"close\":269.43,\"count\":113142,\"high\":273.73,\"low\":267.45,\"open\":269.11,\"time\":\"20251110\",\"volume\":21519063,\"wap\":270.0},\n" +
                "{\"close\":275.25,\"count\":145611,\"high\":275.91,\"low\":269.8,\"open\":269.85,\"time\":\"20251111\",\"volume\":28103275,\"wap\":273.0},\n" +
                "{\"close\":273.47,\"count\":109353,\"high\":275.73,\"low\":271.7,\"open\":275.13,\"time\":\"20251112\",\"volume\":22192153,\"wap\":273.0},\n" +
                "{\"close\":272.95,\"count\":123551,\"high\":276.7,\"low\":272.09,\"open\":274.27,\"time\":\"20251113\",\"volume\":21873012,\"wap\":273.0},\n" +
                "{\"close\":272.41,\"count\":128504,\"high\":275.96,\"low\":269.6,\"open\":271.02,\"time\":\"20251114\",\"volume\":23279907,\"wap\":273.0},\n" +
                "{\"close\":267.46,\"count\":119357,\"high\":270.49,\"low\":265.73,\"open\":268.83,\"time\":\"20251117\",\"volume\":21314781,\"wap\":267.0},\n" +
                "{\"close\":267.44,\"count\":123349,\"high\":270.71,\"low\":265.32,\"open\":269.99,\"time\":\"20251118\",\"volume\":21656608,\"wap\":267.0},\n" +
                "{\"close\":268.56,\"count\":107132,\"high\":272.21,\"low\":265.5,\"open\":265.54,\"time\":\"20251119\",\"volume\":20306034,\"wap\":269.0},\n" +
                "{\"close\":266.25,\"count\":139801,\"high\":275.43,\"low\":265.92,\"open\":270.92,\"time\":\"20251120\",\"volume\":24720694,\"wap\":270.0},\n" +
                "{\"close\":271.49,\"count\":158671,\"high\":273.33,\"low\":265.67,\"open\":265.94,\"time\":\"20251121\",\"volume\":30498266,\"wap\":270.0},\n" +
                "{\"close\":275.92,\"count\":145496,\"high\":277.0,\"low\":270.9,\"open\":271.15,\"time\":\"20251124\",\"volume\":27717736,\"wap\":275.0},\n" +
                "{\"close\":276.97,\"count\":114415,\"high\":280.38,\"low\":275.25,\"open\":275.27,\"time\":\"20251125\",\"volume\":22238292,\"wap\":278.0},\n" +
                "{\"close\":277.55,\"count\":91263,\"high\":279.53,\"low\":276.63,\"open\":276.96,\"time\":\"20251126\",\"volume\":16595358,\"wap\":278.0},\n" +
                "{\"close\":278.85,\"count\":50506,\"high\":279.0,\"low\":275.98,\"open\":277.37,\"time\":\"20251128\",\"volume\":9498788,\"wap\":277.0},\n" +
                "{\"close\":283.1,\"count\":114490,\"high\":283.42,\"low\":276.14,\"open\":278.1,\"time\":\"20251201\",\"volume\":23632321,\"wap\":280.0},\n" +
                "{\"close\":286.19,\"count\":127538,\"high\":287.4,\"low\":282.63,\"open\":282.99,\"time\":\"20251202\",\"volume\":26066733,\"wap\":285.0},\n" +
                "{\"close\":284.15,\"count\":115480,\"high\":288.62,\"low\":283.3,\"open\":286.2,\"time\":\"20251203\",\"volume\":22586809,\"wap\":286.0},\n" +
                "{\"close\":280.7,\"count\":122593,\"high\":284.73,\"low\":278.59,\"open\":284.1,\"time\":\"20251204\",\"volume\":22432307,\"wap\":280.0},\n" +
                "{\"close\":278.78,\"count\":93254,\"high\":281.14,\"low\":278.05,\"open\":280.54,\"time\":\"20251205\",\"volume\":17290679,\"wap\":279.0},\n" +
                "{\"close\":277.89,\"count\":106053,\"high\":279.67,\"low\":276.15,\"open\":278.11,\"time\":\"20251208\",\"volume\":18790681,\"wap\":277.0},\n" +
                "{\"close\":277.18,\"count\":84251,\"high\":280.03,\"low\":276.92,\"open\":278.16,\"time\":\"20251209\",\"volume\":16879302,\"wap\":278.0},\n" +
                "{\"close\":278.78,\"count\":81159,\"high\":279.75,\"low\":276.44,\"open\":277.84,\"time\":\"20251210\",\"volume\":15335546,\"wap\":278.0},\n" +
                "{\"close\":278.03,\"count\":92719,\"high\":279.59,\"low\":273.81,\"open\":279.1,\"time\":\"20251211\",\"volume\":17462097,\"wap\":276.0},\n" +
                "{\"close\":278.28,\"count\":107133,\"high\":279.22,\"low\":276.82,\"open\":277.91,\"time\":\"20251212\",\"volume\":18860844,\"wap\":278.0},\n" +
                "{\"close\":274.11,\"count\":140106,\"high\":280.17,\"low\":272.84,\"open\":280.17,\"time\":\"20251215\",\"volume\":24801092,\"wap\":274.0},\n" +
                "{\"close\":274.61,\"count\":97695,\"high\":275.5,\"low\":271.79,\"open\":272.7,\"time\":\"20251216\",\"volume\":17191726,\"wap\":273.0},\n" +
                "{\"close\":271.84,\"count\":124748,\"high\":276.16,\"low\":271.64,\"open\":275.0,\"time\":\"20251217\",\"volume\":20970018,\"wap\":273.0},\n" +
                "{\"close\":272.19,\"count\":143770,\"high\":273.63,\"low\":266.95,\"open\":273.61,\"time\":\"20251218\",\"volume\":25722115,\"wap\":270.0},\n" +
                "{\"close\":273.67,\"count\":158836,\"high\":274.6,\"low\":269.9,\"open\":272.39,\"time\":\"20251219\",\"volume\":39565798,\"wap\":271.0},\n" +
                "{\"close\":270.97,\"count\":104858,\"high\":273.88,\"low\":270.5,\"open\":272.88,\"time\":\"20251222\",\"volume\":20307738,\"wap\":271.0},\n" +
                "{\"close\":272.36,\"count\":78056,\"high\":272.5,\"low\":269.56,\"open\":270.84,\"time\":\"20251223\",\"volume\":16122549,\"wap\":271.0},\n" +
                "{\"close\":273.81,\"count\":48577,\"high\":275.43,\"low\":272.19,\"open\":272.34,\"time\":\"20251224\",\"volume\":10300242,\"wap\":274.0},\n" +
                "{\"close\":273.4,\"count\":60440,\"high\":275.37,\"low\":272.86,\"open\":274.16,\"time\":\"20251226\",\"volume\":12347456,\"wap\":274.0},\n" +
                "{\"close\":273.76,\"count\":58005,\"high\":274.36,\"low\":272.35,\"open\":272.7,\"time\":\"20251229\",\"volume\":10098230,\"wap\":273.0},\n" +
                "{\"close\":273.08,\"count\":65743,\"high\":274.08,\"low\":272.28,\"open\":272.81,\"time\":\"20251230\",\"volume\":11842308,\"wap\":273.0},\n" +
                "{\"close\":271.86,\"count\":63817,\"high\":273.68,\"low\":271.75,\"open\":273.06,\"time\":\"20251231\",\"volume\":11592379,\"wap\":272.0},\n" +
                "{\"close\":271.01,\"count\":117830,\"high\":277.84,\"low\":269.0,\"open\":272.25,\"time\":\"20260102\",\"volume\":21545028,\"wap\":272.0},\n" +
                "{\"close\":267.26,\"count\":129628,\"high\":271.51,\"low\":266.14,\"open\":270.72,\"time\":\"20260105\",\"volume\":23358039,\"wap\":268.0},\n" +
                "{\"close\":262.36,\"count\":149580,\"high\":267.54,\"low\":262.12,\"open\":267.0,\"time\":\"20260106\",\"volume\":27778809,\"wap\":263.0},\n" +
                "{\"close\":260.33,\"count\":133881,\"high\":263.68,\"low\":259.81,\"open\":263.2,\"time\":\"20260107\",\"volume\":25861770,\"wap\":261.0},\n" +
                "{\"close\":259.04,\"count\":155894,\"high\":259.29,\"low\":255.7,\"open\":257.06,\"time\":\"20260108\",\"volume\":29663776,\"wap\":257.0},\n" +
                "{\"close\":259.37,\"count\":118906,\"high\":260.21,\"low\":256.22,\"open\":259.07,\"time\":\"20260109\",\"volume\":22576594,\"wap\":258.0},\n" +
                "{\"close\":260.25,\"count\":122925,\"high\":261.3,\"low\":256.8,\"open\":259.24,\"time\":\"20260112\",\"volume\":23247409,\"wap\":259.0},\n" +
                "{\"close\":261.05,\"count\":111827,\"high\":261.81,\"low\":258.39,\"open\":258.72,\"time\":\"20260113\",\"volume\":20169911,\"wap\":260.0},\n" +
                "{\"close\":259.96,\"count\":113250,\"high\":261.82,\"low\":256.71,\"open\":259.49,\"time\":\"20260114\",\"volume\":19717808,\"wap\":258.0},\n" +
                "{\"close\":258.21,\"count\":110338,\"high\":261.04,\"low\":257.05,\"open\":260.72,\"time\":\"20260115\",\"volume\":20139542,\"wap\":259.0},\n" +
                "{\"close\":255.53,\"count\":131511,\"high\":258.9,\"low\":255.0,\"open\":258.05,\"time\":\"20260116\",\"volume\":28013707,\"wap\":256.0},\n" +
                "{\"close\":246.7,\"count\":200708,\"high\":254.79,\"low\":243.42,\"open\":252.73,\"time\":\"20260120\",\"volume\":37455825,\"wap\":248.0},\n" +
                "{\"close\":247.65,\"count\":173902,\"high\":251.56,\"low\":245.18,\"open\":248.7,\"time\":\"20260121\",\"volume\":31968731,\"wap\":247.0},\n" +
                "{\"close\":248.35,\"count\":122708,\"high\":251.0,\"low\":248.15,\"open\":249.2,\"time\":\"20260122\",\"volume\":22345506,\"wap\":249.0},\n" +
                "{\"close\":248.04,\"count\":121169,\"high\":249.41,\"low\":244.68,\"open\":247.36,\"time\":\"20260123\",\"volume\":22445439,\"wap\":247.0},\n" +
                "{\"close\":255.41,\"count\":158290,\"high\":256.56,\"low\":249.8,\"open\":251.5,\"time\":\"20260126\",\"volume\":28930104,\"wap\":254.0},\n" +
                "{\"close\":258.27,\"count\":143557,\"high\":261.95,\"low\":258.21,\"open\":259.14,\"time\":\"20260127\",\"volume\":26251224,\"wap\":260.0},\n" +
                "{\"close\":256.44,\"count\":102984,\"high\":258.86,\"low\":254.51,\"open\":257.53,\"time\":\"20260128\",\"volume\":19286782,\"wap\":256.0},\n" +
                "{\"close\":258.28,\"count\":173454,\"high\":259.65,\"low\":254.41,\"open\":258.0,\"time\":\"20260129\",\"volume\":30444882,\"wap\":257.0},\n" +
                "{\"close\":259.48,\"count\":248489,\"high\":261.9,\"low\":252.18,\"open\":255.17,\"time\":\"20260130\",\"volume\":47110619,\"wap\":256.0},\n" +
                "{\"close\":270.01,\"count\":194037,\"high\":270.49,\"low\":259.2,\"open\":260.02,\"time\":\"20260202\",\"volume\":39664024,\"wap\":265.0},\n" +
                "{\"close\":269.48,\"count\":167586,\"high\":271.88,\"low\":267.61,\"open\":269.2,\"time\":\"20260203\",\"volume\":33472513,\"wap\":269.0},\n" +
                "{\"close\":276.49,\"count\":264640,\"high\":278.95,\"low\":272.28,\"open\":272.29,\"time\":\"20260204\",\"volume\":48895823,\"wap\":276.0},\n" +
                "{\"close\":275.91,\"count\":153373,\"high\":279.5,\"low\":273.23,\"open\":278.13,\"time\":\"20260205\",\"volume\":28324221,\"wap\":275.0},\n" +
                "{\"close\":278.12,\"count\":161279,\"high\":280.91,\"low\":276.92,\"open\":277.18,\"time\":\"20260206\",\"volume\":28792933,\"wap\":278.0},\n" +
                "{\"close\":274.62,\"count\":117593,\"high\":278.2,\"low\":271.7,\"open\":277.87,\"time\":\"20260209\",\"volume\":22652748,\"wap\":273.0},\n" +
                "{\"close\":273.68,\"count\":80975,\"high\":275.37,\"low\":272.94,\"open\":274.88,\"time\":\"20260210\",\"volume\":14559046,\"wap\":274.0},\n" +
                "{\"close\":275.5,\"count\":143618,\"high\":280.18,\"low\":274.45,\"open\":274.68,\"time\":\"20260211\",\"volume\":27057565,\"wap\":277.0},\n" +
                "{\"close\":261.73,\"count\":237772,\"high\":275.72,\"low\":260.18,\"open\":275.59,\"time\":\"20260212\",\"volume\":40669107,\"wap\":265.0},\n" +
                "{\"close\":255.78,\"count\":142590,\"high\":262.23,\"low\":255.45,\"open\":261.95,\"time\":\"20260213\",\"volume\":28183236,\"wap\":258.0},\n" +
                "{\"close\":263.88,\"count\":142088,\"high\":266.29,\"low\":255.54,\"open\":257.9,\"time\":\"20260217\",\"volume\":28886840,\"wap\":261.0},\n" +
                "{\"close\":264.35,\"count\":96518,\"high\":266.82,\"low\":262.45,\"open\":263.53,\"time\":\"20260218\",\"volume\":17166924,\"wap\":264.0},\n" +
                "{\"close\":260.58,\"count\":80510,\"high\":264.48,\"low\":260.05,\"open\":262.51,\"time\":\"20260219\",\"volume\":14766272,\"wap\":262.0},\n" +
                "{\"close\":264.58,\"count\":104221,\"high\":264.75,\"low\":258.16,\"open\":258.97,\"time\":\"20260220\",\"volume\":20704836,\"wap\":262.0},\n" +
                "{\"close\":266.18,\"count\":107136,\"high\":269.43,\"low\":263.38,\"open\":263.49,\"time\":\"20260223\",\"volume\":20278380,\"wap\":266.0},\n" +
                "{\"close\":272.14,\"count\":126337,\"high\":274.89,\"low\":267.71,\"open\":267.99,\"time\":\"20260224\",\"volume\":23327362,\"wap\":272.0},\n" +
                "{\"close\":274.23,\"count\":83903,\"high\":274.94,\"low\":271.05,\"open\":271.73,\"time\":\"20260225\",\"volume\":15560864,\"wap\":273.0},\n" +
                "{\"close\":272.95,\"count\":86877,\"high\":276.11,\"low\":270.79,\"open\":274.88,\"time\":\"20260226\",\"volume\":15230594,\"wap\":272.0},\n" +
                "{\"close\":264.18,\"count\":144631,\"high\":272.81,\"low\":262.89,\"open\":272.77,\"time\":\"20260227\",\"volume\":26248240,\"wap\":266.0},\n" +
                "{\"close\":264.72,\"count\":100768,\"high\":266.53,\"low\":260.2,\"open\":262.46,\"time\":\"20260302\",\"volume\":18290674,\"wap\":264.0},\n" +
                "{\"close\":263.75,\"count\":99813,\"high\":265.56,\"low\":260.13,\"open\":263.48,\"time\":\"20260303\",\"volume\":18325416,\"wap\":262.0},\n" +
                "{\"close\":262.52,\"count\":106152,\"high\":266.15,\"low\":261.42,\"open\":264.7,\"time\":\"20260304\",\"volume\":18489944,\"wap\":263.0},\n" +
                "{\"close\":260.29,\"count\":142367,\"high\":261.56,\"low\":257.25,\"open\":260.79,\"time\":\"20260305\",\"volume\":24631414,\"wap\":259.0},\n" +
                "{\"close\":257.46,\"count\":99098,\"high\":258.77,\"low\":254.37,\"open\":258.63,\"time\":\"20260306\",\"volume\":16911153,\"wap\":256.0},\n" +
                "{\"close\":259.88,\"count\":100640,\"high\":261.15,\"low\":253.68,\"open\":255.6,\"time\":\"20260309\",\"volume\":18184074,\"wap\":257.0},\n" +
                "{\"close\":260.83,\"count\":86685,\"high\":262.48,\"low\":256.95,\"open\":257.65,\"time\":\"20260310\",\"volume\":14601251,\"wap\":260.0},\n" +
                "{\"close\":260.81,\"count\":62620,\"high\":262.13,\"low\":259.55,\"open\":261.11,\"time\":\"20260311\",\"volume\":11477086,\"wap\":260.0},\n" +
                "{\"close\":255.76,\"count\":106770,\"high\":258.95,\"low\":254.18,\"open\":258.66,\"time\":\"20260312\",\"volume\":19123215,\"wap\":255.0},\n" +
                "{\"close\":250.12,\"count\":104828,\"high\":256.33,\"low\":249.52,\"open\":255.4,\"time\":\"20260313\",\"volume\":19978936,\"wap\":252.0},\n" +
                "{\"close\":252.82,\"count\":91186,\"high\":253.89,\"low\":249.93,\"open\":252.1,\"time\":\"20260316\",\"volume\":16554954,\"wap\":252.0},\n" +
                "{\"close\":254.23,\"count\":67528,\"high\":255.13,\"low\":252.18,\"open\":253.04,\"time\":\"20260317\",\"volume\":12184946,\"wap\":254.0},\n" +
                "{\"close\":249.94,\"count\":84085,\"high\":254.94,\"low\":249.0,\"open\":252.72,\"time\":\"20260318\",\"volume\":14906088,\"wap\":251.0},\n" +
                "{\"close\":248.96,\"count\":100840,\"high\":251.83,\"low\":247.3,\"open\":249.4,\"time\":\"20260319\",\"volume\":17288592,\"wap\":249.0},\n" +
                "{\"close\":247.99,\"count\":107182,\"high\":249.2,\"low\":246.0,\"open\":247.64,\"time\":\"20260320\",\"volume\":25505272,\"wap\":248.0},\n" +
                "{\"close\":251.49,\"count\":103925,\"high\":254.53,\"low\":250.28,\"open\":253.98,\"time\":\"20260323\",\"volume\":18411193,\"wap\":252.0},\n" +
                "{\"close\":251.64,\"count\":86608,\"high\":254.83,\"low\":249.55,\"open\":250.35,\"time\":\"20260324\",\"volume\":14888597,\"wap\":252.0},\n" +
                "{\"close\":252.62,\"count\":74227,\"high\":255.0,\"low\":251.6,\"open\":254.09,\"time\":\"20260325\",\"volume\":12867706,\"wap\":253.0},\n" +
                "{\"close\":252.89,\"count\":123824,\"high\":257.0,\"low\":250.77,\"open\":252.22,\"time\":\"20260326\",\"volume\":21973089,\"wap\":254.0},\n" +
                "{\"close\":248.8,\"count\":136984,\"high\":255.5,\"low\":248.07,\"open\":253.9,\"time\":\"20260327\",\"volume\":24399928,\"wap\":251.0},\n" +
                "{\"close\":246.63,\"count\":96091,\"high\":250.82,\"low\":245.51,\"open\":250.05,\"time\":\"20260330\",\"volume\":17092725,\"wap\":246.0},\n" +
                "{\"close\":253.79,\"count\":119893,\"high\":255.48,\"low\":247.1,\"open\":247.89,\"time\":\"20260331\",\"volume\":21625512,\"wap\":251.0},\n" +
                "{\"close\":255.63,\"count\":95112,\"high\":256.17,\"low\":253.33,\"open\":253.9,\"time\":\"20260401\",\"volume\":18017733,\"wap\":254.0},\n" +
                "{\"close\":255.92,\"count\":74132,\"high\":256.13,\"low\":250.65,\"open\":254.2,\"time\":\"20260402\",\"volume\":13374260,\"wap\":254.0},\n" +
                "{\"close\":258.86,\"count\":74052,\"high\":262.16,\"low\":256.46,\"open\":256.51,\"time\":\"20260406\",\"volume\":15381581,\"wap\":259.0},\n" +
                "{\"close\":253.5,\"count\":181719,\"high\":256.2,\"low\":245.7,\"open\":256.07,\"time\":\"20260407\",\"volume\":37264538,\"wap\":250.0},\n" +
                "{\"close\":258.9,\"count\":107418,\"high\":259.75,\"low\":256.53,\"open\":258.4,\"time\":\"20260408\",\"volume\":20091146,\"wap\":258.0},\n" +
                "{\"close\":260.49,\"count\":69012,\"high\":261.12,\"low\":256.07,\"open\":259.0,\"time\":\"20260409\",\"volume\":13580639,\"wap\":258.0},\n" +
                "{\"close\":260.48,\"count\":80621,\"high\":262.19,\"low\":259.02,\"open\":259.96,\"time\":\"20260410\",\"volume\":14391530,\"wap\":260.0},\n" +
                "{\"close\":259.2,\"count\":93757,\"high\":260.18,\"low\":256.66,\"open\":259.6,\"time\":\"20260413\",\"volume\":17910105,\"wap\":257.0},\n" +
                "{\"close\":258.83,\"count\":113113,\"high\":261.93,\"low\":257.19,\"open\":259.11,\"time\":\"20260414\",\"volume\":21882872,\"wap\":258.0},\n" +
                "{\"close\":266.43,\"count\":136193,\"high\":266.56,\"low\":257.81,\"open\":258.04,\"time\":\"20260415\",\"volume\":26570991,\"wap\":263.0},\n" +
                "{\"close\":263.4,\"count\":111762,\"high\":267.16,\"low\":261.27,\"open\":266.8,\"time\":\"20260416\",\"volume\":23244766,\"wap\":263.0},\n" +
                "{\"close\":270.23,\"count\":127461,\"high\":272.3,\"low\":266.72,\"open\":267.07,\"time\":\"20260417\",\"volume\":27891246,\"wap\":269.0},\n" +
                "{\"close\":273.05,\"count\":97346,\"high\":274.28,\"low\":270.33,\"open\":270.33,\"time\":\"20260420\",\"volume\":19285289,\"wap\":272.0},\n" +
                "{\"close\":266.17,\"count\":137714,\"high\":272.8,\"low\":265.4,\"open\":271.59,\"time\":\"20260421\",\"volume\":28057266,\"wap\":267.0},\n" +
                "{\"close\":273.17,\"count\":119473,\"high\":273.74,\"low\":266.87,\"open\":267.74,\"time\":\"20260422\",\"volume\":23855766,\"wap\":271.0},\n" +
                "{\"close\":273.43,\"count\":80745,\"high\":275.77,\"low\":271.65,\"open\":274.9,\"time\":\"20260423\",\"volume\":15589414,\"wap\":273.0},\n" +
                "{\"close\":271.06,\"count\":96522,\"high\":273.06,\"low\":269.65,\"open\":272.79,\"time\":\"20260424\",\"volume\":18287115,\"wap\":270.0},\n" +
                "{\"close\":267.61,\"count\":99374,\"high\":268.36,\"low\":265.07,\"open\":266.09,\"time\":\"20260427\",\"volume\":19009160,\"wap\":266.0},\n" +
                "{\"close\":270.71,\"count\":89807,\"high\":273.2,\"low\":268.66,\"open\":272.34,\"time\":\"20260428\",\"volume\":17555587,\"wap\":270.0},\n" +
                "{\"close\":270.17,\"count\":69007,\"high\":271.04,\"low\":267.04,\"open\":267.59,\"time\":\"20260429\",\"volume\":13324507,\"wap\":269.0},\n" +
                "{\"close\":271.35,\"count\":163820,\"high\":276.0,\"low\":268.14,\"open\":270.52,\"time\":\"20260430\",\"volume\":31354599,\"wap\":272.0},\n" +
                "{\"close\":280.14,\"count\":443909,\"high\":287.22,\"low\":278.37,\"open\":278.86,\"time\":\"20260501\",\"volume\":54315309,\"wap\":282.0},\n" +
                "{\"close\":276.83,\"count\":222645,\"high\":280.63,\"low\":274.86,\"open\":279.68,\"time\":\"20260504\",\"volume\":27069575,\"wap\":276.0},\n" +
                "{\"close\":284.18,\"count\":255948,\"high\":284.57,\"low\":276.5,\"open\":276.92,\"time\":\"20260505\",\"volume\":30206074,\"wap\":281.0},\n" +
                "{\"close\":287.51,\"count\":271564,\"high\":288.0,\"low\":281.07,\"open\":281.92,\"time\":\"20260506\",\"volume\":30218533,\"wap\":285.0},\n" +
                "{\"close\":287.44,\"count\":232753,\"high\":292.13,\"low\":285.78,\"open\":289.27,\"time\":\"20260507\",\"volume\":27359057,\"wap\":289.0},\n" +
                "{\"close\":293.32,\"count\":254384,\"high\":294.76,\"low\":290.0,\"open\":290.01,\"time\":\"20260508\",\"volume\":30940312,\"wap\":293.0},\n" +
                "{\"close\":292.68,\"count\":189186,\"high\":293.88,\"low\":290.23,\"open\":291.98,\"time\":\"20260511\",\"volume\":23822531,\"wap\":292.0},\n" +
                "{\"close\":294.8,\"count\":204607,\"high\":295.27,\"low\":292.51,\"open\":292.51,\"time\":\"20260512\",\"volume\":22453046,\"wap\":294.0},\n" +
                "{\"close\":298.87,\"count\":243841,\"high\":300.92,\"low\":293.41,\"open\":293.41,\"time\":\"20260513\",\"volume\":28880850,\"wap\":298.0},\n" +
                "{\"close\":298.21,\"count\":186847,\"high\":300.45,\"low\":295.38,\"open\":299.82,\"time\":\"20260514\",\"volume\":20828291,\"wap\":298.0},\n" +
                "{\"close\":300.23,\"count\":232839,\"high\":303.2,\"low\":296.52,\"open\":297.94,\"time\":\"20260515\",\"volume\":30111709,\"wap\":300.0},\n" +
                "{\"close\":297.84,\"count\":186925,\"high\":300.65,\"low\":294.91,\"open\":300.24,\"time\":\"20260518\",\"volume\":19538418,\"wap\":297.0},\n" +
                "{\"close\":298.97,\"count\":191813,\"high\":300.51,\"low\":296.35,\"open\":296.97,\"time\":\"20260519\",\"volume\":19681004,\"wap\":298.0},\n" +
                "{\"close\":302.25,\"count\":210179,\"high\":302.8,\"low\":298.08,\"open\":298.18,\"time\":\"20260520\",\"volume\":24182059,\"wap\":300.0},\n" +
                "{\"close\":304.99,\"count\":195111,\"high\":305.54,\"low\":300.4,\"open\":301.03,\"time\":\"20260521\",\"volume\":21155576,\"wap\":303.0},\n" +
                "{\"close\":308.82,\"count\":245556,\"high\":311.4,\"low\":305.84,\"open\":306.06,\"time\":\"20260522\",\"volume\":24955666,\"wap\":309.0},\n" +
                "{\"close\":308.33,\"count\":230044,\"high\":311.82,\"low\":307.67,\"open\":309.61,\"time\":\"20260526\",\"volume\":24539756,\"wap\":310.0},\n" +
                "{\"close\":310.85,\"count\":248376,\"high\":313.26,\"low\":308.3,\"open\":308.36,\"time\":\"20260527\",\"volume\":25284160,\"wap\":311.0},\n" +
                "{\"close\":312.51,\"count\":234840,\"high\":312.8,\"low\":309.57,\"open\":310.62,\"time\":\"20260528\",\"volume\":25285936,\"wap\":311.0},\n" +
                "{\"close\":312.06,\"count\":266321,\"high\":315.0,\"low\":309.53,\"open\":311.78,\"time\":\"20260529\",\"volume\":25934353,\"wap\":311.0},\n" +
                "{\"close\":306.31,\"count\":281859,\"high\":310.94,\"low\":305.02,\"open\":309.37,\"time\":\"20260601\",\"volume\":29277168,\"wap\":307.0},\n" +
                "{\"close\":315.2,\"count\":283018,\"high\":315.45,\"low\":306.68,\"open\":307.37,\"time\":\"20260602\",\"volume\":27962454,\"wap\":312.0},\n" +
                "{\"close\":310.26,\"count\":290977,\"high\":316.94,\"low\":308.85,\"open\":314.18,\"time\":\"20260603\",\"volume\":29682864,\"wap\":311.0},\n" +
                "{\"close\":311.23,\"count\":239184,\"high\":313.54,\"low\":309.65,\"open\":313.16,\"time\":\"20260604\",\"volume\":21852834,\"wap\":311.0},\n" +
                "{\"close\":307.34,\"count\":376485,\"high\":315.17,\"low\":307.15,\"open\":312.84,\"time\":\"20260605\",\"volume\":38320425,\"wap\":311.0},\n" +
                "{\"close\":301.54,\"count\":510676,\"high\":317.4,\"low\":301.17,\"open\":308.74,\"time\":\"20260608\",\"volume\":54222405,\"wap\":309.0},\n" +
                "{\"close\":290.55,\"count\":457710,\"high\":300.75,\"low\":287.78,\"open\":300.24,\"time\":\"20260609\",\"volume\":46893234,\"wap\":292.0},\n" +
                "{\"close\":291.58,\"count\":351077,\"high\":294.75,\"low\":287.38,\"open\":290.77,\"time\":\"20260610\",\"volume\":32454284,\"wap\":291.0}" +
                "]";



        // 1. 解析并排序K线
        List<BarData> barList = parseAndSortBars(twsJson);
        // 2. 提取收盘价
        double[] prices = extractClosePrices(barList);
        // 3. 计算对数收益率
        double[] logReturns = calcLogReturn(prices);

        // 持仓市值 示例：1000000 美元
        double positionValue = 1_000_000;

        calculateSingleStockVar("AAPL(苹果)", twsJson, positionValue);
    }

    // ---------- 历史模拟 CVaR ----------
// alpha=0.05 对应 95% 置信
    public static double calcHistoryCVaR(double[] returns, double marketValue, double alpha) {
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
        return Math.abs(cvarRet * marketValue);
    }

    // ---------- 参数法 CVaR（正态分布假设） ----------
// 公式：CVaR = -(μ + φ(z)/α * σ) * MV
// φ 是标准正态密度，z 是分位数（如 Z_95=-1.645）
    public static double calcParamCVaR(double[] returns, double marketValue, double zQuantile, double alpha) {
        if (returns.length == 0) return 0.0;

        double mu = mean(returns);
        double sigma = stdDev(returns);

        // 标准正态密度 φ(z)
        double phi = Math.exp(-0.5 * zQuantile * zQuantile) / Math.sqrt(2 * Math.PI);

        double cvarRet = -(mu + phi / alpha * sigma);
        return cvarRet * marketValue;
    }

    // ---------- 蒙特卡洛 CVaR ----------
    public static double calcMonteCarloCVaR(double[] returns, double marketValue, double alpha) {
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
        return Math.abs(cvarRet * marketValue);
    }
}
