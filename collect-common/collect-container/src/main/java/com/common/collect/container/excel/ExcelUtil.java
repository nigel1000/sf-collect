package com.common.collect.container.excel;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FileUtil;
import com.common.collect.util.ValidUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/4/12.
 */
public class ExcelUtil {

    // 根据表格名获取表格，如果为空则创建
    public static Sheet getSheet(@NonNull Workbook workbook, @NonNull String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        return sheet;
    }

    // 根据下标获取表格
    public static Sheet getSheet(@NonNull Workbook workbook, int sheetIndex) {
        return workbook.getSheetAt(sheetIndex);
    }

    // 根据表格名删除表格
    public static void removeSheet(@NonNull Workbook workbook, @NonNull String sheetName) {
        int sheetIndex = workbook.getSheetIndex(sheetName);
        if (sheetIndex >= 0) {
            workbook.removeSheetAt(sheetIndex);
        }
    }

    // 根据下标删除表格
    public static void removeSheet(@NonNull Workbook workbook, int sheetIndex) {
        workbook.removeSheetAt(sheetIndex);
    }

    // 根据下标设置表格名
    public static void setSheetName(@NonNull Workbook workbook, int sheetIndex, @NonNull String sheetName) {
        workbook.setSheetName(sheetIndex, sheetName);
    }

    // 获取当前激活表格下标
    public static int getActiveSheetIndex(@NonNull Workbook workbook) {
        return workbook.getActiveSheetIndex();
    }

    // 设置表格列宽
    public static void setColumnWidth(@NonNull Sheet sheet, int colIndex, int colWidth) {
        sheet.setColumnWidth(colIndex, colWidth);
    }

    // 获取表格列宽
    public static int getColumnWidth(@NonNull Sheet sheet, int colIndex) {
        return sheet.getColumnWidth(colIndex);
    }

    // 设置表格隐藏
    public static void setColumnHidden(@NonNull Sheet sheet, int colIndex, boolean isHidden) {
        sheet.setColumnHidden(colIndex, isHidden);
    }

    // 获取表格隐藏
    public static boolean isColumnHidden(@NonNull Sheet sheet, int colIndex) {
        return sheet.isColumnHidden(colIndex);
    }

