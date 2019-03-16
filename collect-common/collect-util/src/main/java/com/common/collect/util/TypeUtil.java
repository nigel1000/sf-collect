package com.common.collect.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/7.
 */

public class TypeUtil {

    public static boolean isAssignableFrom(Class<?> source, Class<?> type) {
        return source.isAssignableFrom(type);
    }

    public static boolean isBaseDataType(Class<?> type) {
        return type.isPrimitive();
    }

    public static Object returnBaseDataType(Class<?> returnType) {

        if (Boolean.TYPE == returnType) {
            return false;
        }
        if (Byte.TYPE == returnType) {
            return (byte) 0;
        }
        if (Short.TYPE == returnType) {
            return (short) 0;
        }
        if (Integer.TYPE == returnType) {
            return 0;
        }
        if (Float.TYPE == returnType) {
            return 0f;
        }
        if (Long.TYPE == returnType) {
            return 0L;
        }
        if (Double.TYPE == returnType) {
            return 0d;
        }
        if (Character.TYPE == returnType) {
            return (char) 0;
        }
        return null;
    }

    public static List<Class> getSuperclasses(Class clazz) {
        List<Class> result = new ArrayList<>();
        result.add(clazz);
        result.addAll(Arrays.asList(clazz.getInterfaces()));
        if (clazz.equals(Object.class)) {
            return result;
        }
        result.addAll(getSuperclasses(clazz.getSuperclass()));
        return result;
    }
}
