package com.common.collect.model.retry;

import com.common.collect.api.enums.YesNoEnum;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.util.EmptyUtil;
import com.google.common.base.Throwables;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RetryRecord implements Serializable {

    private Long id;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务 Id
     */
    private String bizId;

    /**
     * 譬如tag，key，集群ip，port等
     */
    private String extra;

    /**
     * 消息体
     */
    private String body;

    /**
     * 尝试次数
     */
    private Integer tryTimes;

    /**
     * 最大尝试次数 默认三次
     */
    private Integer maxTryTimes;

    /**
     * 通知方式
     */
    private String alertType;

    /**
     * 通知目标
     */
    private String alertTarget;

    /**
     * 消费时的错误信息
     */
    private String firstErrorMessage;

    /**
     * 最后一次重试的错误信息
     */
    private String lastErrorMessage;

    /**
     * 状态 0：失败 1：成功
     */
    private Integer state;

    private Date createdAt;

    private Date updatedAt;

    public static RetryRecord of(@NonNull IMetaConfig metaConfig) {
        return RetryRecord.builder().bizType(metaConfig.getBizType()).build();
    }

    public RetryRecord validAdd() {
        if (EmptyUtil.isBlank(this.bizType)) {
            throw UnifiedException.gen("bizType 不合理");
        }
        if (tryTimes == null) {
            tryTimes = 0;
        }
        if (maxTryTimes == null) {
            maxTryTimes = 3;
        }
        if (state == null) {
            state = YesNoEnum.NO.getCode();
        }
        if (bizId == null) {
            bizId = "";
        }
        return this;
    }
}