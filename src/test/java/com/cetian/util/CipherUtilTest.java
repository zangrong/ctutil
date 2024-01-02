/**
 * Cetian Techs Ltd., Co. 2021
 *
 * @ClassName CipherUtilTest
 * @Author Administrator
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 *@ClassName CipherUtilTest
 *@Author Administrator
 *@Date 2023/12/31 9:36
 *@Description TODO
 */
@Slf4j
public class CipherUtilTest {

    @Test
    void desTest() {
        String key = "853F31351E51CD9C5222C28E408BF2A3";
        log.info("key length:{}", key.length());
        String plainText = "今天天气不错，哈哈哈";
        String ciperText = CipherUtil.encrypt3Des(plainText, key);
        log.info("encrypt:{}", ciperText);
        byte[] bytes = CipherUtil.decrypt3Des(ciperText, key);
        log.info("decrypt:{}", new String(bytes));
    }

}
