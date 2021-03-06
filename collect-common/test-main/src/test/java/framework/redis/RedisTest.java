package framework.redis;

import com.common.collect.framework.redis.RedisClient;
import com.common.collect.framework.redis.RedisConfig;
import com.common.collect.framework.redis.ValueWrapper;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.slf4j.Slf4jUtil;
import com.common.collect.lib.util.ExceptionUtil;
import com.common.collect.lib.util.ThreadPoolUtil;
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

    public static void main(String[] args) {
        RedisClient redisClient = new RedisClient();

        String prefix = "test_key_";
        log.info("set without expireTime######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        redisClient.set(prefix, new RedisConfig(), null, null);
        ValueWrapper<RedisConfig> wrapper = redisClient.getValueWrapper(prefix);
        log.info("get obj:{}", wrapper);
        redisClient.remove(prefix);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("get obj after remove:{}", wrapper);

        redisClient.set(prefix, null, null, null);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("get null:{}", wrapper);
        redisClient.remove(prefix);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("get null after remove:{}", wrapper);

        log.info("set with expireTime######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        redisClient.set(prefix, new RedisConfig(), RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("get obj:{}", wrapper);
        ExceptionUtil.eatException(() -> Thread.sleep(1500), null);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("get obj after expire:{}", wrapper);

        redisClient.set(prefix, null, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("get null:{}", wrapper);
        ExceptionUtil.eatException(() -> Thread.sleep(1500), null);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("get null after expire:{}", wrapper);

        log.info("getSet ######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        Supplier<RedisConfig> supplier = () -> {
            log.info("getSet: go into supplier");
            return new RedisConfig();
        };
        redisClient.getSet(prefix, supplier, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("getSet:{}", wrapper);
        redisClient.getSet(prefix, supplier, RedisClient.ONE_SECOND, RedisClient.ONE_SECOND);
        wrapper = redisClient.getValueWrapper(prefix);
        log.info("getSet:{}", wrapper);
        ExceptionUtil.eatException(() -> Thread.sleep(1500), null);
        log.info("getSet after expire:{}", wrapper);

        log.info("batchGetSet ######################################################");
        Slf4jUtil.setLogLevel(null, "info");
        // 缓存3秒，不会被提前两秒失效
        redisClient.batchGetSet(
                prefix,
                Arrays.asList(1, 2, 3),
                (keys) -> {
                    log.info("batchGetSet: go into function, keys:{}", keys);
                    Map<Integer, RedisConfig> map = new HashMap<>();
                    for (Integer key : keys) {
                        if (map.size() > 1) {
                            map.put(key, new RedisConfig());
                        } else {
                            map.put(key, null);
                        }
                    }
                    return map;
                },
                RedisClient.ONE_SECOND * 3,
                RedisClient.ONE_SECOND * 3);

        redisClient.batchGetSet(
                prefix,
                Arrays.asList(1, 2, 3, 4, 5, 6), (keys) -> {
                    log.info("batchGetSet: go into function, keys:{}", keys);
                    return null;
                },
                RedisClient.ONE_SECOND * 3,
                RedisClient.ONE_SECOND * 3);

        for (Integer key : Arrays.asList(1, 2, 3, 4, 5, 6)) {
            wrapper = redisClient.getValueWrapper(prefix + key);
            log.info("batchGetSet, key:{}, value:{}", prefix + key, wrapper);
        }
        ExceptionUtil.eatException(() -> Thread.sleep(3000), null);
        for (Integer key : Arrays.asList(1, 2, 3, 4, 5, 6)) {
            wrapper = redisClient.getValueWrapper(prefix + key);
            log.info("batchGetSet after expire, key:{}, value:{}", prefix + key, wrapper);
        }
        for (Integer key : Arrays.asList(1, 2, 3, 4, 5, 6)) {
            redisClient.remove(prefix + key);
        }

        log.info("version ######################################################");
        String version = redisClient.getVersion(prefix);
        log.info("version get, key:{}, value:{}", prefix, version);
        version = redisClient.increaseVersion(prefix);
        log.info("version increase, key:{}, value:{}", prefix, version);
        redisClient.remove(prefix);

        log.info("lockRelease ######################################################");
        for (int i = 0; i < 10; i++) {
            ThreadPoolUtil.exec(() -> {
                try {
                    redisClient.lockRelease(prefix, RedisClient.ONE_SECOND, "获取锁失败", () -> {
                        log.info("lockRelease:获取锁成功!!");
                        return true;
                    });
                } catch (UnifiedException ex) {
                    log.info(ex.getMessage());
                }
            });
        }
        ExceptionUtil.eatException(() -> Thread.sleep(2000), null);

        log.info("lockMutexGetSet ######################################################");
        for (int i = 0; i < 10; i++) {
            ThreadPoolUtil.exec(() -> {
                try {
                    redisClient.lockMutexGetSet(
                            prefix,
                            () -> {
                                log.info("go into from db start");
                                ExceptionUtil.eatException(() -> Thread.sleep(40), null);
                                log.info("go into from db end");
                                return new RedisConfig();
                            },
                            RedisClient.ONE_SECOND,
                            RedisClient.ONE_SECOND,
                            RedisClient.ONE_SECOND,
                            20L);
                } catch (UnifiedException ex) {
                    log.info(ex.getMessage(), ex.getCause());
                }
            });
        }
        ExceptionUtil.eatException(() -> Thread.sleep(3000), null);

        System.exit(0);
    }

}
