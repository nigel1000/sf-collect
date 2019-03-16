package com.common.collect.container.excel.pojo;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.BeanUtil;
import com.common.collect.container.excel.annotations.ExcelConvert;
import com.common.collect.container.excel.annotations.ExcelExport;
import com.common.collect.container.excel.annotations.model.ExcelConvertModel;
import com.common.collect.container.excel.annotations.model.ExcelExportModel;
import com.common.collect.container.excel.base.ExcelConstants;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

/**
 * Created by hznijianfeng on 2018/8/28.
 */

@Getter
public class ExcelExportParam<C> extends ExcelParam<C> {

    @Data
    public static class ExportInfo<C> {

        private FieldInfo fieldInfo;
        private ClassInfo<C> classInfo;

        private ExcelExport excelExport;
        private ExcelExportModel excelExportModel;
        private ExcelConvert excelConvert;
        private ExcelConvertModel excelConvertModel;

    }

    protected Map<String, ExportInfo<C>> fieldExportMap = Maps.newHashMap();

    public ExcelExportParam(@NonNull Class<C> target) {

        super(target);

        for (Map.Entry<String, FieldInfo> entry : getFieldInfoMap().entrySet()) {
            String fieldName = entry.getKey();
            FieldInfo fieldInfo = entry.getValue();
            ExcelExport excelExport = fieldInfo.getField().getAnnotation(ExcelExport.class);
            if (excelExport == null) {
                continue;
            }
            ExportInfo<C> exportInfo = new ExportInfo<>();
            exportInfo.setExcelExport(excelExport);
            exportInfo.setExcelConvert(fieldInfo.getField().getAnnotation(ExcelConvert.class));
            fieldExportMap.putIfAbsent(fieldName, exportInfo);
        }

        this.validSelf();
    }

    private void validSelf() {
        for (Map.Entry<String, ExportInfo<C>> entry : fieldExportMap.entrySet()) {
            ExportInfo<C> exportInfo = entry.getValue();
            ExcelExport excelExport = exportInfo.getExcelExport();
            FieldInfo fieldInfo = getFieldInfoMap().get(entry.getKey());
            exportInfo.setFieldInfo(fieldInfo);
            exportInfo.setClassInfo(getClassInfo());
            if (excelExport.colIndex() < 0) {
                throw UnifiedException.gen(ExcelConstants.MODULE, "colIndex 不能小于0");
            }
            ExcelExportModel excelExportModel = ExcelExportModel.gen(excelExport,
                    exportInfo.getClassInfo().getExcelEntityModel().getBeanFactoryImpl());
            BeanUtil.genBeanIgnoreTargetNotNullProperty(exportInfo.getClassInfo().getExcelEntityModel(),
                    excelExportModel);
            exportInfo.setExcelExportModel(excelExportModel);
            exportInfo.setExcelConvertModel(ExcelConvertModel.gen(exportInfo.getExcelConvert(),
                    this.getClassInfo().getExcelEntityModel().getBeanFactoryImpl()));
        }
    }
}
