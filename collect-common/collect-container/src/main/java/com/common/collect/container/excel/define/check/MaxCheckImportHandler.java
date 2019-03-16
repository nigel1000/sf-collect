package com.common.collect.container.excel.define.check;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.model.ExcelCheckModel;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.ICheckImportHandler;
import com.common.collect.container.excel.pojo.ExcelImportParam;

import java.math.BigDecimal;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public class MaxCheckImportHandler implements ICheckImportHandler {

    @Override
    public void check(Object value, ExcelImportParam.ImportInfo importInfo) {
        if (importInfo == null || importInfo.getExcelConvert() == null || value == null) {
            return;
        }
        // 校验单元格的字符串的最大长度或者数值的最大值
        ExcelCheckModel excelCheckModel = importInfo.getExcelCheckModel();
        long max = excelCheckModel.getMax();
        if (max != Long.MIN_VALUE) {
            boolean isMaxExp = false;
            Class fieldClazz = value.getClass();
            String tips = ExcelConstants.fillCheckPlaceholder(excelCheckModel.getMaxTips(), importInfo);
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
