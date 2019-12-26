package com.common.collect.container.redis;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by hznijianfeng on 2019/12/26.
 */
@Data
public class ValueWrapper<T> implements Serializable {
    private static final long serialVersionUID = 8812225795374215664L;

    private T target;
    private Long expireTime;

    public ValueWrapper(T target) {
        this.target = target;
    }

    public Long getCacheTime(Long notNullCacheTime, Long nullCacheTime) {
        Long cacheTime;
        if (this.isNullValue()) {
            cacheTime = nullCacheTime;
        } else {
            cacheTime = notNullCacheTime;
        }
        return cacheTime;
    }

    public boolean isNullValue() {
        return target == null;
    }

    public boolean isExpire() {
        if (expireTime == null) {
            return false;
        }
        return System.currentTimeMillis() > expireTime;
    }

    public void setExpireTime(Long cacheTime) {
        if (cacheTime != null) {
            // 提前 2秒 续期
            this.expireTime = System.currentTimeMillis() + cacheTime - TimeUnit.SECONDS.toMillis(2);
        }
    }
}
