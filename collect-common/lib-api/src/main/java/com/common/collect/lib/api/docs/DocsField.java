package com.common.collect.lib.api.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface DocsField {

    // 描述
    String desc() default "";

    // 默认值 可覆盖 RequestParam 的 defaultValue 值
    String defaultValue() default "";

    boolean required() default false;

}
