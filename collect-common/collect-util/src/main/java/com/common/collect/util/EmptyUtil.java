package com.common.collect.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hznijianfeng on 2019/3/26.
 */

public class EmptyUtil {

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static <T> boolean isEmpty(final List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNotEmpty(final List<T> list) {
        return !isEmpty(list);
    }

    public static <K, V> boolean isEmpty(final Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(final Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <T> boolean isEmpty(final Set<T> set) {
        return set == null || set.isEmpty();
    }

    public static <T> boolean isNotEmpty(final Set<T> set) {
        return !isEmpty(set);
    }

}
