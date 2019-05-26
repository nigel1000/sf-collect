package com.common.collect.container.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocsApiMethod {

    // /dir/file.md
    String nodeName();

    String urlSuffix() default "";

    String methodAuthor() default "default";

    String methodDesc() default "";

    // 是否显示字段描述
    boolean showComment() default false;

    boolean reCreate() default true;

    SupportRequest[] supportRequest() default SupportRequest.GET;

    long order() default Long.MAX_VALUE;

}
