package aop.order;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by hznijianfeng on 2019/2/22.
 */
@Aspect
@Order(value = 3)
@Slf4j
@Component
public class AopOrderAspect3 {

    @Pointcut("@annotation(aop.order.IAopOrderAspect3)")
    public void order() {
    }

    @Around("order()")
    public Object around(final ProceedingJoinPoint point) throws Throwable {
        String clazzName = this.getClass().getName();
        log.info("{} before action", clazzName);
        try {
            Object proceed = point.proceed();
            log.info("{} after action normal end", clazzName);
            return proceed;
        } catch (Exception ex) {
            log.info("{} after action handle exception", clazzName);
            log.info("{} after action throw exception", clazzName);
            throw ex;
        } finally {
            log.info("{} after action finally", clazzName);
        }
    }

}
