package collect.debug.aop;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.AopUtil;
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

    @Pointcut("@annotation(collect.debug.aop.IAopOrderAspect3)")
    public void order() {
    }

    @Around("order()")
    public Object around(final ProceedingJoinPoint point) throws Throwable {
        IAopOrderAspect3 iAopOrderAspect3 = AopUtil.getAnnotation(point, IAopOrderAspect3.class);
        String clazzName = this.getClass().getName();
        log.info("{} before action", clazzName);
        if (iAopOrderAspect3.rollback()) {
            point.proceed();
            // 这里抛出异常不会回滚业务数据
            log.info("{} after action throw exception", clazzName);
            throw UnifiedException.gen("after action throw exception");
        }
        try {
            Object proceed = point.proceed();
            log.info("{} after action normal end", clazzName);
            return proceed;
        } catch (Exception ex) {
            log.info("{} after action handle exception", clazzName);
            log.info("{} after action handle throw exception", clazzName);
            throw ex;
        }
    }


}
