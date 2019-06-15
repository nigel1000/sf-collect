package com.common.collect.container.excel.define.check;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.define.ICheckImportHandler;

import java.math.BigDecimal;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public class MaxCheckImportHandler implements ICheckImportHandler {

    @Override
    public void check(Object value, String fieldName, ExcelContext excelContext) {
        if (excelContext.getExcelCheckMap().get(fieldName) == null || value == null) {
            return;
        }
        // 校验单元格的字符串的最大长度或者数值的最大值
        long max = excelContext.getExcelCheckMaxMap().get(fieldName);
        if (max != Long.MIN_VALUE) {
            boolean isMaxExp = false;
            Class fieldClazz = value.getClass();
            String maxTips = excelContext.getExcelCheckMaxTipsMap().get(fieldName);
            String tips = ExcelConstants.fillCheckPlaceholder(maxTips, fieldName, excelContext);
            if (fieldClazz == BigDecimal.class) {
                if (new BigDecimal(value.toString()).compareTo(BigDecimal.valueOf(max)) > 0) {
                    isMaxExp = true;
                }
            } else if (fieldClazz == Long.class || fieldClazz == long.class) {
                if (Long.valueOf(value.toString()) - max > 0) {
                    isMaxExp = true;
                }
            } else if (fieldClazz == Integer.class || fieldClazz == int.class) {
                if (Integer.valueOf(value.toString()) - max > 0) {
                    isMaxExp = true;
                }
            } else if (fieldClazz == String.class) {
                if (String.valueOf(value).length() > max) {
                    isMaxExp = true;
                }
            } else {
                return;
            }
            if (isMaxExp) {
                throw UnifiedException.gen(ExcelConstants.MODULE, tips);
            }
        }
        return;
    }
}
