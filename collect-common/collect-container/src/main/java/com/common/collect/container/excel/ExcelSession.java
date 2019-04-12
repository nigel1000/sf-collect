package com.common.collect.container.excel;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.util.FileUtil;
import com.common.collect.util.constant.Constants;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by hznijianfeng on 2019/3/7.
 */

@Slf4j
@Data
public class ExcelSession {

    protected Workbook workbook;
    protected Sheet sheet;

    protected ExcelSession() {}

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

    ///////////////////////////////// sheet////////////////////////////////
    // 创建sheet
    public Sheet createSheet(@NonNull String name) {
        Sheet tmp = this.getWorkbook().getSheet(name);
        if (tmp == null) {
            tmp = this.getWorkbook().createSheet(name);
        }
        return tmp;
    }

    public void removeSheet(@NonNull String name) {
        int sheetIndex = this.getWorkbook().getSheetIndex(name);
        if (sheetIndex >= 0) {
            this.getWorkbook().removeSheetAt(sheetIndex);
        }
    }

    // 根据名称获取或者创建sheet
    public void changeSheet(@NonNull String name) {
        Sheet tmp = this.getWorkbook().getSheet(name);
        if (tmp == null) {
            setSheet(createSheet(name));
        } else {
            setSheet(tmp);
        }
    }

    // 根据下标获取或者创建sheet
    public void changeSheet(int index) {
        setSheet(this.getWorkbook().getSheetAt(index));
    }

    // 根据下标修改sheet名称
    public void setSheetName(int index, String name) {
        this.getWorkbook().setSheetName(index, name);
    }

    public int getActiveSheetIndex() {
        return getWorkbook().getActiveSheetIndex();
    }

    ///////////////////////////////// sheet////////////////////////////////

    ///////////////////////////////// row&col////////////////////////////////
    // 设置单元格列的宽度
    public void setCellWidth(int colIndex, int colWidth) {
        this.getSheet().setColumnWidth(colIndex, colWidth);
    }

    public int getCellWidth(int colIndex) {
        return this.getSheet().getColumnWidth(colIndex);
    }

    // 隐藏某一列
    public void setHiddenColumn(int colIndex, boolean isHidden) {
        this.getSheet().setColumnHidden(colIndex, isHidden);
    }

    public boolean isHiddenColumn(int colIndex) {
        return this.getSheet().isColumnHidden(colIndex);
    }

