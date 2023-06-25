/**
 * jiayun technologies Ltd., Co. 2019
 *
 * @ClassName JpaUtil
 * @Author zangrong
 */
package com.cetian.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 *@ClassName JpaUtil
 *@Author zangrong
 *@Date 2019-09-07 16:44
 *@Description TODO
 */
public class JpaUtil {

    public static class JpaCondition implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        // 字段名
        private String fieldName;
        // 值
        private Object value;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }


    public static void yearAndMonthRange(Integer year, Integer month, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        Date[] range = DateUtil.range(year, month);
        if (range[0] != null){
            list.add(cb.greaterThanOrEqualTo(from.get(fieldName), range[0]));
        }
        if (range[1] != null){
            list.add(cb.lessThanOrEqualTo(from.get(fieldName), range[1]));
        }
    }

    /**
     * 最后汇总所有查询条件，如果条件为空，则返回空
     * 所有查询条件为 and 关系
     * @param list
     * @param cb
     * @return
     */
    public static Predicate listToPredicate(List<Predicate> list, CriteriaBuilder cb){
        return listToPredicate(list, cb, false);
    }

    /**
     * 最后汇总所有查询条件
     * 所有查询条件为 and 关系
     * @param list
     * @param cb
     * @param def true 返回所有数据， false 返回空
     * @return
     */
    public static Predicate listToPredicate(List<Predicate> list, CriteriaBuilder cb, boolean def){
        if (CollectionUtils.isEmpty(list)) {
            return cb.isTrue(cb.literal(def));
        } else {
            return cb.and(list.stream().toArray(Predicate[]::new));
        }
    }

    /**
     * 最后汇总所有查询条件，如果条件为空，则返回空
     * 所有查询条件为 or 关系
     * @param list
     * @param cb
     * @return
     */
    public static Predicate orListToPredicate(List<Predicate> list, CriteriaBuilder cb){
        return orListToPredicate(list, cb, false);
    }

    /**
     * 最后汇总所有查询条件
     * 所有查询条件为 or 关系
     * @param list
     * @param cb
     * @param def true 返回所有数据， false 返回空
     * @return
     */
    public static Predicate orListToPredicate(List<Predicate> list, CriteriaBuilder cb, boolean def){
        if (CollectionUtils.isEmpty(list)) {
            return cb.isTrue(cb.literal(def));
        } else {
            return cb.or(list.stream().toArray(Predicate[]::new));
        }
    }

    /**
     * 模糊查询
     * @param text 调用者决定怎么带 %
     * @param fieldName 字段名
     * @param list
     * @param from
     * @param cb
     */
    public static void like(String text, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (StringUtils.isNotBlank(text)){
            list.add(cb.like(from.get(fieldName), text));
        }
    }

    /**
     * 模糊查询
     * @param text 方法帮前后加%
     * @param fieldName 字段名
     * @param list
     * @param from
     * @param cb
     */
    public static void likeEmbrace(String text, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (StringUtils.isNotBlank(text)){
            list.add(cb.like(from.get(fieldName), StringUtil.embraceLike(text)));
        }
    }

    /**
     * 多字段搜索
     * @param text 搜索内容，不带 %
     * @param fieldNames 字段数组，可能是多个字段
     * @param list
     * @param from
     * @param cb
     */
    public static void like(String text, String[] fieldNames, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (StringUtils.isBlank(text)){
            return;
        }
        if (ArrayUtils.isEmpty(fieldNames) ){
            return;
        }

        List<Predicate> orList = new ArrayList<>();
        String txt = StringUtil.embraceLike(text);
        for (String fieldName : fieldNames) {
            orList.add(cb.like(from.get(fieldName), txt));
        }
        list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
    }

    /**
     *
     * @param texts 文本内容，可能多个
     * @param fieldName 字段名
     * @param list
     * @param from
     * @param cb
     */
    public static void like(List<String> texts, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(texts) ){ // 标签
            List<Predicate> orList = new ArrayList<>();
            for (String text : texts) {
                orList.add(cb.like(from.get(fieldName), text));
            }
            list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
        }
    }

    /**
     * 相等
     * @param condition 参数条件
     * @param fieldName 字段名
     * @param list
     * @param from
     * @param cb
     */
    public static void equal(Object condition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.equal(from.get(fieldName), condition));
        }
    }

    /**
     * 相等，主要用于表关联查询
     * @param condition1 表1条件
     * @param condition2 表2条件
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void equal(Path<Y> condition1, Path<Y> condition2, List<Predicate> list, CriteriaBuilder cb) {
        list.add(cb.equal(condition1, condition2));
    }

    /**
     * 相等，主要用于表关联查询 eg:a.id = b.id or a.id = c.id
     * @param condition1
     * @param condition2
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void equalOr(Path<Y> condition1, Path<Y> condition2, List<Predicate> list, CriteriaBuilder cb){
        list.add(cb.or(cb.equal(condition1, condition2)));
    }

    /**
     * 或相等，等于指定的任意字段即可
     * @param condition 条件
     * @param fieldNames 字段集合，等于其中任意一个字段值即可
     * @param list
     * @param from
     * @param cb
     */
    public static void equalOr(Object condition, String[] fieldNames, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            List<Predicate> orList = new ArrayList<>();
            for (String fieldName : fieldNames) {
                orList.add(cb.equal(from.get(fieldName), condition));
            }
            list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
        }
    }

    /**
     * 传入的条件集合，满足其中一条即可
     * @param conditions 条件列表
     * @param list
     * @param from
     * @param cb
     *
     */
    public static void or(List<JpaCondition> conditions, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (CollectionUtils.isEmpty(conditions)){
            return;
        }
        Predicate[] predicates = conditions.stream()
                .map(v ->  cb.equal(from.get(v.getFieldName()), v.getValue())).toArray(Predicate[]::new);
        list.add(cb.or(predicates));
    }

    /**
     * 等于
     * @param condition 参数条件
     * @param defaultCondition 默认条件
     * @param fieldName 字段名
     * @param list
     * @param from
     * @param cb
     */
    public static void equal(Object condition, Object defaultCondition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.equal(from.get(fieldName), condition));
        }else{
            list.add(cb.equal(from.get(fieldName), defaultCondition));
        }
    }

    /**
     * 不等于
     * @param condition
     * @param fieldName
     * @param list
     * @param from
     * @param cb
     */
    public static void notEqual(Object condition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.notEqual(from.get(fieldName), condition));
        }
    }

    /**
     * 小于等于
     * @param condition
     * @param fieldName
     * @param list
     * @param from
     * @param cb
     * @param <Y>
     */
    public static <Y extends Comparable<? super Y>> void lessThanOrEqualTo(Y condition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.lessThanOrEqualTo(from.get(fieldName), condition));
        }
    }

    /**
     * 小于
     * @param condition
     * @param fieldName
     * @param list
     * @param from
     * @param cb
     * @param <Y>
     */
    public static <Y extends Comparable<? super Y>> void lessThan(Y condition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.lessThan(from.get(fieldName), condition));
        }
    }

    /**
     * 大于
     * @param condition
     * @param fieldName
     * @param list
     * @param from
     * @param cb
     * @param <Y>
     */
    public static <Y extends Comparable<? super Y>> void greaterThan(Y condition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.greaterThan(from.get(fieldName), condition));
        }
    }

    /**
     * 大于等于
     * @param condition
     * @param fieldName
     * @param list
     * @param from
     * @param cb
     * @param <Y>
     */
    public static <Y extends Comparable<? super Y>> void greaterThanOrEqualTo(Y condition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.greaterThanOrEqualTo(from.get(fieldName), condition));
        }
    }

    /**
     * 在范围内
     * @param begin 开始 包含
     * @param end 结束 包含
     * @param fieldName 字段名
     * @param list
     * @param from
     * @param cb
     * @param <Y> 可比较的类型
     */
    public static <Y extends Comparable<? super Y>> void between(Y begin, Y end, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        list.add(cb.between(from.get(fieldName), begin, end));
    }

    /**
     * in 操作
     * @param condition 数据集合
     * @param fieldName 字段名
     * @param list
     * @param from
     */
    public static void in(Collection<?> condition, String fieldName, List<Predicate> list, From<?, ?> from) {
        if (CollectionUtils.isNotEmpty(condition)){
            list.add(from.get(fieldName).in(condition));
        }
    }

    /**
     * not in 操作
     * @param condition 数据集合
     * @param fieldName 字段名
     * @param list
     * @param from
     */
    public static void notIn(Collection<?> condition, String fieldName, List<Predicate> list, From<?, ?> from) {
        if (CollectionUtils.isNotEmpty(condition)){
            list.add(from.get(fieldName).in(condition).not());
        }
    }

    /**
     * 或 in ，in 指定的任意字段即可
     * @param condition 条件
     * @param fieldNames 字段集合，等于其中任意一个字段值即可
     * @param list
     * @param from
     * @param cb
     */
    public static void inOr(Collection<?> condition, String[] fieldNames, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(condition)){
            List<Predicate> orList = new ArrayList<>();
            for (String fieldName : fieldNames) {
                orList.add(from.get(fieldName).in(condition));
            }
            list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
        }
    }

    /**
     * in 操作
     * @param condition 如果condition 为空集合，则设置条件为false
     * @param fieldName
     * @param list
     * @param from
     * @param cb
     */
    public static void inWithEmptyFalse(Collection<?> condition, String fieldName, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(condition)){
            list.add(from.get(fieldName).in(condition));
        }else{
            setFalse(list, cb);
        }
    }

    /**
     * 或 in ，in 指定的任意字段即可
     * @param condition 条件，如果condition 为空集合，则设置条件为false
     * @param fieldNames 字段集合，等于其中任意一个字段值即可
     * @param list
     * @param from
     * @param cb
     */
    public static void inOrWithEmptyFalse(Collection<?> condition, String[] fieldNames, List<Predicate> list, From<?, ?> from, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(condition)){
            List<Predicate> orList = new ArrayList<>();
            for (String fieldName : fieldNames) {
                orList.add(from.get(fieldName).in(condition));
            }
            list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
        }else{
            setFalse(list, cb);
        }
    }

    /**
     * 指定字段不为空
     * @param fieldName
     * @param list
     * @param from
     */
    public static void isNotNull(String fieldName, List<Predicate> list, From<?, ?> from) {
        list.add(from.get(fieldName).isNotNull());
    }

    /**
     * 指定字段为空
     * @param fieldName 字段名称
     * @param list
     * @param from
     */
    public static void isNull(String fieldName, List<Predicate> list, From<?, ?> from) {
        list.add(from.get(fieldName).isNull());
    }

    /**
     * 设置查询条件为true
     *
     * @param list
     * @param cb
     */
    public static void setTrue(List<Predicate> list, CriteriaBuilder cb) {
        list.add(cb.isTrue(cb.literal(true)));
    }

    /**
     * 设置查询条件为false,
     * 列表严格执行where条件
     * @param list
     * @param cb
     */
    public static void setFalse(List<Predicate> list, CriteriaBuilder cb) {
        list.add(cb.isTrue(cb.literal(false)));
    }

}
