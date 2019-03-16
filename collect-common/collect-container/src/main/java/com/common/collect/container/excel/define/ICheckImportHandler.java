package com.common.collect.container.excel.define;


import com.common.collect.container.excel.pojo.ExcelImportParam;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public interface ICheckImportHandler {

    void check(Object value, ExcelImportParam.ImportInfo importInfo);

}
