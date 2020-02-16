package com.common.collect.lib.api.enums;

import lombok.Getter;

/**
 * Created by nijianfeng on 2018/8/14.
 */

public enum CodeEnum {

    SUCCESS(200, "成功"),
    FAIL(500, "发生未知错误，请联系技术人员排查!"),

    TIMEOUT_ERROR(1100, "服务连接超时"),

    DB_ERROR(1101, "数据库异常"),

    RPC_INVOKE_ERROR(1102, "远程服务调用失败"),;

    @Getter
    private final int code;

    @Getter
    private final String msg;

    CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
