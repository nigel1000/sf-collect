package collect.container.excel;

import collect.container.excel.base.ExcelComposeEO;
import com.common.collect.container.excel.ExcelImportUtil;
import com.common.collect.container.excel.client.ExcelClient;
import com.common.collect.util.log4j.Slf4jUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;


/**
 * Created by hznijianfeng on 2019/3/7.
 */

@Slf4j
public class ExcelSessionTest {

    public static String path;

    static {
        path = ExcelSessionTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "src/test/resources";
        } else {
            path = path.substring(0, path.indexOf("target")) + "src/test/resources";
        }
    }

    public static void main(String[] args) {
        log.info("path:{}", path);
        try {
            new ExcelImportUtil("");
        } catch (Exception ex) {

        }
        Slf4jUtil.setLogLevel("debug");

        ExcelClient excelClient = new ExcelClient();

        List<ExcelComposeEO> corrects =
                excelClient.fileImport(new File(path + "/ExcelImport.xlsx"), ExcelComposeEO.class);
        print(corrects);

        try {
            List<ExcelComposeEO> errors =
                    excelClient.fileImport(new File(path + "/ExcelImportError.xlsx"), ExcelComposeEO.class);
            print(errors);
        } catch (Exception ex) {
            log.error("导出错误信息", ex);
        }

        ExcelComposeEO excelComposeEO = ExcelComposeEO.gen();
        long time = System.currentTimeMillis();
        excelClient.fileExport(ExcelComposeEO.class, "测试", (excelExportUtil -> {
            for (int i = 0; i < 1000000; i++) {
                excelExportUtil.exportForward(Lists.newArrayList(excelComposeEO), ExcelComposeEO.class);
            }
        }));
        // 新建 excel 100 万条数据 30秒
        log.info("耗时：{} 秒", (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        excelClient.fileTplExport("ExcelExportTpl.xlsx", (excelExportUtil -> {
            for (int i = 0; i < 1000000; i++) {
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
