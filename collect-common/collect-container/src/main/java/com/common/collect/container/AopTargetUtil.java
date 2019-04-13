package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

public class AopTargetUtil {


    /**
     * 获取 目标对象
     * proxy 代理对象
     */
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

}  