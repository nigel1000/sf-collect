package com.common.collect.container.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by hznijianfeng on 2019/12/26.
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueWrapper<T> implements Serializable {
    private static final long serialVersionUID = 8812225795374215664L;

    private T target;

    public boolean isNullValue() {
        return target == null;
    }

    public Long getCacheTime(Long notNullCacheTime, Long nullCacheTime) {
        Long expireTime;
        if (this.isNullValue()) {
            expireTime = nullCacheTime;
        } else {
            expireTime = notNullCacheTime;
        }
        return expireTime;
    }

}
