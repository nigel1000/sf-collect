package com.common.collect.lib.util.jackson;

import com.common.collect.lib.api.excps.UnifiedException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class JsonUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.getFactory().disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
    }

    public static String bean2json(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw UnifiedException.gen("toJson出错：", e);
        }
    }

    // array new TypeReference<List<Integer>>()
    public static <T> T json2bean(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw UnifiedException.gen("toBean出错：", e);
        }
    }

    public static <T> T json2bean(String json, Class<T> clz) {
        try {
            return objectMapper.readValue(json, clz);
        } catch (Exception e) {
            throw UnifiedException.gen("toBean出错：", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseNodeValue(JsonNode node, Class<T> clazz) throws IOException {
        if (node == null) {
            return null;
        }
        if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(node.longValue());
        }
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(node.intValue());
        }
        if (clazz == Float.class || clazz == float.class) {
            return (T) Float.valueOf(node.floatValue());
        }
        if (clazz == Double.class || clazz == double.class) {
            return (T) Double.valueOf(node.doubleValue());
        }
        if (clazz.isAssignableFrom(String.class)) {
            return (T) node.asText();
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(node.booleanValue());
        }
        return objectMapper.readValue(node.textValue(), clazz);
    }

    public static JsonNode findNode(JsonNode root, String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, ".");
        JsonNode current = root;
        while (tokenizer.hasMoreElements() && current != null) {
            String key = tokenizer.nextToken();
            current = current.get(key);
        }
        return current;
    }

    public static <T> T getProperty(String json, String path, Class<T> clazz) {
        try {
            JsonNode root = objectMapper.readTree(json);
            return parseNodeValue(findNode(root, path), clazz);
        } catch (IOException e) {
            throw UnifiedException.gen("toBeanByPath出错：", e);
        }
    }

}