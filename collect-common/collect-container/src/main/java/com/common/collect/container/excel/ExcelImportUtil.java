package com.common.collect.container.excel;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.context.ExcelSheetInfo;
import com.common.collect.container.excel.define.ICheckImportHandler;
import com.common.collect.container.excel.define.IConvertImportHandler;
import com.common.collect.container.excel.excps.ExcelImportException;
import com.common.collect.util.EmptyUtil;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.util.List;

/**
 * Created by nijianfeng on 2018/8/26.
 */
@Slf4j
public class ExcelImportUtil extends ExcelSession {

    @Setter
    @Getter
    private int importLimit = 1000;

    @Setter
    @Getter
    // true 碰到一个数据校验不过即告知
    // false 最多碰到100个错误默认20个错误即告知
    private boolean isFastFail = false;
    @Setter
    @Getter
    private int failCount = 20;
    private final int failMaxCount = 100;

    public ExcelImportUtil(@NonNull InputStream inputStream) {
        super(inputStream);
    }

    public ExcelImportUtil(@NonNull String filePath) {
        super(filePath);
    }

    public ExcelImportUtil(@NonNull Workbook workbook, Sheet sheet) {
        super(workbook, sheet);
    }

    public ExcelImportUtil(@NonNull Workbook workbook) {
        super(workbook);
    }

    /**
     * 从startRow开始以行获取excel
     */
    public <C> List<C> excelParse(@NonNull Integer startRow, Class<C> targetClass) throws ExcelImportException {
        return excelParse(startRow, this.getLastRowNum(), targetClass);
    }

    /**
     * 从startRow开始以行获取excel
     */
    public <C> List<C> excelParse(@NonNull Integer from, @NonNull Integer to, Class<C> targetClass)
            throws ExcelImportException {
        if ((to - from) >= importLimit) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "导入超过了限制，限制:" + importLimit);
        }
        if (isFastFail) {
            failCount = 1;
        }
        if (failCount > failMaxCount) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "错误返回超出了限制，限制:" + failMaxCount);
        }
        List<C> retList = Lists.newArrayList();
        boolean isExcelExp = false;
        ExcelImportException excelParseException = new ExcelImportException(failCount);
        ExcelContext excelContext = ExcelContext.excelContext(targetClass);
        for (int i = from; i <= to; i++) {
            boolean isEmpty = this.isEmptyRow(i);
            if (!isEmpty) {
                try {
                    retList.add(rowParse(i, excelContext));
                } catch (ExcelImportException e) {
                    isExcelExp = true;
                    if (!excelParseException.addInfo(e.getInfoList())) {
                        break;
                    }
                }
            }
        }
        if (isExcelExp) {
            throw excelParseException;
        }
        return retList;
    }

    /**
     * 获取excel某一行
     */
    private <C> C rowParse(int rowIndex, ExcelContext excelContext) throws ExcelImportException {
        // 利用反射赋值
        C result = excelContext.newInstance();
        ExcelImportException excelParseException = new ExcelImportException(failCount);
        String sheetName = getSheet().getSheetName();
        List<String> rowMap = getRowValueList(rowIndex);
        int colMaxNum = rowMap.size();
        for (String fieldName : excelContext.getFieldNameList()) {
            if (!excelContext.isImport(fieldName)) {
                continue;
            }
            String title = excelContext.getExcelImportTitleMap().get(fieldName);
            List<Integer> colIndexes = excelContext.getExcelImportColIndexNumMap().get(fieldName);
            List<Object> values = Lists.newArrayList();
            for (Integer colIndex : colIndexes) {
                Object value = null;
                String currentValue;
                if (colMaxNum <= colIndex) {
                    currentValue = null;
                } else {
                    currentValue = rowMap.get(colIndex);
                }
                if (EmptyUtil.isNotBlank(currentValue)) {
                    boolean isConvertSuccess = true;
                    // 后面加的可以覆盖默认 转换 以最后一个为准
                    for (IConvertImportHandler convertHandler : excelContext.getExcelConvertImportHandlerMap()
                            .get(fieldName)) {
                        try {
                            Object convert = convertHandler.convert(currentValue, fieldName, excelContext);
                            if (convert != null) {
                                value = convert;
                            }
                        } catch (Exception ex) {
                            log.debug("数据转换错误", ex);
                            isConvertSuccess = false;
                            ExcelSheetInfo expInfo = ExcelSheetInfo.builder().columnName(title).rowNum(rowIndex)
                                    .sheetName(sheetName).colNum(colIndex).currentValue(currentValue).build();
                            if (ex.getClass() == UnifiedException.class) {
                                expInfo.setErrMsg(ExcelConstants.fillCommonPlaceholder(ex.getMessage(), expInfo));
                            } else {
                                expInfo.setErrMsg(convertHandler.getClass().getName() + " 数据转换错误");
                            }
                            if (!excelParseException.addInfo(expInfo)) {
                                throw excelParseException;
                            }
                            break;
                        }
                    }
                    // 数据转换错误时不进行后续 check 等操作
                    if (!isConvertSuccess) {
                        continue;
                    }
                }
                // 校验
                for (ICheckImportHandler checkHandler : excelContext.getExcelCheckImportHandlerMap().get(fieldName)) {
                    try {
                        checkHandler.check(value, fieldName, excelContext);
                    } catch (UnifiedException ex) {
                        ExcelSheetInfo expInfo = ExcelSheetInfo.builder().columnName(title).rowNum(rowIndex)
                                .sheetName(sheetName).colNum(colIndex).currentValue(currentValue).build();
                        expInfo.setErrMsg(ExcelConstants.fillCommonPlaceholder(ex.getMessage(), expInfo));
                        if (!excelParseException.addInfo(expInfo)) {
                            break;
                        }
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
                    throw UnifiedException.gen(ExcelConstants.MODULE, "单列解析出现异常数据");
                }
            }
        }

        if (!excelParseException.isEmptyInfo()) {
            throw excelParseException;
        }
        return result;
    }

    // 设置错误行的 cellStyle
    public void setErrorCellStyle(ExcelImportException exception, CellStyle cellStyle) {
        if (cellStyle == null) {
            short color = IndexedColors.RED.getIndex();
            cellStyle = this.createDefaultCellStyle();
            cellStyle.setFillBackgroundColor(color);
            cellStyle.setFillBackgroundColor(color);
            cellStyle.setTopBorderColor(color);
            cellStyle.setBottomBorderColor(color);
            cellStyle.setRightBorderColor(color);
            cellStyle.setLeftBorderColor(color);
        }
        List<ExcelSheetInfo> infos = exception.getInfoList();
        for (ExcelSheetInfo info : infos) {
            setCellStyle(info.getRowNum(), info.getColNum(), cellStyle);
        }
    }

}
