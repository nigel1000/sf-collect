package com.common.collect.framework.docs;

import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.ExceptionUtil;
import com.common.collect.lib.util.IdUtil;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hznijianfeng on 2020/3/16.
 */

public class DocsTool {

    private static Random random = new Random();

    public static String typeNamedDocsParameter(@NonNull Class<?> cls) {
        if (cls == Long.class || cls == long.class
                || cls == Integer.class || cls == int.class
                || cls == Float.class || cls == float.class
                || cls == Double.class || cls == double.class
                || cls == Short.class || cls == short.class
                || cls == Byte.class || cls == byte.class
                || cls == BigDecimal.class || cls == Date.class
        ) {
            return DocsContext.Parameter.TypeNameEnum.Number.getName();
        }

        if (cls == Boolean.class || cls == boolean.class) {
            return DocsContext.Parameter.TypeNameEnum.Boolean.getName();
        }

        if (cls == Character.class || cls == char.class || cls == String.class
        ) {
            return DocsContext.Parameter.TypeNameEnum.String.getName();
        }
        return null;
    }

    public static Object mockParameterValue(@NonNull Class<?> cls, String defaultValue) {
        if (cls == Integer.class || cls == int.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> Integer.valueOf(defaultValue), null);
            }
            return random.nextInt(1000);
        }
        if (cls == Long.class || cls == long.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> Long.valueOf(defaultValue), null);
            }
            return random.nextInt(1000000);
        }
        if (cls == Float.class || cls == float.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> Float.valueOf(defaultValue), null);
            }
            return random.nextFloat();
        }
        if (cls == Double.class || cls == double.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> Double.valueOf(defaultValue), null);
            }
            return random.nextDouble();
        }
        if (cls == Short.class || cls == short.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> Short.valueOf(defaultValue), null);
            }
            return random.nextInt(127);
        }
        if (cls == Byte.class || cls == byte.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> Byte.valueOf(defaultValue), null);
            }
            return random.nextInt(1);
        }
        if (cls == BigDecimal.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> new BigDecimal(defaultValue), null);
            }
            return random.nextDouble() * 10;
        }
        if (cls == Date.class) {
            return System.currentTimeMillis();
        }
        if (cls == Boolean.class || cls == boolean.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> Boolean.valueOf(defaultValue), null);
            }
            return random.nextBoolean();
        }
        if (cls == Character.class || cls == char.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> defaultValue.charAt(0), null);
            }
            return IdUtil.uuidHex().substring(0, 1);
        }
        if (cls == String.class) {
            if (EmptyUtil.isNotBlank(defaultValue)) {
                return ExceptionUtil.eatException(() -> defaultValue, null);
            }
            return IdUtil.uuidHex().substring(0, 6);
        }
        return null;
    }

    public static boolean clsInBlackList(@NonNull Class<?> cls) {
        if (cls == HttpServletRequest.class ||
                cls == HttpServletResponse.class ||
                cls == MultipartFile.class ||
                cls == Void.class || cls == void.class ||
                cls == Map.class) {
            return true;
        }
        return false;
    }

    public static boolean isArray(@NonNull Class<?> cls) {
        return cls == List.class || cls.isArray();
    }

    public static List arrayCountList(Object value, int count) {
        List obj = Arrays.asList(value, value);
        for (int i = 0; i < count - 1; i++) {
            obj = Arrays.asList(obj, obj);
        }
        return obj;
    }

}
