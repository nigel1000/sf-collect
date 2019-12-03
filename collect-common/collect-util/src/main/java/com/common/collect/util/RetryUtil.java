package com.common.collect.util;

import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/12/3.
 */
public class RetryUtil {

    public static <T> T retry(int time, Supplier<T> supplier) {
        if (time < 1) {
            throw new RuntimeException("次数不能少于 1 次");
        }

        T t;
        while (true) {
            try {
                t = supplier.get();
                break;
            } catch (Exception ex) {
                if (--time < 1) {
                    throw ex;
                }
            }
        }
        return t;
    }

}
