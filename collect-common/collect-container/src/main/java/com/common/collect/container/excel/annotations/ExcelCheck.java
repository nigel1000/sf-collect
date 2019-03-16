package com.common.collect.container.excel.annotations;

import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.ICheckImportHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCheck {

    // 必填约束
    boolean required() default false;

    String requiredTips() default "定义属性不能为空而录入数据为空";

    // 单元格的字符串的最大长度或者数值的最大值
    // 作用与类型：String,Integer,Long,BigDecimal
    long max() default Long.MIN_VALUE;

    String maxTips() default "数值必须小于" + ExcelConstants.PLACEHOLDER_MAX + "或者字符必须少于" + ExcelConstants.PLACEHOLDER_MAX;

    // 作用与类型：String
    String regex() default "";

    String regexTips() default "正则表达式：" + ExcelConstants.PLACEHOLDER_REGEX;

    // 新增校验器
    Class<? extends ICheckImportHandler>[] checkImportHandlers() default {};

}
