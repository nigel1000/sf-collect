package com.common.collect.model.retry;

import com.common.collect.api.enums.YesNoEnum;
import com.common.collect.container.JsonUtil;
import com.google.common.base.Throwables;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@Data
public class RetryRecord implements Serializable {


    private Long id;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 消息类型 rabbitmq kafka
     */
    private String msgType;

    /**
     * 消息名称: rabbitmq queue 主题名称 kafka topic
     */
    private String msgKey;

    /**
     * 业务 Id
     */
    private String bizId;

    /**
     * 譬如tag，key，集群ip，port等 供你确认是否是被需要重试的消息
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
     * 消费时的错误信息
     */
    private String initErrorMessage;

    /**
     * 最后一次重试的错误信息
     */
    private String endErrorMessage;

    /**
     * 状态 0：失败 1：成功
     */
    private Integer status;

    private Date createdAt;

    private Date updatedAt;

    private RetryRecord() {
    }

    public static RetryRecord gen(Object body, Exception ex) {
        return gen(null, body, null, ex);
    }

    public static RetryRecord gen(String bizId, Object body, Exception ex) {
        return gen(bizId, body, null, ex);
    }

    public static RetryRecord gen(String bizId, Object body, Integer maxTryTimes, Exception ex) {
        RetryRecord retryRecord = new RetryRecord();
        String temp = JsonUtil.bean2json(body);
        retryRecord.body = temp.length() > 2000 ? temp.substring(0, 2000) : temp;
        retryRecord.status = YesNoEnum.NO.getCode();
        retryRecord.tryTimes = 0;
        retryRecord.maxTryTimes = Optional.ofNullable(maxTryTimes).orElse(3);
        retryRecord.bizId = Optional.ofNullable(bizId).orElse("");
        retryRecord.initErrorMessage = subErrorMessage(ex);
        return retryRecord;
    }

    public static String subErrorMessage(Exception ex) {
        if (ex == null) {
            return null;
        }
        String temp = Throwables.getStackTraceAsString(ex);
        return temp.length() > 1000 ? temp.substring(0, 1000) : temp;
    }

}