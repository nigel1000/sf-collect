package framework.excel;

import com.common.collect.framework.excel.EventModelReader;
import com.common.collect.framework.excel.ExcelExportUtil;
import com.common.collect.framework.excel.ExcelSession;
import com.common.collect.framework.excel.client.ExcelClient;
import com.common.collect.framework.excel.context.EventModelContext;
import com.common.collect.framework.excel.define.IEventModelParseHandler;
import com.common.collect.framework.excel.excps.ExcelImportException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.FileUtil;
import com.common.collect.lib.util.StringUtil;
import framework.excel.base.DefaultEventModelParseHandler;
import framework.excel.base.ExcelComposeEO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;


/**
 * Created by hznijianfeng on 2019/3/7.
 */

@Slf4j
public class ExcelTest {

    private static String root = Paths.get(ExcelTest.class.getResource("/").getPath()).getParent().getParent().toString() + "/";

    public static void main(String[] args) throws Exception {

//        log.info("");
//        log.info("eventModelReader ##########");
//        eventModelReader();
//
//        log.info("");
//        log.info("sessionExcel ##########");
//        sessionExcel();
//
//        log.info("");
//        log.info("importCorrect ##########");
//        importCorrect();
//
//        log.info("");
//        log.info("importError ##########");
//        importError();
//
//        log.info("");
//        log.info("exportNew ##########");
//        exportNew();
//
//        log.info("");
//        log.info("exportTpl ##########");
//        exportTpl();
//
//        log.info(" done ");


        String from = "/Users/nijianfeng/Downloads/uid.xlsx";
        log.info("from:\t" + from);
        Set<Long> ids = new HashSet<>();
        IEventModelParseHandler handler = new IEventModelParseHandler() {
            @Override
            public void handle(EventModelContext eventModelContext) {
                List<List<String>> rows = eventModelContext.getRows();
                if (EmptyUtil.isEmpty(rows)) {
                    return;
                }
                if (eventModelContext.isSheetStart()) {
                    rows = rows.subList(1, rows.size());
                }
                for (List<String> row : rows) {
                    if (EmptyUtil.isNotBlank(row.get(0))) {
                        ids.add(Long.valueOf(row.get(0).trim()));
                    }
                }
            }
        };
        EventModelReader eventModelReader = new EventModelReader(new FileInputStream(from), handler);
        eventModelReader.setNeedReadColNum(1);
        eventModelReader.setBatchHandleSize(20);
        eventModelReader.setParseSheetIndex(Arrays.asList(1));
        eventModelReader.processSheet();
        System.out.println(StringUtil.join(ids,","));
    }

    public static void eventModelReader() throws Exception {
        String from = root + "src/test/resources/excel/";
        log.info("from:\t" + from);
        DefaultEventModelParseHandler handler = new DefaultEventModelParseHandler();
        EventModelReader eventModelReader = new EventModelReader(new FileInputStream(from + "EventModelReader.xlsx"), handler);
        eventModelReader.setNeedReadColNum(12);
        eventModelReader.setBatchHandleSize(3);
        eventModelReader.setParseSheetIndex(Arrays.asList(1, 3));
        eventModelReader.setNeedReadRowNum(4);
        eventModelReader.processSheet();
    }

    public static void sessionExcel() throws Exception {
        String from = root + "src/test/resources/excel/";
        log.info("from:\t" + from);

        ExcelSession excelSession = new ExcelSession(new FileInputStream(from + "/ExcelSession.xlsx"));
        excelSession.insertRows(5, 50);
        excelSession.changeSheet("复制");
        excelSession.removeRow(0);
        excelSession.changeSheet("测试");
        excelSession.removeRow(0);
        excelSession.changeSheet("复制");
        excelSession.copySheetRowFollowToTargetSheet("复制后的表", true);
        excelSession.changeSheet("测试");
        excelSession.copySheetRowFollowToTargetSheet("复制后的表", true);
        excelSession.changeSheet("复制");
        excelSession.copySheetRowFollowToTargetSheet("复制后的表", true);
        excelSession.removeSheet("复制");
        excelSession.removeSheet("测试");

        String to = root + "logs/excel/";
        log.info("to:\t" + to);
        FileUtil.createFile(to, true, null, false);
        excelSession.save(to + "sessionExcel.xlsx");
    }

    public static void importCorrect() {
        String from = root + "src/test/resources/excel/";
        log.info("from:\t" + from);

        List<ExcelComposeEO> corrects =
                ExcelClient.fileImport(new File(from + "/ExcelImport.xlsx"), ExcelComposeEO.class);
        print(corrects);
    }

    public static void importError() {
        String from = root + "src/test/resources/excel/";
        log.info("from:\t" + from);

        try {
            List<ExcelComposeEO> errors =
                    ExcelClient.fileImport(new File(from + "/ExcelImportError.xlsx"), ExcelComposeEO.class);
            print(errors);
        } catch (ExcelImportException ex) {
            log.error("导出错误信息:{}", ex.toString());
        }
    }

    public static void exportNew() {
        ExcelComposeEO excelComposeEO = ExcelComposeEO.gen();
        long time = System.currentTimeMillis();
        Consumer<ExcelExportUtil> execute = (excelExportUtil) -> {
            for (int i = 0; i < 2000; i++) {
                excelExportUtil.exportModel(Arrays.asList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            log.info("从0开始 lastRowNum:{} ", excelExportUtil.getLastRowNum());
        };

        String to = root + "logs/excel/";
        log.info("to:\t" + to);
        FileUtil.createFile(to, true, null, false);
        ExcelClient.fileExport(ExcelComposeEO.class, "测试", execute, to + "exportNew.xlsx");
        // 新建 excel 100 万条数据 30秒
        log.info("耗时：{} 秒", (System.currentTimeMillis() - time) / 1000);
    }

    public static void exportTpl() {
        ExcelComposeEO excelComposeEO = ExcelComposeEO.gen();
        String from = root + "src/test/resources/excel/";
        log.info("from:\t" + from);
        String to = root + "logs/excel/";
        log.info("to:\t" + to);
        FileUtil.createFile(to, true, null, false);
        long time = System.currentTimeMillis();
        ExcelClient.fileTplExport(from + "ExcelExportTpl.xlsx", (excelExportUtil -> {
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportModel(Arrays.asList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            excelExportUtil.changeSheet("changeSheet");
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportModel(Arrays.asList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            excelExportUtil.changeSheet(1);
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportModel(Arrays.asList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            excelExportUtil.changeSheet(0);
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportModel(Arrays.asList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            excelExportUtil.changeSheet(1);
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportModel(Arrays.asList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            excelExportUtil.changeSheet("changeSheet");
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportModel(Arrays.asList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            log.info("从0开始 lastRowNum:{} ", excelExportUtil.getLastRowNum());
        }), to + "exportTpl.xlsx");
        // 用模版 100 万条数据 30秒
        log.info("耗时：{} 秒", (System.currentTimeMillis() - time) / 1000);
    }

    private static <T> void print(List<T> list) {
        for (T t : list) {
            log.info("{}", t);
        }
    }

}
