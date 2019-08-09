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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/3/16.
 */
@Slf4j
public class RedisClientUtil {

    /**
     * 根据传入key值 获取对象
     */
    public static <V> V get(IJedisOperator redisClient, String key) {
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
    public <V> V get(IJedisOperator redisClient, @NonNull String key, Supplier<V> supplier, int second) {
        V ret = get(redisClient, key);
        if (ret == null) {
            ret = supplier.get();
            if (put(redisClient, key, ret, second)) {
                fromBiz("get", key);
            }
        }
        return ret;
    }

    /**
     * 批量获取
     */
    public static <V> List<V> batchGet(IJedisOperator redisClient, List<String> keys) {
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
    public static <V> Map<String, V> batchGetMap(IJedisOperator redisClient, List<String> keys) {
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
            logGet("batchGetMap", keys, ret);
        }
        return ret;
    }

    public static <V> Map<String, V> batchGetPut(IJedisOperator redisClient, List<String> keys, int expire,
                                                 Function<List<String>, Map<String, V>> function) {
        if (EmptyUtil.isEmpty(keys)) {
            return new HashMap<>();
        }
        Map<String, V> result = new HashMap<>();
        // 去空去重
        List<String> notEmptyKeys = CollectionUtil.removeDuplicate(keys);
        // 从缓存中取数据
        Map<String, V> cacheMap = batchGetMap(redisClient, notEmptyKeys);

        List<String> unCacheKeys = Lists.newArrayList();
        List<String> cacheKeys = Lists.newArrayList();
        for (String key : keys) {
            V cache = cacheMap.get(key);
            if (cache != null) {
                result.putIfAbsent(key, cache);
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
                if (bizObj != null) {
                    if (put(redisClient, unCacheKey, bizObj, expire)) {
                        fromBiz("batchGetPut", unCacheKey);
                    }
                }
            }
            result.putAll(bizMap);
        }

        return result;
    }


    public static <V> boolean put(IJedisOperator redisClient, String key, V value) {
        return put(redisClient, key, value, Constants.ONE_DAY * 30);
    }

    /**
     * 设定指定key的值，并设置键值对的过期时间
     */
    public static <V> boolean put(IJedisOperator redisClient, String key, V value, int seconds) {
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

    // 空值也缓存，返回不包括空值
    public static <V> Map<String, V> batchGetPutAvoidNullValue(IJedisOperator redisClient, List<String> keys, int expire,
                                                               Function<List<String>, Map<String, V>> function, @NonNull V nullValue) {
        if (EmptyUtil.isEmpty(keys)) {
            return new HashMap<>();
        }
        Map<String, V> result = new HashMap<>();
        // 去空去重
        List<String> notEmptyKeys = CollectionUtil.removeDuplicate(keys);
        // 从缓存中取数据
        Map<String, V> cacheMap = batchGetMap(redisClient, notEmptyKeys);

        List<String> unCacheKeys = Lists.newArrayList();
        List<String> cacheKeys = Lists.newArrayList();
        for (String key : keys) {
            V cache = cacheMap.get(key);
            if (cache != null) {
                if (!nullValue.equals(cache)) {
                    result.putIfAbsent(key, cache);
                }
                cacheKeys.add(key);
            } else {
                unCacheKeys.add(key);
            }
        }
        logGet("batchGetPutAvoidNullValue", cacheKeys);
        // 对于缓存中不存在的key，调用function去db取数据并放入缓存
        if (EmptyUtil.isNotEmpty(unCacheKeys)) {
            Map<String, V> bizMap = function.apply(unCacheKeys);
            for (String unCacheKey : unCacheKeys) {
                V bizObj = bizMap.get(unCacheKey);
                if (putAvoidNullValue(redisClient, unCacheKey, bizObj, nullValue, expire)) {
                    fromBiz("batchGetPutAvoidNullValue", unCacheKey);
                }
                result.putAll(bizMap);
            }
        }

        return result;
    }

    public static <V> boolean putAvoidNullValue(IJedisOperator redisClient, String key, V value, @NonNull V nullValue, int seconds) {
        if (seconds <= 0) {
            throw UnifiedException.gen("过期时间不合法");
        }
        if (value == null) {
            value = nullValue;
        }
        return put(redisClient, key, value, seconds);
    }

    /**
     * 根据 key 值删除缓存
     */
    public static boolean del(IJedisOperator redisClient, String key) {
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

    /**
     * 批量删除
     */
    public static boolean batchDel(IJedisOperator redisClient, List<String> keys) {
        if (EmptyUtil.isEmpty(keys)) {
            return true;
        }
        boolean ret = true;
        try {
            for (String key : keys) {
                if (!del(redisClient, key) && ret) {
                    ret = false;
                }
            }
        } catch (Exception e) {
            log.error(" batchRemove error", e);
        }
        return ret;
    }

    public static <T> T lockRelease(IJedisOperator redisClient, String key, int seconds, String tips, Supplier<T> supplier) {

        if (lock(redisClient, key, seconds)) {
            try {
                return supplier.get();
            } finally {
                release(redisClient, key);
            }
        } else {
            if (EmptyUtil.isEmpty(tips)) {
                tips = "获取 " + key + " 锁失败";
            }
            throw UnifiedException.gen(tips);
        }

    }

    public static boolean lock(IJedisOperator redisClient, String key, int seconds) {
        return lockWithBizId(redisClient, key, "1", seconds);
    }

    // 有 lockId 表示 解锁时只能此业务来解锁，key 还是只能被设置一次的
    public static boolean lockWithBizId(IJedisOperator redisClient, String key, String lockId, int seconds) {
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
            log.error("rdbClientImpl setNx error", e);
        }
        return ret;
    }

    public static boolean release(IJedisOperator redisClient, String key) {
        return releaseWithBizId(redisClient, key, "1");
    }

    public static boolean releaseWithBizId(IJedisOperator redisClient, String key, String lockId) {
        Object result = redisClient.releaseWithBiz(RedisKey.createKey(key), lockId);
        if (Long.valueOf(1).equals(result)) {
            logRelease("releaseWithBizId", key, lockId);
            return true;
        }
        return false;
    }

    private static void fromBiz(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[from biz] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private static void logGet(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[from cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private static void logDel(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[del cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private static void logUpsert(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[upsert cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private static void logLock(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[lock cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

    private static void logRelease(Object... content) {
        if (log.isDebugEnabled()) {
            log.debug("[release cache] [content:{}] ", JsonUtil.bean2json(content));
        }
    }

}
