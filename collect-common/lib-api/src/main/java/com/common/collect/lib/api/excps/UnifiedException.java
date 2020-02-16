package com.common.collect.lib.api.excps;

import com.common.collect.lib.api.enums.CodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一可抛出的异常定义 Created by nijianfeng on 2018/8/14.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class UnifiedException extends RuntimeException implements IBizException {

    private static final long serialVersionUID = 1L;

    /**
     * 业务模块
     */
    private String module;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 错误编码
     */
    private int errorCode;

    /**
     * 异常上下文，可以设置一些关键业务参数
     */
    private Map<String, Object> context;

    private UnifiedException(String module, String errorMessage) {
        this(module, CodeEnum.FAIL.getCode(), errorMessage);
    }

    private UnifiedException(String module, String errorMessage, Throwable cause) {
        this(module, CodeEnum.FAIL.getCode(), errorMessage, cause);
    }

    private UnifiedException(String module, int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.module = module;
    }

    private UnifiedException(String module, int errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.module = module;
    }

    public static UnifiedException gen(String errorMessage) {
        return gen("default", errorMessage);
    }

    public static UnifiedException gen(String errorMessage, Throwable cause) {
        return gen("default", errorMessage, cause);
    }

    public static UnifiedException gen(String module, String errorMessage) {
        return new UnifiedException(module, errorMessage);
    }

    public static UnifiedException gen(String module, String errorMessage, Throwable cause) {
        return new UnifiedException(module, errorMessage, cause);
    }

    public static UnifiedException gen(int errorCode, String errorMessage) {
        return gen("default", errorCode, errorMessage);
    }

    public static UnifiedException gen(int errorCode, String errorMessage, Throwable cause) {
        return gen("default", errorCode, errorMessage, cause);
    }

    public static UnifiedException gen(String module, int errorCode, String errorMessage) {
        return new UnifiedException(module, errorCode, errorMessage);
    }

    public static UnifiedException gen(String module, int errorCode, String errorMessage, Throwable cause) {
        return new UnifiedException(module, errorCode, errorMessage, cause);
    }

    public UnifiedException addContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
        return this;
    }

    public <C> UnifiedException addContext(Map<String, C> add) {
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
