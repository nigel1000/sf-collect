package com.common.collect.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by hznijianfeng on 2018/8/15. 事务细粒度控制，控制到某一业务段
 */

@Service
@Slf4j
public class TransactionHelper {

    @Transactional
    public void aroundBiz(Runnable biz) {
        biz.run();
    }

    // 提供返回
    @Transactional
    public <T> T aroundBiz(Supplier<T> biz) {
        return biz.get();
    }

    public void afterCommit(Runnable biz) {
        AfterTransactionCommitExecutor.SingletonInstance.INSTANCE.executeAfterCommit(biz);
    }

    @Slf4j
    private static class AfterTransactionCommitExecutor extends TransactionSynchronizationAdapter {
        private AfterTransactionCommitExecutor() {
        }

        private static class SingletonInstance {
            private static final AfterTransactionCommitExecutor INSTANCE = new AfterTransactionCommitExecutor();
        }

        private final ThreadLocal<List<Runnable>> runnableTasks = new ThreadLocal<>();

        private void executeAfterCommit(Runnable biz) {
            if (biz == null) {
                return;
            }
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                log.info("transaction synchronization is NOT ACTIVE. Executing right now runnable!");
                ThreadPoolUtil.exec(biz);
                return;
            }
            List<Runnable> threadRunnable = runnableTasks.get();
            if (threadRunnable == null) {
                threadRunnable = new ArrayList<>();
                runnableTasks.set(threadRunnable);
                TransactionSynchronizationManager.registerSynchronization(this);
            }
            threadRunnable.add(biz);
        }

        @Override
        public void afterCommit() {
            List<Runnable> threadRunnable = runnableTasks.get();
            log.info("transaction successfully committed, executing {} runnables", threadRunnable.size());
            for (int i = 0; i < threadRunnable.size(); i++) {
                Runnable biz = threadRunnable.get(i);
                log.info("executing runnable:{}", i);
                try {
                    ThreadPoolUtil.exec(biz);
                } catch (Exception ex) {
                    log.error("Failed to execute runnable:{}, Exception :", i, ex);
                }
            }
        }

        @Override
        public void afterCompletion(int status) {
            log.info("transaction completed with status {}",
                    status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
            runnableTasks.remove();
        }
    }
}
