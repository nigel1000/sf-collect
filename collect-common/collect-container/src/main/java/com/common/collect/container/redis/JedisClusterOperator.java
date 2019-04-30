package com.common.collect.container.redis;

import com.common.collect.container.redis.base.RedisConstants;
import com.common.collect.container.redis.enums.EXPXEnum;
import com.common.collect.container.redis.enums.NXXXEnum;
import com.common.collect.container.redis.helper.SerializeHelper;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;

/**
 * Created by hznijianfeng on 2018/9/6.
 */

public abstract class JedisClusterOperator implements IJedisOperator {

    @Getter
    @Setter
    protected JedisCluster cluster;

    public abstract void init();

    @Override
    public JedisCluster pull() {
        return cluster;
    }

    @Override
    public <T> boolean setIfNotExist(RedisKey redisKey, T object) {
        JedisCluster jedis = pull();
        String ret = jedis.set(redisKey.getKey().getBytes(), SerializeHelper.serialize(object, getRedisConfig().getSerializeEnum()),
                NXXXEnum.NX.getCode().getBytes(), EXPXEnum.EX.getCode().getBytes(), redisKey.getExpireTime());
        push(jedis);
        return RedisConstants.RETURN_OK.equals(ret);
    }

    @Override
    public <T> boolean setWithExpire(RedisKey redisKey, T object) {
        JedisCluster jedis = pull();
        String ret = jedis.setex(redisKey.getKey().getBytes(), redisKey.getExpireTime(), SerializeHelper.serialize(object, getRedisConfig().getSerializeEnum()));
        push(jedis);
        return RedisConstants.RETURN_OK.equals(ret);
    }

    @Override
    public <T> T get(RedisKey redisKey) {
        JedisCluster jedis = pull();
        byte[] object = jedis.get(redisKey.getKey().getBytes());
        T result = SerializeHelper.deserialize(object, getRedisConfig().getSerializeEnum());
        push(jedis);
        return result;
    }

    @Override
    public Long remove(RedisKey redisKey) {
        JedisCluster jedis = pull();
        Long ret = jedis.del(redisKey.getKey().getBytes());
        push(jedis);
        return ret;
    }

    @Override
    public boolean lock(RedisKey redisKey) {
        return setIfNotExist(redisKey, 1);
    }

    @Override
    public void release(RedisKey redisKey) {
        remove(redisKey);
    }

    @Override
    public Object releaseWithBiz(RedisKey redisKey, String bizCode) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] " + "then return redis.call('del', KEYS[1]) "
                + "else return 0 end";
        JedisCluster jedis = pull();
        Object ret = jedis.eval(script.getBytes(), Collections.singletonList(redisKey.getKey().getBytes()), Collections.singletonList(SerializeHelper.serialize(bizCode, getRedisConfig().getSerializeEnum())));
        push(jedis);
        return ret;
    }


}
