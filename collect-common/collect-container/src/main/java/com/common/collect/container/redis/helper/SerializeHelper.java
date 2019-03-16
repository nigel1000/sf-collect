package com.common.collect.container.redis.helper;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.SerializeUtil;
import com.common.collect.container.redis.base.RedisConstants;
import com.common.collect.container.redis.enums.SerializeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by hznijianfeng on 2018/9/6.
 */

@Slf4j
public class SerializeHelper {

    public static byte[] serialize(Object obj, SerializeEnum serializeEnum) {

        if (obj == null) {
            return null;
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
            switch (serializeEnum) {
                case HESSIAN:
                    SerializeUtil.hessianSerialize(obj, outputStream);
                    break;
                case PROTO_STUFF:
                    throw UnifiedException.gen(RedisConstants.MODULE, "暂不支持");
                case JAVA:
                    SerializeUtil.javaSerialize(obj, outputStream);
                    break;
                case KRYO:
                    throw UnifiedException.gen(RedisConstants.MODULE, "暂不支持");
            }
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw UnifiedException.gen(RedisConstants.MODULE, "序列化失败", ex);
        }

    }

    public static <T> T deserialize(byte[] data, SerializeEnum serializeEnum) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            T value = null;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            switch (serializeEnum) {
                case HESSIAN:
                    value = SerializeUtil.hessianDeserialize(inputStream);
                    break;
                case PROTO_STUFF:
                    throw UnifiedException.gen(RedisConstants.MODULE, "暂不支持");
                case JAVA:
                    value = SerializeUtil.javaDeserialize(inputStream);
                    break;
                case KRYO:
                    throw UnifiedException.gen(RedisConstants.MODULE, "暂不支持");
            }
            return value;

        } catch (Exception ex) {
            throw UnifiedException.gen(RedisConstants.MODULE, "反序列化失败", ex);
        }
    }

}
