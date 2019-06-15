package com.common.collect.container.excel.extension;

import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.define.IConvertImportHandler;

/**
 * Created by nijianfeng on 2019/3/8.
 */
public class OptionConvertImportHandler implements IConvertImportHandler {

    @Override
    public Object convert(String value, String fieldName, ExcelContext excelContext) {
        if (value == null) {
            return null;
        }
        Class fieldTypeClass = excelContext.getExcelImportMultiColListTypeMap().get(fieldName);
        Object result = null;
        // 不是一下类型的情况下自行扩展 IConvertImportHandler
        if (IOptionValue.class.isAssignableFrom(fieldTypeClass)) {
            result = ((IOptionValue) fieldTypeClass.getEnumConstants()[0]).getByExportValue(value);
        }
        return result;
    }
}
