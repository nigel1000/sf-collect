package com.common.collect.framework.redis;

/**
 * Created by hznijianfeng on 2019/12/19.
 */

public interface RedisSerialize {

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes);

}
