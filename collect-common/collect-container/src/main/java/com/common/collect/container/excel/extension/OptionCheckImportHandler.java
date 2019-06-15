package com.common.collect.container.excel.extension;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.define.ICheckImportHandler;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public class OptionCheckImportHandler implements ICheckImportHandler {

    @Override
    public void check(Object value, String fieldName, ExcelContext excelContext) {
        if (excelContext.getExcelCheckMap().get(fieldName) == null || value == null) {
            return;
        }

        // 属性类型是枚举的校验
        if (value instanceof IOptionValue) {
            String valueTemp = ((IOptionValue) value).getExportValue();
            if (valueTemp == null) {
                throw UnifiedException.gen(ExcelConstants.MODULE, "录入数据不在指定范围内");
            }
        }

    }
}
