package collect.container;

import com.common.collect.container.redis.JedisOperator;
import com.common.collect.container.redis.client.RedisClientFactory;
import com.common.collect.container.redis.client.RedisClientUtil;
import com.common.collect.container.redis.enums.SerializeEnum;
import com.common.collect.util.log4j.Slf4jUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

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

        String value = "value";

        String key = "test-key-1";
        log.info("put:{}", RedisClientUtil.put(jedisOperator, key, value, 10));
        String ret = RedisClientUtil.get(jedisOperator, key);
        log.info("get:{}", ret);
        log.info("remove:{}", RedisClientUtil.remove(jedisOperator, key));
        ret = RedisClientUtil.get(jedisOperator, key);
        log.info("get:{}", ret);
        log.info("######################################################");

        log.info("lock:{}", RedisClientUtil.lock(jedisOperator, key, 10));
        log.info("lock:{}", RedisClientUtil.lock(jedisOperator, key, 10));
        log.info("release:{}", RedisClientUtil.release(jedisOperator, key));
        log.info("lock:{}", RedisClientUtil.lock(jedisOperator, key, 10));
        log.info("releaseWithBizId:{}", RedisClientUtil.releaseWithBizId(jedisOperator, key, "1"));
        log.info("######################################################");

        log.info("lockWithBizId:{}", RedisClientUtil.lockWithBizId(jedisOperator, key, "2", 10));
        log.info("lockWithBizId:{}", RedisClientUtil.lockWithBizId(jedisOperator, key, "2", 10));
        log.info("releaseWithBizId:{}", RedisClientUtil.releaseWithBizId(jedisOperator, key, "2"));
        log.info("lockWithBizId:{}", RedisClientUtil.lockWithBizId(jedisOperator, key, "2", 10));
        log.info("releaseWithBizId:{}", RedisClientUtil.releaseWithBizId(jedisOperator, key, "2"));
        log.info("releaseWithBizId:{}", RedisClientUtil.releaseWithBizId(jedisOperator, key, "2"));
        log.info("######################################################");

        log.info("put:{}", RedisClientUtil.put(jedisOperator, key, value, 10));
        List<String> personList = RedisClientUtil.batchGet(jedisOperator, Lists.newArrayList(key));
        log.info("batchGet:{}", personList);
        Map<String, String> personMap = RedisClientUtil.batchGetMap(jedisOperator, Lists.newArrayList(key));
        log.info("batchGetMap:{}", personMap);
        log.info("######################################################");

        Slf4jUtil.setLogLevel("debug");

        Map<String, String> batchGetPutMap =
                RedisClientUtil.batchGetPut(jedisOperator, Lists.newArrayList(key, key + "formBiz"), 10, (t) -> {
                    Map<String, String> biz = Maps.newHashMap();
                    for (String s : t) {
                        biz.put(s, value);
                    }
                    return biz;
                });
        log.info("batchGetPut:{}", batchGetPutMap);
        RedisClientUtil.batchRemove(jedisOperator, Lists.newArrayList(key, key + "formBiz"));
        log.info("######################################################");

        Slf4jUtil.setLogLevel("info");
        log.info("lockRelease:{}", RedisClientUtil.lockRelease(jedisOperator, key, 10, null, () -> value));
        log.info("######################################################");

        System.exit(0);
    }

}
