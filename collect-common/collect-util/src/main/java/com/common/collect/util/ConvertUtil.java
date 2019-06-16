package com.common.collect.util;

import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2018/8/18.
 */

@Slf4j
public class ConvertUtil {

    public static String underline2Camel(String underline) {
        if (underline == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String a[] = underline.split("_");
        for (String s : a) {
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static String camel2Underline(String camel) {
        if (camel == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(camel);
        int temp = 0;// 定位
        // 从1开始 第一个大写不做处理
        for (int i = 1; i < camel.length(); i++) {
            if (Character.isUpperCase(camel.charAt(i))) {
                sb.insert(i + temp, "_");
                temp += 1;
            }
        }
        return sb.toString().toLowerCase();
    }

    public static String firstLower(String input) {
        if (input == null) {
            return null;
        }
        if (input.length() == 1) {
            return input.toLowerCase();
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String firstUpper(String input) {
        if (input == null) {
            return null;
        }
        if (input.length() == 1) {
            return input.toUpperCase();
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static Map<String, Object> obj2Map(Object item) {

        Map<String, Object> map = new HashMap<>();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(item.getClass());
        } catch (Exception ex) {
            log.warn("[ConvertUtil][obj2Map]失败!", ex);
            return map;
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            try {
                Object value = getter != null ? getter.invoke(item) : null;
                map.put(key, value);
            } catch (Exception ex) {
                log.warn("[ConvertUtil][obj2Map]失败!", ex);
            }
        }
        return map;
    }

    public static  <T> T selectAfter(T global, T field) {
        return field != null ? field : global;
    }


}
