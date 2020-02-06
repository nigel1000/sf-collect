package com.common.collect.container.redis;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.BeanUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FunctionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/12/19.
 */
@Slf4j
public class JedisProvider {

    // 连接超时
    private static int connectTimeout = 30000;
    // 读写超时
    private static int soTimeout = 30000;

    /**
     * 集群模式
     */
    // 部分或全部cluster节点信息
    private static List<String> nodes;
    // 重试次数
    private static int maxAttempts = 3;

    /**
     * 哨兵模式
     */
    // 主节点名
    private static String masterName;
    // 哨兵节点集合
    private static List<String> sentinels;
    // 当前数据库索引
    private static int database = 0;
    // 客户端名
    private static String clientName;

    /**
     * 单机模式
     */
    private static String hostName = "localhost";
    private static int port = 6379;
    private static int timeout = 30000;

    public static Pool<Jedis> newSingleClient() {
        if (EmptyUtil.isEmpty(hostName)) {
            throw UnifiedException.gen("hostName 不能为空");
        }
        GenericObjectPoolConfig genericObjectPoolConfig = BeanUtil.genBean(new RedisConfig(), GenericObjectPoolConfig.class);
        Pool<Jedis> pool = new JedisPool(genericObjectPoolConfig, hostName, port, timeout);
        // 关闭 客户端
        Runtime.getRuntime().addShutdownHook(new Thread(pool::close));
        return pool;
    }

    public static Pool<Jedis> newSentinelClient() {
        if (EmptyUtil.isEmpty(masterName)) {
            throw UnifiedException.gen("主节点名不能为空");
        }
        if (EmptyUtil.isEmpty(sentinels)) {
            throw UnifiedException.gen("哨兵节点不能为空");
        }
        GenericObjectPoolConfig genericObjectPoolConfig = BeanUtil.genBean(new RedisConfig(), GenericObjectPoolConfig.class);
        Pool<Jedis> pool = new JedisSentinelPool(masterName, new HashSet<>(sentinels),
                genericObjectPoolConfig, connectTimeout);
        // 关闭 客户端
        Runtime.getRuntime().addShutdownHook(new Thread(pool::close));
        return pool;
    }

    public static JedisCluster newClusterClient() {
        if (EmptyUtil.isEmpty(nodes)) {
            throw UnifiedException.gen("cluster 节点信息不能为空");
        }
        GenericObjectPoolConfig genericObjectPoolConfig = BeanUtil.genBean(new RedisConfig(), GenericObjectPoolConfig.class);
        JedisCluster cluster = new JedisCluster(FunctionUtil.valueSet(nodes, HostAndPort::parseString),
                connectTimeout, soTimeout, maxAttempts, genericObjectPoolConfig);
        // 关闭 客户端
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                cluster.close();
            } catch (IOException ex) {
                log.info("关闭 redis cluster [nodes:{}] 失败!", nodes, ex);
            }
        }));

        return cluster;
    }

}
