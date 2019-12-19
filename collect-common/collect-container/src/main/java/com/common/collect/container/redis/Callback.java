package com.common.collect.container.redis;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * Created by hznijianfeng on 2019/12/19.
 */

public interface Callback<T> {
    default T useJedis(@NonNull Jedis jedis) {
        throw UnifiedException.gen("不支持 jedis 操作");
    }

    default T useJedis(@NonNull JedisCluster jedisCluster) {
        throw UnifiedException.gen("不支持 jedisCluster 操作");
    }
}
