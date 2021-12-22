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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: ReflectionUtil
 * @Description: TODO
 * @date 2018/7/27 12:34
 * @author zangrong
 */
@Slf4j
public class ReflectionUtil {

    /**
     * 递归获取字段，如果当前类没有，就查找父类，直至object
     * 
     * @param targetClass
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> targetClass, String fieldName) {
        if (targetClass == null) {
            return null;
        }
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
        } catch (Exception e) {
        }
        if (field == null) {
            field = getField(targetClass.getSuperclass(), fieldName);
        }
        return field;
    }

    /**
     * 通过反射获取指定字段的值
     * 
     * @param target
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <T> String getFieldValueString(T target, String fieldName) {
        String value = null;
        if (target == null) {
            return value;
        }
        try {
            Field field = getField(target.getClass(), fieldName);
            field.setAccessible(true);
            Object valueObject = field.get(target);
            if (valueObject == null) {
                return value;
            }
            value = valueObject + "";
        } catch (Exception e) {
            log.warn("反射读取属性异常 targetType[{}] field[{}] valueType[{}]", target.getClass(), fieldName);
            log.warn("", e);
        }
        return value;
    }

    /**
     * 通过反射获取指定字段的值
     * 
     * @param target
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <T> T getFieldValue(Object target, String fieldName) {
        T value = null;
        if (target == null) {
            return value;
        }
        try {
            Field field = getField(target.getClass(), fieldName);
            field.setAccessible(true);
            Object valueObject = field.get(target);
            if (valueObject == null) {
                return value;
            }
            value = (T) valueObject;
        } catch (Exception e) {
            log.warn("反射读取属性异常 targetType[{}] field[{}] valueType[{}]", target.getClass(), fieldName);
            log.warn("", e);
        }
        return value;
    }

    /**
     * 通过反射向指定属性设置指定值
     * 
     * @param target
     * @param fieldName
     * @param value
     * @param <T>
     * @param <V>
     */
    public static <T, V> void setFieldValue(T target, String fieldName, V value) {
        try {
            Field field = getField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            log.warn("反射写入属性异常 targetType[{}] field[{}] value[{}]", target.getClass(), fieldName, value);
            log.warn("", e);
        }
    }

    /**
     * 之前计划能自动生成随机编码，因反射原因未能实现
     * 
     * @param clazz
     * @param dao
     * @param initial
     * @param length
     * @param <T>
     * @return
     * @throws NoSuchAlgorithmException
     */
    @Deprecated
    public static <T> String generateCode(Class<T> clazz, T dao, String initial, int length)
            throws NoSuchAlgorithmException {
        String code = null;
        try {
            Method method = clazz.getClass().getDeclaredMethod("countByCode", String.class);
            int count = 0;
            do{
                code = initial + RandomStringUtils.randomNumeric(length);
                count = (int) ReflectionUtils.invokeMethod(method, dao, code);
            }while (count > 0);
        } catch (NoSuchMethodException e) {
            log.warn("", e);
        }
        return code;
    }
}
