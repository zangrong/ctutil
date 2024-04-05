/**
 * Cetian Techs Ltd., Co. 2021
 *
 * @ClassName DateUtilTest
 * @Author Administrator
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

/**
 *@ClassName DateUtilTest
 *@Author Administrator
 *@Date 2023-12-5 11:22
 *@Description TODO
 */
@Slf4j
public class DateUtilTest {

    @Test
    void week() {
        String[] week = DateUtil.getWeekByDate("2023-12-05");
        log.info("week:{}", week);
        Integer seconds = DateUtil.durationToSeconds("01:00:00");
        log.info("seconds:{}", seconds);
    }

}
