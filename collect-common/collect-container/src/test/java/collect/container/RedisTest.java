package collect.container;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.redis.JedisOperator;
import com.common.collect.container.redis.client.RedisClientFactory;
import com.common.collect.container.redis.client.RedisClientUtil;
import com.common.collect.container.redis.enums.SerializeEnum;
import com.common.collect.util.FunctionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by nijianfeng on 2019/3/16.
 */

@Slf4j
public class RedisTest {

    public static void main(String[] args) throws Exception {


        RedisClientFactory redisClientFactory = new RedisClientFactory();
        JedisOperator jedisOperator = (JedisOperator) redisClientFactory.newSingleClient();
        jedisOperator.getRedisConfig().setSerializeEnum(SerializeEnum.HESSIAN);
        jedisOperator.init();

        RedisClientUtil redisClientUtil = new RedisClientUtil();
        redisClientUtil.setRedisClient(jedisOperator);

        String value = "value";
        String lockId = "lockId";
        String prefix = "test_key_";
        Long key = 1L;
        String keyStr = prefix + key;
        List<Long> keys = Lists.newArrayList(11L, 12L, 13L, 14L, 15L, 16L);
        List<String> keysStr = FunctionUtil.valueList(keys, (t) -> prefix + t);
        log.info("######################################################");

        log.info("put:{} {}", keyStr, redisClientUtil.put(keyStr, value, 10));
        String ret = redisClientUtil.get(keyStr);
        log.info("get:{} {}", keyStr, ret);
        log.info("remove:{} {}", keyStr, redisClientUtil.del(keyStr));
        ret = redisClientUtil.get(keyStr);
        log.info("get:{} {}", keyStr, ret);

        log.info("######################################################");

        log.info("lock:{} {}", keyStr, redisClientUtil.lock(keyStr, 10));
        log.info("lock:{} {}", keyStr, redisClientUtil.lock(keyStr, 10));
        log.info("release:{} {}", keyStr, redisClientUtil.release(keyStr));
        log.info("lock:{} {}", keyStr, redisClientUtil.lock(keyStr, 10));
        log.info("releaseWithBizId:{} {}", keyStr, redisClientUtil.releaseWithBizId(keyStr, "1"));

        log.info("######################################################");

        log.info("lockWithBizId:{} {} {}", keyStr, lockId, redisClientUtil.lockWithBizId(keyStr, lockId, 10));
        log.info("lockWithBizId:{} {} {}", keyStr, lockId, redisClientUtil.lockWithBizId(keyStr, lockId, 10));
        log.info("releaseWithBizId:{} {} {}", keyStr, lockId, redisClientUtil.releaseWithBizId(keyStr, lockId));
        log.info("lockWithBizId:{} {} {}", keyStr, lockId, redisClientUtil.lockWithBizId(keyStr, lockId, 10));
        log.info("releaseWithBizId:{} {} {}", keyStr, lockId, redisClientUtil.releaseWithBizId(keyStr, lockId));
        log.info("releaseWithBizId:{} {} {}", keyStr, lockId, redisClientUtil.releaseWithBizId(keyStr, lockId));
        try {
            redisClientUtil.lockRelease(keyStr, 10, "获取锁失败了，稍后再试", () -> {
                log.info("lockRelease:{} {} {}", keyStr, lockId, "锁获取成功");
                return true;
            });
            log.info("lockWithBizId:{} {} {}", keyStr, lockId, redisClientUtil.lockWithBizId(keyStr, lockId, 10));
            redisClientUtil.lockRelease(keyStr, 10, "获取锁失败了，稍后再试", () -> {
                log.info("lockRelease:{} {} {}", keyStr, lockId, "锁获取成功");
                return true;
            });
        } catch (UnifiedException ex) {
            log.info("lockRelease:{} {} {}", keyStr, lockId, ex.getErrorMessage());
        }
        log.info("######################################################");

        for (String s : keysStr) {
            log.info("put:{} {}", s, redisClientUtil.put(s, value, 10));
        }
        List<String> personList = redisClientUtil.batchGet(keysStr);
        log.info("batchGet:{} {}", keysStr, personList);
        Map<String, String> personMap = redisClientUtil.batchGetMap(keysStr);
        log.info("batchGetMap:{} {}", keysStr, personMap);

        log.info("######################################################");

//        Slf4jUtil.setLogLevel("debug");
        Random random = new Random();

        redisClientUtil.batchDel(keysStr);
        for (String s : keysStr) {
            redisClientUtil.put(s, value, 10);
        }
        redisClientUtil.batchDel(keysStr.subList(2, 4));
        Map<String, String> batchGetPutStringMap =
                redisClientUtil.batchGetPut(keysStr, 10, (t) -> {
                    Map<String, String> biz = Maps.newHashMap();
                    for (String s : t) {
                        if (random.nextInt(100) > 50) {
                            biz.put(s, value + s);
                        }
                    }
                    return biz;
                });
        log.info("batchGetPutStringMap:{}", batchGetPutStringMap);


        redisClientUtil.batchDel(keysStr);
        for (String s : keysStr) {
            redisClientUtil.put(s, value, 10);
        }
        redisClientUtil.batchDel(keysStr.subList(2, 4));
        batchGetPutStringMap =
                redisClientUtil.batchGetPut(keysStr, 10, (t) -> {
                    Map<String, String> biz = Maps.newHashMap();
                    for (String s : t) {
                        if (random.nextInt(100) > 50) {
                            biz.put(s, value + s);
                        }
                    }
                    return biz;
                }, "default");
        log.info("batchGetPutStringMap:{}", batchGetPutStringMap);
        log.info("batchGetPutStringMap:{}", redisClientUtil.batchGetMap(keysStr));


        redisClientUtil.batchDel(prefix, keys);
        for (Long s : keys) {
            redisClientUtil.put(prefix + s, value, 10);
        }
        redisClientUtil.batchDel(prefix, keys.subList(2, 4));
        Map<Long, String> batchGetPutObjectMap =
                redisClientUtil.batchGetPut(prefix, keys, 10, (t) -> {
                    Map<Long, String> biz = Maps.newHashMap();
                    for (Long s : t) {
                        if (random.nextInt(100) > 50) {
                            biz.put(s, value + s);
                        }
                    }
                    return biz;
                });
        log.info("batchGetPutObjectMap:{}", batchGetPutObjectMap);


        redisClientUtil.batchDel(prefix, keys);
        for (Long s : keys) {
            redisClientUtil.put(prefix + s, value, 10);
        }
        redisClientUtil.batchDel(prefix, keys.subList(2, 4));
        batchGetPutObjectMap =
                redisClientUtil.batchGetPut(prefix, keys, 10, (t) -> {
                    Map<Long, String> biz = Maps.newHashMap();
                    for (Long s : t) {
                        if (random.nextInt(100) > 50) {
                            biz.put(s, value + s);
                        }
                    }
                    return biz;
                }, "default");
        log.info("batchGetPutObjectMap:{}", batchGetPutObjectMap);
        log.info("batchGetPutObjectMap:{}", redisClientUtil.batchGetMap(prefix, keys));

//        Slf4jUtil.setLogLevel("info");

        log.info("######################################################");

        System.exit(0);
    }

}
