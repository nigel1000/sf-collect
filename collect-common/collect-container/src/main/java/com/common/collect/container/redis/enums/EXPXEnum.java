package com.common.collect.container.redis.enums;

import lombok.Getter;

/**
 * Created by hznijianfeng on 2018/9/6.
 */

public enum EXPXEnum {

    EX("ex", "单位为秒"), PX("px", "单位为毫秒"),

    ;

    @Getter
    private String code;
    @Getter
    private String desc;

    EXPXEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
