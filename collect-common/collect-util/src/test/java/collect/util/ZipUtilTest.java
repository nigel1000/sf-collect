package collect.util;

import com.common.collect.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by hznijianfeng on 2018/8/25.
 */

@Slf4j
public class ZipUtilTest {


    private static String path;

    static {
        path = ZipUtilTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "src/test/resources";
        } else {
            path = path.substring(0, path.indexOf("target")) + "src/test/resources";
        }
    }

    public static void main(String[] args) throws Exception {

        log.info("path:\t" + path);

        ZipUtil.ZipModel model1 = ZipUtil.ZipModel.builder().prefixPath("temp").fileName("1.xls")
                .fileBytes(new FileInputStream(path + "/zip/zip1.txt")).build();
        ZipUtil.ZipModel model2 = ZipUtil.ZipModel.builder().prefixPath("temp/temp1/").fileName("2.xls")
                .fileBytes(new FileInputStream(path + "/zip/zip2.txt")).build();
        ZipUtil.ZipModel model3 = ZipUtil.ZipModel.builder().prefixPath("temp3/temp2/temp1").fileName("3.xls")
                .fileBytes(new FileInputStream(path + "/zip/zip3.txt")).build();

        byte[] ret = ZipUtil.zip(Arrays.asList(model1, model2, model3));

        FileOutputStream fos = new FileOutputStream(path + "/zip-demo.zip");
        fos.write(ret, 0, ret.length);
        fos.flush();
        fos.close();

    }

}
