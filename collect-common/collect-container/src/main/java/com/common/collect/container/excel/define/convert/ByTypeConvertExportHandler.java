package com.common.collect.container.excel.define.convert;

import com.common.collect.container.excel.annotations.model.ExcelConvertModel;
import com.common.collect.container.excel.define.IConvertExportHandler;
import com.common.collect.container.excel.pojo.ExcelExportParam;
import com.common.collect.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by nijianfeng on 2019/3/8.
 */
public class ByTypeConvertExportHandler implements IConvertExportHandler {

    @Override
    public String convert(Object value, ExcelExportParam.ExportInfo exportInfo) {
        if (value == null) {
            return "";
        }
        ExcelConvertModel excelConvertModel = exportInfo.getExcelConvertModel();
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
            if (StringUtils.isNotEmpty(excelConvertModel.getDateFormat())) {
                result = DateUtil.format((Date) value, excelConvertModel.getDateFormat());
            } else {
                result = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:ss");
            }
        } else {
            result = String.valueOf(value);
        }
        return result;
    }

}
