package com.common.collect.framework.excel.context;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Data
@Builder
public class ExcelSheetInfo implements Serializable {

    // sheet 名
    private String sheetName;

    // 行号
    private int rowNum;

    // 列号
    private int colNum;

    // 列名
    private String columnName;

    // 单元格值
    private String currentValue;

    // 错误描述
    private String errMsg;

}
