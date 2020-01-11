package com.common.collect.web.idoc;

import com.common.collect.api.Response;
import com.common.collect.container.idoc.IDocField;
import com.common.collect.container.idoc.IDocMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public class IDocDemo {

    @IDocMethod(id = "1", name = "测试接口1", author = "hznijianfeng")
    @RequestMapping(value = "/back/door/bean/invoke", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<IDocObject> idoc1(
            @IDocField(nameDesc = "bean 名称", desc = "注意事项", value = "configDao")
            @RequestParam(value = "beanName")
                    String beanName,
            String methodName,
            @RequestBody IDocObject object1,
            IDocObject object2) {
        return Response.ok();
    }

    @IDocMethod(id = "2", name = "测试接口2", author = "hznijianfeng")
    @RequestMapping(value = "/back/door/bean/invoke", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<IDocObject> idoc2(
            @IDocField(nameDesc = "bean 名称", desc = "注意事项", value = "configDao")
            @RequestParam(value = "beanName")
                    String beanName,
            String methodName,
            @RequestBody IDocObject object1,
            IDocObject object2) {
        return Response.ok();
    }

}


