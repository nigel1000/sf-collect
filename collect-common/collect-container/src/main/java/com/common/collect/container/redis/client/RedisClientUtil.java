package com.common.collect.container.redis.client;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.redis.IJedisOperator;
import com.common.collect.container.redis.RedisKey;
import com.common.collect.util.CollectionUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FunctionUtil;
import com.common.collect.util.constant.Constants;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/3/16.
 */
@Slf4j
public class RedisClientUtil {

    @Setter
    private IJedisOperator redisClient;

    /**
     * 根据传入key值 获取对象
     */
    public <V> V get(String key) {
        V ret = null;
        try {
            ret = redisClient.get(RedisKey.createKey(key));
        } catch (Exception e) {
            log.error(" get error", e);
        }
        if (ret != null) {
            logGet("get", key, ret);
        }
        return ret;
    }

    /**
     * 根据传入key值 获取对象
     */
    public <V> V get(@NonNull String key, Supplier<V> supplier, int second) {
        V ret = get(key);
        if (ret == null) {
            ret = supplier.get();
            if (put(key, ret, second)) {
                fromBiz("get", key);
            }
        }
        return ret;
    }

    /**
     * 批量获取
     */
    public <V, T> List<V> batchGet(@NonNull String prefix, List<T> keys) {
        return batchGet(FunctionUtil.valueList(keys, Objects::nonNull, (t) -> prefix + String.valueOf(t)));
    }

    public <V> List<V> batchGet(List<String> keys) {
        keys = FunctionUtil.filter(keys, Objects::nonNull);
        if (EmptyUtil.isEmpty(keys)) {
            return new ArrayList<>();
        }
        List<V> ret = new ArrayList<>();
        try {
            List<RedisKey> redisKeys = FunctionUtil.valueList(keys, RedisKey::createKey);
            ret = redisClient.batchGet(redisKeys);
        } catch (Exception e) {
            log.error(" batchGet error", e);
        }
        if (EmptyUtil.isNotEmpty(ret)) {
            logGet("batchGet", keys, ret);
        }
        return ret;
    }

    /**
     * key：keys
     */
    public <V, K> Map<K, V> batchGetMap(@NonNull String prefix, List<K> keys) {
        List<String> cacheKeys = new ArrayList<>();
        Map<String, K> cacheKeyMap = new HashMap<>();
        for (K key : keys) {
            if (key == null) {
                continue;
            }
            String cacheKey = prefix + String.valueOf(key);
            cacheKeyMap.put(cacheKey, key);
            cacheKeys.add(cacheKey);
        }
        Map<String, V> cacheData = batchGetMap(cacheKeys);
        Map<K, V> ret = new HashMap<>();
        cacheData.forEach((k, v) -> ret.put(cacheKeyMap.get(k), v));
        return ret;
    }

    public <V> Map<String, V> batchGetMap(List<String> keys) {
        keys = FunctionUtil.filter(keys, Objects::nonNull);
        if (EmptyUtil.isEmpty(keys)) {
            return new HashMap<>();
        }
        Map<String, V> ret = new HashMap<>();
        try {
            List<RedisKey> redisKeys = FunctionUtil.valueList(keys, RedisKey::createKey);
            ret = redisClient.batchGetMap(redisKeys);
        } catch (Exception e) {
            log.error(" batchGetMap error", e);
        }
        if (EmptyUtil.isNotEmpty(ret)) {
            logGet("batchGetMap", ret.keySet(), ret);
        }
        return ret;
    }

    public <V, K> Map<K, V> batchGetPut(@NonNull String prefix, List<K> keys, int expire,
                                        Function<List<K>, Map<K, V>> function) {
        return batchGetPut(prefix, keys, expire, function, null);
    }

    public <V, K> Map<K, V> batchGetPut(@NonNull String prefix, List<K> keys, int expire,
                                        Function<List<K>, Map<K, V>> function, V nullValue) {
        List<String> cacheKeys = new ArrayList<>();
        Map<String, K> cacheKeyMap = new HashMap<>();
        for (K key : keys) {
            if (key == null) {
                continue;
            }
            String cacheKey = prefix + String.valueOf(key);
            cacheKeys.add(cacheKey);
            cacheKeyMap.put(cacheKey, key);
        }
        Function<List<String>, Map<String, V>> cacheFunction = (l) -> {
            List<K> fromDB = new ArrayList<>();
            for (String s : l) {
                fromDB.add(cacheKeyMap.get(s));
            }
            Map<K, V> bizData = function.apply(fromDB);
            Map<String, V> retData = new HashMap<>();
            bizData.forEach((k, v) -> retData.put(prefix + String.valueOf(k), v));
            return retData;
        };
        Map<String, V> cacheData = batchGetPut(cacheKeys, expire, cacheFunction, nullValue);
        Map<K, V> ret = new HashMap<>();
        cacheData.forEach((k, v) -> ret.put(cacheKeyMap.get(k), v));
        return ret;
    }

