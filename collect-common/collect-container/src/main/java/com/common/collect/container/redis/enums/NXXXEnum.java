package com.common.collect.container.redis.enums;

import lombok.Getter;

/**
 * Created by hznijianfeng on 2018/9/6.
 */

public enum NXXXEnum {

    NX("nx", "只有键key不存在的时候才会设置key的值"), XX("xx", "只有键key存在的时候才会设置key的值"),

    ;

    @Getter
    private String code;
    @Getter
    private String desc;

    NXXXEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
