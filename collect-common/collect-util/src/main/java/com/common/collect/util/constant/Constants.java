package com.common.collect.util.constant;

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
    public static final int ONE_SECOND = 1;
    public static final int ONE_MINUTE = 60 * ONE_SECOND;
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    public static final int ONE_DAY = 24 * ONE_HOUR;

}
