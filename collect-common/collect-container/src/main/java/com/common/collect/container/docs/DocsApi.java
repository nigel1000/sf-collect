package com.common.collect.container.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocsApi {

    // 根目录
    String rootDirName() default "";

    // url 前缀
    String urlPrefix() default "";

    // 是否显示字段描述
    boolean showComment() default true;

    boolean reCreate() default true;

}
