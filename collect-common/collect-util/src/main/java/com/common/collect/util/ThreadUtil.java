package com.common.collect.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/4/5.
 */
@Slf4j
public class ThreadUtil {

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("thread sleep exception!", e);
        }
    }

}
