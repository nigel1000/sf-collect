package lib.util;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.ExceptionUtil;
import com.common.collect.lib.util.spring.TransactionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by hznijianfeng on 2019/3/28.
 */

@Slf4j
public class TransactionHelperTest {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");
        TransactionHelper transactionHelper = (TransactionHelper) applicationContext.getBean("transactionHelper");

        transactionHelper.aroundBiz(() -> {
            log.info("commit transaction start");
            transactionHelper.afterCommit("taskName1", () -> {
                log.info("taskName1 start");
                log.info("taskName1 end");
            });
            transactionHelper.afterCommit("taskName2", () -> {
                log.info("taskName2 start");
                log.info("taskName2 end");
            });
            log.info("commit transaction end");
        });

        ExceptionUtil.eatException(() -> Thread.sleep(1000), null);

        try {
            transactionHelper.aroundBiz(() -> {
                log.info("rollback transaction start");
                transactionHelper.afterCommit("taskName3", () -> {
                    log.info("taskName3 start");
                    log.info("taskName3 end");
                });
                transactionHelper.afterCommit("taskName4", () -> {
                    log.info("taskName4 start");
                    log.info("taskName4 end");
                });
                log.info("rollback transaction end");
                throw UnifiedException.gen("回滚");
            });
        } catch (UnifiedException ex) {
            if (!ex.getMessage().equals("回滚")) {
                throw ex;
            }
        }

        try {
            transactionHelper.afterCommit("taskName5", () -> {
                log.info("taskName5 start");
                log.info("taskName5 end");
            }, true, false);
        } catch (UnifiedException ex) {
            log.error("taskName5 exception", ex);
        }

        transactionHelper.afterCommit("taskName5", () -> {
            log.info("taskName5 start");
            log.info("taskName5 end");
        }, false, true);

        ExceptionUtil.eatException(() -> Thread.sleep(3000), null);

        System.exit(-1);
    }

}
