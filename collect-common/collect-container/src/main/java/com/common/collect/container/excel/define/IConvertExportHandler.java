package com.common.collect.container.excel.define;


import com.common.collect.container.excel.pojo.ExcelExportParam;

/**
 * Created by nijianfeng on 2018/8/26.
 */
public interface IConvertExportHandler {

    String convert(Object value, ExcelExportParam.ExportInfo exportInfo);

}
