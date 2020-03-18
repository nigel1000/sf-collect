package com.common.collect.lib.api;

import com.common.collect.lib.api.docs.DocsField;
import com.common.collect.lib.api.docs.DocsFieldExclude;
import com.common.collect.lib.api.enums.CodeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Response<T> implements Serializable {

    @DocsField(desc = "成功:[200-300]", defaultValue = "200")
    private int code;
    @DocsField(desc = "code=[200-300] 为 true", defaultValue = "true")
    private boolean success;

    @DocsField(desc = "返回数据")
    private T result;
    @DocsField(desc = "错误信息", defaultValue = "错误信息")
    private Object error;

    @DocsFieldExclude
    private Map<String, Object> context;

    private Response(int code, T data, Object desc) {
        this.code = code;
        this.result = data;
        this.error = desc;
    }

    public static <T> Response<T> ok() {
        return build(CodeEnum.SUCCESS.getCode(), null, null);
    }

    public static <T> Response<T> ok(T data) {
        return build(CodeEnum.SUCCESS.getCode(), data, null);
    }

    public static <T> Response<T> ok(int code, T data) {
        return build(code, data, null);
    }

    public static <T> Response<T> fail() {
        return build(CodeEnum.FAIL.getCode(), null, null);
    }

    public static <T> Response<T> fail(Object desc) {
        return build(CodeEnum.FAIL.getCode(), null, desc);
    }

    public static <T> Response<T> fail(int code, Object desc) {
        return build(code, null, desc);
    }

    public static <T> Response<T> build(int code) {
        return build(code, null, null);
    }

    public static <T> Response<T> build(int code, T data, Object desc) {
        Response<T> result = new Response<>(code, data, desc);
        result.setSuccess(code >= 200 && code < 300);
        return result;
    }

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

}