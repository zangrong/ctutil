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
import lombok.Data;
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

    @Data
    public static class JpaCondition implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        // 字段名
        private Path<?> path;
        // 值
        private Object value;

    }


    public static void yearAndMonthRange(Path<Date> field, Integer year, Integer month, List<Predicate> list, CriteriaBuilder cb) {
        Date[] range = DateUtil.range(year, month);
        if (range[0] != null){
            list.add(cb.greaterThanOrEqualTo(field, range[0]));
        }
        if (range[1] != null){
            list.add(cb.lessThanOrEqualTo(field, range[1]));
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
     * 模糊查询, 调用者决定怎么带 %
     * @param path
     * @param text
     * @param list
     * @param cb
     */
    public static void like(Path<String> path, String text, List<Predicate> list, CriteriaBuilder cb) {
        if (StringUtils.isNotBlank(text)){
            list.add(cb.like(path, text));
        }
    }

    /**
     * 模糊查询,方法帮前后加%
     * @param path
     * @param text
     * @param list
     * @param cb
     */
    public static void likeEmbrace(Path<String> path, String text, List<Predicate> list, CriteriaBuilder cb) {
        if (StringUtils.isNotBlank(text)){
            list.add(cb.like(path, StringUtil.embraceLike(text)));
        }
    }

    /**
     * 多字段搜索, 搜索内容，调用者决定是否带%
     * @param pathes
     * @param text
     * @param list
     * @param cb
     */
    public static void like(Path<String>[] pathes, String text, List<Predicate> list, CriteriaBuilder cb) {
        if (StringUtils.isBlank(text)){
            return;
        }
        if (ArrayUtils.isEmpty(pathes) ){
            return;
        }

        List<Predicate> orList = new ArrayList<>();
        String txt = StringUtil.embraceLike(text);
        for (Path<String> path : pathes) {
            orList.add(cb.like(path, txt));
        }
        list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
    }

    /**
     * 文本内容，可能多个
     * @param path
     * @param texts
     * @param list
     * @param cb
     */
    public static void like(Path<String> path, List<String> texts, List<Predicate> list, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(texts) ){ // 标签
            List<Predicate> orList = new ArrayList<>();
            for (String text : texts) {
                orList.add(cb.like(path, text));
            }
            list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
        }
    }

    /**
     * 相等
     * @param path
     * @param condition
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void equal(Path<Y> path, Object condition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.equal(path, condition));
        }
    }

    /**
     * 相等，主要用于表关联查询
     * @param path1 表1字段
     * @param path2 表2字段
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void equal(Path<Y> path1, Path<Y> path2, List<Predicate> list, CriteriaBuilder cb) {
        list.add(cb.equal(path1, path2));
    }

    /**
     * 相等，主要用于表关联查询 eg:a.id = b.id or a.id = c.id
     * @param path1
     * @param path2
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void equalOr(Path<Y> path1, Path<Y> path2, List<Predicate> list, CriteriaBuilder cb){
        list.add(cb.or(cb.equal(path1, path2)));
    }

    /**
     * 或相等，等于指定的任意字段即可
     * @param pathes
     * @param condition
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void equalOr(Path<Y>[] pathes, Object condition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            List<Predicate> orList = new ArrayList<>();
            for (Path<Y> path : pathes) {
                orList.add(cb.equal(path, condition));
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
                .map(v ->  cb.equal(v.getPath(), v.getValue())).toArray(Predicate[]::new);
        list.add(cb.or(predicates));
    }

    /**
     * 等于
     * @param path
     * @param condition
     * @param defaultCondition
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void equal(Path<Y> path, Object condition, Object defaultCondition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.equal(path, condition));
        }else{
            list.add(cb.equal(path, defaultCondition));
        }
    }

    /**
     * 不等于
     * @param path
     * @param condition
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void notEqual(Path<Y> path, Object condition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.notEqual(path, condition));
        }
    }

    /**
     * 小于等于
     * @param path
     * @param condition
     * @param list
     * @param cb
     * @param <C>
     */
    public static  <C extends Comparable<? super C>> void lessThanOrEqualTo(Path<C> path, C condition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.lessThanOrEqualTo(path, condition));
        }
    }

    /**
     * 小于
     * @param path
     * @param condition
     * @param list
     * @param cb
     * @param <C>
     */
    public static <C extends Comparable<? super C>> void lessThan(Path<C> path, C condition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.lessThan(path, condition));
        }
    }

    /**
     * 大于
     * @param path
     * @param condition
     * @param list
     * @param cb
     * @param <C>
     */
    public static <C extends Comparable<? super C>> void greaterThan(Path<C> path, C condition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.greaterThan(path, condition));
        }
    }

    /**
     * 大于等于
     * @param path
     * @param condition
     * @param list
     * @param cb
     * @param <C>
     */
    public static <C extends Comparable<? super C>> void greaterThanOrEqualTo(Path<C> path, C condition, List<Predicate> list, CriteriaBuilder cb) {
        if (condition != null){
            list.add(cb.greaterThanOrEqualTo(path, condition));
        }
    }

    /**
     * 在范围内
     * @param path
     * @param begin
     * @param end
     * @param list
     * @param cb
     * @param <C>
     */
    public static <C extends Comparable<? super C>> void between(Path<C> path, C begin, C end, List<Predicate> list, CriteriaBuilder cb) {
        list.add(cb.between(path, begin, end));
    }

    /**
     * in 操作
     * @param path
     * @param condition
     * @param list
     * @param <Y>
     */
    public static <Y> void in(Path<Y> path, Collection<?> condition, List<Predicate> list) {
        if (CollectionUtils.isNotEmpty(condition)){
            list.add(path.in(condition));
        }
    }

    /**
     * in 操作
     * @param path
     * @param condition
     * @param list
     * @param <Y>
     */
    public static <Y> void notIn(Path<Y> path, Collection<?> condition, List<Predicate> list) {
        if (CollectionUtils.isNotEmpty(condition)){
            list.add(path.in(condition).not());
        }
    }

    /**
     * 或 in ，in 指定的任意字段即可
     * @param pathes
     * @param condition
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void inOr(Path<Y>[] pathes, Collection<?> condition, List<Predicate> list, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(condition)){
            List<Predicate> orList = new ArrayList<>();
            for (Path<Y> path : pathes) {
                orList.add(path.in(condition));
            }
            list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
        }
    }

    /**
     * in 操作, 如果condition 为空集合，则设置条件为false
     * @param path
     * @param condition
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void inWithEmptyFalse(Path<Y> path, Collection<?> condition, List<Predicate> list, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(condition)){
            list.add(path.in(condition));
        }else{
            setFalse(list, cb);
        }
    }

    /**
     * 或 in ，in 指定的任意字段即可,如果condition 为空集合，则设置条件为false
     * @param pathes
     * @param condition
     * @param list
     * @param cb
     * @param <Y>
     */
    public static <Y> void inOrWithEmptyFalse(Path<Y>[] pathes, Collection<?> condition, List<Predicate> list, CriteriaBuilder cb) {
        if (CollectionUtils.isNotEmpty(condition)){
            List<Predicate> orList = new ArrayList<>();
            for (Path<Y> path : pathes) {
                orList.add(path.in(condition));
            }
            list.add(cb.or(orList.stream().toArray(Predicate[]::new)));
        }else{
            setFalse(list, cb);
        }
    }

    /**
     * 指定字段不为空
     * @param path
     * @param list
     * @param <Y>
     */
    public static <Y> void isNotNull(Path<Y> path, List<Predicate> list) {
        list.add(path.isNotNull());
    }

    /**
     * 指定字段为空
     * @param path
     * @param list
     * @param <Y>
     */
    public static <Y> void isNull(Path<Y> path, List<Predicate> list) {
        list.add(path.isNull());
    }

    /**
     * 设置查询条件为true
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
