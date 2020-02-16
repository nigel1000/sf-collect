package com.common.collect.test.main.mode.behave;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijianfeng on 2019/10/31.
 * <p>
 * 多个观察对象同时监听某一主题对象
 * 当此主题对象发生变化时，会通知所有观察者
 */
public class Observer {

    public static void main(String[] args) {

        FrameSubject subject = new FrameSubject("press", "a");
        subject.addListener(new KeyBoardListener());
        subject.addListener(new MouseListener());
        subject.addListener(new KeyBoardListener());

        subject.changeKeyBoard("c");
        subject.changeKeyBoard("t");
        subject.changeMouse("release");
    }

}

abstract class Subject {

    private List<Listener> listeners = new ArrayList<>();

    void addListener(Listener listener) {
        listeners.add(listener);
    }

    void notifyListeners(Event event) {
        for (Listener listener : listeners) {
            listener.onEvent(event);
        }
    }

}

class FrameSubject extends Subject {

    private String mouseAction;
    private String keyBoardAction;

    FrameSubject(String mouseAction, String keyBoardAction) {
        this.mouseAction = mouseAction;
        this.keyBoardAction = keyBoardAction;
    }

    void changeMouse(String mouseAction) {
        Event event = new FrameMouseEvent(this.mouseAction, mouseAction);
        this.mouseAction = mouseAction;
        notifyListeners(event);
    }

    void changeKeyBoard(String keyBoardAction) {
        Event event = new FrameKeyBoardEvent(this.keyBoardAction, keyBoardAction);
        this.keyBoardAction = keyBoardAction;
        notifyListeners(event);
    }

}

interface Event {
}

class FrameMouseEvent implements Event {
    private String pre;
    private String now;

    FrameMouseEvent(String pre, String now) {
        this.pre = pre;
        this.now = now;
    }

    @Override
    public String toString() {
        return "pre:" + pre + ",now:" + now;
    }
}

class FrameKeyBoardEvent implements Event {
    private String pre;
    private String now;

    FrameKeyBoardEvent(String pre, String now) {
        this.pre = pre;
        this.now = now;
    }

    @Override
    public String toString() {
        return "pre:" + pre + ",now:" + now;
    }
}

interface Listener {
    void onEvent(Event event);
}

@Slf4j
class MouseListener implements Listener {
    @Override
    public void onEvent(Event event) {
        if (event instanceof FrameMouseEvent) {
            log.info("MouseListener:{}", event);
        }
    }
}

@Slf4j
class KeyBoardListener implements Listener {
    @Override
    public void onEvent(Event event) {
        if (event instanceof FrameKeyBoardEvent) {
            log.info("KeyBoardListener:{}", event);
        }
    }
}
