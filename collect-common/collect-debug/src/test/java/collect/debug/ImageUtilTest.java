package collect.debug;

import com.common.collect.container.HttpUtil;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.ThreadPoolUtil;
import com.common.collect.util.FunctionUtil;
import com.common.collect.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hznijianfeng on 2019/8/23.
 */

@Slf4j
public class ImageUtilTest {

    public static void main(String[] args) {

        testMerge();

//        testSegment();

        System.exit(0);

    }

    public static void testSegment() {
        // 网易考拉温馨提示 276 左右
        String url = "http://haitao.nos.netease.com/3067d54f1aa84e4abdd6b3a37ae6987d1557048755201jvaqmkn510221.jpg";
        List<BufferedImage> bufferedImages = ImageUtil.segmentImage(
                ImageUtil.getBufferedImage(url, ImageUtil.SourceFrom.URL), 250, 5);
        List<File> files = new ArrayList<>();
        int height = 0;
        for (BufferedImage image : bufferedImages) {
            height += image.getHeight();
            files.add(ImageUtil.generateFile(image, ".jpg"));
        }
        for (int i = 0; i < files.size(); i++) {
            log.info("segmentImage path:{}", files.get(i).getAbsolutePath());
        }
        log.info("origin height:{}, segment height total:{}", ImageUtil.getBufferedImage(url, ImageUtil.SourceFrom.URL).getHeight(), height);

    }

    public static void testMerge() {
        List<String> urls = htmlImgSrc("<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_0_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/correct28926621552188959702443.jpg\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_1000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_1500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_2000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_2500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_3000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_3500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_4000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_4500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_5000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_5500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_6000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_6500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_7000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_7500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_8000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_8500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_9000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_9500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_10000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_10500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_11000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_11500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_12000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_12500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_13000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_13500_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_14000_750_500\" /></p> \n<p style=\"text-align:center;\"><img src=\"http://haitao.nos.netease.com/0306eb5b2e1a4e70a7bc25e2b18600351539590784171jna0lnze10374.jpg?imageView&quality=98&crop=0_14500_750_287\" /></p>");
        log.info("{}", JsonUtil.bean2jsonPretty(urls));
        long start = System.currentTimeMillis();
        List<InputStream> inputStreams = ThreadPoolUtil.submit(FunctionUtil.valueList(urls, (t) ->
                () -> HttpUtil.request(new HttpUtil.HttpParam(t), InputStream.class, "image")
        ));
        log.info("async:{}", System.currentTimeMillis() - start);
        // 合图
        List<BufferedImage> bufferedImages = FunctionUtil.valueList(inputStreams,
                (t) -> ImageUtil.getBufferedImage(t, ImageUtil.SourceFrom.INPUT_STREAM));
        BufferedImage bufferedImage = ImageUtil.mergeImage(bufferedImages, false);
        log.info("mergeImage path:{}", ImageUtil.generateFile(bufferedImage, ".jpg").getAbsolutePath());

    }

    private static List<String> htmlImgSrc(String tempGoodsDetailPics) {
        List<String> pics = new ArrayList<>();
        String img;
        Pattern p_image;
        Matcher m_image;
        // 严选的图片有 _src
        String regEx_img = "<img.* src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(tempGoodsDetailPics);
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile(" src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(StringEscapeUtils.unescapeHtml4(m.group(1)));
            }
        }
        return pics;
    }

}
