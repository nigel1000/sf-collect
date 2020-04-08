package com.common.collect.framework.docs.model;

import lombok.Data;

/**
 * Created by hznijianfeng on 2020/4/8.
 */
@Data
public class InterfaceModel {
    // 该接口的名称
    private String name;
    // 该接口的代码映射
    private String clsName;
    // 该接口的介绍
    private String description;
    // 接口方法
    private String method = InterfaceMethodEnum.GET.name();
    // 该接口访问路径
    private String path;

    private InterfaceParameterModel params = new InterfaceParameterModel();

    public enum InterfaceMethodEnum {
        GET,
        POST,
        HEAD,
        PATCH,
        PUT,
        DELETE,
        ;
    }
}
