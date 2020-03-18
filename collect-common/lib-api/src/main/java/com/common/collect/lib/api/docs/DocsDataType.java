package com.common.collect.lib.api.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DocsDataType {

    // 模型名称
    String name() default "";
    // 类型描述
    String desc() default "";

}
