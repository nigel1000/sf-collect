package com.common.collect.debug.design.mode.struct;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/31.
 * <p>
 * 将抽象化和实现化解耦，使得二者可以独立的变化
 * 每一个抽象化角色(动物灵魂，植物灵魂)都对应一个具体实现化角色(猪，狗，茉莉花)
 */

@Slf4j
public class Bridge {

    public static void main(String[] args) {
        Image image = new JpegImage(new WindowImagePlatform());
        image.load();
        image.show();
        log.info("########################");

        image = new JpegImage(new MacImagePlatform());
        image.load();
        image.show();
        log.info("########################");

        image = new PngImage(new WindowImagePlatform());
        image.load();
        image.show();
        log.info("########################");

        image = new PngImage(new MacImagePlatform());
        image.load();
        image.show();
        log.info("########################");

    }

}

// 抽象化角色
abstract class Image {

    protected ImagePlatform imagePlatform;

    Image(ImagePlatform imagePlatform) {
        this.imagePlatform = imagePlatform;
    }

    abstract void load();

    abstract void show();
}

@Slf4j
class JpegImage extends Image {

    JpegImage(ImagePlatform imagePlatform) {
        super(imagePlatform);
    }

    @Override
    void load() {
        log.info("JpegImage load");
        imagePlatform.init();
    }

    @Override
    void show() {
        imagePlatform.paint();
        log.info("JpegImage show");
    }
}

@Slf4j
class PngImage extends Image {

    PngImage(ImagePlatform imagePlatform) {
        super(imagePlatform);
    }

    @Override
    void load() {
        log.info("PngImage load");
        imagePlatform.init();
    }

    @Override
    void show() {
        imagePlatform.paint();
        log.info("JpegImage show");
    }
}

// 具体实现化角色
interface ImagePlatform {

    void init();

    void paint();

}

@Slf4j
class WindowImagePlatform implements ImagePlatform {
    @Override
    public void init() {
        log.info("WindowImagePlatform init");
    }

    @Override
    public void paint() {
        log.info("WindowImagePlatform paint");
    }
}

@Slf4j
class MacImagePlatform implements ImagePlatform {
    @Override
    public void init() {
        log.info("MacImagePlatform init");
    }

    @Override
    public void paint() {
        log.info("MacImagePlatform paint");
    }
}