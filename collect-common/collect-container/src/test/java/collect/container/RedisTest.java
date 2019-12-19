package collect.container;

import com.common.collect.container.redis.RedisClient;
import com.common.collect.container.redis.RedisConfig;
import com.common.collect.util.log4j.Slf4jUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/3/16.
 */

@Slf4j
public class RedisTest {

    public static void main(String[] args) throws Exception {
        RedisClient redisClient = new RedisClient();

        String prefix = "test_key_";
        log.info("set without expireTime######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        redisClient.set(prefix, new RedisConfig(), null);
        RedisConfig obj = redisClient.get(prefix);
        log.info("get:{}", obj);
        redisClient.remove(prefix);
        obj = redisClient.get(prefix);
        log.info("get after remove:{}", obj);

        log.info("set with expireTime######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        redisClient.set(prefix, new RedisConfig(), RedisClient.ONE_SECOND);
        obj = redisClient.get(prefix);
        log.info("get:{}", obj);
        Thread.sleep(1500);
        obj = redisClient.get(prefix);
        log.info("get after expire:{}", obj);

        log.info("getSet ######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        Supplier<RedisConfig> supplier = () -> {
            log.info("getSet: go into supplier");
            return new RedisConfig();
        };
        redisClient.getSet(prefix, supplier, RedisClient.ONE_SECOND);
        redisClient.getSet(prefix, supplier, RedisClient.ONE_SECOND);
        obj = redisClient.get(prefix);
        log.info("getSet:{}", obj);
        Thread.sleep(1500);
        obj = redisClient.get(prefix);
        log.info("getSet after expire:{}", obj);

        log.info("batchGetSet ######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        redisClient.batchGetSet(prefix, Arrays.asList(1, 2, 3), (keys) -> {
            log.info("batchGetSet: go into function, keys:{}", keys);
            Map<Integer, RedisConfig> map = new HashMap<>();
            for (Integer key : keys) {
                map.put(key, new RedisConfig());
            }
            return map;
        }, RedisClient.ONE_SECOND);
        redisClient.batchGetSet(prefix, Arrays.asList(1, 2, 3, 4, 5, 6), (keys) -> {
            log.info("batchGetSet: go into function, keys:{}", keys);
            Map<Integer, RedisConfig> map = new HashMap<>();
            for (Integer key : keys) {
                map.put(key, new RedisConfig());
            }
            return map;
        }, RedisClient.ONE_SECOND);
        redisClient.batchGetSet(prefix, Arrays.asList(1, 4, 6), (keys) -> {
            log.info("batchGetSet: go into function, keys:{}", keys);
            Map<Integer, RedisConfig> map = new HashMap<>();
            for (Integer key : keys) {
                map.put(key, new RedisConfig());
            }
            return map;
        }, RedisClient.ONE_SECOND);
        for (Integer key : Arrays.asList(1, 2, 3, 4, 5, 6)) {
            obj = redisClient.get(prefix + key);
            log.info("get, key:{}, value:{}", prefix + key, obj);
        }
        Thread.sleep(1500);
        for (Integer key : Arrays.asList(1, 2, 3, 4, 5, 6)) {
            obj = redisClient.get(prefix + key);
            log.info("get after expire, key:{}, value:{}", prefix + key, obj);
        }

        log.info("setWithNull######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        try {
            obj = redisClient.getWithNull(prefix);
            log.info("getWithNull null from cache:{}", obj);
            redisClient.setWithNull(prefix, new RedisConfig(), null, null);
            obj = redisClient.getWithNull(prefix);
            log.info("getWithNull:{}", obj);
            // 抛出异常代表缓存中有值但是空值
            redisClient.setWithNull(prefix, null, null, null);
            redisClient.getWithNull(prefix);
        } catch (RedisClient.NullValueException ex) {
            log.info("getWithNull null value from cache");
        }
        redisClient.remove(prefix);
        obj = redisClient.get(prefix);
        log.info("getWithNull after remove:{}", obj);

        log.info("getSetWithNull ######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        supplier = () -> {
            log.info("getSetWithNull: go into supplier, return object");
            return new RedisConfig();
        };
        redisClient.getSetWithNull(prefix, supplier, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        redisClient.getSetWithNull(prefix, supplier, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        obj = redisClient.getWithNull(prefix);
        log.info("getWithNull, return object:{}", obj);
        Thread.sleep(1500);
        obj = redisClient.get(prefix);
        log.info("getSetWithNull after expire:{}", obj);

        supplier = () -> {
            log.info("getSetWithNull: go into supplier, return null");
            return null;
        };
        redisClient.getSetWithNull(prefix, supplier, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        redisClient.getSetWithNull(prefix, supplier, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        try {
            redisClient.getWithNull(prefix);
        } catch (RedisClient.NullValueException ex) {
            log.info("getWithNull, return null value from cache");
        }
        Thread.sleep(1500);
        obj = redisClient.get(prefix);
        log.info("getSetWithNull after expire:{}", obj);

        log.info("batchGetSetWithNull ######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        redisClient.batchGetSetWithNull(prefix, Arrays.asList(1, 2, 3), (keys) -> {
            log.info("batchGetSetWithNull: go into function, keys:{}", keys);
            Map<Integer, RedisConfig> map = new HashMap<>();
            for (Integer key : keys) {
                map.put(key, new RedisConfig());
            }
            return map;
        }, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        redisClient.batchGetSetWithNull(prefix, Arrays.asList(1, 2, 3, 4, 5, 6), (keys) -> {
            log.info("batchGetSetWithNull: go into function, keys:{}", keys);
            Map<Integer, RedisConfig> map = new HashMap<>();
            for (Integer key : keys) {
                map.put(key, null);
            }
            return map;
        }, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        redisClient.batchGetSetWithNull(prefix, Arrays.asList(1, 4, 6), (keys) -> {
            log.info("batchGetSetWithNull: go into function, keys:{}", keys);
            Map<Integer, RedisConfig> map = new HashMap<>();
            for (Integer key : keys) {
                map.put(key, new RedisConfig());
            }
            return map;
        }, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        for (Integer key : Arrays.asList(1, 2, 3, 4, 5, 6, 7)) {
            try {
                obj = redisClient.getWithNull(prefix + key);
                log.info("get, key:{}, value:{}", prefix + key, obj);
            } catch (RedisClient.NullValueException ex) {
                log.info("get, key:{}, null value:{}", prefix + key, null);
            }
        }
        Thread.sleep(1500);
        for (Integer key : Arrays.asList(1, 2, 3, 4, 5, 6)) {
            obj = redisClient.get(prefix + key);
            log.info("get after expire, key:{}, value:{}", prefix + key, obj);
        }

        log.info("lockRelease ######################################################");
        log.info("lock:{}", redisClient.lock(prefix, 1));
        log.info("lock:{}", redisClient.lock(prefix, 1));
        log.info("release:{}", redisClient.release(prefix));
        log.info("lock:{}", redisClient.lock(prefix, 1));
        log.info("release:{}", redisClient.release(prefix));

        System.exit(0);
    }

}
