package com.common.collect.framework.excel.extension;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

public interface IOptionValue {

    IOptionValue getByExportValue(String descValue);

    default String getExportValue() {
        return null;
    }

}
