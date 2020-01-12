package com.common.collect.container.idoc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface IDocField {

    // 公共属性
    // 名称
    String nameDesc() default "";

    String value() default "";

    // 描述
    String desc() default "";


    // request 属性
    // 是否必须
    boolean required() default true;


}
