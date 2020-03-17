package lib.util;

import com.common.collect.lib.util.FileUtil;
import com.common.collect.lib.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by hznijianfeng on 2018/8/25.
 */

@Slf4j
public class ZipUtilTest {

    private static String root = Paths.get(ZipUtilTest.class.getResource("/").getPath().contains(":")
            ? ZipUtilTest.class.getResource("/").getPath().substring(1) : ZipUtilTest.class.getResource("/").getPath())
            .getParent().getParent().toString() + "/";

    public static void main(String[] args) throws Exception {
        log.info("root:\t" + root);

        String filePath = root + "/src/test/java/lib/util/";
        ZipUtil.ZipModel model1 = ZipUtil.ZipModel.builder().prefixPath("temp").fileName("1.java")
                .fileBytes(new FileInputStream(filePath + "ZipUtilTest.java")).build();
        ZipUtil.ZipModel model2 = ZipUtil.ZipModel.builder().prefixPath("temp/temp1/").fileName("2.java")
                .fileBytes(new FileInputStream(filePath + "ZipUtilTest.java")).build();
        ZipUtil.ZipModel model3 = ZipUtil.ZipModel.builder().prefixPath("temp3/temp2/temp1").fileName("3.java")
                .fileBytes(new FileInputStream(filePath + "ZipUtilTest.java")).build();

        byte[] ret = ZipUtil.zip(Arrays.asList(model1, model2, model3));

        String path = root + "logs/zip/";
        FileUtil.createFile(path, true, null, false);
        FileOutputStream fos = new FileOutputStream(path + "/zip-demo.zip");
        fos.write(ret, 0, ret.length);
        fos.flush();
        fos.close();

    }

}
