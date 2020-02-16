package com.common.collect.test.web.controller;

import com.common.collect.framework.redis.RedisClient;
import com.common.collect.framework.redis.ValueWrapper;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/4/11.
 */

@RestController
@RequestMapping("/back/door/redis")
@Slf4j
public class RedisController {

    @Autowired
    private RedisClient redisClient;

    // json 中带有引号等特殊符号，需要用 url encode。
    // http://localhost:8181/back/door/redis/operate?type=set&key=redis&clazz=java.util.ArrayList&value=%5B1%2C2%2C3%5D
    // http://localhost:8181/back/door/redis/operate?type=set&key=redis&value=1&expire=60000

    // http://localhost:8181/back/door/redis/operate?type=get&key=redis
    // http://localhost:8181/back/door/redis/operate?type=del&key=redis

    @RequestMapping("/operate")
    public Object generator(@RequestParam("type") String type,
                            @RequestParam(value = "key") String key,
                            @RequestParam(value = "value", required = false) String value,
                            @RequestParam(value = "clazz", required = false) String clazz,
                            @RequestParam(value = "expire", required = false) Integer expire) {

        Object obj = value;
        if ("set".equals(type) && clazz != null) {
            Class<?> cls = ClassUtil.getClass(clazz);
            if (cls.isAssignableFrom(List.class)) {
                obj = JsonUtil.json2beanList(value, cls);
            } else {
                obj = JsonUtil.json2bean(value, cls);
            }
        }

        if ("del".equals(type)) {
            redisClient.remove(key);
            return "del 操作成功";
        } else if ("get".equals(type)) {
            ValueWrapper valueWrapper = redisClient.getValueWrapper(key);
            if (valueWrapper != null) {
                return valueWrapper.getTarget();
            } else {
                return "key 不存在";
            }
        } else if ("set".equals(type)) {
            if (expire == null) {
                redisClient.set(key, obj, null, null);
            } else {
                redisClient.set(key, obj, Long.valueOf(expire), Long.valueOf(expire));
            }
            return "set 操作成功";
        } else {
            return "操作类型不存在";
        }
    }


}
