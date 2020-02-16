package com.common.collect.lib.util;

import java.util.*;

/**
 * Created by hznijianfeng on 2018/9/3.
 */

public class CollectionUtil {

    public static <T> List<T> pickRepeat(List<T> origin) {
        List<T> result = new ArrayList<>(origin);
        if (result.size() == 0) {
            return result;
        }
        Set<T> originSet = new HashSet<>(result);
        Iterator<T> it = result.iterator();
        while (it.hasNext()) {
            T obj = it.next();
            if (originSet.contains(obj)) {
                originSet.remove(obj);
                it.remove();
            }
        }
        return new ArrayList<>(new HashSet<>(result));
    }

    public static <T> List<T> removeNull(List<T> origin) {
        List<T> result = new ArrayList<>();
        if (origin == null || origin.size() == 0) {
            return result;
        }
        for (T t : origin) {
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }

    public static List<String> removeBlank(List<String> origin) {
        List<String> result = new ArrayList<>();
        if (origin == null || origin.size() == 0) {
            return result;
        }
        for (String t : origin) {
            if (t != null && t.trim().equals("")) {
                continue;
            }
            result.add(t);
        }
        return result;
    }

    public static <T> List<T> distinct(List<T> origin) {
        List<T> result = new ArrayList<>();
        if (origin == null || origin.size() == 0) {
            return result;
        }
        for (T t : origin) {
            if (!result.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

}
