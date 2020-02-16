package com.common.collect.lib.util;

import com.common.collect.lib.api.excps.UnifiedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hznijianfeng on 2018/8/15.
 */

@Slf4j
public class ThreadPoolUtil {

    private final static Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();

    public static ExecutorService obtainExecutorService(@NonNull String poolName) {

        return executorServiceMap.computeIfAbsent(poolName, (key) -> {
            final int CORE_SIZE = Runtime.getRuntime().availableProcessors();// 线程池最少线程数
            final int MAX_SIZE = CORE_SIZE * 2;// 最大线程数
            final int KEEP_ALIVE_TIME = 60;// 最长等待时间
            final int QUEUE_SIZE = 1000;// 最大等待数
            ExecutorService executorService = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<>(QUEUE_SIZE), new LogThreadFactory(key),
                    new ThreadPoolExecutor.AbortPolicy());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                executorService.shutdown();
                try {
                    long start = System.currentTimeMillis();
                    log.info("{} 线程池关闭开始,time:{}", key, start);
                    long i = 1;
                    while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                        log.info("{} 线程池还有任务在执行 {}", key, i++);
                    }
                    log.info("{} 线程池关闭总消耗 {} ms", key, System.currentTimeMillis() - start);
                } catch (Exception ex) {
                    log.error("{} 线程池关闭过程抛出异常", key, ex);
                }
            }));
            return executorService;
        });
    }

    public static void exec(Runnable command) {
        exec("default", command);
    }

    public static <T> Future<T> submit(@NonNull Callable<T> command) {
        return submit("default", command);
    }

    public static <T> List<T> submit(@NonNull List<Callable<T>> commands) {
        return submit("default", commands);
    }

    public static void exec(@NonNull String poolName, @NonNull Runnable command) {
        obtainExecutorService(poolName).execute(TraceIdUtil.wrap(command));
    }

    public static <T> Future<T> submit(@NonNull String poolName, @NonNull Callable<T> command) {
        return obtainExecutorService(poolName).submit(TraceIdUtil.wrap(command));
    }

    public static <T> List<T> submit(@NonNull String poolName, @NonNull List<Callable<T>> commands) {
        List<Future<T>> futures = new ArrayList<>();
        for (Callable<T> command : commands) {
            futures.add(submit(poolName, command));
        }
        List<T> t = new ArrayList<>();
        for (Future<T> future : futures) {
            try {
                t.add(future.get(5, TimeUnit.SECONDS));
            } catch (Exception ex) {
                throw UnifiedException.gen("callable 操作失败", ex);
            }
        }
        return t;
    }

    public static class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e) {
            log.error("线程名:{},异常信息:{}", t.getName(), ExceptionUtil.getStackTraceAsString(e));
        }
    }

    // @See Executors.DefaultThreadFactory
    public static class LogThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        LogThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-thread-pool-util-" + poolNumber.getAndIncrement() + "-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
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

}
