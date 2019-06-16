package com.common.collect.container.excel.client;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.HttpUtil;
import com.common.collect.container.excel.ExcelExportUtil;
import com.common.collect.container.excel.ExcelImportUtil;
import com.common.collect.util.IdUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by hznijianfeng on 2019/3/7.
 */
@Slf4j
public class ExcelClient {

    public <T> List<T> urlImport(@NonNull String excelUrl, Class<T> clazz) {
        ExcelImportUtil excelParse = new ExcelImportUtil(HttpUtil.get(excelUrl));
        // 收集所有的错误
        excelParse.setImportLimit(1000);
        excelParse.setFastFail(false);
        excelParse.setFailCount(20);
        return excelParse.excelParse(1, clazz);
    }

    public <T> List<T> fileImport(@NonNull File file, Class<T> clazz) {
        try {
            ExcelImportUtil excelParse = new ExcelImportUtil(new FileInputStream(file));
            // 收集所有的错误
            excelParse.setImportLimit(1000);
            excelParse.setFastFail(false);
            excelParse.setFailCount(20);
            return excelParse.excelParse(1, clazz);
        } catch (Exception ex) {
            throw UnifiedException.gen("excel 文件解析失败", ex);
        }
    }

    public <T> File fileExport(@NonNull Class<T> clazz, @NonNull String sheetName, Consumer<ExcelExportUtil> execute) {
        // 导出excel
        ExcelExportUtil excelExportUtil = new ExcelExportUtil(sheetName, ExcelExportUtil.ExcelType.BIG_XLSX);
        excelExportUtil.exportTitle(clazz);
        execute.accept(excelExportUtil);
        File file = excelExportUtil.saveTemp(IdUtil.uuidHex());
        log.info("导出文件地址:\n{}", file.getAbsolutePath());
        return file;
    }

    public <T> File fileTplExport(@NonNull String classPathSource, Consumer<ExcelExportUtil> execute) {
        // 导出excel
        try {
            Resource resource = new ClassPathResource(classPathSource);
            ExcelExportUtil excelExportUtil =
                    new ExcelExportUtil(ExcelExportUtil.ExcelType.BIG_XLSX, resource.getInputStream());
            execute.accept(excelExportUtil);
            File file = excelExportUtil.saveTemp(IdUtil.uuidHex());
            log.info("导出文件地址:\n{}", file.getAbsolutePath());
            return file;
        } catch (Exception ex) {
            throw UnifiedException.gen("excel 文件导出失败", ex);
        }
    }

    public <T> File fileUrlExport(@NonNull String excelUrl, Consumer<ExcelExportUtil> execute) {
        // 导出excel
        ExcelExportUtil excelExportUtil =
                new ExcelExportUtil(ExcelExportUtil.ExcelType.BIG_XLSX, HttpUtil.get(excelUrl));
        execute.accept(excelExportUtil);
        File file = excelExportUtil.saveTemp(IdUtil.uuidHex());
        log.info("导出文件地址:\n{}", file.getAbsolutePath());
        return file;
    }

}
