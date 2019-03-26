package com.common.collect.container.redis.client;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.BeanUtil;
import com.common.collect.container.redis.IJedisOperator;
import com.common.collect.container.redis.JedisClusterOperator;
import com.common.collect.container.redis.JedisOperator;
import com.common.collect.container.redis.base.RedisConstants;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FunctionUtil;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by nijianfeng on 2019/3/16.
 */

@Data
@Slf4j
public class RedisClientFactory {

    // 连接超时
    private int connectTimeout = 30000;
    // 读写超时
    private int soTimeout = 30000;

    /**
     * 集群模式
     */
    // 部分或全部cluster节点信息
    private List<String> nodes;
    // 重试次数
    private int maxAttempts = 3;

    /**
     * 哨兵模式
     */
    // 主节点名
    private String masterName;
    // 哨兵节点集合
    private List<String> sentinels;
    // 当前数据库索引
    private int database = 0;
    // 客户端名
    private String clientName;

    /**
     * 单机模式
     */
    private String hostName = "localhost";
    private int port = 6379;
    private int timeout = 30000;

    public IJedisOperator newSingleClient() {
        if (EmptyUtil.isEmpty(getHostName())) {
            throw UnifiedException.gen(RedisConstants.MODULE, "主机名不能为空");
        }
        return new JedisOperator() {
            @Override
            public void init() {
                JedisPoolConfig poolConfig = BeanUtil.genBean(this.getRedisConfig(), JedisPoolConfig.class);
                pool = new JedisPool(poolConfig, getHostName(), getPort(), getTimeout());

                // 关闭 oss 客户端
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (pool != null) {
                        pool.close();
                    }
                }));
            }
        };
    }

    public IJedisOperator newSentinelClient() {
        if (EmptyUtil.isEmpty(getMasterName())) {
            throw UnifiedException.gen(RedisConstants.MODULE, "主节点名不能为空");
        }
        if (EmptyUtil.isEmpty(getSentinels())) {
            throw UnifiedException.gen(RedisConstants.MODULE, "哨兵节点不能为空");
        }
        return new JedisOperator() {
            @Override
            public void init() {
                GenericObjectPoolConfig genericObjectPoolConfig = BeanUtil.genBean(this.getRedisConfig(), GenericObjectPoolConfig.class);
                pool = new JedisSentinelPool(getMasterName(), Sets.newHashSet(getSentinels()),
                        genericObjectPoolConfig, getConnectTimeout());

                // 关闭 oss 客户端
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (pool != null) {
                        pool.close();
                    }
                }));
            }
        };
    }

    public IJedisOperator newClusterClient() {
        if (EmptyUtil.isEmpty(nodes)) {
            throw UnifiedException.gen(RedisConstants.MODULE, "cluster 节点信息不能为空");
        }
        return new JedisClusterOperator() {
            @Override
            public void init() {
                GenericObjectPoolConfig genericObjectPoolConfig = BeanUtil.genBean(this.getRedisConfig(), GenericObjectPoolConfig.class);
                cluster = new JedisCluster(FunctionUtil.valueSet(getNodes(), HostAndPort::parseString),
                        getConnectTimeout(), getSoTimeout(), getMaxAttempts(), genericObjectPoolConfig);

                // 关闭 oss 客户端
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (cluster != null) {
                        try {
                            cluster.close();
                        } catch (IOException ex) {
                            log.info("关闭 redis cluster [nodes:{}] 失败!", getNodes(), ex);
                        }
                    }
                }));
            }
        };
    }


}
