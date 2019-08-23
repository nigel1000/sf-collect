package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/5.
 */

@Slf4j
public class ImageUtil {

    // 获取上传图片的宽高
    // 格式：_width_height
    public static String getImageFileSize(@NonNull Object source, @NonNull SourceFrom sourceFrom) {
        String size = "";
        try {
            BufferedImage buff = getBufferedImage(source, sourceFrom);
            int width = buff.getWidth();
            int height = buff.getHeight();
            size += "_" + width + "_" + height;
        } catch (Exception ex) {
            throw UnifiedException.gen("image 长宽获取失败", ex);
        }
        return size;
    }

    public enum SourceFrom {
        FILE,
        URL,
        INPUT_STREAM,
        IMAGE_INPUT_STREAM
    }

    // 文件地址 或者 url
    public static BufferedImage getBufferedImage(@NonNull Object source, @NonNull SourceFrom sourceFrom) {
        switch (sourceFrom) {
            case FILE:
                File file;
                try {
                    if (source instanceof File) {
                        file = (File) source;
                    } else if (source instanceof String) {
                        file = new File((String) source);
                    } else {
                        throw UnifiedException.gen(StringUtil.format("FILE 输入不合法"));
                    }
                    return ImageIO.read(file);
                } catch (Exception e) {
                    throw UnifiedException.gen(StringUtil.format("path:{},读取失败", source.toString(), e));
                }
            case URL:
                try {
                    return ImageIO.read(new URL((String) source));
                } catch (IOException e) {
                    throw UnifiedException.gen(StringUtil.format("url:{},读取失败", source), e);
                }
            case INPUT_STREAM:
                try {
                    return ImageIO.read((InputStream) source);
                } catch (IOException e) {
                    throw UnifiedException.gen(StringUtil.format("InputStream 流读取失败"), e);
                }
            case IMAGE_INPUT_STREAM:
                try {
                    return ImageIO.read((ImageInputStream) source);
                } catch (IOException e) {
                    throw UnifiedException.gen(StringUtil.format("ImageInputStream 流读取失败"), e);
                }
            default:
                throw UnifiedException.gen(StringUtil.format("不被支持的读取方式"));
        }
    }

    // BufferedImage 转 图片文件
    // nameSuffix=.jpg|.png
    public static File generateFile(BufferedImage buffImg, @NonNull String nameSuffix) {
        try {
            File outFile = File.createTempFile(IdUtil.uuidHex(), nameSuffix);
            if (!ImageIO.write(buffImg, nameSuffix.substring(1), outFile)) {
                throw UnifiedException.gen("图片落文件失败");
            }
            return outFile;
        } catch (IOException ex) {
            throw UnifiedException.gen("输出图片失败", ex);
        }
    }

    // 合并多张图片 isHorizontal 是否水平合并
    public static BufferedImage mergeImage(List<BufferedImage> sources, boolean isHorizontal) {
        if (EmptyUtil.isEmpty(sources)) {
            throw UnifiedException.gen(StringUtil.format("没有合图的图片"));
        }
        // 保证 sources 不变
        List<BufferedImage> bufferedImages = new ArrayList<>(sources);
        // 生成新图片 第一张图
        BufferedImage destImage = bufferedImages.get(0);
        bufferedImages.remove(0);

        for (int i = 0; i < bufferedImages.size(); i++) {
            // 目标图
            int destImgWidth = destImage.getWidth();
            int destImgHeight = destImage.getHeight();
            // 从图片中读取RGB
            // 逐行扫描图像中各个像素的RGB到数组中
            int[] destImageArray = destImage.getRGB(0, 0, destImgWidth, destImgHeight, null, 0, destImgWidth);

            // 合并图
            BufferedImage nextImg = bufferedImages.get(i);
            int nextImgWidth = nextImg.getWidth();
            int nextImgHeight = nextImg.getHeight();
            int[] imageArrayTwo = nextImg.getRGB(0, 0, nextImgWidth, nextImgHeight, null, 0, nextImgWidth);

            try {
                if (isHorizontal) {
                    // 水平方向合并
                    destImage =
                            new BufferedImage(destImgWidth + nextImgWidth, destImgHeight, BufferedImage.TYPE_INT_RGB);
                    destImage.setRGB(0, 0, destImgWidth, destImgHeight, destImageArray, 0, destImgWidth); // 设置上半部分或左半部分的RGB
                    destImage.setRGB(destImgWidth, 0, nextImgWidth, nextImgHeight, imageArrayTwo, 0, nextImgWidth);
                } else {
                    // 垂直方向合并
                    destImage =
                            new BufferedImage(destImgWidth, destImgHeight + nextImgHeight, BufferedImage.TYPE_INT_RGB);
                    destImage.setRGB(0, 0, destImgWidth, destImgHeight, destImageArray, 0, destImgWidth); // 设置上半部分或左半部分的RGB
                    destImage.setRGB(0, destImgHeight, nextImgWidth, nextImgHeight, imageArrayTwo, 0, nextImgWidth); // 设置下半部分的RGB
                }
            } catch (Exception ex) {
                throw UnifiedException.gen(StringUtil.format("宽度高度不匹配, sources index:{} {}", i, i + 1), ex);
            }
        }

        return destImage;
    }

