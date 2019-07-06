package com.common.collect.container.arrange;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/7/6.
 */

@Data
@Slf4j
public class ArrangeRetContext {

    public String bizKey;
    private Map<String, Object> inputMap = new LinkedHashMap<>();
    private Map<String, Object> outputMap = new LinkedHashMap<>();

    public void putInputMap(String key, Object obj) {
        inputMap.put(key, obj);
    }

    public void putOutputMap(String key, Object obj) {
        outputMap.put(key, obj);
    }

}
