package com.common.collect.framework.excel.define.check;

import com.common.collect.framework.excel.base.ExcelConstants;
import com.common.collect.framework.excel.context.ExcelContext;
import com.common.collect.framework.excel.define.ICheckImportHandler;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public class RegexCheckImportHandler implements ICheckImportHandler {

    @Override
    public void check(Object value, String fieldName, ExcelContext excelContext) {
        if (excelContext.getExcelCheckMap().get(fieldName) == null || value == null) {
            return;
        }
        // 校验日期格式
        String regex = excelContext.getExcelCheckRegexMap().get(fieldName);
        if (value.getClass() == String.class && EmptyUtil.isNotBlank(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.matches()) {
                String regexTips = excelContext.getExcelCheckRegexTipsMap().get(fieldName);
                String tips = ExcelConstants.fillCheckPlaceholder(regexTips, fieldName, excelContext);
                throw UnifiedException.gen(ExcelConstants.MODULE, tips);
            }
        }
    }
}
