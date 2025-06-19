/**
 * Cetian Techs Ltd., Co. 2021
 *
 * @ClassName DatetimeUtil
 * @Author zangrong
 */
package com.cetian.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *@ClassName DatetimeUtil
 *@Author zangrong
 *@Date 2025/6/19 09:39
 *@Description TODO
 */
public class DateTimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private DateTimeUtil() {

    }

    /**
     * 将字符串转换为LocalDateTime对象
     * @param dateTimeStr 日期字符串 "2025-06-18T15:35:16+08:00"
     * @return
     */
    public static LocalDateTime string2DateTime(String dateTimeStr) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeStr);
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        return localDateTime;
    }

    /**
     * 将LocalDateTime对象转换为字符串
     * @param localDateTime LocalDateTime对象
     * @return "2025-06-18T15:35:16+08:00"
     */
    public static String dateTime2String(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        String formatted = zonedDateTime.format(formatter);
        return formatted;
    }

    /**
     * 将LocalDateTime对象转换为指定时区的字符串
     * @param localDateTime LocalDateTime对象
     * @param zoneId 时区ID
     * @return
     */
    public static String dateTime2String(LocalDateTime localDateTime, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        String formatted = zonedDateTime.format(formatter);
        return formatted;
    }
}
