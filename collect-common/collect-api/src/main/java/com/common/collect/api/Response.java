package com.common.collect.api;

import com.common.collect.api.idoc.IDocField;
import com.common.collect.api.idoc.IDocFieldExclude;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Response<T> implements Serializable {

    private int code;
    @IDocField(desc = "code in [200-300] 时为 true ")
    private boolean success;

    @IDocField(desc = "返回数据")
    private T result;
    @IDocField(desc = "错误信息", value = "错误信息")
    private Object error;

    @IDocFieldExclude
    private Map<String, Object> context;

    public Response<T> addContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
        return this;
    }

    public <C> Response<T> addContext(Map<String, C> add) {
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
        return build(Constants.SUCCESS, null, null);
    }

    public static <T> Response<T> ok(T data) {
        return build(Constants.SUCCESS, data, null);
    }

    public static <T> Response<T> ok(int code, T data) {
        return build(code, data, null);
    }

    public static <T> Response<T> fail() {
        return build(Constants.ERROR, null, null);
    }

    public static <T> Response<T> fail(Object desc) {
        return build(Constants.ERROR, null, desc);
    }

    public static <T> Response<T> fail(int code, Object desc) {
        return build(code, null, desc);
    }

    public static <T> Response<T> build(int code) {
        return build(code, null, null);
    }

    public static <T> Response<T> build(int code, T data, Object desc) {
        Response<T> result = new Response<>(code, data, desc);
        result.setSuccess(code >= Constants.SUCCESS && code < 300);
        return result;
    }

}