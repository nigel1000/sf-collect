package com.common.collect.container.redis;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.SerializeUtil;

import java.io.IOException;

/**
 * Created by hznijianfeng on 2019/12/19.
 */

public class HessianRedisSerialize implements RedisSerialize {

    @Override
    public byte[] serialize(Object obj) {
        try {
            return SerializeUtil.hessianSerialize(obj);
        } catch (IOException ex) {
            throw UnifiedException.gen("序列化失败", ex);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) {
        try {
            return SerializeUtil.hessianDeserialize(bytes);
        } catch (IOException ex) {
            throw UnifiedException.gen("序列化失败", ex);
        }
    }
}
