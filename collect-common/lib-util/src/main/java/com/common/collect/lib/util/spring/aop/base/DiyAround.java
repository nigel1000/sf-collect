package com.common.collect.lib.util.spring.aop.base;

import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hznijianfeng on 2020/6/16.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DiyAround {

    String module() default "";

    // 必须实现此接口的 class, 具体环绕逻辑接口
    Class<? extends IDiyAround>[] diyAroundCls() default {};

    // 定义每个环绕类的获取方式
    DiyAroundWay[] diyAroundWay() default {};

    enum DiyAroundWay {
        // 通过 diyAround.newInstance() 创建
        reflect,
        // 通过 applicationContext.getBean(diyAround) 获取
        spring_bean
    }

    interface IDiyAround {

        default boolean needLogAroundClsError() {
            return true;
        }

        // 业务执行前
        default void doBefore(DiyAround.DiyAroundContext diyAroundContext, DiyAround diyAround) {
        }

        // 业务执行后
        default void doAfter(DiyAround.DiyAroundContext diyAroundContext, Object ret, DiyAround diyAround) {
        }

        // 业务执行抛出异常后
        default void doException(DiyAround.DiyAroundContext diyAroundContext, Throwable throwable, DiyAround diyAround) {
        }

        // 业务执行抛出异常后 fallback 返回不为 null 时会以此返回作为降级
        default Object doFallback(DiyAround.DiyAroundContext diyAroundContext, Throwable throwable, DiyAround diyAround) {
            return null;
        }

    }

    @Data
    class DiyAroundContext {
        private Object[] args;
        private Class<?> retType;
        private String className;
        private String methodName;
        private Object retValue;
        private Throwable exception;

        public Object getArgsByIndex(int index) {
            return args[index];
        }
    }

}
