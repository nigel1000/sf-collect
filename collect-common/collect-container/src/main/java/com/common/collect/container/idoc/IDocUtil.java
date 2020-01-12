package com.common.collect.container.idoc;

import com.common.collect.container.JsonUtil;
import com.common.collect.util.DateUtil;
import com.common.collect.util.IdUtil;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by nijianfeng on 2020/1/12.
 */
public class IDocUtil {

    public static IDocFieldValueType typeMapping(@NonNull Class cls) {
        if (cls == Long.class || cls == long.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Integer.class || cls == int.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Float.class || cls == float.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Double.class || cls == double.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Boolean.TYPE || cls == boolean.class) {
            return IDocFieldValueType.Boolean;
        }
        if (cls == Byte.TYPE || cls == byte.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Short.TYPE || cls == short.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == BigDecimal.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Character.TYPE || cls == char.class) {
            return IDocFieldValueType.String;
        }
        if (cls == String.class) {
            return IDocFieldValueType.String;
        }
        if (cls == Date.class) {
            return IDocFieldValueType.Date;
        }
        if (cls == List.class ||
                cls.isArray()) {
            return IDocFieldValueType.Array;
        }
        return IDocFieldValueType.Object;
    }

    public static Object typeDefaultValue(@NonNull Class cls) {
        if (cls == Long.class || cls == long.class) {
            return Long.valueOf("20033221");
        }
        if (cls == Integer.class || cls == int.class) {
            return Integer.valueOf("4335");
        }
        if (cls == Float.class || cls == float.class) {
            return Float.valueOf("23.3");
        }
        if (cls == Double.class || cls == double.class) {
            return Double.valueOf("43.35");
        }
        if (cls == Boolean.TYPE || cls == boolean.class) {
            return Boolean.TRUE;
        }
        if (cls == Byte.TYPE || cls == byte.class) {
            return Byte.valueOf("2");
        }
        if (cls == Short.TYPE || cls == short.class) {
            return Short.valueOf("122");
        }
        if (cls == Character.TYPE || cls == char.class) {
            return 'c';
        }
        if (cls == BigDecimal.class) {
            return new BigDecimal("23.43222");
        }
        if (cls == Date.class) {
            return DateUtil.format(DateUtil.now(), "yyyy-MM-dd HH:mm:ss");
        }
        if (cls == String.class) {
            return IdUtil.uuidHex().substring(0, 5);
        }
        return null;
    }

    public static String fromString(Object str) {
        if (str == null) {
            return "";
        }
        if (str instanceof List) {
            return JsonUtil.bean2json(str);
        }
        return str.toString();
    }

}
