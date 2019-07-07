package com.common.collect.container.arrange;

import com.common.collect.api.excps.UnifiedException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/7/6.
 */

@Data
@Slf4j
public class ArrangeRetContext {

    public String bizKey;
    public Object lastRet;
    private Map<String, Object> inputMap = new LinkedHashMap<>();
    private Map<String, Object> outputMap = new LinkedHashMap<>();

    public void putInputMap(String key, Object obj) {
        inputMap.put(key, obj);
    }

    public void putOutputMap(String key, Object obj) {
        outputMap.put(key, obj);
    }

    public <T> T getByIndexFromMap(int index, Map<String, Object> map) {
        if (index <= 0 || index > map.size()) {
            throw UnifiedException.gen("index 不合法");
        }
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        int start = 1;
        Map.Entry<String, Object> temp = null;
        while (iterator.hasNext()) {
            temp = iterator.next();
            if (index == start) {
                break;
            }
            start++;
        }
        return (T) temp.getValue();
    }

    public <T> T getLastRet() {
        return (T)lastRet;
    }
}
