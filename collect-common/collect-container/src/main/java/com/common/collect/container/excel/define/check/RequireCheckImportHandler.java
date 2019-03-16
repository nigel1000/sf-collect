package com.common.collect.container.excel.define.check;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.model.ExcelCheckModel;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.ICheckImportHandler;
import com.common.collect.container.excel.pojo.ExcelImportParam;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public class RequireCheckImportHandler implements ICheckImportHandler {

    @Override
    public void check(Object value, ExcelImportParam.ImportInfo importInfo) {
        if (importInfo == null || importInfo.getExcelCheck() == null) {
            return;
        }
        ExcelCheckModel excelCheckModel = importInfo.getExcelCheckModel();
        boolean required = excelCheckModel.isRequired();
        if (required && (value == null || (value instanceof String && StringUtils.isBlank(String.valueOf(value))))) {
            String tips = excelCheckModel.getRequiredTips();
            throw UnifiedException.gen(ExcelConstants.MODULE, tips);
        }
    }
}
