package com.common.collect.container.excel;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.HttpUtil;
import com.common.collect.container.excel.define.IEventModelParseHandler;
import com.common.collect.container.excel.pojo.EventModelParam;
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

public class EventModelReader {

    private File excelFile;

    public EventModelReader(@NonNull Object excelFile) {
        if (excelFile instanceof InputStream) {
            excelFile((InputStream) excelFile);
        } else if (excelFile instanceof String) {
            excelFile((String) excelFile);
        } else if (excelFile instanceof File) {
            excelFile((File) excelFile);
        } else {
            throw UnifiedException.gen("非合法参数");
        }
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

    // parseSheetIndexs 默认全部sheet
    // batchHandleSize 默认为10
    public void processSheet(List<Integer> parseSheetIndex, Integer batchHandleSize,
                             List<IEventModelParseHandler> handlers) {
        if (this.excelFile == null) {
            throw UnifiedException.gen("未指定解析文件");
        }
        if (EmptyUtil.isEmpty(handlers)) {
            throw UnifiedException.gen("数据处理器不能为空");
        }
        OPCPackage pkg;
        try {
            pkg = OPCPackage.open(this.excelFile.getAbsolutePath());
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = SAXHelper.newXMLReader();
            SheetHandler handler = new SheetHandler(sst);
            handler.setHandlers(handlers);
            if (batchHandleSize != null) {
                handler.setBatchHandleSize(batchHandleSize);
            }
            parser.setContentHandler(handler);
            int currentSheetIndex = 0;
            Iterator<InputStream> sheets = r.getSheetsData();
            while (sheets.hasNext()) {
                if (parseSheetIndex == null || parseSheetIndex.contains(currentSheetIndex)) {
                    handler.setSheetStart(true);
                    InputStream sheet = sheets.next();
                    InputSource sheetSource = new InputSource(sheet);
                    parser.parse(sheetSource);
                    sheet.close();
                    handler.handleData();
                }
            }
        } catch (Exception e) {
            throw UnifiedException.gen("processSheet", e);
        }
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private static class SheetHandler extends DefaultHandler {

        @Setter
        private int batchHandleSize = 10;
        @Setter
        private List<IEventModelParseHandler> handlers;
        @Setter
        private boolean sheetStart = true;
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
                    if (!isEmptyRow) {
                        rows.add(cols);
                    }
                    if (rows.size() >= batchHandleSize) {
                        handleData();
                        sheetStart = false;
                    }
                    oneRow.clear();
                }
            }
        }

        public void handleData() {
            EventModelParam eventModelParam = new EventModelParam();
            eventModelParam.setRows(rows);
            eventModelParam.setSheetStart(sheetStart);
            for (IEventModelParseHandler handler : handlers) {
                handler.handle(eventModelParam);
            }
            rows.clear();
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            lastContents += new String(ch, start, length);
        }
    }

}
