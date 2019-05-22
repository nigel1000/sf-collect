package com.common.collect.container.docs;

import com.alibaba.fastjson.serializer.ValueFilter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hznijianfeng on 2018/8/14.
 */
@Slf4j
public class FieldCommentFilter implements ValueFilter {

    @Override
    public Object process(Object object, String name, Object value) {
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(name);
        } catch (Exception ex) {
            log.trace("解析 field 失败", ex);
        }
        if (field == null) {
            return value;
        }
        JsonComment jsonComment = field.getAnnotation(JsonComment.class);
        if (jsonComment == null) {
            return value;
        }
        return formatComment(field, jsonComment, value);
    }

    private Object formatComment(@NonNull Field field, @NonNull JsonComment jsonComment, Object value) {
        String desc = jsonComment.desc();
        boolean required = jsonComment.required();

        Map<String, Object> extraMap = new LinkedHashMap<>();
        extraMap.put("类型", field.getType().getName());
        extraMap.put("默认值", value == null ? "" : String.valueOf(value));
        extraMap.put("描述", desc);
        if (required) {
            extraMap.put("是否必填", "是");
        }
        return extraMap;
    }
}