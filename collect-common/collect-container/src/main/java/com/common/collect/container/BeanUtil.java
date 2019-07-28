package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FunctionUtil;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

public class BeanUtil {

    public enum NeedPropertyType {

        NOT_NULL,
        NULL,
        ALL

    }

    public static String[] getPropertyNames(Object source, @NonNull NeedPropertyType needType) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            switch (needType) {
                case NOT_NULL:
                    if (srcValue != null) {
                        emptyNames.add(pd.getName());
                    }
                    break;
                case NULL:
                    if (srcValue == null) {
                        emptyNames.add(pd.getName());
                    }
                    break;
                case ALL:
                    emptyNames.add(pd.getName());
                    break;
                default:
                    throw UnifiedException.gen("不支持此类型");
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void genBeanAllProperty(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target);
    }

    public static void genBeanIgnoreSourceNullProperty(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target, getPropertyNames(source, NeedPropertyType.NULL));
    }

    public static void genBeanIgnoreTargetNotNullProperty(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target, getPropertyNames(target, NeedPropertyType.NOT_NULL));
    }

    public static <T> T emptyBean(Class<T> target) {
        T temp;
        try {
            temp = target.newInstance();
        } catch (Exception e) {
            throw UnifiedException.gen("没有默认构造方法", e);
        }
        return temp;
    }

    public static <T> T genBean(Object source, @NonNull Class<T> target) {
        if (source == null) {
            return null;
        }
        T temp = emptyBean(target);
        genBeanIgnoreSourceNullProperty(source, temp);
        return temp;
    }

    public static <S, T> List<T> genBeanList(List<S> sources, @NonNull Class<T> target) {
        if (EmptyUtil.isEmpty(sources)) {
            return new ArrayList<>();
        }
        return FunctionUtil.valueList(sources, (source) -> genBean(source, target));
    }

}
