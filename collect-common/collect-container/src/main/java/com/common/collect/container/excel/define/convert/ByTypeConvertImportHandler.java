package com.common.collect.container.excel.define.convert;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.model.ExcelConvertModel;
import com.common.collect.container.excel.annotations.model.ExcelImportModel;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.IConvertImportHandler;
import com.common.collect.container.excel.pojo.ExcelImportParam;
import com.common.collect.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by nijianfeng on 2019/3/8.
 */
public class ByTypeConvertImportHandler implements IConvertImportHandler {

    @Override
    public Object convert(String value, ExcelImportParam.ImportInfo importInfo) {
        if (value == null) {
            return null;
        }
        ExcelConvertModel excelConvertModel = importInfo.getExcelConvertModel();
        ExcelImportModel excelImportModel = importInfo.getExcelImportModel();
        Class fieldTypeClass = importInfo.getFieldInfo().getFieldType();
        if (excelImportModel.isMultiCol()) {
            fieldTypeClass = excelImportModel.getDataType();
        }
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
            if (StringUtils.isNotBlank(value)) {
                if (StringUtils.isNotEmpty(excelConvertModel.getDateParse())) {
                    try {
                        result = DateUtil.parseDate(value, excelConvertModel.getDateParse());
                    } catch (Exception ex) {
                        String tips = ExcelConstants.fillConvertPlaceholder(
                                importInfo.getExcelConvertModel().getDateParseTips(), importInfo);
                        throw UnifiedException.gen(ExcelConstants.MODULE, tips, ex);
                    }
                } else {
                    try {
                        result = DateUtil.parseDate(value, "yyyy-MM-dd HH:mm:ss");
                    } catch (Exception ex) {
                        throw UnifiedException.gen(ExcelConstants.MODULE,
                                "请在属性" + importInfo.getFieldInfo().getFieldName()
                                        + "上配置@ExcelConvert的正确dateParse,默认支持 yyyy-MM-dd HH:mm:ss",
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
