package com.common.collect.debug.dubbo.invoker;

import com.alibaba.dubbo.common.URL;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;

/**
 * Created by hznijianfeng on 2019/1/17.
 */

@Data
public class InvokerParam {

    private String zkAddress;

    private Class mockClass;
    private String mockClassName;

    private String application = "invoker-debug";
    private String host = "127.0.0.1";
    private String anyHost = "true";
    private String check = "false";
    private String defaultTimeout = "5000";
    private String side = "consumer";

    private String protocol = "dubbo";
    private String group;
    private String version = "1.0";
    private int port = 20880;


    public static Map<String, String> getParams(InvokerParam invokerParam) {
        Map<String, String> params = Maps.newHashMap();
        params.put("anyhost", invokerParam.getAnyHost());
        params.put("application", invokerParam.getApplication());
        params.put("check", invokerParam.getCheck());
        params.put("default.timeout", invokerParam.getDefaultTimeout());
        params.put("side", invokerParam.getSide());
        params.put("path", invokerParam.getMockClassName());
        params.put("interface", invokerParam.getMockClassName());
        params.put("group", invokerParam.getGroup());
        params.put("version", invokerParam.getVersion());
        return params;
    }

    public static Map<String, String> getInvokerParam(URL providerUrl) {
        Map<String, String> param = Maps.newHashMap();
        param.put("protocol", providerUrl.getProtocol());
        param.put("address", providerUrl.getAddress());
        param.put("path", providerUrl.getServiceInterface());
        param.put("group", providerUrl.getParameter("group"));
        return param;
    }

    public void setMockClass(@NonNull Class mockClass) {
        this.mockClass = mockClass;
        this.mockClassName = mockClass.getName();
    }

}
