package com.common.collect.framework.excel.annotations;


import com.common.collect.framework.excel.base.ExcelConstants;
import com.common.collect.framework.excel.define.ICellConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {

    // excel的colIndex
    int colIndex() default ExcelConstants.EXCEL_EXPORT_COL_INDEX_DEFAULT;

    // excel的title
    String title() default "";

    // 默认不做配置
    Class<? extends ICellConfig> cellConfig() default ICellConfig.class;

}
