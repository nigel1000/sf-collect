package aop.order;

import com.common.collect.lib.api.excps.UnifiedException;
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
public class AopOrderAspect4 {

    @Pointcut("@annotation(aop.order.IAopOrderAspect4)")
    public void order() {
    }

    @Around("order()")
    public Object around(final ProceedingJoinPoint point) throws Throwable {
        String clazzName = this.getClass().getName();
        log.info("{} before action", clazzName);
        if (true) {
            throw UnifiedException.gen("抛出异常");
        }
        try {
            Object proceed = point.proceed();
            log.info("{} after action normal end", clazzName);
            return proceed;
        } catch (Exception ex) {
            log.info("{} after action handle exception", clazzName);
        } finally {
            log.info("{} after action finally", clazzName);
            return null;
        }
    }

}
