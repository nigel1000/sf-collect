package com.common.collect.container.excel;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;


/**
 * Created by hznijianfeng on 2019/3/7.
 */

@Slf4j
@Data
public class ExcelSession {

    protected Workbook workbook;
    protected Sheet sheet;

    protected ExcelSession() {
    }

    public ExcelSession(@NonNull Workbook workbook, Sheet sheet) {
        setWorkbook(workbook);
        setSheet(sheet);
    }

    public ExcelSession(@NonNull Workbook workbook) {
        this(workbook, null);
        changeSheet(0);
    }

    public ExcelSession(@NonNull InputStream inputStream) {
        try {
            setWorkbook(WorkbookFactory.create(inputStream));
            changeSheet(0);
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "excel 文件流有误!", e);
        }
    }

    public ExcelSession(@NonNull String filePath) {
        try {
            setWorkbook(WorkbookFactory.create(new FileInputStream(filePath)));
            changeSheet(0);
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, filePath + " 文件流有误!", e);
        }
    }

    public Sheet createSheet(@NonNull String sheetName) {
        return ExcelUtil.getSheet(getWorkbook(), sheetName);
    }

    public void removeSheet(@NonNull String sheetName) {
        ExcelUtil.removeSheet(getWorkbook(), sheetName);
    }

    public void changeSheet(@NonNull String sheetName) {
        setSheet(ExcelUtil.getSheet(getWorkbook(), sheetName));
    }

    public void changeSheet(int sheetIndex) {
        setSheet(ExcelUtil.getSheet(getWorkbook(), sheetIndex));
    }

    public void setSheetName(int sheetIndex, String sheetName) {
        ExcelUtil.setSheetName(getWorkbook(), sheetIndex, sheetName);
    }

    public int getActiveSheetIndex() {
        return ExcelUtil.getActiveSheetIndex(getWorkbook());
    }

    public void setCellWidth(int colIndex, int colWidth) {
        ExcelUtil.setColumnWidth(getSheet(), colIndex, colWidth);
    }

    public int getCellWidth(int colIndex) {
        return ExcelUtil.getColumnWidth(getSheet(), colIndex);
    }

    public void setHiddenColumn(int colIndex, boolean isHidden) {
        ExcelUtil.setColumnHidden(getSheet(), colIndex, isHidden);
    }

    public boolean isHiddenColumn(int colIndex) {
        return ExcelUtil.isColumnHidden(getSheet(), colIndex);
    }

    public Cell setCellValue(int rowIndex, int colIndex, Object value) {
        return ExcelUtil.setCellValue(getSheet(), rowIndex, colIndex, value);
    }

    public String getCellValue(int rowIndex, int colIndex) {
        return ExcelUtil.getCellValue(getSheet(), rowIndex, colIndex);
    }

    public Cell setCellStyle(int rowIndex, int colIndex, CellStyle cellStyle) {
        return ExcelUtil.setCellStyle(getSheet(), rowIndex, colIndex, cellStyle);
    }

    public CellStyle getCellStyle(int rowIndex, int colIndex) {
        return ExcelUtil.getCellStyle(getSheet(), rowIndex, colIndex);
    }

    public CellStyle createDefaultCellStyle() {
        return ExcelUtil.createDefaultCellStyle(getWorkbook());
    }

    public CellStyle createDefaultDoubleCellStyle() {
        return ExcelUtil.createDefaultDoubleCellStyle(getWorkbook());
    }

    public Cell setCellComment(int rowIndex, int colIndex, Comment comment) {
        return ExcelUtil.setCellComment(getSheet(), rowIndex, colIndex, comment);
    }

    public Comment getCellComment(int rowIndex, int colIndex) {
        return ExcelUtil.getCellComment(getSheet(), rowIndex, colIndex);
    }

    public short getCellFontSize(int rowIndex, int colIndex) {
        return ExcelUtil.getCellFontSize(getSheet(), rowIndex, colIndex);
    }

    public Cell getCell(int rowIndex, int colIndex) {
        return ExcelUtil.getCell(getSheet(), rowIndex, colIndex);
    }

    public void setRowHeight(int rowIndex, short rowHeight) {
        ExcelUtil.setRowHeight(getSheet(), rowIndex, rowHeight);
    }

    public short getRowHeight(int rowIndex) {
        return ExcelUtil.getRowHeight(getSheet(), rowIndex);
    }

    public Row getRow(int rowIndex) {
        return ExcelUtil.getRow(getSheet(), rowIndex);
    }

    public void removeRow(int rowIndex) {
        ExcelUtil.removeRow(getSheet(), rowIndex);
    }

    public boolean isEmptyRow(int rowIndex) {
        return ExcelUtil.isEmptyRow(getSheet(), rowIndex);
    }

    public int getLastRowNum() {
        return ExcelUtil.getLastRowNum(getSheet());
    }

    public void freezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        ExcelUtil.freezePane(getSheet(), colSplit, rowSplit, leftmostColumn, topRow);
    }

    public void copyMergeRegionToTargetSheet(Sheet toSheet, int startRowNum) {
        ExcelUtil.copyMergeRegion(getSheet(), toSheet, startRowNum);
    }

    public void copyMergeRegionFromTargetSheet(Sheet fromSheet, int startRowNum) {
        ExcelUtil.copyMergeRegion(fromSheet, getSheet(), startRowNum);
    }

    public Map<Integer, String> getRowValueMap(int rowIndex) {
        return ExcelUtil.getRowValueMap(getSheet(), rowIndex);
    }

    public void insertRows(int startRow, int rowCount) {
        ExcelUtil.insertRows(getSheet(), startRow, rowCount);
    }

    public void copyCellValue(int fromRowIndex, int fromColIndex, int toRowIndex, int toColIndex) {
        ExcelUtil.copyCellValue(getSheet(), fromRowIndex, fromColIndex, getSheet(), toRowIndex, toColIndex);
    }

    public void copyRow(int fromRowIndex, int toRowIndex, boolean isCopyCellValue, boolean isCopyRowHeight,
                        boolean isCopyCellStyle, boolean isCopyCellComment) {
        ExcelUtil.copyRow(getSheet(), fromRowIndex, getSheet(), toRowIndex, isCopyCellValue, isCopyRowHeight, isCopyCellStyle, isCopyCellComment);
    }

    public void copySheetRowFollowToTargetSheet(String toSheetName, boolean ignoreEmptyRow) {
        ExcelUtil.copySheetFollow(getSheet(), createSheet(toSheetName), ignoreEmptyRow);
    }

    public void rowHeightAutoFit(CellRangeAddress cellRangeAddress) {
        ExcelUtil.rowHeightAutoFit(getSheet(), cellRangeAddress);
    }

    public byte[] getBytes() {
        return ExcelUtil.getBytes(getWorkbook());
    }

    public InputStream getInputStream() {
        return ExcelUtil.getInputStream(getWorkbook());
    }

    public ByteArrayOutputStream getOutputStream() {
        return ExcelUtil.getOutputStream(getWorkbook());
    }

    public File save(@NonNull String filePath) {
        return ExcelUtil.save(getWorkbook(), filePath);
    }

    public File saveTemp(@NonNull String prefix, @NonNull String suffix) {
        return ExcelUtil.saveTemp(getWorkbook(), prefix, suffix);
    }

    public void closeSource() {
        ExcelUtil.closeSource(getWorkbook());
    }

}
