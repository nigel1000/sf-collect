package com.common.collect.container.redis;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.redis.base.RedisConstants;
import com.common.collect.util.constant.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by hznijianfeng on 2018/9/6.
 */

@Data
public class RedisKey implements Serializable {

    private String prefixKey = "";

    private String suffixKey = "";

    private Integer expireTime = Constants.ONE_MINUTE;

    private RedisKey() {
    }

    public static RedisKey createKey(String key) {
        RedisKey redisKey = new RedisKey();
        redisKey.setSuffixKey(key);
        return redisKey;
    }

    public static RedisKey createKey(String key, Integer expireTime) {
        RedisKey redisKey = new RedisKey();
        redisKey.setSuffixKey(key);
        redisKey.setExpireTime(expireTime);
        return redisKey;
    }

    public static RedisKey createPrefix(String prefix) {
        RedisKey redisKey = new RedisKey();
        redisKey.setPrefixKey(prefix);
        return redisKey;
    }

    public void validSelf() {
        if (StringUtils.isBlank(prefixKey) && StringUtils.isBlank(suffixKey)) {
            throw UnifiedException.gen(RedisConstants.MODULE, "缓存 key 不能为空");
        }
        if (expireTime == null) {
            throw UnifiedException.gen(RedisConstants.MODULE, "缓存 过期时间 不能为空");
        }
    }

    public String getKey() {
        validSelf();
        return this.getPrefixKey() + this.getSuffixKey();
    }

}