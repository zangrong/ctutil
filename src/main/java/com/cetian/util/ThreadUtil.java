/**
 * jiayun technologies Ltd., Co. 2019
 *
 * @ClassName ThreadUtil
 * @Author zangrong
 */
package com.cetian.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *@ClassName ThreadUtil
 *@Author zangrong
 *@Date 2020-01-20 06:02
 *@Description 线程工具类
 */
public class ThreadUtil {

    private static Logger log = LoggerFactory.getLogger(com.jiayun.util.ThreadUtil.class);

    public static void sleep(int seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            log.warn("", e);
        }
    }

    public static void sleep(long miniseconds){
        try {
            Thread.sleep(miniseconds);
        } catch (InterruptedException e) {
            log.warn("", e);
        }
    }

}
