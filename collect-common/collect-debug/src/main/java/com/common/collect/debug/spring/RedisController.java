package com.common.collect.debug.spring;

import com.common.collect.api.Response;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
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

    private RedisClient redisClient = new RedisClient();

    private String redis_key = "c26b094cc27346379266147682c41fc0";


    // json 中带有引号等特殊符号，需要用 url encode。
    // /back/door/redis/operate?type=set&key=redis&value=%7B%22cmdType%22%3A1%2C%22departmentId%22%3A%2222%22%2C%22enabled%22%3A1%2C%22groupListIdOrGroupNameGroupListId%22%3A%2232%22%2C%22groupListIdOrGroupNameGroupName%22%3A%2232%22%2C%22groupListIds%22%3A%5B%5D%2C%22groupNames%22%3A%5B%5D%2C%22pageNo%22%3A1%2C%22pageSize%22%3A1%2C%22ruleType%22%3A0%2C%22sortBy%22%3A%22create_atdesc%22%2C%22workEmail%22%3A%22%22%2C%22workName%22%3A%22%22%2C%22workNo%22%3A%22%22%7D&clazz=com.common.collect.GroupListContext&expire=60&uuid=c26b094cc27346379266147682c41fc0

    // /back/door/redis/operate?type=set&key=redis&value=1&expire=60&uuid=c26b094cc27346379266147682c41fc0
    // /back/door/redis/operate?type=get&key=redis&uuid=c26b094cc27346379266147682c41fc0

    // /back/door/redis/operate?type=hset&key=redis&field=redis&&value=1&uuid=c26b094cc27346379266147682c41fc0
    // /back/door/redis/operate?type=hget&key=redis&field=redis&uuid=c26b094cc27346379266147682c41fc0
    // /back/door/redis/operate?type=hgetAll&key=redis&uuid=c26b094cc27346379266147682c41fc0

    // /back/door/redis/operate?type=del&key=redis&uuid=c26b094cc27346379266147682c41fc0
    @RequestMapping("/operate")
    public Response<Object> generator(@RequestParam("type") String type,
                                      @RequestParam(value = "key", required = true) String key,
                                      @RequestParam(value = "field", required = false) String field,
                                      @RequestParam(value = "value", required = false) String value,
                                      @RequestParam(value = "clazz", required = false) String clazz,
                                      @RequestParam(value = "expire", required = false) Integer expire,
                                      @RequestParam(value = "uuid") String uuid) {

        if (!uuid.equals(redis_key)) {
            throw UnifiedException.gen("无权限访问此链接");
        }

        Object obj = value;
        if ("set".equals(type) || "hset".equals(type)) {
            if (clazz != null) {
                try {
                    Class<?> cls = Class.forName(clazz);
                    if (cls.isAssignableFrom(List.class)) {
                        obj = JsonUtil.json2beanList(value, cls);
                    } else {
                        obj = JsonUtil.json2bean(value, cls);
                    }
                } catch (Exception ex) {
                    throw UnifiedException.gen("set 操作失败", ex);
                }
            }
        }

        if ("del".equals(type)) {
            redisClient.remove(key);
        } else if ("get".equals(type)) {
            return Response.ok(redisClient.get(key));
        } else if ("set".equals(type)) {
            redisClient.set(key, obj, Long.valueOf(expire));
        } else {
            return Response.ok("操作类型不存在");
        }

        return Response.ok("操作成功");
    }


}
