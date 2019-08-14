package com.common.collect.container.excel.annotations;

import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.ICellConfig;
import com.common.collect.container.excel.define.bean.SingletonBeanFactory;

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

    enum ColIndexStrategyEnum {
        // 以 field 配置为准
        by_field_config,
        // 以 field 在类中的位置为准 从0开始
        by_field_place,
        // 以 field 在类中的位置作为默认值，有配置以配置为准
        by_field_place_default,

        ;
    }

    ColIndexStrategyEnum colIndexStrategy() default ColIndexStrategyEnum.by_field_config;

    Class<? extends ICellConfig> cellConfig() default ICellConfig.class;

    Class<? extends IBeanFactory> beanFactory() default SingletonBeanFactory.class;

}
