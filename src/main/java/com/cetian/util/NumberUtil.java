/**
 * @Copyright: 2017 jiayun.com Inc. All rights reserved.
 * @Title: NumberUtil.java 
 * @date 2017年3月2日 上午11:19:30 
 * @version V1.0
 * @author zangrong
 */
package com.cetian.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @ClassName: NumberUtil
 * @Description: 数字工具类
 * @date: 2017年3月2日 上午11:19:30
 * @author: zangrong
 * 
 */
public class NumberUtil {

	private static final DecimalFormat df = new DecimalFormat("#,###,##0.00");

	/**
	 * @Title: round
	 * @Description: 四舍五入
	 * @param value
	 * @param decimalPlace
	 *            2 小数点后两位<br>
	 *            0 个位<br>
	 *            -2 百位<br>
	 * @return: double
	 * @throws:
	 */
	public static double round(double value, int decimalPlace) {
		BigDecimal b = new BigDecimal(value);
		double roundedValue = b.setScale(decimalPlace, RoundingMode.HALF_UP).doubleValue();
		return roundedValue;
	}

	public static String currencyFormat(Double currency) {
		if (currency == null) {
			return "￥ ";
		} else {
			return "￥ " + df.format(currency);
		}
	}

	/**
	 * 
	 * @Title: formatPopularity
	 * @Description: 对人气数值进行格式化
	 * @param value
	 * @return: String
	 * @throws:
	 */
	public static String formatPopularity(Integer value) {
		if (value == null || value == 0) {
			return "0";
		}
		// 确定单位，1是万，2是亿
		int i = (int) (Math.log(value) / Math.log(10000));
		double d = value / Math.pow(10000, i);
		BigDecimal b = new BigDecimal(d);
		d = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
		String[] array = { "", "万", "亿" };
		return d + array[i];
	}

	/**
	 * @Title: toSimpleChinese
	 * @Description: 数字转简体汉字，有缺陷，不能大于10
	 * @param i
	 * @return: String
	 * @throws:
	 */
	public static String toSimpleChinese(int i) {
		if (i < 0) {
			return "";
		} else if (i >= 0 && i <= 10) {
			switch (i) {
			case 0:
				return "〇";
			case 1:
				return "一";
			case 2:
				return "二";
			case 3:
				return "三";
			case 4:
				return "四";
			case 5:
				return "五";
			case 6:
				return "六";
			case 7:
				return "七";
			case 8:
				return "八";
			case 9:
				return "九";
			case 10:
				return "十";
			default:
				return "";
			}
		} else {
			// TODO 10以上暂不处理
			return "";
		}
	}
	
	/**
	 * @Title: longToInt   
	 * @Description: 把Long型转换为int型
	 * @param longValue
	 * @return: int      
	 * @throws:
	 */
	public static int longToInt(Long longValue) {
		if (longValue == null) {
			return 0;
		}
		long temp = longValue;
		return (int) temp;
	}
	
}
