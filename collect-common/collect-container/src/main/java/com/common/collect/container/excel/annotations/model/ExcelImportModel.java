package com.common.collect.container.excel.annotations.model;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.ExcelImport;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.IColIndexParser;
import com.common.collect.util.CollectionUtil;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

@Data
public class ExcelImportModel {

    private String colIndex;
    private Class<? extends IColIndexParser> colIndexParser;
    private IColIndexParser colIndexParserImpl;
    private List<Integer> colIndexList;
    private boolean isMultiCol;
    private Class dataType;
    private boolean duplicateRemove;
    private String title;

    public static ExcelImportModel gen(ExcelImport excelImport, @NonNull IBeanFactory beanFactory) {
        if (excelImport == null) {
            return null;
        }
        ExcelImportModel excelImportModel = new ExcelImportModel();
        excelImportModel.setColIndex(excelImport.colIndex());
        excelImportModel.setColIndexParser(excelImport.colIndexParser());
        IColIndexParser colIndexParser = beanFactory.getBean(excelImport.colIndexParser());
        if (colIndexParser == null) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "列下标解析器不能为空");
        }
        excelImportModel.setColIndexParserImpl(colIndexParser);
        excelImportModel
                .setColIndexList(CollectionUtil.removeDuplicate(colIndexParser.parseColIndex(excelImport.colIndex())));
        excelImportModel.setMultiCol(excelImportModel.getColIndexList().size() > 1);
        excelImportModel.setDataType(excelImport.dataType());
        excelImportModel.setDuplicateRemove(excelImport.duplicateRemove());
        excelImportModel.setTitle(excelImport.title());
        return excelImportModel;
    }

}
