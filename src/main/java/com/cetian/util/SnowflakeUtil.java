package com.cetian.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.Random;

public class SnowflakeUtil implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 机器ID
     */
    private final long workerId;
    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间，默认2017-01-01
     */
    private final long epoch = 1483200000000L;
    /**
     * 机器id所占的位数（源设计为5位，这里取消dataCenterId，采用10位，既1024台）
     */
    private final long workerIdBits = 10L;
    /**
     * 机器ID最大值: 1023 (从0开始)
     */
    private final long maxWorkerId = -1L ^ -1L << workerIdBits;
    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;
    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)，12位
     */
    private final long sequenceMask = -1L ^ -1L << sequenceBits;
    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;
    /**
     * 时间戳向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    /**
     * 并发控制，毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;
    /**
     * 100,000
     */
    private final int HUNDRED_K = 100_000;
    /**
     * sequence随机种子（兼容低并发下，sequence均为0的情况）
     */
    private static final Random RANDOM = new Random();

    /**
     * @param workerId 机器Id
     */
    private SnowflakeUtil(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            String message = String.format("worker Id can't be greater than %d or less than 0", maxWorkerId);
            throw new IllegalArgumentException(message);
        }
        this.workerId = workerId;
    }

    /**
     * Snowflake Builder
     * 
     * @return Snowflake Instance
     */
    public static SnowflakeUtil create() {
        return new SnowflakeUtil(Integer.parseInt(RandomStringUtils.randomNumeric(3)));
    }

    /**
     * Snowflake Builder
     * 
     * @param workerId 机器Id
     * @return Snowflake Instance
     */
    public static SnowflakeUtil create(long workerId) {
        return new SnowflakeUtil(workerId);
    }

    /**
     * 批量获取ID
     * 
     * @param size 获取大小，最多10万个
     * @return SnowflakeId
     */
    public long[] nextId(int size) {
        if (size <= 0 || size > HUNDRED_K) {
            String message = String.format("Size can't be greater than %d or less than 0", HUNDRED_K);
            throw new IllegalArgumentException(message);
        }
        long[] ids = new long[size];
        for (int i = 0; i < size; i++) {
            ids[i] = nextId();
        }
        return ids;
    }

    /**
     * 获得ID
     * 
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环);
        if (lastTimestamp == timestamp) {
            // 对新的timestamp，sequence从0开始
            sequence = sequence + 1 & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = RANDOM.nextInt(100);
        }
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            String message = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds.",
                    (lastTimestamp - timestamp));
            throw new RuntimeException(message);
        }
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的ID
        return timestamp - epoch << timestampLeftShift | workerId << workerIdShift | sequence;
    }

    public String nextStrId() {
        return nextId() + "";
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     * 
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 下一个毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获得系统当前毫秒数
     * 
     * @return 获得系统当前毫秒数
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
    
}
