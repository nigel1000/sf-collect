package com.common.collect.framework.excel.define.bean;

import com.common.collect.framework.excel.base.ExcelConstants;
import com.common.collect.framework.excel.define.IBeanFactory;
import com.common.collect.lib.api.excps.UnifiedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/3/11.
 */
public class SingletonBeanFactory implements IBeanFactory {

    private Map<String, Object> beanMap = new HashMap<>();

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
