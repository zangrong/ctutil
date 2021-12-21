/**
 * @Copyright: CetianTech 2021
 * @Title: ProcessUtil.java 
 * @date 2017年5月5日 下午4:29:21 
 * @version V1.0
 * @author zangrong
 */
package com.cetian.util;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.format.number.NumberStyleFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @ClassName: ProcessUtil
 * @Description: 进度工具类
 * @date: 2017年5月5日 下午4:29:21
 * @author: zangrong
 * 
 */
public class ProcessUtil {

	private ProcessUtil() {
	}

	/* 计算输出完成率 */
	private static final String process_template = "[%s]: 进度[%s/%s]，百分比[%s%%]，已执行时间[%s]，预计剩余时间[%s]";
	private static final NumberStyleFormatter formatter = new NumberStyleFormatter("###.##");

	public static String completeRate(Date begin, int total, int current, String title) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(begin);
		return completeRate(cal, total, current, title);
	}

	/**
	 * 显示执行消耗的时间
	 * @param begin
	 * @return
	 */
	public static String consumed(Date begin){
		Date end = new Date();
		long executedTime = end.getTime() - begin.getTime();
		String executedTimeStr = DurationFormatUtils.formatDuration(executedTime, "H:m:s SSS");
		return executedTimeStr;
	}

	public static String completeRate(Calendar begin, int total, int current, String title) {
		Long executedTime = System.currentTimeMillis() - begin.getTimeInMillis();
		return completeRate(executedTime, total, current, title);
	}

	public static String completeRate(Long executedTime, int total, int current, String title) {
		String result = null;
		String executedTimeStr = DurationFormatUtils.formatDuration(executedTime, "H:m:s");
		try {
			if (current >= total) {
				result = String.format(process_template, title, current, total, "100.00", executedTimeStr, "0:0:0");
			} else {
				double rate = (double) current / (double) total;
				String completeRate = formatter.print(rate * 100d, Locale.getDefault());

				// 根据过去消耗的时间计算预计剩余需要的完成时间
				double totalTime = (double) executedTime / rate;
				Long remainTime = 0L;
				if (totalTime > executedTime) {
					remainTime = (long) totalTime - executedTime;
				}
				String remain = DurationFormatUtils.formatDuration(remainTime, "H:m:s");
				result = String.format(process_template, title, current, total, completeRate, executedTimeStr, remain);
			}
		} catch (Exception e) {
			result = String.format(process_template + "|有异常", title, current, total, "100.00", executedTimeStr, "0:0:0");
		}
		return result;
	}

	/**
	 * @Title: generateStatistic   
	 * @Description: 产生最终统计信息 注意 这个是指定开始和结束时间
	 * @param begin
	 * @param end
	 * @param title
	 * @param totalCount
	 * @return: String      
	 * @throws:
	 */
	public static String generateStatistic(Date begin, Date end, String title, Integer totalCount) {
		String template = "%s[%d]，耗时[%s]";
		String statistic = String.format(template, title, totalCount,
				DateUtil.duration(begin.getTime(), end.getTime()));
		return statistic;
	}

	/**
	 * @Title: generateStatistic
	 * @Description: 产生最终统计信息
	 * @param begin
	 * @param title
	 * @param totalCount
	 * @return: String
	 * @throws:
	 */
	public static String generateStatistic(Calendar begin, String title, Integer totalCount) {
		Calendar end = Calendar.getInstance();
		String templat = "%s[%d]，耗时[%s]";
		String statistic = String.format(templat, title, totalCount,
				DateUtil.duration(begin.getTimeInMillis(), end.getTimeInMillis()));
		return statistic;
	}

}
