package com.common.collect.test.debug.dubbo.invoker;

import com.alibaba.dubbo.common.URL;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/1/17.
 */

@Data
public class InvokerParam {

    private String zkAddress;

    private Class mockClass;
    private String mockClassName;

    private String targetIp;

    private Map<String, String> ipMap = new HashMap<>();

    private String application = "invoker-debug";
    private String host = "127.0.0.1";
    private String anyHost = "true";
    private String check = "false";
    private String defaultTimeout = "5000";
    private String side = "consumer";

    private String protocol = "dubbo";
    private String group;
    private String version = "1.0";
    private Integer port = 20880;


    public static Map<String, String> getParams(InvokerParam invokerParam) {
        Map<String, String> param = Maps.newHashMap();
        param.put("anyhost", invokerParam.getAnyHost());
        param.put("application", invokerParam.getApplication());
        param.put("check", invokerParam.getCheck());
        param.put("default.timeout", invokerParam.getDefaultTimeout());
        param.put("side", invokerParam.getSide());
        param.put("path", invokerParam.getMockClassName());
        param.put("interface", invokerParam.getMockClassName());
        param.put("group", invokerParam.getGroup());
        param.put("version", invokerParam.getVersion());
        return param;
    }

    public static Map<String, String> getInvokerParam(URL providerUrl, InvokerParam invokerParam) {
        Map<String, String> param = Maps.newHashMap();
        param.put("protocol", providerUrl.getProtocol());
        param.put("address", providerUrl.getAddress());
        param.put("group", providerUrl.getParameter("group"));
        param.put("default.timeout", invokerParam.getDefaultTimeout());
        return param;
    }

    public void setMockClass(@NonNull Class mockClass) {
        this.mockClass = mockClass;
        this.mockClassName = mockClass.getName();
    }

}
