package com.common.collect.framework.excel.define.convert;

import com.common.collect.framework.excel.base.ExcelConstants;
import com.common.collect.framework.excel.context.ExcelContext;
import com.common.collect.framework.excel.define.IConvertImportHandler;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.DateUtil;
import com.common.collect.lib.util.EmptyUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by nijianfeng on 2019/3/8.
 */
public class ByTypeConvertImportHandler implements IConvertImportHandler {

    @Override
    public Object convert(String value, String fieldName, ExcelContext excelContext) {
        if (value == null) {
            return null;
        }
        Class fieldTypeClass = excelContext.getExcelImportMultiColListTypeMap().get(fieldName);
        String dateParse = excelContext.getExcelConvertDateParseMap().get(fieldName);
        String dateParseTips = excelContext.getExcelConvertDateParseTipsMap().get(fieldName);
        Field field = excelContext.getFieldMap().get(fieldName);
        Object result = null;
        // 不是一下类型的情况下自行扩展 IConvertImportHandler
        if (fieldTypeClass == Long.class || fieldTypeClass == long.class) {
            result = Long.valueOf(value);
        } else if (fieldTypeClass == Integer.class || fieldTypeClass == int.class) {
            result = Integer.valueOf(value);
        } else if (fieldTypeClass == BigDecimal.class) {
            result = new BigDecimal(value);
        } else if (fieldTypeClass == Boolean.class || fieldTypeClass == boolean.class) {
            result = Boolean.valueOf(value);
        } else if (fieldTypeClass == Date.class) {
            if (EmptyUtil.isNotBlank(value)) {
                if (EmptyUtil.isNotEmpty(dateParse)) {
                    try {
                        result = DateUtil.parseDate(value, dateParse);
                    } catch (Exception ex) {
                        String tips = ExcelConstants.fillConvertPlaceholder(
                                dateParseTips, fieldName, excelContext);
                        throw UnifiedException.gen(ExcelConstants.MODULE, tips, ex);
                    }
                } else {
                    try {
                        result = DateUtil.parseDate(value, "yyyy-MM-dd HH:mm:ss");
                    } catch (Exception ex) {
                        throw UnifiedException.gen(ExcelConstants.MODULE,
                                "请在属性 " + field.getName()
                                        + " 上配置@ExcelConvert的正确dateParse,默认支持 yyyy-MM-dd HH:mm:ss",
                                ex);
                    }
                }
            }
        } else if (fieldTypeClass == String.class) {
            result = value;
        }

        return result;
    }
}
