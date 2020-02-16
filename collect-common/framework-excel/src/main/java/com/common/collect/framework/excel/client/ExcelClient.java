package com.common.collect.framework.excel.client;

import com.common.collect.framework.excel.ExcelExportUtil;
import com.common.collect.framework.excel.ExcelImportUtil;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.okhttp.HttpUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by hznijianfeng on 2019/3/7.
 */
@Slf4j
public class ExcelClient {

    public static <T> List<T> urlImport(@NonNull String excelUrl, Class<T> clazz) {
        ExcelImportUtil excelParse = new ExcelImportUtil(HttpUtil.getInputStream(excelUrl));
        // 收集所有的错误
        excelParse.setImportLimit(1000);
        excelParse.setFastFail(false);
        excelParse.setFailCount(20);
        return excelParse.excelParse(1, clazz);
    }

    public static <T> List<T> fileImport(@NonNull File file, Class<T> clazz) {
        try {
            ExcelImportUtil excelParse = new ExcelImportUtil(new FileInputStream(file));
            // 收集所有的错误
            excelParse.setImportLimit(1000);
            excelParse.setFastFail(false);
            excelParse.setFailCount(20);
            return excelParse.excelParse(1, clazz);
        } catch (FileNotFoundException ex) {
            throw UnifiedException.gen("文件未找到", ex);
        }
    }

    public static <T> File fileExport(@NonNull Class<T> clazz, @NonNull String sheetName, Consumer<ExcelExportUtil> execute, @NonNull String to) {
        // 导出excel
        ExcelExportUtil excelExportUtil = new ExcelExportUtil(sheetName, ExcelExportUtil.ExcelType.BIG_XLSX);
        excelExportUtil.exportTitle(clazz, 0);
        execute.accept(excelExportUtil);
        File file = excelExportUtil.save(to);
        log.info("导出文件地址:\n{}", file.getAbsolutePath());
        return file;
    }

    public static File fileTplExport(@NonNull String from, Consumer<ExcelExportUtil> execute, @NonNull String to) {
        // 导出excel
        try {
            ExcelExportUtil excelExportUtil =
                    new ExcelExportUtil(ExcelExportUtil.ExcelType.BIG_XLSX, from);
            execute.accept(excelExportUtil);
            File file = excelExportUtil.save(to);
            log.info("导出文件地址:\n{}", file.getAbsolutePath());
            return file;
        } catch (Exception ex) {
            throw UnifiedException.gen("excel 文件导出失败", ex);
        }
    }

    public static File fileUrlExport(@NonNull String excelUrl, Consumer<ExcelExportUtil> execute, @NonNull String to) {
        // 导出excel
        ExcelExportUtil excelExportUtil =
                new ExcelExportUtil(ExcelExportUtil.ExcelType.BIG_XLSX, HttpUtil.getInputStream(excelUrl));
        execute.accept(excelExportUtil);
        File file = excelExportUtil.save(to);
        log.info("导出文件地址:\n{}", file.getAbsolutePath());
        return file;
    }

}
