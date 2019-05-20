package com.common.collect.container;

import com.common.collect.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Slf4j
public class AspectUtil {

    private AspectUtil() {
    }

    /**
     * 递归查找目标类的所有子类的注解
     * 方法上优先，类上次之，找到即返回
     */
    public static <T extends Annotation> T recursionGetAnnotation(final JoinPoint point, Class<T> annotationClass) {

        if (point == null) {
            return null;
        }

        List<Class> classes = ClassUtil.getSuperclasses(point.getTarget().getClass());
        T result = null;
        T candidate = null;
        for (Class<?> clazz : classes) {
            Method method = getMethod(point);
            if (method != null) {
                result = method.getAnnotation(annotationClass);
                if (result == null) {
                    result = clazz.getAnnotation(annotationClass);
                }
                if (result != null) {
                    break;
                }
            } else {
                candidate = clazz.getAnnotation(annotationClass);
            }
        }
        return Optional.ofNullable(result).orElse(candidate);
    }

    /**
     * 方法上优先 类上次之
     */
    public static <T extends Annotation> T getAnnotation(final JoinPoint point, Class<T> annotationClass) {
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

    public static <T extends Annotation> T[] getAnnotationsByType(final JoinPoint point, Class<T> annotationClass) {
        if (point == null) {
            return null;
        }
        Class<?> clazz = point.getTarget().getClass();
        Method method = getMethod(point);
        if (method == null) {
            return null;
        }
        return Optional.ofNullable(method.getAnnotationsByType(annotationClass))
                .orElse(clazz.getAnnotationsByType(annotationClass));
    }

    public static Method getMethod(final JoinPoint point) {
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
