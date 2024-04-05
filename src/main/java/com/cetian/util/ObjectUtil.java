/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright [2014] [zangrong CetianTech]
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 *
 *
 *
 * @author zangrong
 * @Date 2020-01-20 06:02
 */
@Slf4j
public class ObjectUtil {

	private static final DecimalFormat NUMBER_FORMAT = (DecimalFormat) DecimalFormat.getInstance();

	static {
		NUMBER_FORMAT.setGroupingSize(3);
	}

	/**
	 * Object null = "";<br/>
	 * Object obj = obj.toString().trim();<br/>
	 * String "abc" = "abc";<br/>
	 * String " abc " = "abc";<br/>
	 * 
	 * @Title: trimToEmpty
	 * @Description: 把一个对象转成对应字符串，如果为null则返回空字符串""
	 * @param obj
	 * @return: String
	 * @throws:
	 */
	public static String trimToEmpty(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return StringUtils.trim(obj.toString());
		}
	}

	/**
	 * 获取枚举类型的 name属性值
	 * @param obj
	 * @return
	 */
	public static String trimToEmpty(Enum obj) {
		String value = "";
		if (obj == null) {
			return value;
		} else {
			Field valueField = null;
			Class<? extends Enum> enumType = obj.getClass();
			try{
				valueField = enumType.getDeclaredField("name");
				valueField.setAccessible(true);
				value = StringUtils.trimToEmpty(valueField.get(obj).toString());
			}catch(Exception e){
				log.warn("枚举类型[{}] 没有text字段", enumType);
				return "";
			}
			return value;
		}
	}



	/**
	 * Boolean null = 0;<br/>
	 * Boolean false = 0;<br/>
	 * Boolean true = 1;<br/>
	 * 
	 * @Title: trimToEmpty
	 * @Description: 处理一个Boolean对象，如果为null则返回"0",如果为true则返回"1"，如果为false则返回"0"
	 * @param obj
	 * @return: String
	 * @throws:
	 */
	public static int trimToEmpty(Boolean obj) {
		if (obj == null) {
			return 0;
		} else {
			return obj ? 1 : 0;
		}
	}

	public static Long trimToEmpty(Long obj) {
		if (obj == null) {
			return 0L;
		} else {
			return obj;
		}
	}

	public static Long trimToLong(Object obj) {
		if (obj == null) {
			return null;
		} else {
			return Long.parseLong(obj.toString());
		}
	}

	public static Integer trimToEmpty(Integer obj) {
		if (obj == null) {
			return 0;
		} else {
			return obj;
		}
	}

	public static Integer trimToInteger(Object obj) {
		if (obj == null) {
			return 0;
		} else {
			return Integer.parseInt(obj.toString());
		}
	}

	public static Double trimToDouble(Object obj) {
		if (obj == null) {
			return 0d;
		} else {
			return Double.parseDouble(obj.toString());
		}
	}

	/**
	 * 
	 * @Title: getStringWithdefaultValue
	 * @Description: 如果value是null返回dafaultValue
	 * @param value
	 * @param dafaultValue
	 * @return
	 * @return: String
	 * @throws:
	 */
	public static String getStringWithdefaultValue(Object value, String dafaultValue) {
		if (value == null) {
			return dafaultValue;
		}
		return value.toString();
	}

	public static String deleteEnterAndSpace(String data) {
		Pattern p = Pattern.compile("\\s{2,}|\t|\r|\n|&#x([^a|A|d|D]|.{2});");
		Matcher m = p.matcher(data);
		return m.replaceAll("");
	}

	public static boolean equals(Object obj, String value) {
		return (obj != null && obj.toString().equals(value));
	}

	public static boolean isNotNull(Object obj) {
		return (obj != null && obj.toString().length() != 0);
	}

	/**
	 * @Title: numberFormat
	 * @Description: 给数字格式化
	 * @param num
	 * @return: String
	 * @throws:
	 */
	public static String numberFormat(Integer num) {
		if (num == null) {
			return "0";
		}
		String format = NUMBER_FORMAT.format(num);
		return format;
	}

	public static String numberFormat(Long num) {
		if (num == null) {
			return "0";
		}
		String format = NUMBER_FORMAT.format(num);
		return format;
	}

	public static Long objectToLong(Object obj){
		Long value = null;
		try{
			if (obj == null){
				return null;
			}
			value = Long.parseLong(obj.toString());
		}catch (Exception e){
			log.warn("", e);
		}
		return value;
	}

}
