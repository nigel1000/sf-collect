package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

public class DateUtil {

    private DateUtil() {
    }

    public static Date now() {
        return new Date();
    }

    // 字符串与date的转换
    // date   2018-11-12 11:20:21
    // format yyyy-MM-dd HH:mm:ss
    public static Date parseDate(@NonNull String date, @NonNull String format) {
        try {
            return toDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format)));
        } catch (Exception ex) {
            return toDate(LocalDate.parse(date, DateTimeFormatter.ofPattern(format)));
        }
    }

    //yyyy-MM-dd
    //yyyy-MM-dd HH:mm:ss
    //yyyy-MM-dd HH:mm:ss.SSS
    //yyyy年MM月dd日
    public static String format(@NonNull Date date, @NonNull String format) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return DateTimeFormatter.ofPattern(format).format(localDateTime);
    }

    // 获取时间信息
    // 2018-01-31T00:00
    public static Date getStartOfDay(@NonNull Date date) {
        LocalDate localDate = toLocalDate(date);
        return toDate(localDate.atStartOfDay());
    }

    // 2018-01-31T23:59:59.999999999
    public static Date getEndOfDay(@NonNull Date date) {
        LocalDate localDate = toLocalDate(date);
        return toDate(LocalDateTime.of(localDate, LocalTime.MAX));
    }

    public static int getYear(@NonNull Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return localDateTime.getYear();
    }

    public static int getMonthValue(@NonNull Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return localDateTime.getMonthValue();
    }

    public static int getDayOfMonth(@NonNull Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return localDateTime.getDayOfMonth();
    }

    public static int getDayOfYear(@NonNull Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return localDateTime.getDayOfYear();
    }

    public static String getDayOfWeek(@NonNull Date date) {
        switch (toLocalDate(date).getDayOfWeek()) {
            case MONDAY:
                return "一";
            case TUESDAY:
                return "二";
            case WEDNESDAY:
                return "三";
            case THURSDAY:
                return "四";
            case FRIDAY:
                return "五";
            case SATURDAY:
                return "六";
            case SUNDAY:
                return "日";
            default:
                throw UnifiedException.gen("数据异常");
        }
    }

    // 增减时间
    public static Date plusDays(@NonNull Date date, long days) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.plusDays(days));
    }

    public static Date plusMonths(@NonNull Date date, long months) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.plusMonths(months));
    }

    public static Date plusYears(@NonNull Date date, long years) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.plusYears(years));
    }

    public static Date minusDays(@NonNull Date date, long days) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.minusDays(days));
    }

    public static Date minusMonths(@NonNull Date date, long months) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.minusMonths(months));
    }

    public static Date minusYears(@NonNull Date date, long years) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.minusYears(years));
    }

    // jdk 新旧版本转换
    public static LocalDate toLocalDate(Date date) {
        if (date == null)
            return null;
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null)
            return null;
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null)
            return null;
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null)
            return null;
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

}
