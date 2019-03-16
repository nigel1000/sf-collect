package com.common.collect.container.trace.aop;

import com.common.collect.container.trace.TraceIdUtil;
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
@Order(value = 1)
@Slf4j
public class TraceIdAspectJ {

    @Pointcut("@within(com.common.collect.container.trace.aop.TraceId)")
    public void clazz() {
    }

    @Pointcut("@annotation(com.common.collect.container.trace.aop.TraceId)")
    public void method() {
    }

    @Before("clazz() || method()")
    public void before(final JoinPoint point) {
        TraceIdUtil.initTraceIdIfAbsent();
    }

    @AfterReturning("clazz() || method()")
    public void afterReturning(final JoinPoint point) {
        TraceIdUtil.clearTraceId();
    }

    @AfterThrowing("clazz() || method()")
    public void afterThrowing(final JoinPoint point) {
        TraceIdUtil.clearTraceId();
    }

}

