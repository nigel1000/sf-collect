package com.common.collect.container.excel.annotations.model;

import com.common.collect.container.excel.annotations.ExcelExport;
import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.ICellConfig;
import lombok.Data;
import lombok.NonNull;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

@Data
public class ExcelExportModel {

    private int colIndex;
    private String title;
    private Class<? extends ICellConfig> cellConfig;
    private ICellConfig cellConfigImpl;

    public static ExcelExportModel gen(ExcelExport excelExport, @NonNull IBeanFactory beanFactory) {
        if (excelExport == null) {
            return null;
        }
        ExcelExportModel excelExportModel = new ExcelExportModel();
        excelExportModel.setColIndex(excelExport.colIndex());
        excelExportModel.setTitle(excelExport.title());
        excelExportModel.setCellConfig(excelExport.cellConfig());
        excelExportModel.setCellConfigImpl(beanFactory.getBean(excelExport.cellConfig()));
        return excelExportModel;
    }

}
