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

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description
 *
 * Chinese utility
 * @author zangrong
 * @date 2021-12-24
 */
@Slf4j
public class ChineseUtil {

    private static final int[] LI_SEC_POS_VALUE = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212,
            3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590 };

    private static final String[] LC_FIRST_LETTER = { "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "w", "x", "y", "z" };

    private static final int CHINESE_UNICODE_BEGIN = 0x4e00;// 汉字在unicode编码中的开始数字
    private static final int CHINESE_UNICODE_END = 0x9fbb;// 汉字在unicode编码中的结束数字

    /**
     *
     * @param text
     * @return
     */
    public static String getChineseFirstChar(String text){
        if (!isAllChinese(text)) {
            throw new IllegalArgumentException("给定字符串不是汉字");
        }
        if (StringUtils.isBlank(text)) {
            return "";
        }

        String _str = "";
        for (int i = 0; i < text.length(); i++) {
            _str = _str + getFirstLetter(text.substring(i, i + 1));
        }
        return _str;
    }

    /**
     * 取得给定汉字的首字母,即声母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    public static String getFirstLetter(String chinese) {
        if (StringUtils.isBlank(chinese)) {
            return "";
        }
        chinese = StringUtil.transform(chinese, StringUtil.CHARSET_GB2312, StringUtil.CHARSET_ISO8859_1);

        if (chinese.length() > 1)
        {
            // 汉字区码
            int li_SectorCode = (int) chinese.charAt(0);
            // 汉字位码
            int li_PositionCode = (int) chinese.charAt(1);
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;
            // 汉字区位码
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode;
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {
                for (int i = 0; i < 23; i++) {
                    if (li_SecPosCode >= LI_SEC_POS_VALUE[i]
                            && li_SecPosCode < LI_SEC_POS_VALUE[i + 1]) {
                        chinese = LC_FIRST_LETTER[i];
                        break;
                    }
                }
            } else {
                chinese = StringUtil.transform(chinese, StringUtil.CHARSET_ISO8859_1, StringUtil.CHARSET_GB2312);
                chinese = chinese.substring(0, 1);
            }
        }
        return chinese;
    }

    public static boolean existChineseCharacter(String text) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * @Title: isAllChinese
     * @Description: 判断输入的字符串是否全是中文
     * @param text
     * @return: boolean
     * @throws:
     */
    public static boolean isAllChinese(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        char[] charArray = text.toCharArray();
        for (char ch : charArray) {
            if (!isChinese(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @Title: containsChinese
     * @Description: 判断输入的字符串是否包含中文
     * @param text
     * @return: boolean
     */
    public static boolean containsChinese(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        char[] charArray = text.toCharArray();
        for (char ch : charArray) {
            if (isChinese(ch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Title: isChinese
     * @Description: 判断一个字符是否是中文
     * @param ch
     * @return: boolean
     */
    public static boolean isChinese(char ch) {
        if (ch >= CHINESE_UNICODE_BEGIN && ch <= CHINESE_UNICODE_END) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 重庆 : chongqing
     * @Title: toPinyin
     * @Description: 把文本中的中文转换成拼音
     * @return: String
     * @throws:
     */
    public static String toPinyinFull(String text) {
        String result = null;
        try {
            result = PinyinHelper.convertToPinyinString(text, "", PinyinFormat.WITHOUT_TONE);
        } catch (Exception e) {
            log.warn("", e);
        }
        return result;
    }

    /**
     *
     * @Title: toPinyinInitial
     * @Description: 把文本中的中文转换成拼音首字母
     * @return: String
     * @throws:
     */
    public static String toPinyinInitial(String text) {
        String result = null;
        try {
            result = PinyinHelper.getShortPinyin(text);
        } catch (Exception e) {
            log.warn("", e);
        }
        return result;
    }

    public static String urlChineseEncode(String chinese) {
        try {
            String code = URLEncoder.encode(chinese, StringUtil.CHARSET_UTF_8);
            return code;
        } catch (Exception e) {
            log.warn("", e);
        }
        return null;
    }

}
