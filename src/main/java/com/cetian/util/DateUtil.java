/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright [2014] [zangrong CetianTech]
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;


/**
 * @Description
 * @author zangrong
 * @Date 2020-01-20 06:02
 */
public class DateUtil {

    public static final String DATETIME_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_2 = "yyyyMMddHHmmss";
    public static final String DATETIME_FORMAT_3 = "yyyyMMddHHmmssSSS";
    public static final String DATE_FORMAT_2 = "yyyyMMdd";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_SLASH = "yyyy/M/d";
    public static final String DATE_FORMAT_MONTH = "yyyy-MM";
    public static final String DATE_FORMAT_CN = "yyyy年MM月dd日";
    public static final String DURATION_FORMAT = "H:m:s.SSS";
    public static final String DURATION_FORMAT_DAY = "d天 H时 m分";
    public static final String DURATION_FORMAT_CN = " m 分 s 秒";

    private DateUtil() {
    }

    /**
     * 通过给定字符串格式解析文本到 ISO-8601 日期，
     *
     * @param pattern  字符串格式
     * @param text     要解析的文本
     * @return 格式后的日期对象
     */
    public static LocalDate parseAsLocalDate(String pattern, String text) {
        return LocalDate.from(DateTimeFormatter.ofPattern(pattern).parse(text));
    }

    public static LocalDateTime parseAsLocalDateTime(String pattern, String text) {
        return LocalDateTime.from(DateTimeFormatter.ofPattern(pattern).parse(text));
    }

    public static Date parseAsDate(String pattern, String text) {
        return toDate(parseAsLocalDate(pattern, text));
    }

    public static Date parseAsDate(String pattern, String text, ZoneId zoneId) {
        return toDate(parseAsLocalDate(pattern, text), zoneId);
    }

    public static Date toDate(LocalDate localDate) {
        return toDate(localDate, ZoneId.systemDefault());
    }

    public static Date toDate(LocalDate localDate, ZoneId zoneId) {
        return Date.from(localDate.atStartOfDay(zoneId).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return toDate(localDateTime, ZoneId.systemDefault());
    }

    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    /**
     * 当前日期后指定跨度的日期
     *
     * @param currentDate 当前日期
     * @param amountToAdd 跨度，不允许负数
     * @param unit        跨度单位
     * @return 当前时间在指定跨度长度后端日期
     */
    public static Date before(Date currentDate, int amountToAdd, TemporalUnit unit) {
        if (amountToAdd < 0) {
            throw new IllegalArgumentException("days 参数必须大于等于0");
        }
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault()).plus(amountToAdd, unit);
        return toDate(localDateTime);
    }

    /**
     * 当前日期后指定天数后的日期，这是 {@link #before(Date, int, TemporalUnit)} 的简化功能
     * @param currentDate 当前日期
     * @param days        天数，不能为负
     * @return 当前日期后指定天数后的日期
     */
    public static Date before(Date currentDate, int days) {
        return before(currentDate, days, ChronoUnit.DAYS);
    }

