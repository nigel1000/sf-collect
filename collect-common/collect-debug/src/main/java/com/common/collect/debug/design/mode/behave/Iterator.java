package com.common.collect.debug.design.mode.behave;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/11/1.
 * <p>
 * 顺序的访问一个集合而不暴露集合的内部
 * 内部(内禀)迭代-写在集合类内部的迭代器，因为在类内部可直接获取元素
 * 外部(外禀)迭代-写在集合类外部的迭代器，需要集合提供获取元素的功能
 * 静态迭代(拷贝一份集合，迭代期间对原集合不影响)
 * 动态迭代(迭代器可修改原集合并且原集合修改会导致迭代快速失败)
 */

@Slf4j
public class Iterator {

    public static void main(String[] args) {
        List<String> list = new List<>(new String[]{"1", "2", "3"});
        for (Itr itr = list.itrForward(); itr.hasNext(); ) {
            log.info("{}:{}", itr.getClass().getSimpleName(), itr.next());
        }

        for (Itr itr = list.itrBack(); itr.hasNext(); ) {
            log.info("{}:{}", itr.getClass().getSimpleName(), itr.next());
        }
    }

}


interface Collect<T> {
    Itr<T> itrForward();

    Itr<T> itrBack();
}

interface ListCollect<T> extends Collect<T> {

    int size();

    T index(int index);

}

class List<T> implements ListCollect<T> {

    private T[] elements;

    public List(T[] elements) {
        this.elements = elements;
    }

    @Override
    public Itr<T> itrForward() {
        return new ForwardItr<>(this);
    }

    @Override
    public Itr<T> itrBack() {
        return new BackItr<T>(this);
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public T index(int index) {
        return elements[index];
    }
}

interface Itr<T> {

    boolean hasNext();

    T next();

}

// 外部迭代 往前遍历
class ForwardItr<T> implements Itr<T> {

    private int index;
    private ListCollect<T> collect;

    ForwardItr(ListCollect<T> collect) {
        this.collect = collect;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = (collect.size() != index);
        if (!hasNext) {
            index = 0;
        }
        return hasNext;
    }

    @Override
    public T next() {
        return collect.index(index++);
    }
}

// 外部迭代 往后遍历
class BackItr<T> implements Itr<T> {

    private int index;
    private ListCollect<T> collect;

    BackItr(ListCollect<T> collect) {
        this.collect = collect;
        index = collect.size();
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = (index > 0);
        if (!hasNext) {
            index = collect.size();
        }
        return hasNext;
    }

    @Override
    public T next() {
        return collect.index(--index);
    }
}