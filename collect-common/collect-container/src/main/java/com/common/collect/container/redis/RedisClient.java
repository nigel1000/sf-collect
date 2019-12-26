package com.common.collect.container.redis;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.util.CtrlUtil;
import com.common.collect.util.EmptyUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by hznijianfeng on 2019/12/19.
 */
@Slf4j
public class RedisClient {

    // 时间
    public static final long ONE_SECOND = TimeUnit.SECONDS.toMillis(1);
    public static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    public static final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);
    public static final long ONE_DAY = TimeUnit.DAYS.toMillis(1);

    // redis 返回
    public static final String RETURN_OK = "OK";
    private RedisService redisService = new RedisService();
    private RedisSerialize hessianRedisSerialize = new HessianRedisSerialize();

    public <T> boolean set(@NonNull String key,
                           T obj,
                           Long notNullCacheTime,
                           Long nullCacheTime) {
        ValueWrapper<T> wrapper = new ValueWrapper<>(obj);
        Long cacheTime = wrapper.getCacheTime(notNullCacheTime, nullCacheTime);
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public Boolean useJedis(Jedis jedis) {
                String ret;
                if (cacheTime == null || cacheTime <= 0) {
                    ret = jedis.set(serialize(key), serialize(wrapper));
                } else {
                    ret = jedis.setex(serialize(key), cacheTime.intValue() / 1000, serialize(wrapper));
                }
                return RETURN_OK.equals(ret);
            }
        };
        logUpsert(key, wrapper, cacheTime);
        boolean result = redisService.useJedis(callback);
        if (!result) {
            log.error("set(key:{}, obj:{}, cacheTime:{}) failed", key, wrapper, cacheTime);
        }
        return result;
    }

    public <T> ValueWrapper<T> getValueWrapper(@NonNull String key) {
        Callback<ValueWrapper<T>> callback = new Callback<ValueWrapper<T>>() {
            @Override
            public ValueWrapper<T> useJedis(Jedis jedis) {
                byte[] ret = jedis.get(serialize(key));
                return deserialize(ret);
            }
        };
        ValueWrapper<T> t = redisService.useJedis(callback);
        logGet(key, t);
        return t;
    }

    public <T> T getSet(@NonNull String key,
                        @NonNull Supplier<T> supplier,
                        Long notNullExpireTime,
                        Long nullExpireTime) {
        ValueWrapper<T> wrapper = getValueWrapper(key);
        if (wrapper == null) {
            T t = supplier.get();
            set(key, t, notNullExpireTime, nullExpireTime);
            return t;
        } else {
            return wrapper.getTarget();
        }
    }

    @SuppressWarnings("unchecked")
    public <K, T> Map<K, T> batchGetSet(@NonNull String keyPrefix,
                                        @NonNull List<K> keys,
                                        @NonNull Function<List<K>, Map<K, T>> function,
                                        Long notNullCacheTime,
                                        Long nullCacheTime) {
        Map<K, T> result = new HashMap<>();
        List<K> unCachedKeys = new ArrayList<>();
        for (K key : keys) {
            ValueWrapper<T> wrapper = getValueWrapper(keyPrefix + key);
            if (wrapper == null) {
                unCachedKeys.add(key);
            } else {
                result.put(key, wrapper.getTarget());
            }
        }
        if (EmptyUtil.isNotEmpty(unCachedKeys)) {
            Map<K, T> fromDB = function.apply(unCachedKeys);
            if (fromDB == null) {
                fromDB = new HashMap<>();
            }
            result.putAll(fromDB);
            for (K unCachedKey : unCachedKeys) {
                set(keyPrefix + unCachedKey, fromDB.get(unCachedKey), notNullCacheTime, nullCacheTime);
            }
        }
        return result;
    }

    public boolean remove(@NonNull String key) {
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public Boolean useJedis(Jedis jedis) {
                Long ret = jedis.del(serialize(key));
                if (ret == null || ret < 1) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        logDel(key);
        return redisService.useJedis(callback);
    }

    public String increaseVersion(@NonNull String versionKey) {
        String version = System.currentTimeMillis() + "_";
        this.set(versionKey, version, null, null);
        return version;
    }

    public String getVersion(@NonNull String versionKey) {
        ValueWrapper<String> t = getValueWrapper(versionKey);
        String version;
        if (t != null && !t.isNullValue()) {
            version = t.getTarget();
        } else {
            version = this.increaseVersion(versionKey);
        }
        return version;
    }

    public boolean lock(@NonNull String key, long millis) {
        return lockWithBizId(key, "1", millis);
    }

    // 有 lockId 表示 解锁时只能此业务来解锁，key 还是只能被设置一次的
    public boolean lockWithBizId(@NonNull String key, @NonNull String lockId, long millis) {
        if (millis <= 0) {
            throw UnifiedException.gen("过期时间不合法");
        }
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public Boolean useJedis(Jedis jedis) {
                String ret = jedis.set(key, lockId, "nx", "ex", millis / 1000);
                logLock(key, lockId);
                return RETURN_OK.equals(ret);
            }
        };
        return redisService.useJedis(callback);
    }

    public boolean release(@NonNull String key) {
        return releaseWithBizId(key, "1");
    }

    public boolean releaseWithBizId(@NonNull String key, @NonNull String lockId) {
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public Boolean useJedis(Jedis jedis) {
                String script = "if redis.call('get', KEYS[1]) == '" + lockId + "' then return redis.call('del', KEYS[1]) "
                        + "else return 0 end";
                Object ret = jedis.eval(script, 1, key);
                logRelease("releaseWithBizId", key, lockId);
                return Long.valueOf(1).equals(Long.valueOf(ret.toString()));
            }
        };
        return redisService.useJedis(callback);
    }

    public <T> T lockRelease(@NonNull String key, long millis, String tips, @NonNull Supplier<T> supplier) {
        if (lock(key, millis)) {
            try {
                return supplier.get();
            } finally {
                release(key);
            }
        } else {
            if (EmptyUtil.isEmpty(tips)) {
                tips = "获取 " + key + " 锁失败";
            }
            throw UnifiedException.gen(tips);
        }
    }

    /**
     * key 缓存不存在时，只透过一次从数据库取值，重试一次
     *
     * @param key
     * @param fromDB               从存储取数据
     * @param dataNotNullCacheTime 数据缓存时间
     * @param lockReleaseTime      锁释放时间 建议100ms
     * @param retryWaitTime        重试时间隔时间 建议10ms
     */
    public <T> T lockMutexGetSet(@NonNull String key,
                                 Supplier<T> fromDB,
                                 Long dataNotNullCacheTime,
                                 Long dataNullCacheTime,
                                 long lockReleaseTime,
                                 long retryWaitTime) {
        ValueWrapper<T> wrapper = getValueWrapper(key);
        if (wrapper != null) {
            logGet(key);
            return wrapper.getTarget();
        }
        String lockMutexKey = "mutex:" + key;
        return CtrlUtil.retry(2, () -> {
            if (lock(lockMutexKey, lockReleaseTime)) {
                try {
                    T db = fromDB.get();
                    set(key, db, dataNotNullCacheTime, dataNullCacheTime);
                    return db;
                } finally {
                    release(lockMutexKey);
                }
            } else {
                try {
                    Thread.sleep(retryWaitTime);
                } catch (InterruptedException e) {
                    log.info("sleep 失败", e);
                }
                ValueWrapper<T> cache = getValueWrapper(key);
                if (cache != null) {
                    logGet(key);
                    return cache.getTarget();
                } else {
                    throw UnifiedException.gen("系统繁忙，请稍后再试");
                }
            }
        });
    }

    private byte[] serialize(Object obj) {
        return hessianRedisSerialize.serialize(obj);
    }

    private <T> T deserialize(byte[] bytes) {
        return hessianRedisSerialize.deserialize(bytes);
    }

    private void logDel(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[del cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private void logGet(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[from cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private void logUpsert(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[upsert cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private void logLock(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[lock cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private void logRelease(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[release cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

}
