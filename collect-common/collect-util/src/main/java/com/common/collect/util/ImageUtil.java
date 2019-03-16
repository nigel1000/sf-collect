package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by hznijianfeng on 2019/3/5.
 */

@Slf4j
public class ImageUtil {

    /**
     * 获取上传图片的宽高
     * 格式：_width_height
     */
    public static String getImageFileSize(File file) {
        String size = "";
        try {
            BufferedImage buff = ImageIO.read(file);
            int width = buff.getWidth();
            int height = buff.getHeight();
            size += "_" + width + "_" + height;
        } catch (Exception ex) {
            throw UnifiedException.gen("image 长宽获取失败", ex);
        }
        return size;
    }


}