    // 获取行，不存在则创建
    public static Row getRow(@NonNull Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    // 设置行高
    public static void setRowHeight(@NonNull Sheet sheet, int rowIndex, short rowHeight) {
        getRow(sheet, rowIndex).setHeight(rowHeight);
    }

    // 获取行高
    public static short getRowHeight(@NonNull Sheet sheet, int rowIndex) {
        return getRow(sheet, rowIndex).getHeight();
    }

    // 获取表格使用行数
    public static int getLastRowNum(@NonNull Sheet sheet) {
        return sheet.getLastRowNum();
    }

    // 删除一行 不保留位置
    public static void removeRow(@NonNull Sheet sheet, int rowIndex) {
        int lastRowNum = getLastRowNum(sheet);
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            // 将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        } else if (rowIndex == lastRowNum) {
            Row removingRow = getRow(sheet, rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }

    // 判断是否空行
    public static boolean isEmptyRow(@NonNull Sheet sheet, int rowIndex) {
        Iterator<Cell> cellIter = getRow(sheet, rowIndex).cellIterator();
        boolean isRowEmpty = true;
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String value = getCellValue(sheet, cell.getRowIndex(), cell.getColumnIndex());
            if (EmptyUtil.isNotEmpty(value)) {
                isRowEmpty = false;
                break;
            }
        }
        return isRowEmpty;
    }

    // 拷贝行
    public static void copyRow(@NonNull Sheet fromSheet, int fromRowIndex,
                               @NonNull Sheet toSheet, int toRowIndex,
                               boolean isCopyCellValue, boolean isCopyRowHeight,
                               boolean isCopyCellStyle, boolean isCopyCellComment) {
        // 设置行高
        if (isCopyRowHeight) {
            setRowHeight(toSheet, toRowIndex, getRowHeight(fromSheet, fromRowIndex));
        }
        for (Iterator<Cell> cellIt = getRow(fromSheet, fromRowIndex).cellIterator(); cellIt.hasNext(); ) {
            Cell fromCell = cellIt.next();
            Cell toCell = getCell(toSheet, toRowIndex, fromCell.getColumnIndex());
            // 样式
            if (isCopyCellStyle) {
                toCell.setCellStyle(fromCell.getCellStyle());
            }
            if (isCopyCellComment) {
                // 评论
                toCell.setCellComment(fromCell.getCellComment());
            }
            // 不同数据类型处理
            if (isCopyCellValue) {
                copyCellValue(fromSheet, fromRowIndex, fromCell.getColumnIndex(), toSheet, toRowIndex, toCell.getColumnIndex());
            }
        }
    }

    // 在 startRow 行后插入若干行 带样式 带行高
    public static void insertRows(@NonNull Sheet sheet, int startRow, int rowCount) {
        Row fromRow = getRow(sheet, startRow);
        for (int i = 1; i <= rowCount; i++) {
            Row toRow = getRow(sheet, startRow + i);
            copyRow(sheet, fromRow.getRowNum(), sheet, toRow.getRowNum(), false, true, true, false);
        }
    }

    // 获取单元格，不存在则创建
    public static Cell getCell(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        return getRow(sheet, rowIndex).getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    // 设置单元格描述
    public static Cell setCellComment(@NonNull Sheet sheet, int rowIndex, int colIndex, Comment comment) {
        Cell cell = getCell(sheet, rowIndex, colIndex);
        cell.setCellComment(comment);
        return cell;
    }

    // 获取单元格描述
    public static Comment getCellComment(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        return getCell(sheet, rowIndex, colIndex).getCellComment();
    }

    // 获取单元格字体大小
    public static short getCellFontSize(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        CellStyle cellStyle = getCellStyle(sheet, rowIndex, colIndex);
        Font font = sheet.getWorkbook().getFontAt(cellStyle.getFontIndex());
        return font.getFontHeightInPoints();
    }

    // 设置单元格值
    public static Cell setCellValue(@NonNull Sheet sheet, int rowIndex, int colIndex, Object value) {
        Cell cell = getCell(sheet, rowIndex, colIndex);
        if (value == null) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("");
        } else if (value.getClass() == String.class) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(String.valueOf(value));
        } else if (value.getClass() == Double.class || value.getClass() == double.class) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Double) value);
        } else if (value.getClass() == Float.class || value.getClass() == float.class) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Float) value);
        } else if (value.getClass() == BigDecimal.class) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value.getClass() == Date.class) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Date) value);
        } else if (value.getClass() == Integer.class || value.getClass() == int.class) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) value);
        } else if (value.getClass() == Long.class || value.getClass() == long.class) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Long) value);
        } else if (value.getClass() == Boolean.class || value.getClass() == boolean.class) {
            cell.setCellType(CellType.BOOLEAN);
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(String.valueOf(value));
        }
        return cell;
    }

    // 获取单元格值
    public static String getCellValue(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        String ret;
        Cell cell = getCell(sheet, rowIndex, colIndex);
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                ret = com.common.collect.util.DateUtil.format(date, ExcelConstants.DATE_FORMAT);
            } else {
                ret = NumberToTextConverter.toText(cell.getNumericCellValue());
            }
        } else if (cell.getCellTypeEnum() == CellType.STRING) {
            ret = cell.getRichStringCellValue().getString();
        } else if (cell.getCellTypeEnum() == CellType.BLANK) {
            ret = "";
        } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            ret = String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellTypeEnum() == CellType.ERROR) {
            ret = String.valueOf(cell.getErrorCellValue());
        } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
            ret = cell.getCellFormula();
        } else {
            ret = cell.getStringCellValue();
        }
        return ret.trim();
    }

    // 复制单元格
    public static void copyCellValue(@NonNull Sheet fromSheet, int fromRowIndex, int fromColIndex,
                                     @NonNull Sheet toSheet, int toRowIndex, int toColIndex) {
        Cell fromCell = getCell(fromSheet, fromRowIndex, fromColIndex);
        Cell toCell = getCell(toSheet, toRowIndex, toColIndex);
        CellType srcCellType = fromCell.getCellTypeEnum();
        toCell.setCellType(srcCellType);
        if (srcCellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(fromCell)) {
                toCell.setCellValue(fromCell.getDateCellValue());
            } else {
                toCell.setCellValue(fromCell.getNumericCellValue());
            }
        } else if (srcCellType == CellType.STRING) {
            toCell.setCellValue(fromCell.getRichStringCellValue());
        } else if (srcCellType == CellType.BLANK) {
            toCell.setCellValue(fromCell.getStringCellValue());
        } else if (srcCellType == CellType.BOOLEAN) {
            toCell.setCellValue(fromCell.getBooleanCellValue());
        } else if (srcCellType == CellType.ERROR) {
            toCell.setCellErrorValue(fromCell.getErrorCellValue());
        } else if (srcCellType == CellType.FORMULA) {
            toCell.setCellFormula(fromCell.getCellFormula());
        } else {
            toCell.setCellValue(fromCell.getRichStringCellValue());
        }
    }

    // 设置单元格样式
    public static Cell setCellStyle(@NonNull Sheet sheet, int rowIndex, int colIndex, CellStyle cellStyle) {
        Cell cell = getCell(sheet, rowIndex, colIndex);
        if (cellStyle == null) {
            return cell;
        }
        cell.setCellStyle(cellStyle);
        return cell;
    }

    // 获取单元格样式
    public static CellStyle getCellStyle(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        return getCell(sheet, rowIndex, colIndex).getCellStyle();
    }

    // 资源缓存
    protected static final String defaultStyle = "default_style";
    protected static final String defaultNumberStyle = "default_number_style";
    protected static Map<String, CellStyle> cellStylesMap = Maps.newHashMap();

    // 默认样式
    public static CellStyle createDefaultCellStyle(@NonNull Workbook workbook) {
        CellStyle defaultCellStyle = cellStylesMap.get(defaultStyle);
        if (defaultCellStyle != null) {
            return defaultCellStyle;
        }
        CellStyle cellStyle = workbook.createCellStyle();
        // 单元格靠左
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        // 单元格置顶
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        // 单元格内容显示不下时自动换行
        cellStyle.setWrapText(true);
        // toStyle.setAlignment(fromStyle.getAlignment());
        // // 边框和边框颜色
        // toStyle.setBorderBottom(fromStyle.getBorderBottom());
        // toStyle.setBorderLeft(fromStyle.getBorderLeft());
        // toStyle.setBorderRight(fromStyle.getBorderRight());
        // toStyle.setBorderTop(fromStyle.getBorderTop());
        // toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        // toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        // toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        // toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());
        // // 背景和前景
        // toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        // toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());
        // toStyle.setDataFormat(fromStyle.getDataFormat());
        // toStyle.setFillPattern(fromStyle.getFillPattern());
        // toStyle.setHidden(fromStyle.getHidden());
        // toStyle.setIndention(fromStyle.getIndention());// 首行缩进
        // toStyle.setLocked(fromStyle.getLocked());
        // toStyle.setRotation(fromStyle.getRotation());// 旋转
        // toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
        // toStyle.setWrapText(fromStyle.getWrapText());
        cellStylesMap.putIfAbsent(defaultStyle, cellStyle);
        return cellStyle;
    }

    // 默认样式
    public static CellStyle createDefaultDoubleCellStyle(@NonNull Workbook workbook) {
        CellStyle numberCellStyle = cellStylesMap.get(defaultNumberStyle);
        if (numberCellStyle != null) {
            return numberCellStyle;
        }
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(createDefaultCellStyle(workbook));
        cellStyle.setDataFormat(workbook.createDataFormat().getFormat(ExcelConstants.NUMBER_FORMAT));
        cellStylesMap.putIfAbsent(defaultNumberStyle, cellStyle);
        return cellStyle;
    }

    // 冻结窗口
    public static void freezePane(@NonNull Sheet sheet, int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
    }

    // 拷贝合并区域到 toSheet 的 startRowNum 行后区域
    public static void copyMergeRegion(Sheet fromSheet, Sheet toSheet, int startRowNum) {
        int num = fromSheet.getNumMergedRegions();
        if (startRowNum > 0) {
            startRowNum++;
        }
        for (int i = 0; i < num; i++) {
            CellRangeAddress cellRangeAddress = fromSheet.getMergedRegion(i);
            cellRangeAddress.setFirstRow(startRowNum + cellRangeAddress.getFirstRow());
            cellRangeAddress.setLastRow(startRowNum + cellRangeAddress.getLastRow());
            toSheet.addMergedRegion(cellRangeAddress);
        }
    }

    // key是列下标，value是单元格值
    public static Map<Integer, String> getRowValueMap(@NonNull Sheet sheet, int rowIndex) {
        Map<Integer, String> ret = Maps.newHashMap();
        Row row = getRow(sheet, rowIndex);
        // row.getLastCellNum() 不是从0开始的
        int lastCellNum = row.getLastCellNum();
        for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
            ret.put(colIndex, getCellValue(sheet, rowIndex, colIndex));
        }
        return ret;
    }

    public static List<String> getRowValueList(@NonNull Sheet sheet, int rowIndex) {
        List<String> ret = Lists.newArrayList();
        Row row = getRow(sheet, rowIndex);
        // row.getLastCellNum() 不是从0开始的
        int lastCellNum = row.getLastCellNum();
        for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
            ret.add(colIndex, getCellValue(sheet, rowIndex, colIndex));
        }
        return ret;
    }

    // 复制sheet
    public static void copySheetFollow(@NonNull Sheet fromSheet, @NonNull Sheet toSheet, boolean ignoreEmptyRow) {
        // 合并区域处理
        int startRowNum = toSheet.getLastRowNum();
        copyMergeRegion(fromSheet, toSheet, startRowNum);
        if (startRowNum != 0) {
            startRowNum++;
        }
        int fromRowNum = 0;
        for (Iterator<Row> rowIt = fromSheet.rowIterator(); rowIt.hasNext(); ) {
            Row fromRow = rowIt.next();
            if (ignoreEmptyRow && isEmptyRow(fromSheet, fromRow.getRowNum())) {
                continue;
            }
            copyRow(fromSheet, fromRow.getRowNum(), toSheet, startRowNum + fromRowNum, true, true, true, true);
            fromRowNum++;
        }
    }

    // 获取单元格近似列宽(像素)
    public static float getCellApproximateWidth(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        int first = colIndex;
        int last = colIndex;
        int sheetMergeCount = sheet.getNumMergedRegions();
        // 判断该单元格是否是合并区域的内容
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            int firstColumn = cellRangeAddress.getFirstColumn();
            int lastColumn = cellRangeAddress.getLastColumn();
            int firstRow = cellRangeAddress.getFirstRow();
            int lastRow = cellRangeAddress.getLastRow();
            // 如果cell在合并单元格中
            if (rowIndex >= firstRow && rowIndex <= lastRow && colIndex >= firstColumn && colIndex <= lastColumn) {
                first = firstColumn;
                last = lastColumn;
                break;
            }
        }
        float totalWidthInPixels = 0;
        for (int i = first; i <= last; i++) {
            totalWidthInPixels += getColumnWidth(sheet, i);
        }
        return totalWidthInPixels * 1.2F * getCellFontSize(sheet, rowIndex, colIndex) / 2 / 256;
    }

    // 获取单元格内容的字符宽度
    // 中文字符算两个
    public static int getCellValueLength(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        String value = getCellValue(sheet, rowIndex, colIndex);
        return ValidUtil.getCharLength(value);
    }

    // 获取近似行高
    public static float getCellApproximateHeight(@NonNull Sheet sheet, int rowIndex, int colIndex) {
        short fontSize = getCellFontSize(sheet, rowIndex, colIndex);
        float cellWidth = getCellApproximateWidth(sheet, rowIndex, colIndex);
        int contentLen = getCellValueLength(sheet, rowIndex, colIndex);
        return fontSize * fontSize * contentLen / cellWidth + 1;
    }

    // 设置合适的行高
    public static void rowHeightAutoFit(@NonNull Sheet sheet, CellRangeAddress cellRangeAddress) {
        int firstRow = cellRangeAddress.getFirstRow();
        int firstColumn = cellRangeAddress.getFirstColumn();
        int lastRow = cellRangeAddress.getLastRow();
        int lastColumn = cellRangeAddress.getLastColumn();
        for (int row = firstRow; row <= lastRow; row++) {
            float rowHeightLimit = 35;
            for (int column = firstColumn; column <= lastColumn; column++) {
                float tmpHeight = getCellApproximateHeight(sheet, row, column);
                if (tmpHeight > rowHeightLimit) {
                    rowHeightLimit = tmpHeight;
                }
            }
            getRow(sheet, row).setHeightInPoints(rowHeightLimit);
        }
    }

    // 获取byte数组
    public static byte[] getBytes(@NonNull Workbook workbook) {
        return getOutputStream(workbook).toByteArray();
    }

    // workbook 转换成 InputStream
    public static InputStream getInputStream(@NonNull Workbook workbook) {
        return new ByteArrayInputStream(getBytes(workbook));
    }

    // workbook 转换成 OutputStream
    public static ByteArrayOutputStream getOutputStream(@NonNull Workbook workbook) {
        if (workbook == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            workbook.write(os);
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "workbook转换成OutputStream!", ex);
        }
        return os;
    }

    // 保存到此文件地址
    public static File save(@NonNull Workbook workbook, @NonNull String filePath) {
        try {
            File temp = new File(filePath);
            FileOutputStream out = new FileOutputStream(temp);
            workbook.write(out);
            return temp;
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, filePath + "保存失败", ex);
        }
    }

    // 保存到临时目录
    public static File saveTemp(@NonNull Workbook workbook, @NonNull String prefix, @NonNull String suffix) {
        try {
            File temp = FileUtil.saveTempFile(prefix, suffix);
            FileOutputStream out = new FileOutputStream(temp);
            workbook.write(out);
            return temp;
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, prefix + suffix + "保存到临时目录失败", ex);
        }
    }

    // 关闭 workbook
    public static void closeSource(@NonNull Workbook workbook) {
        try {
            workbook.close();
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "释放资源失败", ex);
        }
    }

}
