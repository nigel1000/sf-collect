package com.common.collect.framework.excel;

import com.common.collect.framework.excel.context.EventModelContext;
import com.common.collect.framework.excel.define.IEventModelParseHandler;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.FileUtil;
import com.common.collect.lib.util.IdUtil;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.lib.util.okhttp.HttpUtil;
import lombok.NonNull;
import lombok.Setter;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

public class SaxReader extends DefaultHandler {

    private final static int need_read_row_num_mark_code = 5267386;
    // 提供三种方式设置文件： 流，url，文件
    private File excelFile;
    // 业务逻辑处理器
    private IEventModelParseHandler bizHandler;
    @Setter
    // 每个sheet取多少数据就停止解析xml，默认全部
    private int needReadRowNum = Integer.MAX_VALUE;
    @Setter
    // 表格列数 一行数据列数如果不足此数，自动填充空字符串
    private int needReadColNum = 1;
    @Setter
    // 每取到多少数据后交给 handler 进行处理，解决业务数据占内存问题，默认100
    private int batchHandleSize = 100;
    @Setter
    // 需要解析的sheet下标，默认解析全部，从1开始，非0开始
    private List<Integer> parseSheetIndex = new ArrayList<>(Arrays.asList(1));
    @Setter
    // 是否处理格式化数据
    private boolean needDataFormat = false;

    private List<List<String>> rows = new LinkedList<>();
    private CellContext cellContext = new CellContext();

    private EventModelContext eventModelContext;

    private SharedStringsTable sharedStringsTable;
    private StylesTable stylesTable;

    public SaxReader(@NonNull Object excelFile, @NonNull IEventModelParseHandler bizHandler) {
        if (excelFile instanceof InputStream) {
            excelFile((InputStream) excelFile);
        } else if (excelFile instanceof String) {
            excelFile((String) excelFile);
        } else if (excelFile instanceof File) {
            excelFile((File) excelFile);
        } else {
            throw UnifiedException.gen("非合法参数");
        }
        this.bizHandler = bizHandler;
    }

    private void excelFile(@NonNull InputStream is) {
        this.excelFile = FileUtil.saveTempFile(is, IdUtil.timeDiy("excel"), ".xlsx");
    }

    private void excelFile(@NonNull String url) {
        this.excelFile = FileUtil.saveTempFile(HttpUtil.getInputStream(url), IdUtil.timeDiy("excel"), ".xlsx");
    }

    private void excelFile(@NonNull File file) {
        this.excelFile = file;
    }