    public <V> Map<String, V> batchGetPut(List<String> keys, int expire,
                                          Function<List<String>, Map<String, V>> function) {
        return batchGetPut(keys, expire, function, null);
    }

    // nullValue 防缓存击穿
    // nullValue 为空则 v 为空不放入缓存
    // nullValue 不为空则 v 为空是把 nullValue 放入缓存
    public <V> Map<String, V> batchGetPut(List<String> keys, int expire,
                                          Function<List<String>, Map<String, V>> function, V nullValue) {
        if (EmptyUtil.isEmpty(keys)) {
            return new HashMap<>();
        }
        Map<String, V> result = new HashMap<>();
        // 去空去重
        List<String> notEmptyKeys = CollectionUtil.removeDuplicate(keys);
        // 从缓存中取数据
        Map<String, V> cacheMap = batchGetMap(notEmptyKeys);

        List<String> unCacheKeys = Lists.newArrayList();
        List<String> cacheKeys = Lists.newArrayList();
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            V cache = cacheMap.get(key);
            if (cache != null) {
                if (nullValue == null) {
                    result.putIfAbsent(key, cache);
                } else {
                    if (!cache.equals(nullValue)) {
                        result.putIfAbsent(key, cache);
                    }
                }
                cacheKeys.add(key);
            } else {
                unCacheKeys.add(key);
            }
        }
        logGet("batchGetPut", cacheKeys);
        // 对于缓存中不存在的key，调用function去db取数据并放入缓存
        if (EmptyUtil.isNotEmpty(unCacheKeys)) {
            Map<String, V> bizMap = function.apply(unCacheKeys);
            for (String unCacheKey : unCacheKeys) {
                V bizObj = bizMap.get(unCacheKey);
                // 业务对象为空时
                if (nullValue != null && bizObj == null) {
                    bizObj = nullValue;
                }
                if (put(unCacheKey, bizObj, expire)) {
                    fromBiz("batchGetPut", unCacheKey);
                }
                result.putAll(bizMap);
            }
        }

        return result;
    }

    public <V> boolean put(String key, V value) {
        return put(key, value, Constants.ONE_DAY * 30);
    }

    /**
     * 设定指定key的值，并设置键值对的过期时间
     */
    public <V> boolean put(String key, V value, int seconds) {
        if (value == null) {
            return true;
        }
        if (seconds <= 0) {
            throw UnifiedException.gen("过期时间不合法");
        }
        try {
            Boolean ret = redisClient.setWithExpire(RedisKey.createKey(key, seconds), value);
            if (ret) {
                logUpsert("put", key, value);
                return true;
            }
        } catch (Exception e) {
            log.error(" put error", e);
        }
        return false;
    }

    /**
     * 根据 key 值删除缓存
     */
    public boolean del(String key) {
        try {
            Long ret = redisClient.remove(RedisKey.createKey(key));
            if (ret > 0) {
                logDel("remove", key);
                return true;
            }
        } catch (Exception e) {
            log.error(" remove error", e);
        }
        return false;
    }


    public <T> boolean batchDel(@NonNull String prefix, List<T> keys) {
        return batchDel(FunctionUtil.valueList(keys, Objects::nonNull, (t) -> prefix + String.valueOf(t)));
    }

    /**
     * 批量删除
     */
    public boolean batchDel(List<String> keys) {
        if (EmptyUtil.isEmpty(keys)) {
            return true;
        }
        boolean ret = true;
        try {
            for (String key : keys) {
                if (!del(key) && ret) {
                    ret = false;
                }
            }
        } catch (Exception e) {
            log.error(" batchRemove error", e);
        }
        return ret;
    }

    public <T> T lockRelease(String key, int seconds, String tips, Supplier<T> supplier) {

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

    public boolean lock(String key, int seconds) {
        return lockWithBizId(key, "1", seconds);
    }

    // 有 lockId 表示 解锁时只能此业务来解锁，key 还是只能被设置一次的
    public boolean lockWithBizId(String key, String lockId, int seconds) {
        if (seconds <= 0) {
            throw UnifiedException.gen("过期时间不合法");
        }
        boolean ret = false;
        try {
            ret = redisClient.setIfNotExist(RedisKey.createKey(key, seconds), lockId);
            if (ret) {
                logLock("lockWithBizId", key, lockId);
            }
        } catch (Exception e) {
            log.error("redis lockWithBizId error", e);
        }
        return ret;
    }

    public boolean release(String key) {
        return releaseWithBizId(key, "1");
    }

    public boolean releaseWithBizId(String key, String lockId) {
        Object result = redisClient.releaseWithBiz(RedisKey.createKey(key), lockId);
        if (Long.valueOf(1).equals(result)) {
            logRelease("releaseWithBizId", key, lockId);
            return true;
        }
        return false;
    }

    private void fromBiz(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[from biz] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private void logGet(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[from cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private void logDel(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[del cache] [content:{}] ", JsonUtil.bean2json(content));
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
