package com.common.collect.container;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

public class JsonUtil {

    private JsonUtil() {
    }

    public static String bean2json(Object bean) {

        SerializerFeature[] serializerFeatures = new SerializerFeature[]{
                SerializerFeature.DisableCircularReferenceDetect};
        return JSON.toJSONString(bean, serializerFeatures);
    }

    public static String bean2jsonPretty(Object bean) {
        SerializerFeature[] serializerFeatures = new SerializerFeature[]{
                SerializerFeature.PrettyFormat,
                SerializerFeature.DisableCircularReferenceDetect};
        return JSON.toJSONString(bean, serializerFeatures);
    }

    public static <T> JSONArray list2json(List<T> list) {

        return JSONArray.parseArray(bean2json(list));
    }

    public static <T> JSONArray set2json(Set<T> set) {

        return JSONArray.parseArray(bean2json(set));
    }

    public static <T> JSONObject map2json(Map<String, T> map) {

        return JSONObject.parseObject(bean2json(map));
    }

    public static <T> T json2bean(String jsonStr, Class<T> objClass) {

        return JSON.parseObject(jsonStr, objClass);
    }

    public static <T> List<T> json2beanList(String jsonArrayString, Class<T> beanClass) {

        return JSON.parseArray(jsonArrayString, beanClass);
    }

}
