package com.common.collect.lib.api;

import java.util.concurrent.TimeUnit;

/**
 * Created by hznijianfeng on 2018/11/16.
 */

public class Constants {

    /**
     * 中文字符正则
     */
    public static final String CHINESE_REG_EX = "[\\u4e00-\\u9fa5]";

    /**
     * 16 进制字符
     */
    public static final char[] HEX_LOOKUP_STRING = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
            'd', 'e', 'f'};

    /**
     * 时间
     */
    public static final long SECOND_MILLIS = TimeUnit.SECONDS.toMillis(1);
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;

}