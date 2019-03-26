package com.common.collect.container.excel.pojo;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.ExcelCheck;
import com.common.collect.container.excel.annotations.ExcelConvert;
import com.common.collect.container.excel.annotations.ExcelImport;
import com.common.collect.container.excel.annotations.model.ExcelCheckModel;
import com.common.collect.container.excel.annotations.model.ExcelConvertModel;
import com.common.collect.container.excel.annotations.model.ExcelImportModel;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.util.EmptyUtil;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2018/8/28.
 */

@Getter
public class ExcelImportParam<C> extends ExcelParam<C> {

    @Data
    public static class ImportInfo<C> {

        private FieldInfo fieldInfo;
        private ClassInfo<C> classInfo;

        private ExcelImport excelImport;
        private ExcelImportModel excelImportModel;
        private ExcelCheck excelCheck;
        private ExcelCheckModel excelCheckModel;
        private ExcelConvert excelConvert;
        private ExcelConvertModel excelConvertModel;

    }

    protected Map<String, ImportInfo<C>> fieldImportMap = Maps.newHashMap();

    public ExcelImportParam(@NonNull Class<C> target) {

        super(target);

        for (Map.Entry<String, FieldInfo> entry : getFieldInfoMap().entrySet()) {
            String fieldName = entry.getKey();
            FieldInfo fieldInfo = entry.getValue();
            ExcelImport excelImport = fieldInfo.getField().getAnnotation(ExcelImport.class);
            if (excelImport == null) {
                continue;
            }
            ImportInfo<C> importInfo = new ImportInfo<>();
            importInfo.setExcelImport(excelImport);
            importInfo.setExcelCheck(fieldInfo.getField().getAnnotation(ExcelCheck.class));
            importInfo.setExcelConvert(fieldInfo.getField().getAnnotation(ExcelConvert.class));
            fieldImportMap.putIfAbsent(fieldName, importInfo);
        }

        this.validSelf();
    }

    private void validSelf() {
        for (Map.Entry<String, ImportInfo<C>> entry : fieldImportMap.entrySet()) {
            ImportInfo<C> importInfo = entry.getValue();
            FieldInfo fieldInfo = getFieldInfoMap().get(entry.getKey());
            importInfo.setFieldInfo(fieldInfo);
            importInfo.setClassInfo(getClassInfo());
            ExcelImport excelImport = importInfo.getExcelImport();
            String colIndex = excelImport.colIndex();
            if (EmptyUtil.isBlank(colIndex)) {
                throw UnifiedException.gen(ExcelConstants.MODULE, "colIndex不能为空");
            }
            // 赋值注解
            importInfo.setExcelImportModel(ExcelImportModel.gen(excelImport,
                    importInfo.getClassInfo().getExcelEntityModel().getBeanFactoryImpl()));
            importInfo.setExcelCheckModel(ExcelCheckModel.gen(importInfo.getExcelCheck(),
                    importInfo.getClassInfo().getExcelEntityModel().getBeanFactoryImpl()));
            importInfo.setExcelConvertModel(ExcelConvertModel.gen(importInfo.getExcelConvert(),
                    this.getClassInfo().getExcelEntityModel().getBeanFactoryImpl()));

            if (importInfo.getExcelImportModel().isMultiCol()) {
                Class fieldType = fieldInfo.getFieldType();
                if (fieldType != List.class) {
                    throw UnifiedException.gen(ExcelConstants.MODULE, "指定多列时，属性必须是 List 类型");
                }
            }
        }
    }
}
