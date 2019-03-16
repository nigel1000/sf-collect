package com.common.collect.util;

import java.util.function.Supplier;

/**
 * Created by hznijianfeng on 2018/8/17.
 */

public class NullUtil {

    private static final String module = "空校验工具";

    // 加入抛出空指针 则降级处理
    public static <T> T validDefault(Supplier<T> supplier, T def) {
        try {
            return supplier.get();
        } catch (NullPointerException ex) {
            return def;
        }
    }

    // 校验是否为空 为空降级处理
    public static <T> T validDefault(T obj, T def) {
        if (obj == null) {
            return def;
        }
        return obj;
    }

}
