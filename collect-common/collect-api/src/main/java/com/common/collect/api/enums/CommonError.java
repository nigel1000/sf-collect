package com.common.collect.api.enums;

import lombok.Getter;

/**
 * Created by nijianfeng on 2018/8/14.
 */

public enum CommonError {

    SYSTEM_ERROR(500, "发生未知错误，请联系技术人员排查!"),

    TIMEOUT_ERROR(1100, "服务连接超时"),

    DB_ERROR(1101, "数据库异常"),

    RPC_INVOKE_ERROR(1102, "远程服务调用失败"),

    ;

    @Getter
    private final int errorCode;

    @Getter
    private final String errorMessage;

    CommonError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
