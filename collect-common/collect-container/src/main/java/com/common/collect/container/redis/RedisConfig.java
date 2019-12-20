package com.common.collect.container.redis;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by nijianfeng on 2019/3/16.
 */
@Data
public class RedisConfig implements Serializable {
    private static final long serialVersionUID = 2130760718083668250L;

    // 连接池的配置
    // 最大连接数
    private Integer maxTotal = 8;
    // 最大空闲连接数
    private Integer maxIdle = 8;
    // 最小空闲连接数
    private Integer minIdle = 0;
    private Boolean lifo = true;
    private Boolean fairness = false;

    // 连接的最后空闲时间，达到此值后空闲连接被移除
    private Long minEvictableIdleTimeMillis = 60000L;
    private Long softMinEvictableIdleTimeMillis = 1800000L;
    // 做空闲连接检测时，每次的采样数
    private Integer numTestsPerEvictionRun = -1;
    private String evictionPolicyClassName = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";
    private Boolean testOnCreate = false;
    // 借用连接时是否做连接有效性检测 ping
    private Boolean testOnBorrow = false;
    // 归还时是否做连接有效性检测 ping
    private Boolean testOnReturn = false;
    // 借用连接时是否做空闲检测
    private Boolean testWhileIdle = true;
    // 空闲连接的检测周期
    private Long timeBetweenEvictionRunsMillis = 30000L;
    // 连接池用尽后，调用者是否等待
    private Boolean blockWhenExhausted = true;
    // 连接池没有连接后客户端的最大等待时间 －1表示不超时 一直等
    private Long maxWaitMillis = -1L;
    // 开启jmx功能
    private Boolean jmxEnabled = true;
    private String jmxNamePrefix = "pool";
    private String jmxNameBase = null;

}