package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/4/5.
 */
@Slf4j
public class ThreadUtil {

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("thread sleep exception!", e);
        }
    }

    public static void sleepThrow(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw UnifiedException.gen("Thread sleep 失败 ", e);
        }
    }

}
