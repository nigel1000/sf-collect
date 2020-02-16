package com.common.collect.framework.redis;

import com.common.collect.lib.api.excps.UnifiedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.Pool;

/**
 * Created by hznijianfeng on 2019/12/19.
 */
@Slf4j
public class RedisService {

    private static Pool<Jedis> pool = null;
    private static JedisCluster jedisCluster = null;

    static {
        try {
            pool = JedisProvider.newSingleClient();
        } catch (UnifiedException ex) {
        }
        try {
            if (pool == null) {
                pool = JedisProvider.newSentinelClient();
            }
        } catch (UnifiedException ex) {
        }
        try {
            if (pool == null) {
                jedisCluster = JedisProvider.newClusterClient();
            }
        } catch (UnifiedException ex) {
        }
    }

    public JedisCommands pullJedis() {
        if (pool != null) {
            return pool.getResource();
        } else if (jedisCluster != null) {
            return jedisCluster;
        } else {
            throw UnifiedException.gen("当前未连接 redis 服务");
        }
    }

    public void pushJedis(@NonNull JedisCommands commands) {
        if (commands instanceof Jedis) {
            ((Jedis) commands).close();
        }
    }

    public <T> T useJedis(@NonNull Callback<T> callback) {
        JedisCommands commands = pullJedis();
        T t;
        try {
            if (commands instanceof Jedis) {
                t = callback.useJedis((Jedis) commands);
            } else if (commands instanceof JedisCluster) {
                t = callback.useJedis((JedisCluster) commands);
            } else {
                throw UnifiedException.gen("不支持的 redis 客户端");
            }
        } catch (UnifiedException ex) {
            throw ex;
        } catch (Exception ex) {
            throw UnifiedException.gen("useJedis 操作异常", ex);
        }
        pushJedis(commands);
        return t;
    }
}
