package com.common.collect.model.taskrecord.infrastructure;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hznijianfeng on 2020/02/15.
 *
 * 重试纪录表
 */

@Data
@NoArgsConstructor
public class TaskRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    private Long id;
    
    /**
     * 业务类型
     */
    private String bizType;
    
    /**
     * 业务 id
     */
    private String bizId;
    
    /**
     * 消息体
     */
    private String body;
    
    /**
     * 譬如tag，key，集群ip，port等
     */
    private String extra;
    
    /**
     * 尝试次数
     */
    private Integer tryTimes;
    
    /**
     * 最大尝试次数 默认三次
     */
    private Integer maxTryTimes;
    
    /**
     * 状态 0：失败 1：成功
     */
    private Integer state;
    
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
     * 创建时间
     */
    private Date createAt;
    
    /**
     * 修改时间
     */
    private Date updateAt;
    

}