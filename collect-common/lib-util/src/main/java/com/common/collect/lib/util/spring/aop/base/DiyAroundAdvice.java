package com.common.collect.lib.util.spring.aop.base;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.ExceptionUtil;
import com.common.collect.lib.util.spring.AopUtil;
import com.common.collect.lib.util.spring.SpringContextUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/6/16.
 */
@Aspect
@Component
@Order(value = 1)
@Slf4j
public class DiyAroundAdvice {

    @Pointcut("@within(com.common.collect.lib.util.spring.aop.base.DiyAround)")
    public void clazz() {
    }

    @Pointcut("@annotation(com.common.collect.lib.util.spring.aop.base.DiyAround)")
    public void method() {
    }

    @Around(value = "clazz() || method()")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        DiyAround diyAround = AopUtil.getAnnotation(joinPoint, DiyAround.class);
        DiyAround.DiyAroundContext diyAroundContext = getDiyAroundContext(joinPoint);
        List<DiyAround.IDiyAround> instances;
        try {
            instances = getHandlerInstance(diyAround);
        } catch (Exception ex) {
            log.error("获取环绕处理类异常, 未处理环绕逻辑. className:[{}], methodName:[{}]",
                    diyAroundContext.getClassName(), diyAroundContext.getMethodName(), ex);
            return joinPoint.proceed();
        }

        for (DiyAround.IDiyAround instance : instances) {
            ExceptionUtil.eatException(() -> instance.doBefore(diyAroundContext, diyAround),
                    instance.needLogAroundClsError() ? instance.getClass().getSimpleName() + " 环绕类 doBefore 失败" : null);
        }
        try {
            Object ret = joinPoint.proceed();
            diyAroundContext.setRetValue(ret);
            for (DiyAround.IDiyAround instance : instances) {
                ExceptionUtil.eatException(() -> instance.doAfter(diyAroundContext, ret, diyAround),
                        instance.needLogAroundClsError() ? instance.getClass().getSimpleName() + " 环绕类 doAfter 失败" : null);
            }
            return ret;
        } catch (Throwable ex) {
            diyAroundContext.setException(ex);
            for (DiyAround.IDiyAround instance : instances) {
                ExceptionUtil.eatException(() -> instance.doException(diyAroundContext, ex, diyAround),
                        instance.needLogAroundClsError() ? instance.getClass().getSimpleName() + " 环绕类 doException 失败" : null);
            }
            for (DiyAround.IDiyAround instance : instances) {
                Object ret = ExceptionUtil.eatException(() -> instance.doFallback(diyAroundContext, ex, diyAround),
                        instance.needLogAroundClsError() ? instance.getClass().getSimpleName() + " 环绕类 doFallback 失败" : null);
                if (ret != null) {
                    return ret;
                }
            }
            throw ex;
        }
    }

    private List<DiyAround.IDiyAround> getHandlerInstance(@NonNull DiyAround diyAround) {
        if (diyAround.diyAroundCls().length != diyAround.diyAroundWay().length) {
            throw UnifiedException.gen("环绕类和环绕类获取方式长度不一致");
        }
        List<DiyAround.IDiyAround> diyArounds = new ArrayList<>(diyAround.diyAroundCls().length);
        for (int i = 0; i < diyAround.diyAroundWay().length; i++) {
            Class<? extends DiyAround.IDiyAround> diyAroundCls = diyAround.diyAroundCls()[i];
            DiyAround.IDiyAround instance;
            switch (diyAround.diyAroundWay()[i]) {
                case reflect:
                    instance = ClassUtil.newInstance(diyAroundCls);
                    break;
                case spring_bean:
                    instance = SpringContextUtil.getBean(diyAroundCls);
                    break;
                default:
                    throw UnifiedException.gen("环绕类获取方式不合法");
            }
            diyArounds.add(instance);
        }
        return diyArounds;
    }

    private DiyAround.DiyAroundContext getDiyAroundContext(ProceedingJoinPoint point) {
        DiyAround.DiyAroundContext diyAroundContext = new DiyAround.DiyAroundContext();
        diyAroundContext.setArgs(point.getArgs());
        diyAroundContext.setClassName(point.getTarget().getClass().getName());
        diyAroundContext.setMethodName(point.getSignature().getName());
        diyAroundContext.setRetType(((MethodSignature) point.getSignature()).getReturnType());
        return diyAroundContext;
    }

}
