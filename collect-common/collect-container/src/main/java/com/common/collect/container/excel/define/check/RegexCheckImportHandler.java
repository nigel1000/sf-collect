package com.common.collect.container.excel.define.check;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.model.ExcelCheckModel;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.ICheckImportHandler;
import com.common.collect.container.excel.pojo.ExcelImportParam;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public class RegexCheckImportHandler implements ICheckImportHandler {

    @Override
    public void check(Object value, ExcelImportParam.ImportInfo importInfo) {
        if (importInfo == null || importInfo.getExcelCheck() == null || value == null) {
            return;
        }
        ExcelCheckModel excelCheckModel = importInfo.getExcelCheckModel();
        // 校验日期格式
        String regex = excelCheckModel.getRegex();
        if (value.getClass() == String.class && StringUtils.isNotBlank(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.matches()) {
                String tips = ExcelConstants.fillCheckPlaceholder(excelCheckModel.getRegexTips(), importInfo);
                throw UnifiedException.gen(ExcelConstants.MODULE, tips);
            }
        }
    }
}
