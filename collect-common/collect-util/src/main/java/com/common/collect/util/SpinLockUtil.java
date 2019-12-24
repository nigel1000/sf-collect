package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/12/24.
 */
// 自旋锁
@Slf4j
public class SpinLockUtil {

    private static boolean lockRetry(int timeout, Supplier<Boolean> supplier) {
        try {
            CtrlUtil.retry(2, () -> {
                boolean success = supplier.get();
                if (success) {
                    return true;
                } else {
                    try {
                        Thread.sleep(timeout);
                    } catch (Exception ex) {
                        log.info("", ex);
                    }
                    throw UnifiedException.gen("");
                }
            });
        } catch (UnifiedException ex) {
            return false;
        }
        return true;
    }

    @Slf4j
    public static class NotFairLock {
        private AtomicBoolean available = new AtomicBoolean();

        public boolean lock(int timeout) {
            return lockRetry(timeout, () -> available.compareAndSet(false, true));
        }

        public boolean unlock() {
            return available.compareAndSet(true, false);
        }
    }

    // TicketLock 虽然解决了公平性的问题，
    // 但是多处理器系统上，每个进程/线程占用的处理器都在读写同一个变量queueNum ，
    // 每次读写操作都必须在多个处理器缓存之间进行缓存同步，
    // 这会导致繁重的系统总线和内存的流量，大大降低系统整体的性能。
    @Slf4j
    public static class QueueFairLock {
        // 队列票据(当前排队号码)
        private AtomicInteger queueNum = new AtomicInteger();

        // 出队票据(当前需等待号码)
        private AtomicInteger dueueNum = new AtomicInteger();

        private ThreadLocal<Integer> ticketLocal = new ThreadLocal<>();

        public boolean lock(int timeout) {
            int currentTicketNum = dueueNum.incrementAndGet();
            // 获取锁的时候，将当前线程的排队号保存起来
            ticketLocal.set(currentTicketNum);
            return lockRetry(timeout, () -> currentTicketNum != queueNum.get());
        }

        // 释放锁：从排队缓冲池中取
        public boolean unLock() {
            Integer currentTicket = ticketLocal.get();
            queueNum.compareAndSet(currentTicket, currentTicket + 1);
            return true;
        }
    }


}

