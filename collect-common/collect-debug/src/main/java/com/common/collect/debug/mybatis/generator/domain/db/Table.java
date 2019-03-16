package com.common.collect.debug.mybatis.generator.domain.db;

import lombok.Data;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Data
public class Table {

    private String name;
    private String comment;
    private List<Field> fields;

}
