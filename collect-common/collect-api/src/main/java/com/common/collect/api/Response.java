package com.common.collect.api;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Response<T> implements Serializable {

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;

    private int code;
    private boolean success;

    private T result;
    private Object error;

    private Map<String, Object> context;

    public Response<T> addContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
        return this;
    }

    public Response<T> addContext(Map<String, Object> add) {
        if (add == null) {
            return this;
        }
        if (context == null) {
            context = new HashMap<>();
        }
        context.putAll(add);
        return this;
    }

    private Response(int code, T data, Object desc) {
        this.code = code;
        this.result = data;
        this.error = desc;
    }

    public static <T> Response<T> ok() {
        return build(SUCCESS, null, null);
    }

    public static <T> Response<T> ok(T data) {
        return build(SUCCESS, data, null);
    }

    public static <T> Response<T> ok(int code, T data) {
        return build(code, data, null);
    }

    public static <T> Response<T> fail() {
        return build(ERROR, null, null);
    }

    public static <T> Response<T> fail(Object desc) {
        return build(ERROR, null, desc);
    }

    public static <T> Response<T> fail(int code, Object desc) {
        return build(code, null, desc);
    }

    public static <T> Response<T> build(int code) {
        return build(code, null, null);
    }

    public static <T> Response<T> build(int code, T data, Object desc) {
        Response<T> result = new Response<>(code, data, desc);
        result.setSuccess(code >= SUCCESS && code < 300);
        return result;
    }

}