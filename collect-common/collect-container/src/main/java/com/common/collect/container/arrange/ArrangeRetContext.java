package com.common.collect.container.arrange;

import com.common.collect.util.ClassUtil;
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
    private LinkedHashMap<String, Object> inputMap = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> outputMap = new LinkedHashMap<>();

    public void putInputMap(String key, Object obj) {
        inputMap.put(key, obj);
    }

    public void putOutputMap(String key, Object obj) {
        outputMap.put(key, obj);
    }

    public <T> T getLastRet() {
        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) ClassUtil.getFieldValue(outputMap, "tail");
        return (T) entry.getValue();
    }


}
