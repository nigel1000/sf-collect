package com.common.collect.container.excel.annotations.model;

import com.common.collect.container.excel.annotations.ExcelConvert;
import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.IConvertExportHandler;
import com.common.collect.container.excel.define.IConvertImportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertExportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertImportHandler;
import com.common.collect.util.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

@Data
public class ExcelConvertModel {

    // 导入使用
    private String dateParse;
    private String dateParseTips;
    private Class<? extends IConvertImportHandler>[] convertImportHandlers;
    private List<IConvertImportHandler> convertImportHandlersList = Lists.newArrayList();

    // 导出使用
    private String dateFormat;
    private Class<? extends IConvertExportHandler>[] convertExportHandlers;
    private List<IConvertExportHandler> convertExportHandlersList = Lists.newArrayList();

    public static ExcelConvertModel gen(ExcelConvert excelConvert, @NonNull IBeanFactory beanFactory) {
        ExcelConvertModel excelConvertModel = new ExcelConvertModel();
        List<IConvertImportHandler> convertImportHandlers = Lists.newArrayList();
        convertImportHandlers.add(beanFactory.getBean(ByTypeConvertImportHandler.class));
        List<IConvertExportHandler> convertExportHandlers = Lists.newArrayList();
        convertExportHandlers.add(beanFactory.getBean(ByTypeConvertExportHandler.class));
        if (excelConvert != null) {
            excelConvertModel.setDateParse(excelConvert.dateParse());
            excelConvertModel.setDateParseTips(excelConvert.dateParseTips());
            Class<? extends IConvertImportHandler>[] importHandlers = excelConvert.convertImportHandlers();
            excelConvertModel.setConvertImportHandlers(importHandlers);
            for (Class<? extends IConvertImportHandler> handler : importHandlers) {
                convertImportHandlers.add(beanFactory.getBean(handler));
            }

            excelConvertModel.setDateFormat(excelConvert.dateFormat());
            Class<? extends IConvertExportHandler>[] exportHandlers = excelConvert.convertExportHandlers();
            excelConvertModel.setConvertExportHandlers(exportHandlers);
            for (Class<? extends IConvertExportHandler> handler : exportHandlers) {
                convertExportHandlers.add(beanFactory.getBean(handler));
            }
        }
        excelConvertModel.setConvertImportHandlersList(CollectionUtil.removeDuplicate(convertImportHandlers));
        excelConvertModel.setConvertExportHandlersList(CollectionUtil.removeDuplicate(convertExportHandlers));
        return excelConvertModel;
    }

}
