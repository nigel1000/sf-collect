package com.common.collect.container.idoc.demo;

import com.common.collect.api.Response;
import com.common.collect.container.idoc.annotations.IDocField;
import com.common.collect.container.idoc.annotations.IDocFieldExclude;
import com.common.collect.container.idoc.annotations.IDocMethod;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DemoController {

    @IDocMethod(id = "1", name = "测试接口", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/bean/iDocDemo", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<DocObject> iDocDemo(
            @IDocField(nameDesc = "bean 名称", desc = "注意事项")
            @RequestParam(value = "beanName", defaultValue = "configDao")
                    String beanName,
            DocObjectSub docObjectSub,
            String methodName) {
        return Response.ok();
    }

    @IDocMethod(id = "2", name = "测试接口", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/bean/arrayCount", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<List<List<String[][]>>> arrayCount(String[][] strArray, List<DocObjectSub[]> subList) {
        return Response.ok();
    }

    @Data
    public static class DocObject {
        private Long[] longArray;
        private DocObjectSub[] subArray;
        private List<Long> longList;
        private List<DocObjectSub> subList;
        private String key;
    }

    @Data
    public static class DocObjectSub {
        @IDocField(desc = "bool 值", nameDesc = "测试 bool", required = false)
        private Boolean bool;
        @IDocField(desc = "long 值", nameDesc = "测试 long")
        private Long longNum;
        @IDocField(value = "1234567890")
        private Integer intNum;
        private Float floatNum;
        private Double doubleNum;
        private Byte byteNum;
        private Short shortNum;
        private BigDecimal bigDecimalNum;
        private Character chatStr;
        private String str;
        @IDocFieldExclude
        private Date dateExclude;
        private Date date;
    }

}



