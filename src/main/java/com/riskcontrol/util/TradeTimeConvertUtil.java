package com.riskcontrol.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TradeTimeConvertUtil {

    // 输入解析格式：yyyyMMdd HH:mm:ss
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
    // 日期时间部分输出格式（不带时区后缀）
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
    // 目标时区固定：美东 America/New_York
    private static final ZoneId TARGET_ZONE = ZoneId.of("America/New_York");
    // 固定后缀 US/Eastern
    private static final String TARGET_ZONE_ALIAS = "US/Eastern";

    /**
     * 多时区转美东，输出格式：yyyyMMdd HH:mm:ss US/Eastern
     * @param timeStr 原始时间 例：20260701 15:21:08
     * @param sourceZoneId 源时区ID：Europe/Berlin / America/Chicago / Asia/Shanghai
     * @return 如 20260630 10:26:57 US/Eastern
     */
    public static String convertToUsEasternStr(String timeStr, String sourceZoneId) {
        // 1. 解析源时间
        LocalDateTime localDt = LocalDateTime.parse(timeStr, INPUT_FMT);
        ZonedDateTime sourceZdt = ZonedDateTime.of(localDt, ZoneId.of(sourceZoneId));
        // 2. 转换为美东瞬时时间
        ZonedDateTime etZdt = sourceZdt.withZoneSameInstant(TARGET_ZONE);
        // 3. 拼接固定格式 + 固定后缀 US/Eastern
        String dateTimePart = etZdt.format(DATE_TIME_FMT);
        return dateTimePart + " " + TARGET_ZONE_ALIAS;
    }

    // 重载：返回ZonedDateTime用于业务判断交易时段
    public static ZonedDateTime convertToUsEasternZdt(String timeStr, String sourceZoneId) {
        LocalDateTime localDt = LocalDateTime.parse(timeStr, INPUT_FMT);
        ZonedDateTime sourceZdt = ZonedDateTime.of(localDt, ZoneId.of(sourceZoneId));
        return sourceZdt.withZoneSameInstant(TARGET_ZONE);
    }

    public static String convertToUsEasternStr(String dateTimeStr){
        String part1 = dateTimeStr.substring(0, 17);
        String part2 = dateTimeStr.substring(18);

        return convertToUsEasternStr(part1, part2);
    }

    // 测试入口
    public static void main(String[] args) {
        // 案例1：欧洲MET时间
        String euTime = "20260701 15:21:08";
        System.out.println("欧洲MET转美东：" + convertToUsEasternStr(euTime, "Europe/Berlin"));

        // 案例2：美中US/Central
        String ctTime = "20260701 08:21:08";
        System.out.println("美中CT转美东：" + convertToUsEasternStr(ctTime, "America/Chicago"));

        // 案例3：北京时间
        String shTime = "20260703 09:48:56";
        System.out.println("北京时间转美东：" + convertToUsEasternStr(shTime, "Asia/Shanghai"));

        String a = "20260702 22:47:45 Asia/Shanghai";

        System.out.println(convertToUsEasternStr(a));

        String b = "20260701 15:21:08 MET";
        System.out.println(convertToUsEasternStr(b));
    }
}
