package com.common.collect.test.main.mode.behave;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/11/2.
 * <p>
 * 允许一个对象在其内部状态改变的时候改变其行为
 * 策略模式：环境角色选择了具体策略后，在整个生命周期里不会改变此策略
 * 状态模式：环境角色有明显的状态转移，在整个生命周期里会有不同的状态对象被使用
 */
public class State {

    public static void main(String[] args) {
        DoorContext doorContext = new DoorContext(new CloseState());
        doorContext.open();
        doorContext.close();
        doorContext.open();
    }

}

class DoorContext {
    private DoorState doorState;

    DoorContext(DoorState doorState) {
        this.doorState = doorState;
    }

    void setDoorState(DoorState doorState) {
        this.doorState = doorState;
    }

    void open() {
        doorState.open(this);
    }

    void close() {
        doorState.close(this);
    }
}

interface DoorState {
    void open(DoorContext context);

    void close(DoorContext context);
}

@Slf4j
class CloseState implements DoorState {
    @Override
    public void open(DoorContext context) {
        log.info("open door");
        context.setDoorState(new OpenState());
    }

    @Override
    public void close(DoorContext context) {
        throw new RuntimeException("当前是 close 状态，不能执行 close 动作");
    }
}

@Slf4j
class OpenState implements DoorState {
    @Override
    public void open(DoorContext context) {
        throw new RuntimeException("当前是 open 状态，不能执行 open 动作");
    }

    @Override
    public void close(DoorContext context) {
        log.info("open close");
        context.setDoorState(new CloseState());
    }
}


