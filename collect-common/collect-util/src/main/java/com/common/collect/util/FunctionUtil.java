package com.common.collect.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

public class FunctionUtil {

    // set
    public static <T, K> Set<K> valueSet(Collection<T> collection, Predicate<T> predicate, Function<T, K> valueFunc) {

        if (collection == null || collection.size() == 0 || predicate == null || valueFunc == null) {
            return new HashSet<>();
        }
        return collection.stream().filter(predicate).map(valueFunc).collect(Collectors.toSet());
    }

    // set
    public static <T, K> Set<K> valueSet(Collection<T> collection, Function<T, K> valueFunc) {

        if (collection == null || collection.size() == 0 || valueFunc == null) {
            return new HashSet<>();
        }
        return collection.stream().map(valueFunc).collect(Collectors.toSet());
    }

    // list
    public static <T, K> List<K> valueList(Collection<T> collection, Predicate<T> predicate, Function<T, K> valueFunc) {

        if (collection == null || collection.size() == 0 || predicate == null || valueFunc == null) {
            return new ArrayList<>();
        }
        return collection.stream().filter(predicate).map(valueFunc).collect(Collectors.toList());
    }

    // list
    public static <T, K> List<K> valueList(Collection<T> collection, Function<T, K> valueFunc) {

        if (collection == null || collection.size() == 0 || valueFunc == null) {
            return new ArrayList<>();
        }
        return collection.stream().map(valueFunc).collect(Collectors.toList());
    }

    // filter
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {

        if (collection == null || collection.size() == 0 || predicate == null) {
            return new ArrayList<>();
        }
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    // map
    public static <V, K> Map<K, V> keyValueMap(Collection<V> collection, Function<V, K> keyFunc) {

        if (collection == null || collection.size() == 0 || keyFunc == null) {
            return new HashMap<>();
        }
        return collection.stream().collect(Collectors.toMap(keyFunc, Function.identity(), (k1, k2) -> k1));
    }

    // map
    public static <T, K> Map<K, List<T>> valueMap(Collection<T> collection, Function<T, K> valueFunc) {

        if (collection == null || collection.size() == 0 || valueFunc == null) {
            return new HashMap<>();
        }
        return collection.stream().collect(
                Collectors.groupingBy(valueFunc, Collectors.mapping(Function.identity(), Collectors.toList())));
    }

    // group
    public static <T> List<Object> batchFunctionResults(Collection<Function<Collection<T>, Object>> functions, Collection<T> collection) {
        return functions.stream().map(f -> f.apply(collection)).collect(Collectors.toList());
    }

    private FunctionUtil() {
    }

}
