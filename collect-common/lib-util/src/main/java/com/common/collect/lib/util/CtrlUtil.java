package com.common.collect.lib.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/12/3.
 */
public class CtrlUtil {

    // 异常重试
    public static <T> T retry(int time, Supplier<T> supplier) {
        if (time < 1) {
            throw new RuntimeException("次数不能少于 1 次");
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
            if (Objects.equals(result, breakFlag)) {
                break;
            }
        }
    }

}
