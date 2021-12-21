/**
 * @Copyright: 2018 jiayun.com Inc. All rights reserved.
 * @Title: EnumUtil.java
 * @date 2018/7/3 14:53
 * @version V1.0
 * @author zangrong
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zangrong
 * @ClassName: EnumUtil
 * @Description: TODO
 * @date 2018/7/3 14:53
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

}
