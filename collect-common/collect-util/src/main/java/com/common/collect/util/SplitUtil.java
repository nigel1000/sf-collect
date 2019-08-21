package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

public class SplitUtil {

    public static final String COMMA_SPECIAL = ",";

    // split
    public static String[] split2Array(String key, @NonNull String special) {
        if (key == null || "".equals(key.trim())) {
            return new String[]{};
        }
        // 去空 去空格
        String[] keys = key.split(StringUtil.escapeRegex(special));
        int index = 0;
        for (int i = 0; i < keys.length; i++) {
            String temp = keys[i];
            if (EmptyUtil.isNotBlank(temp)) {
                keys[index] = temp;
                index++;
            }
        }
        return Arrays.copyOfRange(keys, 0, index);
    }

    public static <T> List<T> split(String key, @NonNull String special, Function<String, T> valueFunc) {
        return FunctionUtil.valueList(Arrays.asList(split2Array(key, special)), valueFunc);
    }

    public static List<Long> split2LongByComma(String key) {
        return split(key, COMMA_SPECIAL, Long::valueOf);
    }

    public static List<String> split2StringByComma(String key) {
        return split(key, COMMA_SPECIAL, s -> s);
    }

    // join
    public static <T> String joinByComma(List<T> list) {
        return join(list, COMMA_SPECIAL);
    }

    public static <T> String join(List<T> keys, @NonNull String special) {
        if (keys == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (T key : keys) {
            // 去空
            if (key != null && !"".equals(key.toString())) {
                sb.append(key).append(special);
            }
        }
        String result = sb.toString();
        if (result.length() == 0) {
            return result;
        } else {
            return result.substring(0, result.length() - special.length());
        }
    }

    // 批处理限流
    public static <T> void splitExecute(List<T> objList, int everyTimeSize, Consumer<List<T>> execute) {
        if (objList == null || objList.size() == 0) {
            return;
        }
        int totalPage = (objList.size() + everyTimeSize - 1) / everyTimeSize;
        for (int i = 0; i < totalPage; i++) {
            int fromIndex = i * everyTimeSize;
            int toIndex = Math.min((i + 1) * everyTimeSize, objList.size());
            execute.accept(objList.subList(fromIndex, toIndex));
        }
    }

    public static <T, R> void splitExecute(List<T> objList, int everyTimeSize, Function<List<T>, R> execute, R breakFlag) {
        if (objList == null || objList.size() == 0) {
            return;
        }
        int totalPage = (objList.size() + everyTimeSize - 1) / everyTimeSize;
        for (int i = 0; i < totalPage; i++) {
            int fromIndex = i * everyTimeSize;
            int toIndex = Math.min((i + 1) * everyTimeSize, objList.size());
            R result = execute.apply(objList.subList(fromIndex, toIndex));
            if (result == breakFlag || (result != null && result.equals(breakFlag))) {
                break;
            }
        }
    }

    public static <T> T retry(int time, Supplier<T> supplier) {
        if (time < 1) {
            throw UnifiedException.gen("次数不能少于 1 次");
        }

        T t;
        while (true) {
            try {
                t = supplier.get();
                break;
            } catch (Exception ex) {
                if (--time < 1) {
                    throw ex;
                }
            }
        }
        return t;
    }

}
