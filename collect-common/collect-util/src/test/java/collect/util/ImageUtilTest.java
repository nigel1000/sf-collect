package collect.util;

import com.common.collect.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/8/23.
 */

@Slf4j
public class ImageUtilTest {

    public static void main(String[] args) {

        List<String> urls = Arrays.asList(
                "http://haitao.nos.netease.com/1bc22701e4d941e2b52b796d0e6f6e5b15525409026272780526-1.jpg",
                "http://haitao.nos.netease.com/1ee1872501474cee9b72ecb86f6ffa92_750_500.jpg",
                "http://haitao.nos.netease.com/correct278052615520563592624856.jpg",
                "http://haitao.nos.netease.com/a10186f33ad34b718ab428d1a9efdb35_750_258.jpg",
                "http://haitao.nos.netease.com/1bc22701e4d941e2b52b796d0e6f6e5b15525409026272780526-1.jpg",
                "http://haitao.nos.netease.com/1ee1872501474cee9b72ecb86f6ffa92_750_500.jpg",
                "http://haitao.nos.netease.com/correct278052615520563592624856.jpg",
                "http://haitao.nos.netease.com/a10186f33ad34b718ab428d1a9efdb35_750_258.jpg",
                "http://haitao.nos.netease.com/1bc22701e4d941e2b52b796d0e6f6e5b15525409026272780526-1.jpg",
                "http://haitao.nos.netease.com/1ee1872501474cee9b72ecb86f6ffa92_750_500.jpg",
                "http://haitao.nos.netease.com/correct278052615520563592624856.jpg",
                "http://haitao.nos.netease.com/a10186f33ad34b718ab428d1a9efdb35_750_258.jpg");

        // 合图
        BufferedImage bufferedImage = ImageUtil.mergeImage(ImageUtil.SourceFrom.URL, false, urls);
        File file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("mergeImage path:{}", file.getAbsolutePath());

        // 切图
        List<BufferedImage> bufferedImages = ImageUtil.segmentImage(ImageUtil.getBufferedImage("http://haitao.nos.netease.com/f8c0286f5fb5415fadc325e9bbcbd6ca1543839629293jp8a95v010493.jpg",
                ImageUtil.SourceFrom.URL), 250);
        List<File> files = new ArrayList<>();
        for (BufferedImage image : bufferedImages) {
            files.add(ImageUtil.generateFile(image, ".jpg"));
        }
        List<String> segmentUrls = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            segmentUrls.add(files.get(i).getAbsolutePath());
            log.info("segmentImage path:{}", files.get(i).getAbsolutePath());
        }
        // 切图后合图
        bufferedImage = ImageUtil.mergeImage(ImageUtil.SourceFrom.FILE, false, segmentUrls);
        file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("mergeImage 切图后合图 path:{}", file.getAbsolutePath());

        // 黑白
        bufferedImage = ImageUtil.gray(ImageUtil.getBufferedImage(file.getAbsolutePath(), ImageUtil.SourceFrom.FILE));
        file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("gray path:{}", file.getAbsolutePath());

    }

}
