package com.common.collect.container.excel.define.bean;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.IBeanFactory;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by nijianfeng on 2019/3/11.
 */
public class SingletonBeanFactory implements IBeanFactory {

    private Map<String, Object> beanMap = Maps.newHashMap();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        if (clazz == null || clazz.isInterface()) {
            return null;
        }
        String key = clazz.getTypeName();
        T result = (T) beanMap.get(key);
        if (result == null) {
            try {
                result = clazz.newInstance();
                beanMap.putIfAbsent(key, result);
            } catch (Exception ex) {
                throw UnifiedException.gen(ExcelConstants.MODULE, key + " 无构造函数");
            }
        }
        return result;
    }
}
