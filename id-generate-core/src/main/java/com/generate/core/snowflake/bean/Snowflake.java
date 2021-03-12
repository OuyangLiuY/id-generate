package com.generate.core.snowflake.bean;


import lombok.Data;

import java.util.Random;

/**
 * 基于雪花算法的ID生成器
 * 64bit固定长度，具体分段如下:
 * +--------+-----------------------------------------------+------------+---------------+
 * | 1bit   |               41bit                           |  10bit     |     12bit     |
 * +--------+-----------------------------------------------+------------+---------------+
 * |   0    |00000000 00000000 00000000 00000000 00000000 0 |00000 00000 |00000 00000 00 |
 * +--------+-----------------------------------------------+------------+---------------+
 * | no used|              TimeStamp                        | WorkId   |  SequenceId   |
 * +------+---+---------------------------------------------+------------+---------------+
 *
 */
@Data
public class Snowflake {
    /**
     * 起始时间戳
     */
    private long epoch;
    /**
     * 机器节点占10位
     */
    private final long WorkIdBits = 10L;
    /**
     * 最大能够分配的WorkId =1023
     */
    private final long maxWorkId = ~(-1L << WorkIdBits);
    /**
     * 12 bit 序列号
     */
    private final long sequenceBits = 12L;
    /**
     * 机器节点id 偏移
     */
    private final long WorkIdShift = sequenceBits;
    /**
     * 时间戳的偏移
     */
    private final long timestampLeftShift = sequenceBits + WorkIdBits;
    /**
     * 序列号掩码
     */
    private final long sequenceMask = ~(-1L << sequenceBits);
    /**
     * 机器节点ID
     */
    private long WorkId;
    /**
     * 并发控制，毫秒内序列(0~4095)
     */
    private volatile long sequence = 0L;
    /**
     * 上一次生成ID的时间戳
     */
    private long lastTimeStamp = -1L;
    /**
     * sequence随机种子
     */
    private static final Random RANDOM = new Random();
}
