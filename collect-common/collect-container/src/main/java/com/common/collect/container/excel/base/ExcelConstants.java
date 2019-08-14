package com.common.collect.container.excel.base;

import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.context.ExcelSheetInfo;

/**
 * Created by hznijianfeng on 2019/3/7.
 */

public class ExcelConstants {

    public final static String MODULE = "excel 组件";

    public final static int EXCEL_EXPORT_COL_INDEX_DEFAULT = -1;

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String NUMBER_FORMAT = " #,##0.00 ";

    // tips 占位符
    // convert
    public final static String PLACEHOLDER_DATE_PARSE = "#{date_parse}";
    // check
    public final static String PLACEHOLDER_MAX = "#{max_check}";
    public final static String PLACEHOLDER_REGEX = "#{regex_check}";
    // common
    public final static String PLACEHOLDER_CELL_VALUE = "#{cell_value}";
    public final static String PLACEHOLDER_ROW_NUM = "#{row_num}";
    public final static String PLACEHOLDER_COL_NUM = "#{col_num}";
    public final static String PLACEHOLDER_COL_TITLE = "#{col_title}";
    public final static String PLACEHOLDER_SHEET_NAME = "#{sheet_name}";

    public static String fillCommonPlaceholder(String value, ExcelSheetInfo info) {
        if (info == null) {
            return value;
        }
        return value.replace(PLACEHOLDER_CELL_VALUE, info.getCurrentValue())
                .replace(PLACEHOLDER_ROW_NUM, info.getRowNum() + "").replace(PLACEHOLDER_COL_NUM, info.getColNum() + "")
                .replace(PLACEHOLDER_COL_TITLE, info.getColumnName())
                .replace(PLACEHOLDER_SHEET_NAME, info.getSheetName());
    }

    public static String fillConvertPlaceholder(String value, String fieldName, ExcelContext excelContext) {
        return "属性 " + fieldName + " " + value.replace(PLACEHOLDER_DATE_PARSE, excelContext.getExcelConvertDateParseMap().get(fieldName));
    }

    public static String fillCheckPlaceholder(String value, String fieldName, ExcelContext excelContext) {
        return "属性 " + fieldName + " " + value.replace(PLACEHOLDER_MAX, excelContext.getExcelCheckMaxMap().get(fieldName) + "").replace(PLACEHOLDER_REGEX,
                excelContext.getExcelCheckRegexMap().get(fieldName));
    }

}
