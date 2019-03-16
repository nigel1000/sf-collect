package com.common.collect.api.enums;

import lombok.Getter;

/**
 * Created by hznijianfeng on 2018/8/15.
 */

public enum YesNoEnum {

    NULL(-1, null, "异常状态"),

    YES(1, Boolean.TRUE, "是"),

    NO(0, Boolean.FALSE, "否"),
    ;

    @Getter
    private Integer code;
    @Getter
    private Boolean isYes;
    @Getter
    private String desc;

    YesNoEnum(Integer code, Boolean isYes, String desc) {
        this.code = code;
        this.isYes = isYes;
        this.desc = desc;
    }

    public boolean isEqual(Integer code) {
        return this.getCode().equals(code);
    }

    public static boolean isAvailable(Integer code) {
        return !parseCode(code).equals(NULL);
    }

    public static YesNoEnum parseCode(Integer code) {

        for (YesNoEnum item : YesNoEnum.values()) {
            if (item.getCode().equals(code))
                return item;
        }
        return NULL;
    }

}
