package com.riskcontrol.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String localDateToString(LocalDate date, String formatter){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(formatter);
        return date.format(fmt);
    }

    public static LocalDate stringToLocalDate(String date, String formatter){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(formatter);
        return LocalDate.parse(date, fmt);
    }

    public static String localDateToString(LocalDate date){
        return localDateToString(date, "yyyy-MM-dd");
    }

    public static String localDateTimeToString(LocalDateTime date, String formatter){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(formatter);
        return date.format(fmt);
    }

    public static String localDateTimeToString(LocalDateTime date){
        return localDateTimeToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    private static final DateTimeFormatter IBKR_UTC_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");

    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 当天最后一刻 UTC（23:59:59）
     */
    public static String toIbkrUtcEndTime(LocalDate localDate) {
        ZonedDateTime utcTime = ZonedDateTime.of(localDate, LocalTime.MAX, ZoneId.of("UTC"));
        return utcTime.format(IBKR_UTC_FORMAT);
    }
}
