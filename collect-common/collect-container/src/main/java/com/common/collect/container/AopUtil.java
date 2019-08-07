package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class AopUtil {


    // 获取 目标对象
    // proxy 代理对象
    public static Object getTarget(Object proxy) {

        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }

        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else {
            return getCglibProxyTargetObject(proxy);
        }
    }

    public static Object getCglibProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);

            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);

            Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();

            return target;
        } catch (Exception ex) {
            throw UnifiedException.gen("AopTargetUtil.getCglibProxyTargetObject", ex);
        }
    }


    public static Object getJdkDynamicProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy) h.get(proxy);

            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);

            Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();

            return target;
        } catch (Exception ex) {
            throw UnifiedException.gen("AopTargetUtil.getJdkDynamicProxyTargetObject", ex);
        }
    }


    public static void setCglibProxyTargetObject(Object proxy, Object spyObject)
            throws NoSuchFieldException, IllegalAccessException {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).setTarget(spyObject);

    }

    public static void setJdkDynamicProxyTargetObject(Object proxy, Object spyObject)
            throws NoSuchFieldException, IllegalAccessException {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        ((AdvisedSupport) advised.get(aopProxy)).setTarget(spyObject);
    }

    // 方法上优先 类上次之
    public static <T extends Annotation> T getAnnotation(@NonNull JoinPoint point, Class<T> annotationClass) {
        if (point == null) {
            return null;
        }
        Class<?> clazz = point.getTarget().getClass();
        Method method = getMethod(point);
        if (method == null) {
            return null;
        }
        return Optional.ofNullable(method.getAnnotation(annotationClass)).orElse(clazz.getAnnotation(annotationClass));
    }

    public static Method getMethod(@NonNull JoinPoint point) {
        Class<?> clazz = point.getTarget().getClass();
        String methodName = point.getSignature().getName();
        Class[] parameterTypes = ((MethodSignature) point.getSignature()).getParameterTypes();
        // 必须用这种方式获得method
        // ((MethodSignature) pjp.getSignature()).getMethod() 无法通过接口拿到实现类方法上的注解
        Method method = null;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignored) {
        }
        return method;
    }


}  