package com.common.collect.model.flowlog;

/**
 * Created by nijianfeng on 2019/3/17.
 */
public interface IMetaConfig {

    String getBizType();

    default String getBizName() {
        return "";
    }

}
