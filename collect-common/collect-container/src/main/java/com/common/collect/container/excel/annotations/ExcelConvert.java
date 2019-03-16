package com.common.collect.container.excel.annotations;

/**
 * Created by hznijianfeng on 2019/3/7.
 */

import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.IConvertExportHandler;
import com.common.collect.container.excel.define.IConvertImportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertExportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertImportHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelConvert {

    // 作用与类型：Date
    // 作用于 导入
    String dateParse() default "yyyy-MM-dd HH:mm:ss";

    // 占位符替换成 dateParse()的值
    String dateParseTips() default "日期格式需要满足：" + ExcelConstants.PLACEHOLDER_DATE_PARSE;

    // 作用与类型：Date
    // 作用于 导出
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    // 默认导出数据都转成 string
    // 新增导出转换器 遍历全部转换器 以最后一个不返回 null 的为准
    Class<? extends IConvertExportHandler>[] convertExportHandlers() default {ByTypeConvertExportHandler.class};

    // 默认导入值都为 string ，即 string 根据类型转成相应数据
    // 新增导入转换器 遍历全部转换器 以最后一个不返回 null 的为准
    Class<? extends IConvertImportHandler>[] convertImportHandlers() default {ByTypeConvertImportHandler.class};

}
