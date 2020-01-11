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

    // 名称
    String nameDesc() default "";

    // 类型
    String type() default "";

    // 默认值
    String value() default "";

    // 描述
    String desc() default "";

    // 是否必须
    boolean required() default true;


}
