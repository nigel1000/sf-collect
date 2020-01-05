package com.common.collect.model.flowlog;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import lombok.*;

import java.util.Date;

/**
 * Created by nijianfeng on 2019/3/17.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


    public static FlowLog of(@NonNull IMetaConfig metaConfig) {
        return FlowLog.builder().bizType(metaConfig.getBizType()).build();
    }

    public FlowLog validAdd() {
        if (EmptyUtil.isBlank(this.bizType)) {
            throw UnifiedException.gen("bizType 不合理");
        }
        return this;
    }

}
