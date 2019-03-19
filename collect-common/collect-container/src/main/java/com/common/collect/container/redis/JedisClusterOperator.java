package com.common.collect.container.redis;

import com.common.collect.container.redis.base.RedisConstants;
import com.common.collect.container.redis.enums.EXPXEnum;
import com.common.collect.container.redis.enums.NXXXEnum;
import com.common.collect.container.redis.helper.SerializeHelper;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.List;

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
        if (RedisConstants.RETURN_OK.equals(ret)) {
            return true;
        }
        return false;
    }

    @Override
    public <T> void setWithExpire(RedisKey redisKey, T object) {
        JedisCluster jedis = pull();
        jedis.setex(redisKey.getKey().getBytes(), redisKey.getExpireTime(), SerializeHelper.serialize(object, getRedisConfig().getSerializeEnum()));
        push(jedis);
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
    public void remove(List<RedisKey> redisKey) {
        JedisCluster jedis = pull();
        byte[][] keys = new byte[redisKey.size()][];
        for (int i = 0; i < redisKey.size(); i++) {
            keys[i] = redisKey.get(i).getKey().getBytes();
        }
        jedis.del(keys);
        push(jedis);
    }

    @Override
    public boolean lock(RedisKey redisKey) {
        return setIfNotExist(redisKey, 1);
    }

    @Override
    public void release(RedisKey redisKey) {
        remove(Lists.newArrayList(redisKey));
    }

    @Override
    public void releaseWithBiz(RedisKey redisKey, String bizCode) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] " + "then return redis.call('del', KEYS[1]) "
                + "else return 0 end";
        JedisCluster jedis = pull();
        jedis.eval(script, Collections.singletonList(redisKey.getKey()), Collections.singletonList(bizCode));
        push(jedis);
    }


}
