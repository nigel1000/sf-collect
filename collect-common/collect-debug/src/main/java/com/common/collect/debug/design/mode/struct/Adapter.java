package com.common.collect.debug.design.mode.struct;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/30.
 * <p>
 * 把一个类的接口变换成客户端所期待的另一种接口
 * 改变接口不改变功能
 * <p>
 * 适配模式&包装(wrapper)模式
 */
public class Adapter {

    public static void main(String[] args) {
        SocketTarget socketTarget = new ThreeSeatCls();
        socketTarget.twoSeat();
        socketTarget.threeSeat();

        socketTarget = new ThreeSeatObj(new TwoSeat());
        socketTarget.twoSeat();
        socketTarget.threeSeat();
    }

}

// 插座目标接口 有插两座的 有插三座的
// 缺省适配
interface SocketTarget {
    // 两座
    default void twoSeat() {
        throw new RuntimeException("不支持插二座插头");
    }

    // 三座
    default void threeSeat() {
        throw new RuntimeException("不支持插三座插头");
    }
}

// 两坐插座只能插二座的
@Slf4j
class TwoSeat implements SocketTarget {

    @Override
    public void twoSeat() {
        log.info("TwoSeat twoSeat");
    }
}

// 类的适配
// 三座插口即可以插二座的也可以插三座的
@Slf4j
class ThreeSeatCls extends TwoSeat implements SocketTarget {

    @Override
    public void threeSeat() {
        log.info("ThreeSeatCls threeSeat");
    }
}

// 对象的适配
// 三座插口即可以插二座的也可以插三座的
@Slf4j
class ThreeSeatObj implements SocketTarget {

    private TwoSeat twoSeat;

    ThreeSeatObj(TwoSeat twoSeat) {
        this.twoSeat = twoSeat;
    }

    @Override
    public void threeSeat() {
        log.info("ThreeSeatObj threeSeat");
    }

    @Override
    public void twoSeat() {
        twoSeat.twoSeat();
    }
}


