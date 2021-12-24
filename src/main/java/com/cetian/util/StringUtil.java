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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 *
 * @author zangrong
 * @date 2018/8/28 19:57
 */
@Slf4j
public class StringUtil {

    public static final String DEFAULT_SEPARATOR = "-";
    public static final String CHARSET_GB2312 = "GB2312";
    public static final String CHARSET_ISO8859_1 = "ISO8859-1";
    public static final String CHARSET_UTF_8 = "UTF-8";

    /**
     * text transform from one charset to another
     *
     * @param text text to transform
     * @param fromCharsetName  original charset
     * @param toCharsetName target charset
     * @return text after transform
     */
    public static String transform(String text, String fromCharsetName, String toCharsetName) {
        try {
            text = new String(text.getBytes(fromCharsetName), toCharsetName);
        } catch (UnsupportedEncodingException ex) {
            log.warn("text encode transform error. text[{}] message[{}]", text, ex.getMessage());
        }
        return text;
    }

    /**
     * 把字符串包含在指定文本中
     *
     * @param text
     * @param fix
     * @return
     */
    public static String embrace(String text, String fix) {
        text = StringUtils.trimToEmpty(text);
        return fix + text + fix;
    }

    /**
     * 把字符串包含在指定文本中
     *
     * @param text
     * @return
     */
    public static String embraceLike(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        } else {
            text = StringUtils.trimToEmpty(text);
            return "%" + text + "%";
        }
    }

    /**
     * 把字符串分解成集合
     *
     * @param strings
     * @return
     */
    public static Set<String> split(String strings) {
        Set<String> set = new HashSet<>();
        String[] split = StringUtils.split(StringUtils.trimToEmpty(strings), ",");
        if (ArrayUtils.isNotEmpty(split)) {
            for (String str : split) {
                set.add(str);
            }
        }
        return set;
    }


    public static List<Integer> splitToIntList(String string, String... seperators) {
        List<Integer> list = null;
        if (StringUtils.isBlank(string)) {
            return list;
        }
        try {
            String[] split = null;
            if (ArrayUtils.isEmpty(seperators)) {
                // 如果 seperators 为空，则采用默认英文半角逗号分隔
                split = StringUtils.split(string, ",");
            } else if (seperators.length == 1) {
                // 如果 seperators 是1个，则才用它作为分隔
                split = StringUtils.split(string, seperators[0]);
            } else {
                // 如果 seperators 是多个，则把前面的都替换成最后一个，然后再分隔
                String last = seperators[seperators.length - 1];
                for (int i = 0; i < seperators.length - 2; i++) {
                    string = RegExUtils.replaceAll(string, seperators[i], last);
                }
                split = StringUtils.split(string, last);
            }
            if (ArrayUtils.isNotEmpty(split)) {
                list = new ArrayList<>();
                for (String s : split) {
                    list.add(Integer.parseInt(s));
                }
            }
        } catch (Exception e) {
            log.warn("", e);
            list = null;
        }
        return list;
    }

    public static List<String> arrayToList(String[] array) {
        if (array == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String string : array) {
            if (StringUtils.isNotBlank(string)) {
                list.add(string);
            }
        }
        return list;
    }

    public static String join(String... eles) {
        if (ArrayUtils.isEmpty(eles)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String ele : eles) {
            list.add(ele);
        }
        return StringUtils.join(list, DEFAULT_SEPARATOR);
    }

    /**
     * 任意为空 返回true
     *
     * @param params
     * @return
     */
    public static boolean isAnyBlank(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            return true;
        }
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 任意不为空，返回true
     *
     * @param params
     * @return
     */
    public static boolean isAnyNotBlank(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            return false;
        }
        for (String param : params) {
            if (StringUtils.isNotBlank(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 全部不为空，返回true
     *
     * @param params
     * @return
     */
    public static boolean isAllNotBlank(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            return false;
        }
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 全部为空，返回true
     *
     * @param params
     * @return
     */
    public static boolean isAllBlank(String... params) {
        if (ArrayUtils.isEmpty(params)) {
            return true;
        }
        for (String param : params) {
            if (StringUtils.isNotBlank(param)) {
                return false;
            }
        }
        return true;
    }

    public static String upperCaseAndTrim(String text) {
        return StringUtils.upperCase(StringUtils.trimToEmpty(text));
    }

    /**
     * 是否全部是字母
     *
     * @param text
     * @return
     */
    public static boolean isAllLetter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean greaterThanOrEqualTo(String str1, String str2) {
        return str1.compareTo(str2) >= 0;
    }

    public static boolean lessThanOrEqualTo(String str1, String str2) {
        return str1.compareTo(str2) <= 0;
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
     * 打码，通常用在联系电话
     * @param text 原文本
     * @param startIndex 开始位置
     * @param length 长度
     * @param replacement 替换字符，如果为null，则默认为 *
     * @return
     */
    public static String mask(String text, int startIndex, int length, Character replacement){
        if (StringUtils.isBlank(text)){
            return text;
        }
        if (replacement == null){
            replacement = '*';
        }
        char[] chars = text.toCharArray();

        if ((length+startIndex) <= chars.length){
            for (int i = startIndex; i < length + startIndex; i++){
                chars[i] = replacement;
            }
        }

        return new String(chars);
    }

}
