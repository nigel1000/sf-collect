package com.common.collect.lib.util.spring.aop;

import com.common.collect.lib.util.spring.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Aspect
@Component
@Order(value = 2)
@Slf4j
public class EasyLogAspectJ {

    @Pointcut("@within(com.common.collect.lib.util.spring.aop.EasyLog)")
    public void clazz() {
    }

    @Pointcut("@annotation(com.common.collect.lib.util.spring.aop.EasyLog)")
    public void method() {
    }

    @Before("clazz() || method()")
    public void before(final JoinPoint point) {
        if (log.isDebugEnabled()) {
            EasyLog easyLog = AopUtil.getAnnotation(point, EasyLog.class);
            String module = easyLog.module();
            log.debug("执行之前。模块：{}，类名：{}，方法：{}，入参：{}", module, point.getTarget().getClass().getName(),
                    point.getSignature().getName(), point.getArgs());
        }
    }

    @AfterReturning(returning = "rtObj", value = "clazz() || method()")
    public void afterReturning(final JoinPoint point, final Object rtObj) {
        if (log.isDebugEnabled()) {
            EasyLog easyLog = AopUtil.getAnnotation(point, EasyLog.class);
            String module = easyLog.module();
            log.debug("执行返回。执行之前。模块：{}，类名：{}，方法：{}，返回：{}", module, point.getTarget().getClass().getName(),
                    point.getSignature().getName(), rtObj);
        }
    }


    @AfterThrowing(throwing = "ex", value = "clazz() || method()")
    public void afterThrowing(final JoinPoint point, final Throwable ex) {
        if (log.isDebugEnabled()) {
            EasyLog easyLog = AopUtil.getAnnotation(point, EasyLog.class);
            String module = easyLog.module();
            log.debug("执行异常。模块：{}，类名：{}，方法：{}", module, point.getTarget().getClass().getName(),
                    point.getSignature().getName(), ex);
        }
    }

}

