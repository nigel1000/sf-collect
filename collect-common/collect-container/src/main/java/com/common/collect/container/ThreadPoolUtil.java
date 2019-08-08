package com.common.collect.container;

import com.common.collect.container.trace.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hznijianfeng on 2018/8/15.
 */

@Slf4j
public class ThreadPoolUtil {

    public static class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            log.error("线程名:{},异常信息:{}", t.getName(), stringWriter.toString());
        }
    }

    // @See Executors.DefaultThreadFactory
    public static class LogThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        LogThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "thread-pool-util-" + poolNumber.getAndIncrement() + "-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setUncaughtExceptionHandler(new LogUncaughtExceptionHandler());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private final static ExecutorService executorService;

    static {
        final int CORE_SIZE = Runtime.getRuntime().availableProcessors();// 线程池最少线程数
        final int MAX_SIZE = CORE_SIZE * 2;// 最大线程数
        final int KEEP_ALIVE_TIME = 60;// 最长等待时间
        final int QUEUE_SIZE = 1000;// 最大等待数
        executorService = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_SIZE), new LogThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                long start = System.currentTimeMillis();
                log.info("线程池关闭开始,time:{}", start);
                long i = 1;
                while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    log.info("线程池还有任务在执行 {}", i++);
                }
                log.info("线程池关闭总消耗 {} ms", System.currentTimeMillis() - start);
            } catch (Exception ex) {
                log.error("线程池关闭过程抛出异常", ex);
            }
        }));
    }

    public static void exec(Runnable command) {
        executorService.execute(TraceIdUtil.wrap(command));
    }

    public static <T> Future<T> submit(Callable<T> command) {
        return executorService.submit(TraceIdUtil.wrap(command));
    }

}