    // 设置单元格的值
    public Cell setCellValue(int rowIndex, int colIndex, Object value) {
        Cell cell = this.getCell(rowIndex, colIndex);
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

    public String getCellValue(int rowIndex, int colIndex) {
        String ret;
        Cell cell = this.getCell(rowIndex, colIndex);
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

    // 设置单元格并指定样式
    public Cell setCellStyle(int rowIndex, int colIndex, CellStyle cellStyle) {
        Cell cell = this.getCell(rowIndex, colIndex);
        if (cellStyle == null) {
            return cell;
        }
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public CellStyle getCellStyle(int rowIndex, int colIndex) {
        Cell cell = this.getCell(rowIndex, colIndex);
        return cell.getCellStyle();
    }

    // 资源缓存
    protected static final String defaultStyle = "default_style";
    protected static final String defaultNumberStyle = "default_number_style";
    protected Map<String, CellStyle> cellStylesMap = Maps.newHashMap();

    public CellStyle createDefaultCellStyle() {
        CellStyle defaultCellStyle = cellStylesMap.get(defaultStyle);
        if (defaultCellStyle != null) {
            return defaultCellStyle;
        }
        CellStyle cellStyle = getWorkbook().createCellStyle();
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

    public CellStyle createDefaultDoubleCellStyle() {
        CellStyle numberCellStyle = cellStylesMap.get(defaultNumberStyle);
        if (numberCellStyle != null) {
            return numberCellStyle;
        }
        CellStyle cellStyle = getWorkbook().createCellStyle();
        cellStyle.cloneStyleFrom(createDefaultCellStyle());
        cellStyle.setDataFormat(this.getWorkbook().createDataFormat().getFormat(ExcelConstants.NUMBER_FORMAT));
        cellStylesMap.putIfAbsent(defaultNumberStyle, cellStyle);
        return cellStyle;
    }

    // 设置单元格描述
    public Cell setCellComment(int rowIndex, int colIndex, Comment comment) {
        Cell cell = this.getCell(rowIndex, colIndex);
        cell.setCellComment(comment);
        return cell;
    }

    public Comment getCellComment(int rowIndex, int colIndex) {
        Cell cell = this.getCell(rowIndex, colIndex);
        return cell.getCellComment();
    }

    // 获取单元格字体大小
    public short getCellFontSize(int rowIndex, int colIndex) {
        CellStyle cellStyle = getCellStyle(rowIndex, colIndex);
        Font font = getWorkbook().getFontAt(cellStyle.getFontIndex());
        return font.getFontHeightInPoints();
    }

    // 获取cell
    public Cell getCell(int rowIndex, int colIndex) {
        return getRow(rowIndex).getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    // 设置单元格列的宽度
    public void setRowHeight(int rowIndex, short rowHeight) {
        getRow(rowIndex).setHeight(rowHeight);
    }

    public short getRowHeight(int rowIndex) {
        return getRow(rowIndex).getHeight();
    }

    // 创建 row
    public Row getRow(int rowIndex) {
        Row row = this.getSheet().getRow(rowIndex);
        if (row == null) {
            row = this.getSheet().createRow(rowIndex);
        }
        return row;
    }

    // 删除一行 不保留位置
    public void removeRow(int rowIndex) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            // 将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        } else if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }

    public boolean isEmptyRow(int rowIndex) {
        Iterator<Cell> cellIter = getRow(rowIndex).cellIterator();
        boolean isRowEmpty = true;
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String value = getCellValue(cell.getRowIndex(), cell.getColumnIndex());
            if (value != null && !"".equals(value.trim())) {
                isRowEmpty = false;
                break;
            }
        }
        return isRowEmpty;
    }

    public int getLastRowNum() {
        return this.getSheet().getLastRowNum();
    }

    // 冻结窗口 colSplit, rowSplit, leftmostColumn, topRow
    public void freezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        this.getSheet().createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
    }

    // 当前 sheet 拷贝到 toSheet，toSheet 的 startRowNum
    public void copyMergeRegionToTargetSheet(Sheet toSheet, int startRowNum) {
        int num = getSheet().getNumMergedRegions();
        if (startRowNum > 0) {
            startRowNum++;
        }
        for (int i = 0; i < num; i++) {
            CellRangeAddress cellRangeAddress = getSheet().getMergedRegion(i);
            cellRangeAddress.setFirstRow(startRowNum + cellRangeAddress.getFirstRow());
            cellRangeAddress.setLastRow(startRowNum + cellRangeAddress.getLastRow());
            toSheet.addMergedRegion(cellRangeAddress);
        }
    }

    // fromSheet 拷贝到 当前 sheet ，当前 sheet 的 startRowNum
    public void copyMergeRegionFromTargetSheet(Sheet fromSheet, int startRowNum) {
        int num = fromSheet.getNumMergedRegions();
        if (startRowNum > 0) {
            startRowNum++;
        }
        for (int i = 0; i < num; i++) {
            CellRangeAddress cellRangeAddress = fromSheet.getMergedRegion(i);
            cellRangeAddress.setFirstRow(startRowNum + cellRangeAddress.getFirstRow());
            cellRangeAddress.setLastRow(startRowNum + cellRangeAddress.getLastRow());
            getSheet().addMergedRegion(cellRangeAddress);
        }
    }


    public int getMergeColCount(int rowIndex, int colIndex) {
        int sheetMergeCount = this.getSheet().getNumMergedRegions();
        // 判断该单元格是否是合并区域的内容
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress cellRangeAddress = this.getSheet().getMergedRegion(i);
            int firstColumn = cellRangeAddress.getFirstColumn();
            int lastColumn = cellRangeAddress.getLastColumn();
            int firstRow = cellRangeAddress.getFirstRow();
            int lastRow = cellRangeAddress.getLastRow();
            // 如果cell在合并单元格中，则返回此合并单元格的列数
            if (rowIndex >= firstRow && rowIndex <= lastRow && colIndex >= firstColumn && colIndex <= lastColumn) {
                return lastColumn - firstColumn + 1;
            }
        }
        return 1;
    }

    public Map<Integer, String> getRowValueMap(int rowIndex) {
        Map<Integer, String> ret = Maps.newHashMap();
        Row row = getRow(rowIndex);
        // row.getLastCellNum() 不是从0开始的
        int lastCellNum = row.getLastCellNum();
        for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
            ret.put(colIndex, getCellValue(row.getRowNum(), colIndex));
        }
        return ret;
    }
    ///////////////////////////////// row&col////////////////////////////////


    ///////////////////////////////// compose////////////////////////////////

    // 在 startRow -1 行后插入若干行 带样式 带行高
    public void insertRows(int startRow, int rowCount) {
        if (getSheet().getRow(startRow - 1) == null) {
            throw UnifiedException.gen(startRow - 1 + " 行不存在");
        }
        Row fromRow = getRow(startRow - 1);
        for (int i = 0; i < rowCount; i++) {
            Row toRow = getRow(startRow + i);
            copyRow(fromRow, toRow, false, true, true, false);
        }
    }

    // 复制列
    public void copyCellValue(@NonNull Cell fromCell, @NonNull Cell toCell) {
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

    public void copyRow(@NonNull Row fromRow, @NonNull Row toRow, boolean isCopyCellValue, boolean isCopyRowHeight,
                        boolean isCopyCellStyle, boolean isCopyCellComment) {
        // 设置行高
        if (isCopyRowHeight) {
            toRow.setHeight(fromRow.getHeight());
        }
        for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
            Cell fromCell = cellIt.next();
            Cell toCell = toRow.createCell(fromCell.getColumnIndex());
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
                copyCellValue(fromCell, toCell);
            }
        }
    }

