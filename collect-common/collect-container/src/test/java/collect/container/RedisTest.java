package collect.container;

import com.common.collect.container.redis.JedisOperator;
import com.common.collect.container.redis.client.RedisClientFactory;
import com.common.collect.container.redis.client.RedisClientUtil;
import com.common.collect.container.redis.enums.SerializeEnum;
import com.common.collect.util.constant.Constants;
import com.common.collect.util.log4j.Slf4jUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by nijianfeng on 2019/3/16.
 */

@Slf4j
@SuppressWarnings("unchecked")
public class RedisTest {

    public static void main(String[] args) throws Exception {

        RedisClientFactory redisClientFactory = new RedisClientFactory();
        JedisOperator jedisOperator = (JedisOperator) redisClientFactory.newSingleClient();
        jedisOperator.getRedisConfig().setSerializeEnum(SerializeEnum.HESSIAN);
        jedisOperator.init();

        new RedisClientUtil();
        Slf4jUtil.setLogLevel("debug");

        int expire = Constants.ONE_SECOND * 10;
        //放入缓存 11L, 22L, 33L
        Map<Long, Long> cacheRet = RedisClientUtil.batchGetPut(jedisOperator, "collect.container",
                Lists.newArrayList(11L, 22L, 33L), expire, (keys) -> {
                    Map ret = Maps.newHashMap();
                    for (Object key : keys) {
                        ret.put(key, key);
                    }
                    return ret;
                });
        log.info("ret:{}", cacheRet);
        //删除缓存 11L, 22L
        RedisClientUtil.batchDelete(jedisOperator, "collect.container",
                Lists.newArrayList(11L, 22L));
        //放入缓存 11L, 22L, 33L, 44L
        cacheRet = RedisClientUtil.batchGetPut(jedisOperator, "collect.container",
                Lists.newArrayList(11L, 22L, 33L, 44L), 10, (keys) -> {
                    Map ret = Maps.newHashMap();
                    for (Object key : keys) {
                        ret.put(key, key);
                    }
                    return ret;
                });
        log.info("ret:{}", cacheRet);
        try {
            RedisClientUtil.lock(jedisOperator, "collect.container.lock", expire, "分布式锁设置失败1");
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        try {
            RedisClientUtil.lock(jedisOperator, "collect.container.lock", expire, "分布式锁设置失败2");
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        RedisClientUtil.release(jedisOperator, "collect.container.lock");
        RedisClientUtil.release(jedisOperator, "collect.container.lock");

        RedisClientUtil.upsert(jedisOperator, "collect.container.upsert", "upsert", expire);

        Thread.sleep(2000);
        System.exit(0);
    }

}
