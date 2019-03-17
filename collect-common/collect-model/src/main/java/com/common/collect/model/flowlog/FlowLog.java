package com.common.collect.model.flowlog;

import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * Created by nijianfeng on 2019/3/17.
 */
@Data
public class FlowLog {

    private Long id;

    /**
     * 业务 id
     */
    private String bizId;

    /**
     * 操作类型
     */
    private String bizType;

    /**
     * 操作类型 名称
     */
    private String bizTypeName;

    /**
     * 先前的值 建议转成 json
     */
    private String beforeValue;

    /**
     * 修改参数 建议转成 json
     */
    private String updateValue;

    /**
     * 修改后的值 建议转成 json
     */
    private String afterValue;

    /**
     * 额外的扩展字段，存sql等
     */
    private String extra;

    /**
     * 操作人 id
     */
    private String operatorId;

    /**
     * 操作人 姓名
     */
    private String operatorName;

    /**
     * 操作备注
     */
    private String operateRemark;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 修改时间
     */
    private Date updateAt;

    private FlowLog() {
    }

    public static FlowLog gen(String bizId, String beforeValue, String updateValue, String afterValue, String extra, String operatorId, String operatorName, String operateRemark) {
        FlowLog result = new FlowLog();
        result.setBizId(bizId);
        result.setBeforeValue(beforeValue);
        result.setUpdateValue(updateValue);
        result.setAfterValue(afterValue);
        result.setExtra(extra);
        result.setOperatorId(Optional.ofNullable(operatorId).orElse("system"));
        result.setOperatorName(Optional.ofNullable(operatorName).orElse("system"));
        result.setOperateRemark(operateRemark);
        return result;
    }

    public static FlowLog gen(String bizId, String beforeValue, String updateValue, String afterValue, String extra) {
        return gen(bizId, beforeValue, updateValue, afterValue, extra, null, null, null);
    }

    public static FlowLog gen(String bizId, String beforeValue, String updateValue, String afterValue) {
        return gen(bizId, beforeValue, updateValue, afterValue, null, null, null, null);
    }

}
