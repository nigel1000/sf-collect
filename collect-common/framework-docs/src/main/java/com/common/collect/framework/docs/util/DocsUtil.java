package com.common.collect.framework.docs.util;

import com.common.collect.framework.docs.base.DocsFieldValueType;
import com.common.collect.lib.util.IdUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2020/1/12.
 */
public class DocsUtil {

    public static DocsFieldValueType typeMapping(@NonNull Class cls) {
        if (cls == Long.class || cls == long.class) {
            return DocsFieldValueType.Number;
        }
        if (cls == Integer.class || cls == int.class) {
            return DocsFieldValueType.Number;
        }
        if (cls == Float.class || cls == float.class) {
            return DocsFieldValueType.Number;
        }
        if (cls == Double.class || cls == double.class) {
            return DocsFieldValueType.Number;
        }
        if (cls == Boolean.class || cls == boolean.class) {
            return DocsFieldValueType.Boolean;
        }
        if (cls == Byte.class || cls == byte.class) {
            return DocsFieldValueType.Number;
        }
        if (cls == Short.class || cls == short.class) {
            return DocsFieldValueType.Number;
        }
        if (cls == BigDecimal.class) {
            return DocsFieldValueType.Number;
        }
        if (cls == Character.class || cls == char.class) {
            return DocsFieldValueType.String;
        }
        if (cls == String.class) {
            return DocsFieldValueType.String;
        }
        if (cls == Date.class) {
            return DocsFieldValueType.Date;
        }
        if (cls == List.class ||
                cls.isArray()) {
            return DocsFieldValueType.Array;
        }
        if (cls == HttpServletRequest.class ||
                cls == HttpServletResponse.class ||
                cls == MultipartFile.class ||
                cls == Object.class ||
                cls == Map.class) {
            return DocsFieldValueType.UnKnow;
        }
        return DocsFieldValueType.Object;
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
        if (cls == Boolean.class || cls == boolean.class) {
            return Boolean.TRUE;
        }
        if (cls == Byte.class || cls == byte.class) {
            return Byte.valueOf("2");
        }
        if (cls == Short.class || cls == short.class) {
            return Short.valueOf("122");
        }
        if (cls == Character.class || cls == char.class) {
            return 'c';
        }
        if (cls == BigDecimal.class) {
            return new BigDecimal("23.43222");
        }
        if (cls == Date.class) {
            return System.currentTimeMillis();
        }
        if (cls == String.class) {
            return IdUtil.uuidHex().substring(0, 5);
        }
        return null;
    }

    public static String convert2String(Object str) {
        // null
        if (str == null) {
            return "";
        } else if (str instanceof Map) {
            // map
            return "";
        } else if (typeDefaultValue(str.getClass()) == null) {
            // list
            return JsonUtil.bean2json(str);
        }
        return String.valueOf(str);
    }

    public static List arrayCountList(Object value, int count) {
        List obj = Arrays.asList(value, value);
        for (int i = 0; i < count - 1; i++) {
            obj = Arrays.asList(obj, obj);
        }
        return obj;
    }

}
