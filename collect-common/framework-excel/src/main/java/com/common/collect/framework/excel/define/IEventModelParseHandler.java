package com.common.collect.framework.excel.define;

import com.common.collect.framework.excel.context.EventModelContext;
import com.common.collect.framework.excel.context.ExcelContext;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.CollectionUtil;
import com.common.collect.lib.util.EmptyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

public interface IEventModelParseHandler {

    void handle(EventModelContext eventModelContext);

    default <C> List<C> toDomains(List<List<String>> data, Class<C> cls) {
        List<C> ret = new ArrayList<>();
        if (EmptyUtil.isEmpty(data)) {
            return ret;
        }
        for (List<String> obj : data) {
            C c = toDomain(obj, cls);
            if (c != null) {
                ret.add(c);
            }
        }
        return ret;
    }

    default <C> C toDomain(List<String> data, Class<C> cls) {
        // 都是空格代表是空行
        if (EmptyUtil.isEmpty(CollectionUtil.removeBlank(data))) {
            return null;
        }
        if (EmptyUtil.isEmpty(data)) {
            return null;
        }
        ExcelContext excelContext = ExcelContext.excelContext(cls);
        C result = excelContext.newInstance();
        for (String fieldName : excelContext.getFieldNameList()) {
            if (!excelContext.isImport(fieldName)) {
                continue;
            }
            List<Integer> colIndexes = excelContext.getExcelImportColIndexNumMap().get(fieldName);
            List<Object> values = new ArrayList<>();
            for (Integer colIndex : colIndexes) {
                Object value = null;
                String currentValue = data.get(colIndex);
                if (EmptyUtil.isNotBlank(currentValue)) {
                    // 后面加的可以覆盖默认 转换 以最后一个为准
                    for (IConvertImportHandler convertHandler : excelContext.getExcelConvertImportHandlerMap().get(fieldName)) {
                        try {
                            Object convert = convertHandler.convert(currentValue, fieldName, excelContext);
                            if (convert != null) {
                                value = convert;
                            }
                        } catch (UnifiedException ex) {
                            throw ex;
                        } catch (Exception ex) {
                            throw UnifiedException.gen("数据转换失败", ex);
                        }
                    }
                }
                // 校验
                for (ICheckImportHandler checkHandler : excelContext.getExcelCheckImportHandlerMap().get(fieldName)) {
                    try {
                        checkHandler.check(value, fieldName, excelContext);
                    } catch (UnifiedException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw UnifiedException.gen("数据校验失败", ex);
                    }
                }
                if (value != null) {
                    values.add(value);
                }
            }
            if (excelContext.getExcelImportIsMultiColMap().get(fieldName)) {
                excelContext.setFieldValue(fieldName, result, values);
            } else {
                if (values.size() == 1) {
                    excelContext.setFieldValue(fieldName, result, values.get(0));
                } else if (values.size() != 0) {
                    throw UnifiedException.gen("单列解析出现异常数据");
                }
            }
        }
        return result;
    }

}
