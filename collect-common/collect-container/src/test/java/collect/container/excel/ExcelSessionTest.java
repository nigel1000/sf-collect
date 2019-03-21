package collect.container.excel;

import collect.container.excel.base.ExcelComposeEO;
import com.common.collect.container.excel.ExcelImportUtil;
import com.common.collect.container.excel.ExcelSession;
import com.common.collect.container.excel.client.ExcelClient;
import com.common.collect.util.IdUtil;
import com.common.collect.util.log4j.Slf4jUtil;
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
        Slf4jUtil.setLogLevel("debug");

        sessionExcel();

//        importCorrect();

//        importError();

//        exportNew();

//        exportTpl();


    }

    public static void sessionExcel() throws Exception {
        ExcelSession excelSession = new ExcelSession(new FileInputStream(path + "/ExcelSession.xlsx"));
        excelSession.insertRows(5, 10);
        excelSession.removeRow(excelSession.createSheet("复制"),0);
        excelSession.removeRow(excelSession.createSheet("测试"),0);
        excelSession.copySheetFollow(excelSession.createSheet("复制"), "复制后的表");
        excelSession.copySheetFollow(excelSession.createSheet("测试"), "复制后的表");
        excelSession.copySheetFollow(excelSession.createSheet("复制"), "复制后的表");
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
            for (int i = 0; i < 10; i++) {
                excelExportUtil.exportForward(Lists.newArrayList(excelComposeEO), ExcelComposeEO.class);
            }
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
                excelExportUtil.exportForward(Lists.newArrayList(excelComposeEO), ExcelComposeEO.class);
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
