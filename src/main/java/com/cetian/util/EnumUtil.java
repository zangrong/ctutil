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
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 *
 *
 *
 * @author zangrong
 * @Date 2020-01-20 06:02
 */
@Slf4j
public class EnumUtil {

    private static String fieldName = "value";

    private EnumUtil(){}

    public static <E extends Enum<E>> List<Integer> toValueList(List<E> enumList){
        if (enumList == null){
            return Collections.emptyList();
        }
        
        return enumList.stream()
                        .map(e -> Integer.parseInt(ReflectionUtil.getFieldValueString(e, fieldName)))
                        .collect(Collectors.toList());
    }

    public static <E extends Enum<E>> List<E> toEnumList(List<Integer> valueList, Class<E> enumClass){
        if (valueList == null){
            return Collections.emptyList();
        }
        List<E> enumTypes = EnumUtils.getEnumList(enumClass);
        return valueList.stream().map(v -> toEnum(v, enumTypes)).collect(Collectors.toList());
    }

    /**
     * 把一个int型 value 转换为指定 enum 枚举
     * @param value
     * @param enumClass enum枚举类型
     * @param <E>
     * @return
     */
    public static <E extends Enum<E>> E toEnum(int value, Class<E> enumClass){
        List<E> enumTypes = EnumUtils.getEnumList(enumClass);
        E e = toEnum(value, enumTypes);
        if (e == null){
            log.warn("value[{}] to Enum[{}] 失败", value, enumClass.getName());
        }
        return e;
    }

    public static <E extends Enum<E>> E toEnum(int value, List<E> enumTypes){
        for (E enumType : enumTypes) {
            String enumTypeValue = ReflectionUtil.getFieldValueString(enumType, fieldName);
            if (StringUtils.equals(value + "", enumTypeValue)){
                return enumType;
            }
        }
        log.warn("value[{}] to Enum[{}] 失败", value);
        return null;
    }

    /**
     * 枚举类型转换，从枚举类型A 转 枚举类型B
     * 根据枚举的value属性比较
     * @param src 原枚举类型实例
     * @param destClass 目标枚举类型
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S extends Enum<S>, D extends Enum<D>> D convertEnum(S src, Class<D> destClass){
        D dest = null;
        if (src == null) {
            return dest;
        }
        try{
            int value1 = Integer.parseInt(ReflectionUtil.getFieldValueString(src, fieldName));
            List<D> destList = EnumUtils.getEnumList(destClass);
            for (D d : destList) {
                Integer value2 = Integer.parseInt(ReflectionUtil.getFieldValueString(d, fieldName));
                if ((int)value1 == (int)value2) {
                    dest = d;
                    break;
                }
            }
            if (dest == null) {
                log.warn("src[{}] destClass[{}]", src, destClass);
                dest = destList.get(0);
            }
        }catch(Exception e){
            log.warn("src[{}] destClass[{}]", src, destClass);
            log.warn("", e);
        }
        return dest;
    }

    public static <S extends Enum<S>, D extends Enum<D>> String convertToDestName(S src, Class<D> destClass){
        D dest = convertEnum(src, destClass);
        if (dest == null) {
            return "";
        }
        return ReflectionUtil.getFieldValueString(dest, "name");
    }

    public static <E extends Enum<E>> E convertFromFieldValue(String fieldName, String value, Class<E> enumClass){
        List<E> enumList = EnumUtils.getEnumList(enumClass);
        for (E e : enumList) {
            String eValue =  ReflectionUtil.getFieldValueString(e, fieldName);
            if (StringUtils.equals(value, eValue)){
                return e;
            }
        }
        return null;
    }

}
