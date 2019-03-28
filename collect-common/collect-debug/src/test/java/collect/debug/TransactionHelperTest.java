package collect.debug;

import com.common.collect.container.TransactionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by hznijianfeng on 2019/3/28.
 */

@Slf4j
public class TransactionHelperTest {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
        TransactionHelper transactionHelper = (TransactionHelper) applicationContext.getBean("transactionHelper");

        transactionHelper.aroundBiz(() -> {
            log.info("outer transaction start");
            transactionHelper.afterCommit("taskName1", () -> {
                log.info("taskName1 start");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("taskName1 end");
            });
            transactionHelper.afterCommit("taskName2", () -> {
                log.info("taskName2 start");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("taskName2 end");
            });
            log.info("outer transaction end");
        });

        Thread.sleep(3000);
        System.exit(-1);
    }

}
