package com.riskcontrol.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OccOptionUtil {

    // OCC符号示例：AMD260724C00525000
    private static final DateTimeFormatter OCC_DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter SHORT_EXP_FMT = DateTimeFormatter.ofPattern("ddMMMyy", Locale.ENGLISH);

    /**
     * OCC正式代码 -> 简写名称：AMD 24JUL26 525 C
     * @param occSymbol OCC完整期权代码，不带空格，如 AMD260724C00525000
     * @return 可读简写名称
     */
    public static String occToShortName(String occSymbol) {
        if (occSymbol == null || occSymbol.length() < 15) {
            throw new IllegalArgumentException("非法OCC期权代码");
        }

        // 从尾部截取固定长度字段：最后9位 = [C/P] + 8位行权价
        int suffixLen = 9;
        String suffix = occSymbol.substring(occSymbol.length() - suffixLen);
        String mainPart = occSymbol.substring(0, occSymbol.length() - suffixLen);

        char right = suffix.charAt(0); // C / P
        String strikeCode = suffix.substring(1); // 8位行权价串 00525000

        // mainPart最后6位：YYMMDD
        String yymmdd = mainPart.substring(mainPart.length() - 6).trim();
        String symbol = mainPart.substring(0, mainPart.length() - 6).trim();

        // 解析到期日
        LocalDate expiry = LocalDate.parse(yymmdd, OCC_DATE_FMT);
        String expStr = expiry.format(SHORT_EXP_FMT).toUpperCase();

        // 解析行权价
        double strike = parseStrikeFromOccCode(strikeCode);

        // 格式化输出
        if (strike == Math.floor(strike)) {
            // 整数行权价，不显示小数
            return String.format("%s %s %.0f %c", symbol, expStr, strike, right);
        } else {
            return String.format("%s %s %.3f %c", symbol, expStr, strike, right);
        }
    }

    /**
     * 8位行权字符串转价格
     * 规则：数值 / 1000
     */
    private static double parseStrikeFromOccCode(String strikeCode) {
        long num = Long.parseLong(strikeCode);
        return num / 1000.0;
    }

    // ========== 反向可选：简写名称生成OCC代码（附带）==========
    /**
     * 简写名称 -> OCC代码
     * @param symbol AMD
     * @param expiryDate yyyyMMdd 例如 20260724
     * @param right C/P
     * @param strike 行权价
     * @return AMD260724C00525000
     */
    public static String buildOccSymbol(String symbol, String expiryDate, char right, double strike) {
        LocalDate date = LocalDate.parse(expiryDate, DateTimeFormatter.BASIC_ISO_DATE);
        String yyMmDd = date.format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 行权价转为8位字符串 *1000，补前导零
        long strikeRaw = Math.round(strike * 1000);
        String strikeStr = String.format("%08d", strikeRaw);

        return symbol + yyMmDd + right + strikeStr;
    }


    public static String futShortName(String symbol, String lastTradeDate){
        DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(lastTradeDate, inFormatter);
        DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern("ddMMMyy");

        return symbol + " " + date.format(outFormatter).toUpperCase();
    }

    public static void main(String[] args) {
        // 测试
        String occ1 = "ASML  260724P01800000";
        System.out.println(occToShortName(occ1));
        // 输出：AMD 24JUL26 525 C

        String occ2 = "AMD260724C00525500";
        System.out.println(occToShortName(occ2));
        // 输出：AMD 24JUL26 525.500 C

        // 反向测试
        String occGen = buildOccSymbol("AMD", "20260724", 'C', 525.0);
        System.out.println(occGen);
        // AMD260724C00525000
    }
}
