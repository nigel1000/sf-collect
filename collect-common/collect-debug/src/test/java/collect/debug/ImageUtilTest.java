package collect.debug;

import com.common.collect.container.HttpUtil;
import com.common.collect.util.FunctionUtil;
import com.common.collect.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
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

        log.info("suffix:{}", ImageUtil.suffix("<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/51699be535d04531bdd2e14d7fc98a901543839322364jp8a2l1710486.jpeg\" /></p>\n"));

        // 合图
        BufferedImage bufferedImage = ImageUtil.mergeImage(
                FunctionUtil.valueList(urls, (t) ->
                        ImageUtil.getBufferedImage(HttpUtil.request(new HttpUtil.HttpParam(t), InputStream.class, "image"),
                                ImageUtil.SourceFrom.INPUT_STREAM)), false);
        File file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("mergeImage path:{}", file.getAbsolutePath());

        // 切图
        String url = "http://haitao.nos.netease.com/f8c0286f5fb5415fadc325e9bbcbd6ca1543839629293jp8a95v010493.jpg";
        // 网易考拉温馨提示 276 左右
        List<BufferedImage> bufferedImages = ImageUtil.segmentImage(
                ImageUtil.getBufferedImage(url, ImageUtil.SourceFrom.URL), 250, 5);
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
        bufferedImage = ImageUtil.mergeImage(FunctionUtil.valueList(segmentUrls, (t) -> ImageUtil.getBufferedImage(t, ImageUtil.SourceFrom.FILE)), false);
        file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("mergeImage 切图后合图 path:{}", file.getAbsolutePath());

        log.info("mergeImage 切图后合图:{},原图:{}",
                ImageUtil.getImageFileSize(file, ImageUtil.SourceFrom.FILE),
                ImageUtil.getImageFileSize(url, ImageUtil.SourceFrom.URL));

        // 黑白
        bufferedImage = ImageUtil.gray(ImageUtil.getBufferedImage(file.getAbsolutePath(), ImageUtil.SourceFrom.FILE));
        file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("gray path:{}", file.getAbsolutePath());

    }

}
