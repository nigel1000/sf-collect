package com.common.collect.framework.excel;

import com.common.collect.framework.excel.base.ExcelConstants;
import com.common.collect.framework.excel.client.PoiClient;
import com.common.collect.lib.api.excps.UnifiedException;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
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
        return PoiClient.getSheet(getWorkbook(), sheetName);
    }

    public void removeSheet(@NonNull String sheetName) {
        PoiClient.removeSheet(getWorkbook(), sheetName);
    }

    public int getSheetIndex() {
        return PoiClient.getSheetIndex(getWorkbook(), getSheet());
    }

    public void changeSheet(@NonNull String sheetName) {
        setSheet(PoiClient.getSheet(getWorkbook(), sheetName));
        PoiClient.setActiveSheet(getWorkbook(), getSheetIndex());
    }

    public void changeSheet(int sheetIndex) {
        setSheet(PoiClient.getSheet(getWorkbook(), sheetIndex));
        PoiClient.setActiveSheet(getWorkbook(), getSheetIndex());
    }

    public void setSheetName(int sheetIndex, String sheetName) {
        PoiClient.setSheetName(getWorkbook(), sheetIndex, sheetName);
    }

    public int getActiveSheetIndex() {
        return PoiClient.getActiveSheetIndex(getWorkbook());
    }

    public void setCellWidth(int colIndex, int colWidth) {
        PoiClient.setColumnWidth(getSheet(), colIndex, colWidth);
    }

    public int getCellWidth(int colIndex) {
        return PoiClient.getColumnWidth(getSheet(), colIndex);
    }

    public void setHiddenColumn(int colIndex, boolean isHidden) {
        PoiClient.setColumnHidden(getSheet(), colIndex, isHidden);
    }

    public boolean isHiddenColumn(int colIndex) {
        return PoiClient.isColumnHidden(getSheet(), colIndex);
    }

    public Cell setCellValue(int rowIndex, int colIndex, Object value) {
        return PoiClient.setCellValue(getSheet(), rowIndex, colIndex, value);
    }

    public String getCellValue(int rowIndex, int colIndex) {
        return PoiClient.getCellValue(getSheet(), rowIndex, colIndex);
    }

    public Cell setCellStyle(int rowIndex, int colIndex, CellStyle cellStyle) {
        return PoiClient.setCellStyle(getSheet(), rowIndex, colIndex, cellStyle);
    }

    public CellStyle getCellStyle(int rowIndex, int colIndex) {
        return PoiClient.getCellStyle(getSheet(), rowIndex, colIndex);
    }

    public CellStyle createDefaultCellStyle() {
        return PoiClient.createDefaultCellStyle(getWorkbook());
    }

    public CellStyle createDefaultDoubleCellStyle() {
        return PoiClient.createDefaultDoubleCellStyle(getWorkbook());
    }

    public Cell setCellComment(int rowIndex, int colIndex, Comment comment) {
        return PoiClient.setCellComment(getSheet(), rowIndex, colIndex, comment);
    }

    public Comment getCellComment(int rowIndex, int colIndex) {
        return PoiClient.getCellComment(getSheet(), rowIndex, colIndex);
    }

    public short getCellFontSize(int rowIndex, int colIndex) {
        return PoiClient.getCellFontSize(getSheet(), rowIndex, colIndex);
    }

    public Cell getCell(int rowIndex, int colIndex) {
        return PoiClient.getCell(getSheet(), rowIndex, colIndex);
    }

    public void setRowHeight(int rowIndex, short rowHeight) {
        PoiClient.setRowHeight(getSheet(), rowIndex, rowHeight);
    }

    public short getRowHeight(int rowIndex) {
        return PoiClient.getRowHeight(getSheet(), rowIndex);
    }

    public Row getRow(int rowIndex) {
        return PoiClient.getRow(getSheet(), rowIndex);
    }

    public void removeRow(int rowIndex) {
        PoiClient.removeRow(getSheet(), rowIndex);
    }

    public boolean isEmptyRow(int rowIndex) {
        return PoiClient.isEmptyRow(getSheet(), rowIndex);
    }

    public int getLastRowNum() {
        return PoiClient.getLastRowNum(getSheet());
    }

    public void freezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        PoiClient.freezePane(getSheet(), colSplit, rowSplit, leftmostColumn, topRow);
    }

    public void copyMergeRegionToTargetSheet(Sheet toSheet, int startRowNum) {
        PoiClient.copyMergeRegion(getSheet(), toSheet, startRowNum);
    }

    public void copyMergeRegionFromTargetSheet(Sheet fromSheet, int startRowNum) {
        PoiClient.copyMergeRegion(fromSheet, getSheet(), startRowNum);
    }

    public Map<Integer, String> getRowValueMap(int rowIndex) {
        return PoiClient.getRowValueMap(getSheet(), rowIndex);
    }

    public List<String> getRowValueList(int rowIndex) {
        return PoiClient.getRowValueList(getSheet(), rowIndex);
    }

    public void insertRows(int startRow, int rowCount) {
        PoiClient.insertRows(getSheet(), startRow, rowCount);
    }

    public void copyCellValue(int fromRowIndex, int fromColIndex, int toRowIndex, int toColIndex) {
        PoiClient.copyCellValue(getSheet(), fromRowIndex, fromColIndex, getSheet(), toRowIndex, toColIndex);
    }

    public void copyRow(int fromRowIndex, int toRowIndex, boolean isCopyCellValue, boolean isCopyRowHeight,
                        boolean isCopyCellStyle, boolean isCopyCellComment) {
        PoiClient.copyRow(getSheet(), fromRowIndex, getSheet(), toRowIndex, isCopyCellValue, isCopyRowHeight, isCopyCellStyle, isCopyCellComment);
    }

    public void copySheetRowFollowToTargetSheet(String toSheetName, boolean ignoreEmptyRow) {
        PoiClient.copySheetFollow(getSheet(), createSheet(toSheetName), ignoreEmptyRow);
    }

    public void rowHeightAutoFit(CellRangeAddress cellRangeAddress) {
        PoiClient.rowHeightAutoFit(getSheet(), cellRangeAddress);
    }

    public byte[] getBytes() {
        return PoiClient.getBytes(getWorkbook());
    }

    public InputStream getInputStream() {
        return PoiClient.getInputStream(getWorkbook());
    }

    public ByteArrayOutputStream getOutputStream() {
        return PoiClient.getOutputStream(getWorkbook());
    }

    public File save(@NonNull String filePath) {
        return PoiClient.save(getWorkbook(), filePath);
    }

    public File saveTemp(@NonNull String prefix, @NonNull String suffix) {
        return PoiClient.saveTemp(getWorkbook(), prefix, suffix);
    }

    public void closeSource() {
        PoiClient.closeSource(getWorkbook());
    }

}
