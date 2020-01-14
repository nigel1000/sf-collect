package com.common.collect.container.idoc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IDocMethod {

    // 创建删除时唯一标示
    String id();

    // 接口名称
    String name();

    String author();

    // 当没有设置值时使用 @RequestMapping 的 value
    String requestUrl() default "";

    // 当没有设置值时使用 @RequestMapping 的 method， get,post
    String requestMethod() default "";

    // 是否需要删除并新建
    boolean reCreate() default false;

}
