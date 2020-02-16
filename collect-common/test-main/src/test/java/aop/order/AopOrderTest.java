package aop.order;

import com.common.collect.lib.api.excps.UnifiedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by nijianfeng on 2019/3/23.
 */

@Slf4j
public class AopOrderTest {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");

        AopOrder aopOrder = applicationContext.getBean(AopOrder.class);

        log.info("--------------------- ret ---------------------------");
        aopOrder.normal();

        log.info("--------------------- exception1 ---------------------------");
        aopOrder.exception1();

        log.info("--------------------- exception2 ---------------------------");
        aopOrder.exception2();

    }

    @Slf4j
    @Component
    public static class AopOrder {

        @IAopOrderAspect1
        @IAopOrderAspect2
        public void normal() {
            log.info("{} in action start", this.getClass().getName());
            log.info("{} in action end", this.getClass().getName());
        }

        @IAopOrderAspect1
        @IAopOrderAspect2
        // 执行函数 捕获异常后再次抛出
        @IAopOrderAspect3
        public void exception1() {
            log.info("{} in action start", this.getClass().getName());
            log.info("{} in action throw exception", this.getClass().getName());
            throw UnifiedException.gen("抛出异常");
        }

        @IAopOrderAspect1
        @IAopOrderAspect2
        // 未执行函数 抛出异常
        @IAopOrderAspect4
        public void exception2() {
            log.info("{} in action start", this.getClass().getName());
            log.info("{} in action end", this.getClass().getName());
        }

    }

}
