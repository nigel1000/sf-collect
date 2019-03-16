package com.common.collect.container.excel.extension;

import com.common.collect.container.excel.annotations.model.ExcelImportModel;
import com.common.collect.container.excel.define.IConvertImportHandler;
import com.common.collect.container.excel.pojo.ExcelImportParam;

/**
 * Created by nijianfeng on 2019/3/8.
 */
public class OptionConvertImportHandler implements IConvertImportHandler {

    @Override
    public Object convert(String value, ExcelImportParam.ImportInfo importInfo) {
        if (value == null) {
            return null;
        }
        ExcelImportModel excelImportModel = importInfo.getExcelImportModel();
        Class fieldTypeClass = importInfo.getFieldInfo().getFieldType();
        if (excelImportModel.isMultiCol()) {
            fieldTypeClass = excelImportModel.getDataType();
        }
        Object result = null;
        // 不是一下类型的情况下自行扩展 IConvertImportHandler
        if (IOptionValue.class.isAssignableFrom(fieldTypeClass)) {
            result = ((IOptionValue) fieldTypeClass.getEnumConstants()[0]).getByExportValue(value);
        }
        return result;
    }
}
