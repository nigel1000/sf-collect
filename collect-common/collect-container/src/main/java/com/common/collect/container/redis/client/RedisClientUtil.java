package com.common.collect.container.redis.client;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.redis.IJedisOperator;
import com.common.collect.container.redis.RedisKey;
import com.common.collect.util.EmptyUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by nijianfeng on 2019/3/16.
 */
@Slf4j
public class RedisClientUtil {

    @SuppressWarnings("unchecked")
    public static Map batchGetPut(@NonNull IJedisOperator jedisOperator, String keyPrefix, List<Object> keys, int expire,
                                  Function<List<Object>, Map> function) {
        if (EmptyUtil.isEmpty(keys)) {
            return Maps.newHashMap();
        }
        if (keyPrefix == null) {
            keyPrefix = "";
        }
        // 去空后的 key
        List<Object> realKeys = Lists.newArrayList();
        List<RedisKey> redisKeys = Lists.newArrayList();
        for (Object key : keys) {
            if (key == null) {
                continue;
            }
            redisKeys.add(RedisKey.createKey(keyPrefix + key));
            realKeys.add(key);
        }
        // 从缓存中取数据
        Map result = Maps.newHashMap();
        List<Object> unCacheKeys = Lists.newArrayList();
        List<Object> cacheKeys = Lists.newArrayList();
        Map<RedisKey, Object> cacheMap = jedisOperator.batchGet(redisKeys);
        for (Object realKey : realKeys) {
            Object cache = cacheMap.get(RedisKey.createKey(keyPrefix + realKey));
            if (cache != null) {
                result.putIfAbsent(realKey, cache);
                cacheKeys.add(realKey);
            } else {
                unCacheKeys.add(realKey);
            }
        }
        logGet(cacheKeys);
        // 从底层取数据并放入缓存
        if (EmptyUtil.isNotEmpty(unCacheKeys)) {
            Map bizMap = function.apply(unCacheKeys);
            fromBiz(unCacheKeys);
            Map<RedisKey, Object> putMap = Maps.newHashMap();
            for (Object unCacheKey : unCacheKeys) {
                Object bizObj = bizMap.get(unCacheKey);
                if (bizObj != null) {
                    putMap.putIfAbsent(RedisKey.createKey(keyPrefix + unCacheKey, expire), bizObj);
                }
            }
            if (EmptyUtil.isNotEmpty(putMap)) {
                jedisOperator.batchSetWithExpire(putMap);
            }
            result.putAll(bizMap);
        }
        return result;
    }

    public static void batchDelete(@NonNull IJedisOperator jedisOperator, String keyPrefix, List<Object> keys) {
        if (EmptyUtil.isEmpty(keys)) {
            return;
        }
        if (keyPrefix == null) {
            keyPrefix = "";
        }
        // 去空后的 key
        List<Object> realKeys = Lists.newArrayList();
        List<RedisKey> redisKeys = Lists.newArrayList();
        for (Object key : keys) {
            if (key == null) {
                continue;
            }
            redisKeys.add(RedisKey.createKey(keyPrefix + key));
            realKeys.add(key);
        }
        jedisOperator.remove(redisKeys);
        logDel(realKeys);
    }

    public static void lock(@NonNull IJedisOperator jedisOperator, @NonNull String key, int expire, String failTips) {
        RedisKey redisKey = RedisKey.createKey(key);
        redisKey.setExpireTime(expire);
        if (jedisOperator.lock(redisKey)) {
            logLock(Lists.newArrayList(key));
        } else {
            throw UnifiedException.gen("分布式缓存锁", failTips);
        }
    }

    public static void release(@NonNull IJedisOperator jedisOperator, @NonNull String key) {
        jedisOperator.release(RedisKey.createKey(key));
        logRelease(Lists.newArrayList(key));
    }

    public static void upsert(@NonNull IJedisOperator jedisOperator, @NonNull String key, @NonNull Object value, int expire) {
        RedisKey redisKey = RedisKey.createKey(key);
        redisKey.setExpireTime(expire);
        jedisOperator.setWithExpire(RedisKey.createKey(key), value);
        logUpsert(Lists.newArrayList(key));
    }

    private static void fromBiz(List<Object> keys) {
        log.debug("[from biz] [key:{}] ", keys);
    }

    private static void logGet(List<Object> keys) {
        log.debug("[from cache] [key:{}] ", keys);
    }

    private static void logDel(List<Object> keys) {
        log.debug("[del cache] [key:{}] ", keys);
    }

    private static void logUpsert(List<Object> keys) {
        log.debug("[upsert cache] [key:{}] ", keys);
    }

    private static void logLock(List<Object> keys) {
        log.debug("[lock cache] [key:{}] ", keys);
    }

    private static void logRelease(List<Object> keys) {
        log.debug("[release cache] [key:{}] ", keys);
    }

}
