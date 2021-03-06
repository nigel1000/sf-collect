package com.common.collect.lib.util.spring;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.ThreadPoolUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Supplier;

/**
 * Created by hznijianfeng on 2018/8/15.
 * <p>
 * 事务细粒度控制，控制到某一业务段
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

    // 若在事务内则事务提交后异步执行
    // 若不在事务内则直接异步执行
    public void afterCommit(@NonNull String taskName, Runnable biz) {
        afterCommit(taskName, biz, false, false);
    }

    public void afterCommit(@NonNull String taskName, Runnable biz, boolean isThrowWhenNoTransaction,
                            boolean isUseDefaultPool) {
        boolean isDebug = log.isDebugEnabled();
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            if (isThrowWhenNoTransaction) {
                throw UnifiedException.gen("当前没有开启事务");
            } else {
                if (isDebug) {
                    log.debug("事务未开启，任务执行:{}", taskName);
                }
                if (isUseDefaultPool) {
                    ThreadPoolUtil.exec(biz);
                } else {
                    ThreadPoolUtil.exec(taskName, biz);
                }
                return;
            }
        }

        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();

        TransactionSynchronizationAdapter adapter = new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                if (isDebug) {
                    log.debug("事务提交，任务执行:{},transactionName:{}", taskName, transactionName);
                }
                // 异步任务自己负责异常的处理
                if (isUseDefaultPool) {
                    ThreadPoolUtil.exec(biz);
                } else {
                    ThreadPoolUtil.exec(taskName, biz);
                }
            }

            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    if (isDebug) {
                        log.debug("事务回滚,任务未执行:{},transactionName:{}", taskName, transactionName);
                    }
                }
            }
        };
        if (isDebug) {
            log.debug("事务提交,任务注册:{},transactionName:{}", taskName, transactionName);
        }
        TransactionSynchronizationManager.registerSynchronization(adapter);
    }

}
