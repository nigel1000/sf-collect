package com.common.collect.container.excel.define.check;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.define.ICheckImportHandler;
import com.common.collect.util.EmptyUtil;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public class RequireCheckImportHandler implements ICheckImportHandler {

    @Override
    public void check(Object value, String fieldName, ExcelContext excelContext) {
        if (excelContext.getExcelCheckMap().get(fieldName) == null) {
            return;
        }
        boolean required = excelContext.getExcelCheckRequiredMap().get(fieldName);
        if (required && (value == null || (value instanceof String && EmptyUtil.isBlank(String.valueOf(value))))) {
            String requiredTips = excelContext.getExcelCheckRequiredTipsMap().get(fieldName);
            throw UnifiedException.gen(ExcelConstants.MODULE, requiredTips);
        }
    }
}
