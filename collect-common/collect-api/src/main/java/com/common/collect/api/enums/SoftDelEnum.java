package com.common.collect.api.enums;

import lombok.Getter;

/**
 * Created by hznijianfeng on 2018/8/15.
 */

public enum SoftDelEnum {

    /**
     * 正常使用
     */
    USED(0L, Boolean.FALSE, "正常使用"),
    /**
     * 不等于0即为删除
     */
    DELETED(null, Boolean.TRUE, "已删除"),
    ;

    @Getter
    private Long code;
    @Getter
    private Boolean isDel;
    @Getter
    private String desc;

    SoftDelEnum(Long code, Boolean isDel, String desc) {
        this.code = code;
        this.isDel = isDel;
        this.desc = desc;
    }

    public static SoftDelEnum parseCode(Long code) {
        if (code == null) {
            return SoftDelEnum.DELETED;
        }
        if (code == 0) {
            return SoftDelEnum.USED;
        }
        return SoftDelEnum.DELETED;
    }

}
