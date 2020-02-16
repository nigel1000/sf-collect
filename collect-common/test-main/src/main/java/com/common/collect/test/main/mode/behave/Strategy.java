package com.common.collect.test.main.mode.behave;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/31.
 * <p>
 * 保持接口不变的情况下，使具体算法可以互相替换。
 * 把行为和环境分割开来
 * 客户端必须决定使用哪一种算法
 */
public class Strategy {

    public static void main(String[] args) {

        Context context = new Context(new BubbleSort());
        context.sort();
    }

}

class Context {
    private Sort sort;

    Context(Sort sort) {
        this.sort = sort;
    }

    void sort() {
        sort.sort();
    }
}

interface Sort {
    void sort();
}

@Slf4j
class HeadSort implements Sort{
    @Override
    public void sort() {
        log.info(this.getClass().getSimpleName());
    }
}

@Slf4j
class BubbleSort implements Sort{
    @Override
    public void sort() {
        log.info(this.getClass().getSimpleName());
    }
}