    public void processSheet() {
        Iterator<InputStream> sheets;
        XMLReader xmlReader;
        try {
            OPCPackage opcPackage = OPCPackage.open(this.excelFile.getAbsolutePath());

            XSSFReader xssfReader = new XSSFReader(opcPackage);
            sharedStringsTable = xssfReader.getSharedStringsTable();
            stylesTable = xssfReader.getStylesTable();
            sheets = xssfReader.getSheetsData();

            xmlReader = SAXHelper.newXMLReader(); //XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            xmlReader.setContentHandler(this);
        } catch (Exception e) {
            throw UnifiedException.gen("processSheet init", e);
        }
        int currentSheetIndex = 0;
        while (sheets.hasNext()) {
            currentSheetIndex++;
            if (parseSheetIndex == null || parseSheetIndex.contains(currentSheetIndex)) {
                try {
                    eventModelContext = new EventModelContext();
                    eventModelContext.setNeedReadColNum(needReadColNum);
                    eventModelContext.setCurSheetIndex(currentSheetIndex);
                    eventModelContext.setSheetStart(true);
                    InputStream sheet = sheets.next();
                    InputSource sheetSource = new InputSource(sheet);
                    xmlReader.parse(sheetSource);
                    sheet.close();
                } catch (UnifiedException ex) {
                    switch (ex.getErrorCode()) {
                        case need_read_row_num_mark_code:
                            break;
                        default:
                            throw ex;
                    }
                } catch (Exception e) {
                    throw UnifiedException.gen("processSheet handle", e);
                } finally {
                    handleData();
                    // 一个 sheet 处理完后
                    eventModelContext = new EventModelContext();
                }
            } else {
                sheets.next();
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        // c => 单元格
        if ("c".equals(name)) {
            cellContext.curColRef = attributes.getValue("r").replaceAll("\\d", "");
            cellContext.oneRow.put(cellContext.curColRef, "");

            String cellType = attributes.getValue("t");
            // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
            if ("s".equals(cellType)) {
                cellContext.nextIsString = true;
            } else {
                cellContext.nextIsString = false;
            }
            // 设定单元格类型
            if ("b".equals(cellType)) {
                cellContext.nextCellDataType = CellDataType.BOOL;
            } else if ("e".equals(cellType)) {
                cellContext.nextCellDataType = CellDataType.ERROR;
            } else if ("inlineStr".equals(cellType)) {
                cellContext.nextCellDataType = CellDataType.INLINE_STR;
            } else if ("s".equals(cellType)) {
                cellContext.nextCellDataType = CellDataType.SST_INDEX;
            } else if ("str".equals(cellType)) {
                cellContext.nextCellDataType = CellDataType.FORMULA;
            } else {
                cellContext.nextCellDataType = CellDataType.NUMBER;
            }

            String cellStyleStr = attributes.getValue("s");
            cellContext.dataFormat = -1;
            cellContext.dataFormatStr = null;
            if (needDataFormat && cellStyleStr != null) {
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                cellContext.dataFormat = style.getDataFormat();
                cellContext.dataFormatStr = style.getDataFormatString();
            }
        }
        // 当元素为t时
        if ("t".equals(name)) {
            cellContext.isTElement = true;
        } else {
            cellContext.isTElement = false;
        }
        // 置空
        cellContext.lastContents = "";
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        // 根据SST的索引值的到单元格的真正要存储的字符串
        // 这时characters()方法可能会被调用多次
        if (cellContext.nextIsString && EmptyUtil.isNotBlank(cellContext.lastContents) && StringUtil.isNumeric(cellContext.lastContents)) {
            int idx = Integer.parseInt(cellContext.lastContents);
            cellContext.lastContents = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx)).toString();
            cellContext.nextIsString = false;
        }

        // t元素也包含字符串
        if (cellContext.isTElement) {
            // 将单元格内容加入 oneRow 中，在这之前先去掉字符串前后的空白符
            String value = cellContext.lastContents.trim();
            cellContext.oneRow.put(cellContext.curColRef, value);
            cellContext.isTElement = false;
        } else if ("v".equals(name)) {
            // v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
            String value = cellContext.lastContents.trim();
            if (EmptyUtil.isNotBlank(value) && cellContext.nextCellDataType != null) {
                switch (cellContext.nextCellDataType) {
                    case BOOL:
                        char first = value.charAt(0);
                        value = (first == '0') ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
                        break;
                    case ERROR:
                        break;
                    case FORMULA:
                        break;
                    case INLINE_STR:
                        break;
                    case SST_INDEX:
                        break;
                    case NUMBER:
                        if (EmptyUtil.isNotBlank(cellContext.dataFormatStr)) {
                            value = new DataFormatter().formatRawCellContents(Double.parseDouble(value), cellContext.dataFormat, cellContext.dataFormatStr).trim();
                        }
                        break;
                    default:
                        break;
                }
            }
            cellContext.oneRow.put(cellContext.curColRef, value);
        } else {
            // 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
            if (name.equals("row")) {
                try {
                    if (cellContext.isEmptyRow()) {
                        return;
                    }
                    if (eventModelContext.getSheetAlreadyReadRowNum() + 1 > needReadRowNum) {
                        handleData();
                        throw UnifiedException.gen(need_read_row_num_mark_code, "读取行数已大于每个 sheet 读取配置行数 " + needReadRowNum);
                    }
                    rows.add(cellContext.fetchColsWithPadding(needReadColNum));
                    eventModelContext.incrementRowNum();
                    if (rows.size() >= batchHandleSize) {
                        handleData();
                    }
                } finally {
                    // 一行数据处理完后
                    cellContext.finishOneRow();
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        cellContext.lastContents += new String(ch, start, length);
    }

    private void handleData() {
        if (EmptyUtil.isEmpty(rows)) {
            return;
        }
        eventModelContext.setRows(rows);
        bizHandler.handle(eventModelContext);
        // 一批业务数据处理完后
        rows = new LinkedList<>();
        eventModelContext.setSheetStart(false);
    }

    // 单元格中的数据可能的数据类型
    enum CellDataType {
        BOOL, ERROR, FORMULA, INLINE_STR, SST_INDEX, NUMBER,
        ;
    }

    private class CellContext {
        private String curColRef;
        private String lastContents;
        private boolean nextIsString;
        private boolean isTElement;
        private CellDataType nextCellDataType;
        private int dataFormat = -1;
        private String dataFormatStr;

        // ColMap <A-Z, Value>
        private Map<String, String> oneRow = new LinkedHashMap<>();

        public void finishOneRow() {
            oneRow = new LinkedHashMap<>();
        }

        public boolean isEmptyRow() {
            boolean isEmptyRow = true;
            for (Map.Entry<String, String> entry : oneRow.entrySet()) {
                String value = entry.getValue();
                if (EmptyUtil.isNotEmpty(value)) {
                    isEmptyRow = false;
                }
            }
            return isEmptyRow;
        }

        // 填充空字符串
        public List<String> fetchColsWithPadding(int needColNum) {
            List<String> cols = new LinkedList<>();
            for (Map.Entry<String, String> entry : oneRow.entrySet()) {
                cols.add(entry.getValue());
            }
            if (needColNum > cols.size()) {
                int time = needColNum - cols.size();
                for (int i = 0; i < time; i++) {
                    cols.add("");
                }
            }
            return cols;
        }

    }

}

