package com.common.collect.container.excel;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.HttpUtil;
import com.common.collect.container.excel.context.EventModelContext;
import com.common.collect.container.excel.define.IEventModelParseHandler;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FileUtil;
import com.common.collect.util.IdUtil;
import lombok.NonNull;
import lombok.Setter;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

public class EventModelReader {

    private final static int need_read_row_num_code = 50000;

    // 提供三种方式设置文件： 流，url，文件
    private File excelFile;

    // 业务逻辑处理器
    private IEventModelParseHandler handler;

    @Setter
    // 每个sheet取多少数据就停止解析xml，默认全部
    private Integer needReadRowNum;

    @Setter
    // 每取到多少数据后交给 handler 进行处理，解决业务数据占内存问题，默认100
    private Integer batchHandleSize;

    @Setter
    // 需要解析的sheet下标，默认解析全部，从1开始，非0开始
    private List<Integer> parseSheetIndex;

    @Setter
    // 表格列数 一行数据列数如果不足此数，自动填充空字符串
    private Integer needReadColNum;

    public EventModelReader(@NonNull Object excelFile, @NonNull IEventModelParseHandler handler) {
        if (excelFile instanceof InputStream) {
            excelFile((InputStream) excelFile);
        } else if (excelFile instanceof String) {
            excelFile((String) excelFile);
        } else if (excelFile instanceof File) {
            excelFile((File) excelFile);
        } else {
            throw UnifiedException.gen("非合法参数");
        }
        this.handler = handler;
    }

    private void excelFile(@NonNull InputStream is) {
        this.excelFile = FileUtil.saveTempFile(is, IdUtil.timeDiy("excel"), ".xlsx");
    }

    private void excelFile(@NonNull String url) {
        this.excelFile = FileUtil.saveTempFile(HttpUtil.get(url), IdUtil.timeDiy("excel"), ".xlsx");
    }

    private void excelFile(@NonNull File file) {
        this.excelFile = file;
    }

    public void processSheet() {
        OPCPackage opcPackage;
        SheetHandler sheetHandler;
        XMLReader xmlReader;
        XSSFReader xssfReader;
        Iterator<InputStream> sheets;
        try {
            opcPackage = OPCPackage.open(this.excelFile.getAbsolutePath());
            xssfReader = new XSSFReader(opcPackage);
            SharedStringsTable sst = xssfReader.getSharedStringsTable();
            xmlReader = SAXHelper.newXMLReader();
            sheetHandler = new SheetHandler(sst);
            sheetHandler.setHandler(handler);
            // 对整个所有 sheet 都生效的配置
            if (needReadRowNum != null) {
                sheetHandler.setNeedReadRowNum(needReadRowNum);
            }
            if (batchHandleSize != null) {
                sheetHandler.setBatchHandleSize(batchHandleSize);
            }
            if (needReadColNum != null) {
                sheetHandler.setNeedReadColNum(needReadColNum);
            }
            xmlReader.setContentHandler(sheetHandler);
            sheets = xssfReader.getSheetsData();
        } catch (Exception e) {
            throw UnifiedException.gen("processSheet", e);
        }
        int currentSheetIndex = 0;
        while (sheets.hasNext()) {
            currentSheetIndex++;
            if (parseSheetIndex == null || parseSheetIndex.contains(currentSheetIndex)) {
                sheetHandler.setCurSheetIndex(currentSheetIndex);
                sheetHandler.startReadSheet();
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                try {
                    xmlReader.parse(sheetSource);
                    sheet.close();
                } catch (UnifiedException ex) {
                    if (ex.getErrorCode() == need_read_row_num_code) {
                        sheetHandler.handleData();
                        continue;
                    } else {
                        throw ex;
                    }
                } catch (Exception e) {
                    throw UnifiedException.gen("processSheet", e);
                }
                sheetHandler.handleData();
            } else {
                sheets.next();
            }
        }

    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private static class SheetHandler extends DefaultHandler {

        @Setter
        private int batchHandleSize = 100;
        @Setter
        private IEventModelParseHandler handler;
        @Setter
        private int needReadRowNum = Integer.MAX_VALUE;
        @Setter
        private int needReadColNum = 0;
        @Setter
        private int curSheetIndex = 0;

        private boolean sheetStart;
        private int sheetAlreadyReadRowNum;

        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private boolean isTElement;

        private String curColIndex;
        // ColMap <A-Z, Value>
        private Map<String, String> oneRow = new LinkedHashMap<>();
        private List<List<String>> rows = new LinkedList<>();

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        private void startReadSheet() {
            sheetStart = true;
            sheetAlreadyReadRowNum = 0;
            curColIndex = null;
            oneRow = new LinkedHashMap<>();
            rows = new LinkedList<>();
        }


        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) {
            // c => 单元格
            if ("c".equals(name)) {
                curColIndex = attributes.getValue("r").replaceAll("\\d", "");
                oneRow.put(curColIndex, "");
                // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
                String cellType = attributes.getValue("t");
                if ("s".equals(cellType)) {
                    nextIsString = true;
                } else {
                    nextIsString = false;
                }
            }
            // 当元素为t时
            if ("t".equals(name)) {
                isTElement = true;
            } else {
                isTElement = false;
            }
            // Clear contents cache
            lastContents = "";
        }

        @Override
        public void endElement(String uri, String localName, String name) {
            // 根据SST的索引值的到单元格的真正要存储的字符串
            // 这时characters()方法可能会被调用多次
            if (nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                nextIsString = false;
            }

            // t元素也包含字符串
            if (isTElement) {
                String value = lastContents.trim();
                oneRow.put(curColIndex, value);
                isTElement = false;
            } else if ("v".equals(name)) {
                // v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
                // 将单元格内容加入 rowlist 中，在这之前先去掉字符串前后的空白符
                if (name.equals("v")) {
                    String value = lastContents.trim();
                    oneRow.put(curColIndex, value);
                }
            } else {
                // 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
                if (name.equals("row")) {
                    List<String> cols = new LinkedList<>();
                    boolean isEmptyRow = true;
                    for (Map.Entry<String, String> entry : oneRow.entrySet()) {
                        String value = entry.getValue();
                        if (EmptyUtil.isNotEmpty(value)) {
                            isEmptyRow = false;
                        }
                        cols.add(value);
                    }
                    oneRow = new LinkedHashMap<>();
                    if (isEmptyRow) {
                        return;
                    }
                    if (sheetAlreadyReadRowNum + 1 > needReadRowNum) {
                        throw UnifiedException.gen(need_read_row_num_code, "读取行数已大于每个 sheet 读取行数 " + needReadRowNum);
                    }
                    sheetAlreadyReadRowNum++;
                    // 填充空字符串
                    if (needReadColNum > cols.size()) {
                        int time = needReadColNum - cols.size();
                        for (int i = 0; i < time; i++) {
                            cols.add("");
                        }
                    }
                    rows.add(cols);
                    if (rows.size() >= batchHandleSize) {
                        handleData();
                        sheetStart = false;
                    }
                }
            }
        }

        private void handleData() {
            EventModelContext eventModelParam = new EventModelContext();
            eventModelParam.setRows(rows);
            eventModelParam.setSheetStart(sheetStart);
            eventModelParam.setNeedReadColNum(needReadColNum);
            eventModelParam.setCurSheetIndex(curSheetIndex);
            eventModelParam.setSheetAlreadyReadRowNum(sheetAlreadyReadRowNum);
            handler.handle(eventModelParam);
            rows = new LinkedList<>();
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            lastContents += new String(ch, start, length);
        }
    }

}
