package com.common.collect.container.aops;

import com.common.collect.container.AopUtil;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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

    @Pointcut("@within(com.common.collect.container.aops.EasyLog)")
    public void clazz() {
    }

    @Pointcut("@annotation(com.common.collect.container.aops.EasyLog)")
    public void method() {
    }

    @Before("clazz() || method()")
    public void before(final JoinPoint point) {

        EasyLog easyLog = AopUtil.getAnnotation(point, EasyLog.class);
        String module = easyLog.module();
        log.debug(LogConstant.START_LOG_PREFIX + " args:{}", module, point.getTarget().getClass().getName(),
                point.getSignature().getName(), LogConstant.getObjString(point.getArgs()));
    }

    @AfterReturning(returning = "rtObj", value = "clazz() || method()")
    public void afterReturning(final JoinPoint point, final Object rtObj) {

        EasyLog easyLog = AopUtil.getAnnotation(point, EasyLog.class);
        String module = easyLog.module();
        log.debug(LogConstant.FINISH_LOG_PREFIX + " return:{}", module, point.getTarget().getClass().getName(),
                point.getSignature().getName(), LogConstant.getObjString(rtObj));
    }


    @AfterThrowing(throwing = "ex", value = "clazz() || method()")
    public void afterThrowing(final JoinPoint point, final Throwable ex) {

        EasyLog easyLog = AopUtil.getAnnotation(point, EasyLog.class);
        String module = easyLog.module();
        log.debug(LogConstant.FINISH_LOG_PREFIX + " exception:{}", module, point.getTarget().getClass().getName(),
                point.getSignature().getName(), Throwables.getStackTraceAsString(ex));
    }

}

