package com.common.collect.debug.design.mode.create;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/29.
 *
 * 确保某个类只有一个实例，而且自行实例化并向整个系统提供这个实例
 */
public class Singleton {

    public static void main(String[] args) {
        EagerMode.getInstance();
        LazyMode.getInstanceBySync();
        LazyMode.getInstanceByStatic();
    }

}

// 饿汉加载
@Slf4j
class EagerMode {

    private EagerMode() {
        log.info("init EagerMode");
    }

    private static final EagerMode eagerModel = new EagerMode();

    public static EagerMode getInstance() {
        return eagerModel;
    }

}

// 懒汉加载
@Slf4j
class LazyMode {

    private LazyMode() {
        log.info("init LazyMode");
    }

    /////////  使用 synchronized 保证
    private static LazyMode lazyMode = null;

    public synchronized static LazyMode getInstanceBySync() {
        if (lazyMode == null) {
            lazyMode = new LazyMode();
            return lazyMode;
        }
        return lazyMode;
    }
    /////////

    /////////  使用 static 保证
    private static class SingletonInstance {
        private static final LazyMode lazyMode = new LazyMode();
    }

    public synchronized static LazyMode getInstanceByStatic() {
        return SingletonInstance.lazyMode;
    }
    /////////


}
