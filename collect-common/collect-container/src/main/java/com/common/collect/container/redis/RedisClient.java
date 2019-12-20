package com.common.collect.container.redis;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.util.EmptyUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
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
    public static final long ONE_SECOND = TimeUnit.SECONDS.toSeconds(1);
    public static final long ONE_MINUTE = TimeUnit.MINUTES.toSeconds(1);
    public static final long ONE_HOUR = TimeUnit.HOURS.toSeconds(1);
    public static final long ONE_DAY = TimeUnit.DAYS.toSeconds(1);

    // redis 返回
    public static final String RETURN_OK = "OK";

    private RedisService redisService = new RedisService();
    private RedisSerialize hessianRedisSerialize = new HessianRedisSerialize();

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
        this.set(versionKey, version, null);
        return version;
    }

    public String getVersion(@NonNull String versionKey) {
        String version = this.get(versionKey);
        if (version == null) {
            version = this.increaseVersion(versionKey);
        }
        return version;
    }

    // 不处理空值
    public <T> boolean set(@NonNull String key, T obj, Long expireTime) {
        if (obj == null) {
            return true;
        }
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public Boolean useJedis(Jedis jedis) {
                String ret;
                if (expireTime == null || expireTime <= 0) {
                    ret = jedis.set(serialize(key), serialize(obj));
                } else {
                    ret = jedis.setex(serialize(key), expireTime.intValue(), serialize(obj));
                }
                return RETURN_OK.equals(ret);
            }
        };
        logUpsert(key, obj, expireTime);
        boolean result = redisService.useJedis(callback);
        if (!result) {
            log.error("set(key:{}, obj:{}, expireTime:{}) failed", key, obj, expireTime);
        }
        return result;
    }

    public <T> T get(@NonNull String key) {
        Callback<T> callback = new Callback<T>() {
            @Override
            public T useJedis(Jedis jedis) {
                byte[] ret = jedis.get(serialize(key));
                T t = deserialize(ret);
                return t;
            }
        };
        T t = redisService.useJedis(callback);
        logGet(key, t);
        return t;
    }

    public <T> T getSet(@NonNull String key, @NonNull Supplier<T> supplier, Long expireTime) {
        T t = get(key);
        if (t != null) {
            return t;
        }
        t = supplier.get();
        set(key, t, expireTime);
        return t;
    }

    public <K, T> Map<K, T> batchGetSet(@NonNull String keyPrefix,
                                        @NonNull List<K> keys,
                                        @NonNull Function<List<K>, Map<K, T>> function,
                                        Long expireTime) {
        Map<K, T> result = new HashMap<>();
        List<K> unCachedKeys = new ArrayList<>();
        for (K key : keys) {
            T t = get(keyPrefix + key);
            if (t != null) {
                result.put(key, t);
            } else {
                unCachedKeys.add(key);
            }
        }
        if (EmptyUtil.isNotEmpty(unCachedKeys)) {
            Map<K, T> fromDB = function.apply(unCachedKeys);
            fromDB.forEach((k, v) -> set(keyPrefix + k, v, expireTime));
            result.putAll(fromDB);
        }
        return result;
    }

    // 处理空值 避免穿透
    public <T> boolean setWithNull(@NonNull String key, T obj, Long notNullExpireTime, Long nullExpireTime) {
        if (obj == null) {
            return set(key, new NullValue(), nullExpireTime);
        } else {
            return set(key, obj, notNullExpireTime);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getWithNull(@NonNull String key) throws NullValueException {
        Object obj = get(key);
        if (obj == null) {
            return null;
        } else if (obj instanceof NullValue) {
            // 抛出异常代表缓存中有值但是空值
            throw new NullValueException();
        } else {
            return (T) obj;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetWithNull(@NonNull String key, @NonNull Supplier<T> supplier, Long notNullExpireTime, Long nullExpireTime) {
        Object obj = get(key);
        if (obj == null) {
            T t = supplier.get();
            setWithNull(key, t, notNullExpireTime, nullExpireTime);
            return t;
        } else if ((obj instanceof NullValue)) {
            return null;
        } else {
            return (T) obj;
        }
    }

    @SuppressWarnings("unchecked")
    public <K, T> Map<K, T> batchGetSetWithNull(@NonNull String keyPrefix,
                                                @NonNull List<K> keys,
                                                @NonNull Function<List<K>, Map<K, T>> function,
                                                Long notNullExpireTime,
                                                Long nullExpireTime) {
        Map<K, T> result = new HashMap<>();
        List<K> unCachedKeys = new ArrayList<>();
        for (K key : keys) {
            Object obj = get(keyPrefix + key);
            if (obj == null) {
                unCachedKeys.add(key);
            } else if ((obj instanceof NullValue)) {
                result.put(key, null);
            } else {
                result.put(key, (T) obj);
            }
        }
        if (EmptyUtil.isNotEmpty(unCachedKeys)) {
            Map<K, T> fromDB = function.apply(unCachedKeys);
            fromDB.forEach((k, v) -> {
                setWithNull(keyPrefix + k, v, notNullExpireTime, nullExpireTime);
            });
            result.putAll(fromDB);
            unCachedKeys.removeAll(fromDB.keySet());
        }
        if (EmptyUtil.isNotEmpty(unCachedKeys)) {
            for (K k : unCachedKeys) {
                setWithNull(keyPrefix + k, null, notNullExpireTime, nullExpireTime);
            }
        }
        return result;
    }

    public <T> T lockRelease(@NonNull String key, int seconds, String tips, @NonNull Supplier<T> supplier) {
        if (lock(key, seconds)) {
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

    public boolean lock(@NonNull String key, int seconds) {
        return lockWithBizId(key, "1", seconds);
    }

    // 有 lockId 表示 解锁时只能此业务来解锁，key 还是只能被设置一次的
    public boolean lockWithBizId(@NonNull String key, @NonNull String lockId, int seconds) {
        if (seconds <= 0) {
            throw UnifiedException.gen("过期时间不合法");
        }
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public Boolean useJedis(Jedis jedis) {
                String ret = jedis.set(key, lockId, "nx", "ex", (long) seconds);
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

    public static class NullValueException extends Exception {
        private static final long serialVersionUID = -8452694128579803995L;
    }

    public static class NullValue implements Serializable {
        private static final long serialVersionUID = -1368725118708856931L;
    }

}
