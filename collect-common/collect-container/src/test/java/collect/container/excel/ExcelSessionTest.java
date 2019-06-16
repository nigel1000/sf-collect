package collect.container.excel;

import collect.container.excel.base.DefaultEventModelParseHandler;
import collect.container.excel.base.ExcelComposeEO;
import com.common.collect.container.excel.EventModelReader;
import com.common.collect.container.excel.ExcelImportUtil;
import com.common.collect.container.excel.ExcelSession;
import com.common.collect.container.excel.client.ExcelClient;
import com.common.collect.util.IdUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;


/**
 * Created by hznijianfeng on 2019/3/7.
 */

@Slf4j
public class ExcelSessionTest {

    private static String path;

    static {
        path = ExcelSessionTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "src/test/resources";
        } else {
            path = path.substring(0, path.indexOf("target")) + "src/test/resources";
        }
    }

    public static void main(String[] args) throws Exception {
        log.info("path:{}", path);
        try {
            new ExcelImportUtil("");
        } catch (Exception ex) {

        }
//        Slf4jUtil.setLogLevel("debug");

//        log.info("eventModelReader ##########");
//        eventModelReader();
//
//        log.info("sessionExcel ##########");
//        sessionExcel();
//
//        log.info("importCorrect ##########");
//        importCorrect();
//
//        log.info("importError ##########");
//        importError();

        log.info("exportNew ##########");
        exportNew();

//        log.info("exportTpl ##########");
//        exportTpl();

    }

    public static void eventModelReader() throws Exception {
        DefaultEventModelParseHandler handler = new DefaultEventModelParseHandler();
        EventModelReader eventModelReader = new EventModelReader(new FileInputStream(path + "/EventModelReader.xlsx"), handler);
        eventModelReader.setNeedReadColNum(7);
        eventModelReader.setBatchHandleSize(3);
        eventModelReader.setParseSheetIndex(Lists.newArrayList(1, 3));
        eventModelReader.setNeedReadRowNum(4);
        eventModelReader.processSheet();
    }

    public static void sessionExcel() throws Exception {
        ExcelSession excelSession = new ExcelSession(new FileInputStream(path + "/ExcelSession.xlsx"));
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
        File file = excelSession.saveTemp(IdUtil.uuidHex(), ".xlsx");
        log.info("导出文件地址:\n{}", file.getAbsolutePath());
    }

    public static void importCorrect() {
        ExcelClient excelClient = new ExcelClient();

        List<ExcelComposeEO> corrects =
                excelClient.fileImport(new File(path + "/ExcelImport.xlsx"), ExcelComposeEO.class);
        print(corrects);
    }

    public static void importError() {
        ExcelClient excelClient = new ExcelClient();

        try {
            List<ExcelComposeEO> errors =
                    excelClient.fileImport(new File(path + "/ExcelImportError.xlsx"), ExcelComposeEO.class);
            print(errors);
        } catch (Exception ex) {
            log.error("导出错误信息", ex);
        }
    }

    public static void exportNew() {
        ExcelClient excelClient = new ExcelClient();

        ExcelComposeEO excelComposeEO = ExcelComposeEO.gen();
        long time = System.currentTimeMillis();
        excelClient.fileExport(ExcelComposeEO.class, "测试", (excelExportUtil -> {
            for (int i = 0; i < 1000; i++) {
                excelExportUtil.exportForward(Lists.newArrayList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
            log.info("lastRowNum:{} ", excelExportUtil.getLastRowNum());
        }));
        // 新建 excel 100 万条数据 30秒
        log.info("耗时：{} 秒", (System.currentTimeMillis() - time) / 1000);
    }

    public static void exportTpl() {
        ExcelClient excelClient = new ExcelClient();

        ExcelComposeEO excelComposeEO = ExcelComposeEO.gen();
        long time = System.currentTimeMillis();
        excelClient.fileTplExport("ExcelExportTpl.xlsx", (excelExportUtil -> {
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportForward(Lists.newArrayList(excelComposeEO, excelComposeEO), ExcelComposeEO.class);
            }
        }));
        // 用模版 100 万条数据 30秒
        log.info("耗时：{} 秒", (System.currentTimeMillis() - time) / 1000);
    }

    private static <T> void print(List<T> list) {
        for (T t : list) {
            log.info("{}", t);
        }
    }

}
