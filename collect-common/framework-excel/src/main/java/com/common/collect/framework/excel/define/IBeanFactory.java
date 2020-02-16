package com.common.collect.framework.excel.define;


/**
 * Created by nijianfeng on 2018/8/26.
 */
public interface IBeanFactory {

    <T> T getBean(Class<T> clazz);

}