    // 切割图片 以白色横条为准切割图片
    // ignoreBlankHeight 高度在此之下的白色不视为切割点
    // lowestHeight 上一个切割点和下一个切割点需保持的最小的距离
    public static List<BufferedImage> segmentImage(BufferedImage sourceImg, @NonNull int lowestHeight, @NonNull int ignoreBlankHeight) {
        // 转灰白
        BufferedImage grayImg = gray(sourceImg);
        int grayImgWidth = grayImg.getWidth();
        int grayImgHeight = grayImg.getHeight();
        // 从图片中读取RGB
        int[] grayImageArray = grayImg.getRGB(0, 0, grayImgWidth, grayImgHeight, null, 0, grayImgWidth);

        // 获取 全部白色行
        List<Integer> allBlankRows = new ArrayList<>();
        int height = 0;
        for (int i = 0; i < grayImageArray.length; i = height * grayImgWidth) {
            boolean isBlank = true;
            for (int w = 0; w < grayImgWidth; w++) {
                // if (w != 0 && grayImageArray[i + w] != grayImageArray[i + w - 1]) {
                // -1 是白色
                if (w != 0 && grayImageArray[i + w] != -1 && grayImageArray[i + w - 1] != -1) {
                    isBlank = false;
                    break;
                }
            }
            if (isBlank) {
                allBlankRows.add(height);
            }
            height++;
        }
        // 去除相隔小于 lowestHeight 的空白行
        List<List<Integer>> series = new ArrayList<>();
        List<Integer> serial = new ArrayList<>();
        for (int i = 0; i < allBlankRows.size(); i++) {
            int currRow = allBlankRows.get(i);
            if (i == 0) {
                serial = new ArrayList<>();
                serial.add(currRow);
            } else {
                int preRow = allBlankRows.get(i - 1);
                if (currRow - preRow == 1) {
                    serial.add(currRow);
                } else {
                    series.add(new ArrayList<>(serial));
                    serial = new ArrayList<>();
                    serial.add(currRow);
                }
            }
        }
        series.add(new ArrayList<>(serial));

        List<Integer> cutRowIndex = new ArrayList<>();
        for (List<Integer> rowIndex : series) {
            if (EmptyUtil.isEmpty(rowIndex) || rowIndex.size() < ignoreBlankHeight) {
                continue;
            }
            int lastCutRow = cutRowIndex.size() == 0 ? 0 : cutRowIndex.get(cutRowIndex.size() - 1);
            int currCutRow = (rowIndex.get(0) + rowIndex.get(rowIndex.size() - 1)) / 2;
            if (currCutRow - lastCutRow >= lowestHeight) {
                cutRowIndex.add(currCutRow);
            }
        }
        // 加入最后一行
        if (!cutRowIndex.contains(grayImgHeight)) {
            cutRowIndex.add(grayImgHeight);
        }

        List<BufferedImage> bufferedImages = new ArrayList<>();
        int preRow = 0;
        for (Integer rowIndex : cutRowIndex) {
            int rowNum = rowIndex - preRow;
            int[] destImageArray = sourceImg.getRGB(0, preRow, grayImgWidth, rowNum, null, 0, grayImgWidth);
            BufferedImage destImage = new BufferedImage(grayImgWidth, rowNum, BufferedImage.TYPE_INT_RGB);
            destImage.setRGB(0, 0, grayImgWidth, rowNum, destImageArray, 0, grayImgWidth);
            bufferedImages.add(destImage);
            preRow = rowIndex;
        }

        return bufferedImages;
    }

    // 彩色转为黑白
    public static BufferedImage gray(@NonNull BufferedImage src) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        src = op.filter(src, null);
        return src;
    }

}
