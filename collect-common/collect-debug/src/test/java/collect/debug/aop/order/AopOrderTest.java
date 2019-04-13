package collect.debug.aop.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nijianfeng on 2019/3/23.
 */

@Slf4j
public class AopOrderTest {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");

        log.info("--------------------- order ---------------------------");
        AopOrder aopOrder = applicationContext.getBean(AopOrder.class);
        log.info("--------------------- ret ---------------------------");
        aopOrder.normal();
        log.info("--------------------- excp ---------------------------");
        aopOrder.exception();
        log.info("--------------------- rollback ---------------------------");
        aopOrder.rollback();

    }

}
