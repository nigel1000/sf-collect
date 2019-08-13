package com.common.collect.container.excel.annotations;

import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.ICellConfig;
import com.common.collect.container.excel.define.bean.SingletonBeanFactory;
import com.common.collect.container.excel.define.cell.DefaultCellConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 类上的配置会覆盖属性上的配置
public @interface ExcelEntity {

    boolean colIndexSortByField() default false;

    Class<? extends ICellConfig> cellConfig() default DefaultCellConfig.class;

    Class<? extends IBeanFactory> beanFactory() default SingletonBeanFactory.class;

}
