package com.common.collect.framework.excel.annotations;


import com.common.collect.framework.excel.define.IColIndexParser;
import com.common.collect.framework.excel.define.cell.SplitColIndexParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    // 譬如指定多列 8:22,0:5,24，
    // 会把第0到5列，第8列，22到24列合并成 List<dataType> 返回，顺序与下标顺序一致
    // 故多列时 field 类型必须是 List
    // 譬如指定单列 8 此时会把第8列转换成 field 类型返回
    String colIndex() default "";

    // 解析 colIndex 为 List<Integer>
    Class<? extends IColIndexParser> colIndexParser() default SplitColIndexParser.class;

    boolean isMultiCol() default false;

    // 多列时生效即属性类型是List
    Class dataType() default String.class;

    // excel的title 无实际使用意义 类似于备注使字段可以自解释
    String title() default "";

}