    /**
     * 当前日期前指定跨度的日期
     *
     * @param currentDate 当前日期
     * @param amountToAdd 跨度，不允许负数
     * @param unit        跨度单位
     * @return 当前时间在指定跨度长度前端日期
     */
    public static Date after(Date currentDate, int amountToAdd, TemporalUnit unit) {
        if (amountToAdd < 0) {
            throw new IllegalArgumentException("days 参数必须大于等于0");
        }
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault()).plus(amountToAdd, unit);
        return toDate(localDateTime);
    }

    /**
     * 当前日期后指定天数前的日期，这是 {@link #after(Date, int, TemporalUnit)} 的简化功能
     * @param currentDate 当前日期
     * @param days        天数，不能为负
     * @return 当前日期后指定天数前的日期
     */
    public static Date after(Date currentDate, int days) {
        return before(currentDate, days, ChronoUnit.DAYS);
    }

    /**
     * 格式化日期对象
     *
     * @param date     要格式化的日期对象
     * @param pattern  字符串格式
     * @return 符合给定字符串格式的日期字符串
     */
    public static String format(Date date, String pattern) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    /**
     * 计算从当前到传入日期的 距离天数
     *
     * @param remoteDate
     * @return
     */
    public static int getDayFromNow(String remoteDate) {
        Date now = DateUtils.truncate(new Date(), Calendar.DATE);
        return (int) between(now, DateUtils.truncate(DateUtil.from(remoteDate).to(Date.class), Calendar.DATE),
                Calendar.DATE);
    }

    /**
     * @param start 更早的时间
     * @param end   更新的时间
     * @param field Calendar 日，小时，分，秒
     * @Title: between
     * @Description: 计算两个时间直接的间隔<br>
     *               例如 2018-03-10 2018-03-12 的值是2<br>
     *               例如 2018-03-12 2018-03-10 的值是-2<br>
     * @return: int
     * @throws:
     */
    public static long between(Date start, Date end, int field) {
        if (start == null || end == null) {
            return 0L;
        }
        long unit = 0L;
        switch (field) {
            case Calendar.DATE:
                unit = 1000 * 3600 * 24;
                break;
            case Calendar.HOUR:
            case Calendar.HOUR_OF_DAY:
                unit = 1000 * 3600;
                break;
            case Calendar.MINUTE:
                unit = 1000 * 60;
                break;
            case Calendar.SECOND:
                unit = 1000;
                break;
            default:
                throw new RuntimeException("参数不正确");
        }
        long count = (end.getTime() - start.getTime()) / unit;
        return count;
    }

    public static long between(Calendar start, Calendar end, int field) {
        return between(start.getTime(), end.getTime(), field);
    }

    /**
     * @param dates
     * @Title: max
     * @Description: 传入多个时间，返回最晚(最大)的那个时间<br>
     *               如果所有传入时间都是null，则返回当前时间
     * @return: Date
     * @throws:
     */
    public static Date max(Date... dates) {
        Date now = new Date();
        Date newer = now;
        if (ArrayUtils.isEmpty(dates)) {
            return now;
        }
        for (Date date : dates) {
            if (date != null && date.after(newer)) {
                newer = date;
            }
        }
        return newer;
    }

    /**
     * @param dates
     * @Title: min
     * @Description: 传入多个时间，返回最早(最小)的那个时间<br>
     *               如果所有传入时间都是null，则返回当前时间
     * @return: Date
     * @throws:
     */
    public static Date min(Date... dates) {
        Date now = new Date();
        Date older = now;
        if (ArrayUtils.isEmpty(dates)) {
            return now;
        }
        for (Date date : dates) {
            if (date != null && date.before(older)) {
                older = date;
            }
        }
        return older;
    }

    /**
     * H:m:s.SSS
     *
     * @param start
     * @param end
     * @Title: duration
     * @Description: 计算两个时间之间的时间差，用字符串形式展现
     * @return: String
     * @throws:
     */
    public static String duration(long start, long end) {
        return DurationFormatUtils.formatPeriod(start, end, DURATION_FORMAT);
    }

    public static String duration(Calendar start, Calendar end) {
        return DurationFormatUtils.formatPeriod(start.getTimeInMillis(), end.getTimeInMillis(), DURATION_FORMAT);
    }

    public static String duration(Date start, Date end) {
        return DurationFormatUtils.formatPeriod(start.getTime(), end.getTime(), DURATION_FORMAT);
    }

    public static String durationDay(Date start, Date end) {
        return DurationFormatUtils.formatPeriod(start.getTime(), end.getTime(), DURATION_FORMAT_DAY);
    }

    public static String duration(Date start, Date end, String format) {
        return DurationFormatUtils.formatPeriod(start.getTime(), end.getTime(), format);
    }

    public static String duration(long length) {
        return DurationFormatUtils.formatDuration(length, DURATION_FORMAT_CN);
    }

    /**
     *
     * @param duration HH:mm:ss
     * @return
     */
    public static Integer durationToSeconds(String duration) {
        Integer sec = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date reference = dateFormat.parse("00:00:00");
            Date date = dateFormat.parse(duration);
            long seconds = (date.getTime() - reference.getTime()) / 1000L;
            sec = (int)seconds;
        }catch (Exception e){
        }
        return sec;
    }

    /**
     * 传入日期 yyyy-MM-dd 返回，对应日期的 23:59:59的date对象
     * @param date
     * @return
     */
    public static Date getDayEnd(String date){
        Date temp = from(date).to(Date.class);
        temp = DateUtils.ceiling(temp, Calendar.DATE);
        temp = DateUtils.addSeconds(temp, -1);
        return temp;
    }

    /**
     * @Title: weekRange
     * @Description: 传入一个日期，获取该日期所在周的开始和结束日期，精确到秒<br>
     *               注意，这里采用中国传统常识: 周的开始日期是周一，周的结束日期是周日<br>
     *               传入 周六 2018-05-12 16:02:07 返回: 2018-05-07 00:00:00 和 2018-05-13
     *               23:59:59<br>
     *               传入 周一 2018-05-14 16:02:07 返回: 2018-05-14 00:00:00 和 2018-05-20
     *               23:59:59<br>
     *               传入 周日 2018-05-13 16:02:07 返回: 2018-05-07 00:00:00 和 2018-05-13
     *               23:59:59<br>
     * @return: Date[]
     * @throws:
     */
    public static Date[] weekRange(Date date) {
        Date[] week = new Date[2];
        // 先获取开始日期 周一00:00:00
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                // 周一 什么也不做
                break;
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
            case Calendar.SATURDAY:
                // 周二到周六 设置为当周的周一
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case Calendar.SUNDAY:
                // 周日
                cal.add(Calendar.DATE, -6);
                break;
            default:
                throw new IllegalArgumentException();
        }
        // 周一00:00:00
        Date start = DateUtils.truncate(cal.getTime(), Calendar.DATE);
        // 周日23:59:59
        Date end = DateUtils.addWeeks(start, 1);
        end = DateUtils.addSeconds(end, -1);
        week[0] = start;
        week[1] = end;
        return week;
    }

    public static String[] weekRangeString(Date date) {
        Date[] range = weekRange(date);
        String[] rangeString = new String[2];
        rangeString[0] = from(range[0]).to(String.class);
        rangeString[1] = from(range[1]).to(String.class);
        return rangeString;
    }

    public static Date[] monthRange(String date) {
        return monthRange(from(date).to(Date.class));
    }

    public static String[] monthRangeString(String date) {
        return monthRangeString(from(date).to(Date.class));
    }

    /**
     * @param date
     * @Title: monthRange
     * @Description: 传入一个日期，获取该日期所在月的开始和结束日期，精确到秒<br>
     *               传入 周六 2018-05-12 16:02:07 返回: 2018-05-01 00:00:00 和 2018-05-31
     *               23:59:59<br>
     * @return: Date[]
     * @throws:
     */
    public static Date[] monthRange(Date date) {
        Date[] month = new Date[2];
        // 先获取开始日期 1号00:00:00
        Date start = DateUtils.truncate(date, Calendar.MONTH);
        // 获取当月最后一天 23:59:59
        Date end = DateUtils.ceiling(date, Calendar.MONTH);
        end = DateUtils.addSeconds(end, -1);
        month[0] = start;
        month[1] = end;
        return month;
    }

    /**
     * @Title: monthRange
     * @Description: 传入一个日期，获取该日期所在月的开始和结束日期，精确到秒<br>
     * 传入 周六 2018-05-12 16:02:07 返回: 2018-05-01 00:00:00 和 2018-05-31 23:59:59<br>
     * @param date
     * @return: Date[]
     * @throws:
     */
    public static String[] monthRangeString(Date date) {
        Date[] dates = monthRange(date);
        String[] month = new String[2];
        month[0] = from(dates[0]).to(String.class);
        month[1] = from(dates[1]).to(String.class);
        return month;
    }

    public static String beginOfMonth(String date) {
        // 获取开始日期 1号00:00:00
        Date begin = DateUtils.truncate(from(date).to(Date.class), Calendar.MONTH);
        return from(begin).to(String.class);
    }

    public static String endOfMonth(String date) {
        // 获取当月最后一天 23:59:59
        Date end = DateUtils.ceiling(from(date).to(Date.class), Calendar.MONTH);
        end = DateUtils.addSeconds(end, -1);
        return from(end).to(String.class);
    }

    /**
     * @param date
     * @Title: dayRange
     * @Description: 传入一个日期，获取该日期所在日的开始和结束日期，精确到秒<br>
     *               传入 周六 2018-05-12 16:02:07 返回: 2018-05-12 00:00:00 和 2018-05-12
     *               23:59:59<br>
     * @return: Date[]
     * @throws:
     */
    public static Date[] dayRange(Date date) {
        Date[] day = new Date[2];
        // 先获取 今天00:00:00
        Date start = DateUtils.truncate(date, Calendar.DATE);
        // 获取今天 23:59:59
        Date end = DateUtils.ceiling(date, Calendar.DATE);
        end = DateUtils.addSeconds(end, -1);
        day[0] = start;
        day[1] = end;
        return day;
    }

    /**
     * @Title: getNowMonth
     * @Description: 获取当前时间的月份
     * @return: String
     * @throws:
     */
    public static String getNowMonth() {
        return DateFormatUtils.format(new Date(), DATE_FORMAT_MONTH);
    }

    /**
     * 对比两个日期时间是否相等，精确到毫秒
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equal(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.getTime() == date2.getTime();
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int daysBetween(Date startDate, Date endDate) {
        long difference = (endDate.getTime() - startDate.getTime()) / 86400000;
        return (int) difference;
    }

    /**
     * @param date
     * @return yyyyMMddHHmmss
     */
    public static String datetimeString(Date date) {
        String dateText = DateFormatUtils.format(date, DATETIME_FORMAT_2);
        return dateText;
    }

    /**
     * @param date
     * @return yyyyMMddHHmmssSSS
     */
    public static String datetimemsString(Date date) {
        String dateText = DateFormatUtils.format(date, DATETIME_FORMAT_3);
        return dateText;
    }

    /**
     * @param date
     * @return yyyyMMdd
     */
    public static String dateShortString(Date date) {
        String dateText = DateFormatUtils.format(date, DATE_FORMAT_2);
        return dateText;
    }

    public static Date[] range(Integer year, Integer month) {
        Date[] range = new Date[2];
        if (year == null && month == null) {
            return range;
        }
        Calendar cal = Calendar.getInstance();
        if (year != null) {
            cal.set(Calendar.YEAR, year);
        }
        if (month != null) {
            cal.set(Calendar.MONTH, month - 1);
            range[0] = DateUtils.truncate(cal.getTime(), Calendar.MONTH);
            range[1] = DateUtils.ceiling(cal.getTime(), Calendar.MONTH);
        } else {
            range[0] = DateUtils.truncate(cal.getTime(), Calendar.YEAR);
            range[1] = DateUtils.ceiling(cal.getTime(), Calendar.YEAR);
        }
        return range;
    }

    /**
     * 找到指定的weekday
     *
     * @param weekDelta -1 表示上周 0表示本周 1表示下周
     * @param weekday   Calendar.Monday Tuesday Wednesday...
     * @return
     */
    public static Date getWeekDay(int weekDelta, int weekday) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        if (weekDelta != 0) {
            cal.add(Calendar.WEEK_OF_YEAR, weekDelta);
        }
        Date startDay = DateUtils.truncate(cal.getTime(), Calendar.DATE);
        return startDay;
    }

    /**
     * 根据传入的整数，获取未来days天的日期，转换成string，存入集合
     *
     * @param days
     * @return
     */
    public static List<String> dateList(int days) {
        List<String> dates = new ArrayList<>();
        Date date = new Date();
        if (days >= 0) {
            int i = 0;
            do {
                dates.add(DateUtil.from(date).to(String.class));
                date = DateUtils.addDays(date, 1);
                i++;
            } while (i < days);
        } else {
            int i = 0;
            do {
                dates.add(DateUtil.from(date).to(String.class));
                date = DateUtils.addDays(date, -1);
                i--;
            } while (i > days);
        }
        return dates;
    }

    public static List<String> dateList(String dateBegin, String dateEnd) {
        Date begin = from(dateBegin).to(Date.class);
        Date end = from(dateEnd).to(Date.class);
        List<String> dates = new ArrayList<>();
        while (!begin.after(end)) {
            dates.add(from(begin).to(String.class));
            begin = DateUtils.addDays(begin, 1);
        }
        return dates;
    }

    /**
     * @param date
     * @param dayOffset
     * @return
     */
    public static String lastYear(String date, int dayOffset) {
        Date dateLastYear = from(date).to(Date.class);
        dateLastYear = DateUtils.addDays(dateLastYear, dayOffset);
        dateLastYear = DateUtils.addYears(dateLastYear, -1);
        String dateLastYearStr = from(dateLastYear).to(String.class);
        return dateLastYearStr;
    }

    /**
     * 返回传入日期的月份，1-12
     *
     * @param dateStr
     * @return
     */
    public static int getMonth(String dateStr) {
        Date date = from(dateStr).to(Date.class);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    public static boolean lt(String date1, String date2) {
        return date1.compareTo(date2) < 0;
    }

    public static boolean le(String date1, String date2) {
        return date1.compareTo(date2) <= 0;
    }

    public static boolean ge(String date1, String date2) {
        return date1.compareTo(date2) >= 0;
    }

    public static boolean gt(String date1, String date2) {
        return date1.compareTo(date2) > 0;
    }

    /**
     * 包含边界
     *
     * @param date
     * @param dateBegin
     * @param dateEnd
     * @return
     */
    public static boolean in(String date, String dateBegin, String dateEnd) {
        return ge(date, dateBegin) && le(date, dateEnd);
    }

    public static String addDays(String date, int days) {
        return from(DateUtils.addDays(from(date).to(Date.class), days)).to(String.class);
    }

    /**
     * 国际标准，周日开始
     * 周日: 1
     * 周一: 2
     * ...
     * 周六: 7
     * @param date
     * @return
     */
    public static int weekDay(String date){
        Calendar cal = from(date).to(Calendar.class);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static String[] getWeekByDate(String date){
        int weekDay = weekDay(date);
        String weekBegin = addDays(date, 1 - weekDay);
        String weekEnd = addDays(date, 7 - weekDay);
        return new String[]{weekBegin, weekEnd};
    }

    public static int[] getYearAndWeek(String date){
        Calendar cal = from(date).to(Calendar.class);
        int[] result = new int[2];
        result[0] = cal.get(Calendar.YEAR);
        result[1] = cal.get(Calendar.WEEK_OF_YEAR);
        return result;
    }


    /**
     * 返回当前日期的星期，周一到周六 1-6，周日 7
     *
     * @param date
     * @return
     */
    public static int weekDayCn(String date) {
        return weekDayCn(from(date).to(Date.class));
    }

    /**
     * 返回当前日期的星期，周一到周六 1-6，周日 7
     *
     * @param date
     * @return
     */
    public static int weekDayCn(Date date) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTime(date);
        // 周一到周六 1-6，周日 7
        int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(weekDay == 0){
            weekDay = 7;
        }
        return weekDay;
    }

    /**
     * 获取一年的所有周
     * 周的概念以中式习惯为准，一个完整周是从周一到周日
     * 一年以第一个达到4天的周作为第一周
     * @param year
     * @return
     */
    public static List<String[]> getWeeksOfYearCn(int year){
        List<String[]> weeks = new ArrayList<>();
        // 找到当年1月1日
        String yearBegin = String.format("%s-01-01", year);
        // 获得元旦是星期几
        int weekDay = weekDayCn(yearBegin);
        String weekBegin = null;
        String weekEnd = null;
        if (weekDay <= 4){
            // 元旦属于当年第一周
            // 元旦所在周有4天及以上属于当年，则该周为当年第一周
            weekBegin = addDays(yearBegin, -(weekDay-1));
            weekEnd = addDays(weekBegin, 6);
        }else{
            // 元旦不属于当年第一周
            // 元旦所在周小于等于3天属于当年，则该周为上年最后一周，下周属于当年第一周
            weekBegin = addDays(yearBegin, 7 - weekDay + 1);
            weekEnd = addDays(weekBegin, 6);
        }
        weeks.add(ArrayUtils.toArray(weekBegin, weekEnd));

        // 下一年元旦
        String nextYearBegin = String.format("%s-01-01", year + 1);

        while (true){
            weekBegin = addDays(weekBegin, 7);
            weekEnd = addDays(weekEnd, 7);
            if (isBetween(nextYearBegin, weekBegin, weekEnd)){
                // 如果当周包含下年元旦，就要判断当周是否属于本年或是下一年
                int nextYearWeekDay = weekDayCn(nextYearBegin);
                // 元旦所在周小于等于3天属于下年，则该周为当年最后一周
                if (!(nextYearWeekDay <= 4)){
                    // 下年元旦属于本年最后一周
                    weeks.add(ArrayUtils.toArray(weekBegin, weekEnd));
                }
                break;
            }
            weeks.add(ArrayUtils.toArray(weekBegin, weekEnd));
        }
        return weeks;
    }

    /**
     * 获取一年的第n周
     * 周的概念以中式习惯为准，一个完整周是从周一到周日
     * @param year
     * @param weekNum 1 ... 52
     * @return
     */
    public static String[] getWeekOfYearCn(int year, int weekNum){
        if (weekNum < 1 ||weekNum > 52){
            throw new IllegalArgumentException("周的有效范围是1-52");
        }
        List<String[]> weeks = getWeeksOfYearCn(year);
        return weeks.get(weekNum-1);
    }

    /**
     * 获取当日所对应的周
     * 周的概念以中式习惯为准，一个完整周是从周一到周日
     * @param date yyyy-MM-dd
     * @return weekBegin, weekEnd
     */
    public static String[] getWeekByDateCn(String date){
        int weekDay = weekDayCn(date);
        String weekBegin = addDays(date, 1 - weekDay);
        String weekEnd = addDays(date, 7 - weekDay);

        return new String[]{weekBegin, weekEnd};
    }

    /**
     * 获取当日所对应的年和周
     * 周的概念以中式习惯为准，一个完整周是从周一到周日
     * @param date yyyy-MM-dd
     * @return year, weekNum
     */
    public static int[] getYearAndWeekCn(String date){
        int year = Integer.parseInt(StringUtils.substring(date, 0, 4));
        // 获取当年所有周
        List<String[]> weeks = getWeeksOfYearCn(year);
        // 尝试当年中的正常周
        for (int i = 0;i <weeks.size(); i++) {
            String[] inWeek = weeks.get(i);
            if (isBetween(date, inWeek[0], inWeek[1])){
                return new int[]{year, i+1};
            }
        }
        // 获取当日所在周
        String[] week = getWeekByDateCn(date);
        // 获取当日所在年元旦
        String yearBegin = String.format("%s-01-01", year);
        if (isBetween(yearBegin, week[0], week[1])){
            // 如果当日所在周包含当年元旦，但是因为当年没匹配到
            // 所以到上年周匹配
            int lastYear = year - 1;
            List<String[]> lastYearWeeks = getWeeksOfYearCn(lastYear);
            for (int i = 0;i <lastYearWeeks.size(); i++) {
                String[] inWeek = lastYearWeeks.get(i);
                if (isBetween(date, inWeek[0], inWeek[1])){
                    return new int[]{lastYear, i+1};
                }
            }
        }else{
            // 在下年里匹配
            int nextYear = year+1;
            List<String[]> nextYearWeeks = getWeeksOfYearCn(nextYear);
            for (int i = 0;i <nextYearWeeks.size(); i++) {
                String[] inWeek = nextYearWeeks.get(i);
                if (isBetween(date, inWeek[0], inWeek[1])){
                    return new int[]{nextYear, i+1};
                }
            }
        }
        throw new RuntimeException(String.format("[%s]获取年周信息异常", date));
    }


    /**
     * 判断传入的日期是否匹配 星期
     *
     * @param weekDays 星期 1234567
     * @param date     2020-10-10
     * @return
     */
    public static boolean weekDayMatchCn(String weekDays, String date) {
        if (StringUtils.isBlank(weekDays)) {
            return false;
        }
        int weekDay = weekDayCn(date);
        return StringUtils.contains(weekDays, weekDay + "");
    }

    /**
     * 返回当前日期的星期，周一到周六 1-6，周日 7
     *
     * @param date
     * @return
     */
    public static String weekDayChinese(String date) {
        switch (weekDayCn(date)) {
            case 7:
                return "星期日";
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            default:
                return "未知";
        }
    }

    public static boolean isBetween(String date, String dateBegin, String dateEnd) {
        if (StringUtil.greaterThanOrEqualTo(date, dateBegin) && StringUtil.lessThanOrEqualTo(date, dateEnd)) {
            return true;
        }
        return false;
    }

    public static String[] datetimeRange(Date date) {
        Date[] dayRange = dayRange(date);
        String[] datetimes = new String[2];
        datetimes[0] = from(dayRange[0]).setDateFormat(DATETIME_FORMAT_1).to(String.class);
        datetimes[1] = from(dayRange[1]).setDateFormat(DATETIME_FORMAT_1).to(String.class);
        return datetimes;
    }

    /**
     * 指定年份的对应日期，周信息相等
     *
     * @param date
     * @param year
     * @return
     */
    public static String weekRelativeDayCn(String date, int year) {
        int weekDay = weekDayCn(date);
        Date dateValue = from(date).to(Date.class);
        Date targetValue = DateUtils.setYears(dateValue, year);
        int targetWeekDay = weekDayCn(targetValue);
        targetValue = DateUtils.addDays(targetValue, weekDay - targetWeekDay);
        return from(targetValue).to(String.class);
    }

    public static String relativeDay(String date, int year, int offset) {
        Date dateValue = from(date).to(Date.class);
        Date targetValue = DateUtils.setYears(dateValue, year);
        targetValue = DateUtils.addDays(targetValue, offset);
        return from(targetValue).to(String.class);
    }

    public static int[] getYearAndMonth(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);// 根据开始日期确定 年 和 月
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        return new int[]{year, month};
    }

    public static DateConvertor from(Object src) {
        return new DateConvertor(src);
    }

    @Slf4j
    public static class DateConvertor {
        private Date temp;
        private Object src;
        private String dateFormat;

        public DateConvertor(Object src) {
            this.src = src;
        }

        public DateConvertor setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        /**
         * @param dateLong
         * @Title: longToDate
         * @Description: 把Long转换为Date对象，Long精确到秒
         * @return: Date
         * @throws:
         */
        public static Date longToDate(Long dateLong) {
            if (dateLong == null) {
                return null;
            }
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(dateLong * 1000);
            return date.getTime();
        }

        /**
         * @param date
         * @Title: dateToLong
         * @Description: date对象转成Long，单位精确到秒
         * @return: Long
         * @throws:
         */
        public static Long dateToLong(Date date) {
            if (date == null) {
                return null;
            }
            return date.getTime() / 1000;
        }

        public <T> T to(Class<T> clazz) {
            if (this.src == null) {
                return null;
            }

            // 先转成中间日期对象 temp
            if (this.src instanceof String) {
                try {
                    if (StringUtils.isBlank(this.dateFormat)) {
                        this.temp = DateUtils.parseDate((String) this.src, DateUtil.DATE_FORMAT);
                    } else {
                        temp = DateUtils.parseDate((String) this.src, this.dateFormat);
                    }
                } catch (Exception e) {
                    log.warn("日期转换异常[{}] 格式[{}]", this.src, this.dateFormat);
                    log.warn("日期转换异常", e);
                }
            } else if (src instanceof Date) {
                temp = (Date) src;
            } else if (src instanceof Calendar) {
                temp = ((Calendar) src).getTime();
            } else if (src instanceof Long) {
                // 如果是微秒的要转换成秒，这里会丢失微秒精度
                Long val = (Long) src;
                if (Long.toString((Long) src).length() == 13) {
                    val = val / 1000;
                }
                temp = longToDate(val);
            }

            if (temp == null) {
                return null;
            }
            // 转成目标对象类型
            if (clazz == String.class) {
                if (StringUtils.isBlank(this.dateFormat)) {
                    return (T) DateFormatUtils.format(this.temp, DATE_FORMAT);
                } else {
                    return (T) DateFormatUtils.format(this.temp, this.dateFormat);
                }
            } else if (clazz == Date.class) {
                return (T) temp;
            } else if (clazz == Calendar.class) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(temp);
                return (T) cal;
            } else if (clazz == Long.class) {
                return (T) dateToLong(temp);
            }
            throw new IllegalArgumentException("转换日期的目标类型不正确");
        }

    }

}


