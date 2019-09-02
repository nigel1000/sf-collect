package collect.debug;

import com.common.collect.container.HttpUtil;
import com.common.collect.container.ThreadPoolUtil;
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
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_0_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_1000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_1500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_2000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_2500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_3000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_3500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_4000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_4500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_5000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_5500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_6000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_6500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_7000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_7500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_8000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_8500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_9000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_9500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_10000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_10500_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_11000_750_500",
                "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg?imageView&quality=98&crop=0_11500_750_466");

        log.info("suffix:{}", ImageUtil.suffix("<p style=\"text-align:center;\">" +
                "<img src=\"http://haitao.nos.netease.com/51699be535d04531bdd2e14d7fc98a901543839322364jp8a2l1710486.jpeg\" />" +
                "</p>\n"));

        long start;

        List<InputStream> inputStreams;

        // 第一次访问建立连接 存入连接池缓存
        // Logger.getLogger(OkHttpClient.class.getName()).setLevel(java.util.logging.Level.FINE);
        // https://blog.csdn.net/Gaugamela/article/details/78482564
        HttpUtil.request(new HttpUtil.HttpParam(
                        "http://haitao.nos.netease.com/51699be535d04531bdd2e14d7fc98a901543839322364jp8a2l1710486.jpeg"),
                InputStream.class, "image");

        start = System.currentTimeMillis();
        inputStreams = FunctionUtil.valueList(
                urls, (t) -> HttpUtil.request(new HttpUtil.HttpParam(t), InputStream.class, "image"));
        log.info("sync:{}", System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        inputStreams = ThreadPoolUtil.submit(FunctionUtil.valueList(urls, (t) ->
                () -> HttpUtil.request(new HttpUtil.HttpParam(t), InputStream.class, "image")
        ));
        log.info("async:{}", System.currentTimeMillis() - start);

        // 合图
        BufferedImage bufferedImage = ImageUtil.mergeImage(
                FunctionUtil.valueList(inputStreams, (t) ->
                        ImageUtil.getBufferedImage(t, ImageUtil.SourceFrom.INPUT_STREAM)), false);

        File file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("mergeImage path:{}", file.getAbsolutePath());

        // 切图
        String url = file.getAbsolutePath();
        // 网易考拉温馨提示 276 左右
        List<BufferedImage> bufferedImages = ImageUtil.segmentImage(
                ImageUtil.getBufferedImage(url, ImageUtil.SourceFrom.FILE), 250, 5);
        List<File> files = new ArrayList<>();
        for (
                BufferedImage image : bufferedImages) {
            files.add(ImageUtil.generateFile(image, ".jpg"));
        }

        List<String> segmentUrls = new ArrayList<>();
        for (
                int i = 0; i < files.size(); i++) {
            segmentUrls.add(files.get(i).getAbsolutePath());
            log.info("segmentImage path:{}", files.get(i).getAbsolutePath());
        }
        // 切图后合图
        bufferedImage = ImageUtil.mergeImage(FunctionUtil.valueList(segmentUrls, (t) -> ImageUtil.getBufferedImage(t, ImageUtil.SourceFrom.FILE)), false);
        file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("mergeImage 切图后合图 path:{}", file.getAbsolutePath());

        log.info("mergeImage 切图后合图:{},原图:{}",
                ImageUtil.getImageFileSize(file, ImageUtil.SourceFrom.FILE),
                ImageUtil.getImageFileSize(url, ImageUtil.SourceFrom.FILE));

        // 黑白
        bufferedImage = ImageUtil.gray(ImageUtil.getBufferedImage(file.getAbsolutePath(), ImageUtil.SourceFrom.FILE));
        file = ImageUtil.generateFile(bufferedImage, ".jpg");
        log.info("gray path:{}", file.getAbsolutePath());

        System.exit(0);

    }

}