    // 复制sheet
    public void copySheetRowFollowToTargetSheet(String toSheetName, boolean ignoreEmptyRow) {
        Sheet toSheet = createSheet(toSheetName);
        // 合并区域处理
        int startRowNum = toSheet.getLastRowNum();
        copyMergeRegionToTargetSheet(toSheet, startRowNum);
        if (startRowNum != 0) {
            startRowNum++;
        }
        int fromRowNum = 0;
        for (Iterator<Row> rowIt = getSheet().rowIterator(); rowIt.hasNext();) {
            Row fromRow = rowIt.next();
            if (ignoreEmptyRow && isEmptyRow(fromRow.getRowNum())) {
                continue;
            }
            Row toRow = toSheet.createRow(startRowNum + fromRowNum);
            copyRow(fromRow, toRow, true, true, true, true);
            fromRowNum++;
        }
    }

    ///////////////////////////////// compose////////////////////////////////

    ///////////////////////////////// 智能////////////////////////////////
    // 获取近似列宽(像素)
    public float getCellApproximateWidth(int rowIndex, int colIndex) {
        Cell cell = this.getSheet().getRow(rowIndex).getCell(colIndex);
        int x = getMergeColCount(cell.getRowIndex(), cell.getColumnIndex());
        float totalWidthInPixels = 0;
        for (int i = 0; i < x; i++) {
            totalWidthInPixels += cell.getSheet().getColumnWidth(i + cell.getColumnIndex());
        }
        Font font = cell.getSheet().getWorkbook().getFontAt(cell.getCellStyle().getFontIndex());
        return totalWidthInPixels * 1.2F * font.getFontHeightInPoints() / 2 / 256;
    }

    // 获取单元格内容的字符宽度
    public int getCellValueLength(int rowIndex, int colIndex) {
        String value = getCellValue(rowIndex, colIndex);
        int num = 0;
        for (int index = 0; index < value.length(); index++) {
            Matcher matcher = Pattern.compile(Constants.CHINESE_REG_EX).matcher(value.substring(index, index + 1));
            if (matcher.find()) {
                num++;
            }
        }
        return num + value.length();
    }

    // 获取近似行高
    public float getCellApproximateHeight(int rowIndex, int colIndex) {
        short fontSize = getCellFontSize(rowIndex, colIndex);
        float cellWidth = getCellApproximateWidth(rowIndex, colIndex);
        int contentLen = getCellValueLength(rowIndex, colIndex);
        return fontSize * fontSize * contentLen / cellWidth + 1;
    }

    // 设置合适的行高
    public void rowHeightAutoFit(CellRangeAddress cellRangeAddress) {
        int firstRow = cellRangeAddress.getFirstRow();
        int firstColumn = cellRangeAddress.getFirstColumn();
        int lastRow = cellRangeAddress.getLastRow();
        int lastColumn = cellRangeAddress.getLastColumn();
        for (int row = firstRow; row <= lastRow; row++) {
            float rowHeightLimit = 35;
            for (int column = firstColumn; column <= lastColumn; column++) {
                float tmpHeight = getCellApproximateHeight(row, column);
                if (tmpHeight > rowHeightLimit) {
                    rowHeightLimit = tmpHeight;
                }
            }
            getRow(row).setHeightInPoints(rowHeightLimit);
        }
    }
    ///////////////////////////////// 智能////////////////////////////////

    ///////////////////////////////// 转换////////////////////////////////
    // 获取byte数组
    public byte[] getBytes() {
        return getOutputStream().toByteArray();
    }

    // workbook转换成InputStream
    public InputStream getInputStream() {
        return new ByteArrayInputStream(getBytes());
    }

    // workbook转换成OutputStream
    public ByteArrayOutputStream getOutputStream() {
        if (workbook == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            getWorkbook().write(os);
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "workbook转换成OutputStream!", ex);
        }
        return os;
    }

    // 获取byte数组
    public File save(@NonNull String filePath) {
        try {
            File temp = new File(filePath);
            FileOutputStream out = new FileOutputStream(temp);
            getWorkbook().write(out);
            return temp;
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, filePath + "保存失败", ex);
        }
    }

    public File saveTemp(@NonNull String prefix, @NonNull String suffix) {
        try {
            File temp = FileUtil.saveTempFile(prefix, suffix);
            FileOutputStream out = new FileOutputStream(temp);
            getWorkbook().write(out);
            return temp;
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, prefix + suffix + "保存到临时目录失败", ex);
        }
    }

    public void closeSource() {
        try {
            getWorkbook().close();
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
        } catch (IOException ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "释放资源失败", ex);
        }
    }
    ///////////////////////////////// 转换////////////////////////////////

}
