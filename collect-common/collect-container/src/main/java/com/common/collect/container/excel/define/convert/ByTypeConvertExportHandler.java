package com.common.collect.container.excel.define.convert;

import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.define.IConvertExportHandler;
import com.common.collect.util.DateUtil;
import com.common.collect.util.EmptyUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by nijianfeng on 2019/3/8.
 */
public class ByTypeConvertExportHandler implements IConvertExportHandler {

    @Override
    public String convert(Object value, String fieldName, ExcelContext excelContext) {
        if (value == null) {
            return "";
        }
        String dateFormat = excelContext.getExcelConvertDateFormatMap().get(fieldName);
        String result;
        Class fieldTypeClass = value.getClass();
        // 不是一下类型的情况下自行扩展 IConvertExportHandler
        if (fieldTypeClass == Long.class || fieldTypeClass == long.class) {
            result = String.valueOf(value);
        } else if (fieldTypeClass == Integer.class || fieldTypeClass == int.class) {
            result = String.valueOf(value);
        } else if (fieldTypeClass == BigDecimal.class) {
            result = String.valueOf(value);
        } else if (fieldTypeClass == Boolean.class || fieldTypeClass == boolean.class) {
            result = String.valueOf(value);
        } else if (fieldTypeClass == Date.class) {
            if (EmptyUtil.isNotEmpty(dateFormat)) {
                result = DateUtil.format((Date) value, dateFormat);
            } else {
                result = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:ss");
            }
        } else {
            result = String.valueOf(value);
        }
        return result;
    }

}
