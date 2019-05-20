package com.common.collect.container.docs;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@Data
@Builder
public class DocsMethodParamConfig implements Serializable {

    @Builder.Default
    private boolean required = false;
    private Object defValue;
    private String paramType;
    private String paramName;
    private String paramDesc;

    public String getRequired() {
        return required ? "true" : "false";
    }
}
