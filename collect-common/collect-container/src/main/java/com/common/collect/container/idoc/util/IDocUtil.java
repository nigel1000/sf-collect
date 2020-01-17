package com.common.collect.container.idoc.util;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.idoc.base.GlobalConfig;
import com.common.collect.container.idoc.base.IDocFieldType;
import com.common.collect.container.idoc.base.IDocFieldValueType;
import com.common.collect.container.idoc.context.IDocFieldObj;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.IdUtil;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        if (cls == Boolean.class || cls == boolean.class) {
            return IDocFieldValueType.Boolean;
        }
        if (cls == Byte.class || cls == byte.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Short.class || cls == short.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == BigDecimal.class) {
            return IDocFieldValueType.Number;
        }
        if (cls == Character.class || cls == char.class) {
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
        } else if (str instanceof String) {
            if (GlobalConfig.directReturnKey.equals(str)) {
                return GlobalConfig.directReturnKeyShow;
            }
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

    public static Object fieldFieldMapMock(Map<String, IDocFieldObj> docFieldObjMap) {
        Map<String, Object> bean = new LinkedHashMap<>();
        if (EmptyUtil.isEmpty(docFieldObjMap)) {
            return bean;
        }
        IDocFieldObj fieldObj = docFieldObjMap.get(GlobalConfig.directReturnKey);
        if (docFieldObjMap.size() == 1 && fieldObj != null && fieldObj.getIDocFieldType().equals(IDocFieldType.response)) {
            if (fieldObj.isObjectType()) {
                docFieldObjMap = new LinkedHashMap<>();
                docFieldObjMap.putAll((Map<String, IDocFieldObj>) fieldObj.getValue());
            } else if (fieldObj.isArrayType()) {
                if (fieldObj.isArrayObjectType()) {
                    Object sub = fieldFieldMapMock((Map<String, IDocFieldObj>) fieldObj.getValue());
                    return IDocUtil.arrayCountList(sub, fieldObj.getArrayTypeCount());
                } else {
                    return IDocUtil.arrayCountList(fieldObj.getValue(), fieldObj.getArrayTypeCount());
                }
            } else {
                return fieldObj.getValue();
            }
        }
        for (Map.Entry<String, IDocFieldObj> entry : docFieldObjMap.entrySet()) {
            String k = convert2String(entry.getKey());
            IDocFieldObj v = entry.getValue();
            if (v.getValue() instanceof Map) {
                Object sub = fieldFieldMapMock((Map<String, IDocFieldObj>) v.getValue());
                if (v.isArrayType()) {
                    bean.put(k, IDocUtil.arrayCountList(sub, v.getArrayTypeCount()));
                } else {
                    bean.put(k, sub);
                }
            } else {
                if (v.isArrayType()) {
                    bean.put(k, IDocUtil.arrayCountList(v.getValue(), v.getArrayTypeCount()));
                } else {
                    bean.put(k, v.getValue());
                }
            }
        }
        return bean;
    }

    public static void fieldFieldMapSort(Map<String, IDocFieldObj> map) {
        if (EmptyUtil.isEmpty(map)) {
            return;
        }
        Map<String, IDocFieldObj> baseMap = new LinkedHashMap<>();

        Map<String, IDocFieldObj> objStringMap = new LinkedHashMap<>();
        Map<String, IDocFieldObj> objMap = new LinkedHashMap<>();

        Map<String, IDocFieldObj> arrayBaseMap = new LinkedHashMap<>();
        Map<String, IDocFieldObj> arrayObjStringMap = new LinkedHashMap<>();
        Map<String, IDocFieldObj> arrayObjMap = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            if (v.getValue() instanceof Map) {
                fieldFieldMapSort((Map<String, IDocFieldObj>) v.getValue());
            }
            if (v.isObjectType()) {
                if (v.getValue() instanceof String) {
                    objStringMap.put(k, v);
                } else if (v.getValue() instanceof Map) {
                    objMap.put(k, v);
                }
            } else if (v.isArrayType()) {
                if (v.isArrayObjectType()) {
                    if (v.getValue() instanceof String) {
                        arrayObjStringMap.put(k, v);
                    } else if (v.getValue() instanceof Map) {
                        arrayObjMap.put(k, v);
                    }
                } else {
                    arrayBaseMap.put(k, v);
                }
            } else {
                baseMap.put(k, v);
            }
        });
        map.clear();
        map.putAll(baseMap);
        map.putAll(objStringMap);
        map.putAll(objMap);
        map.putAll(arrayObjStringMap);
        map.putAll(arrayBaseMap);
        map.putAll(arrayObjMap);
    }

}
