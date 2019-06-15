package com.common.collect.container.excel.extension;

import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.define.IConvertExportHandler;

/**
 * Created by nijianfeng on 2019/3/8.
 */
public class OptionConvertExportHandler implements IConvertExportHandler {

    @Override
    public String convert(Object value, String fieldName, ExcelContext excelContext) {
        if (value == null) {
            return "";
        }
        String result = null;
        Class fieldTypeClass = value.getClass();
        // 不是一下类型的情况下自行扩展 IConvertExportHandler
        if (IOptionValue.class.isAssignableFrom(fieldTypeClass)) {
            result = ((IOptionValue) value).getExportValue();
        }
        return result;
    }

}
