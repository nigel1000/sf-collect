package collect.debug;

import com.common.collect.container.HttpUtil;
import com.common.collect.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/8/7.
 */

@Slf4j
public class HttpUtilTest {


    private static String path;

    static {
        path = MybatisGenerator.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "logs/http/";
        } else {
            path = path.substring(0, path.indexOf("target")) + "logs/http/";
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("data", "106!6Jqmc0clzgnHEdUOHmjz5Frca5nTQ0AzUUam0LHtKjRU56cm9eYfYkVQ/dHCbt6rZcBA1AVVfNQ5ij5XA3Yh/Eu9VSWmK+NEjvasJA6dBb/7mtbWtpLDzNmAZRHU+IQKjJ4pJT7nusWPtZVdxU4ZOarI9AnG666eHHqK1Hh1URFswyXJOrYHvYaSPnWqu+Qy2k3enCELCD9/R7o4EC8kmYaewFudE0lm42Uj9s/+46HBFgn0SdeUH5CGm6OT/2kU5FUm6T/w42UdmtyZRYT0AtDl9RsxvcEbvnI4vOen/WuRAcaASdAsYPCY5yvhL5gsgIAAqMEwFOL2Fph6dnKR/8ijJujD71HCYPkroR1l6xhjdnXBngnkKmf6gsyviVualDWtTk3fi3lWuinTyd8hTL5DEbKgnTA+BKk9NVADZ5xO1GafdPVl8i71KSamhTBJ8AOTj9PXh+8nvXAbpvSuP8pqIJ8AOWrz4apsXtDRSzHMN4LJ8Lih+hN7rluk3VJogxWc+YHQI61ABMNaWpLc8558NmiI354z96krqwUG3SPRa1h3pppa9rJ6xOPlN+Khc7ItOdaHb70p0E8VizMvKQBBU8jDY+Cz4hMJl0xiChMVxUZXV3dV5Q3UoE9h0Am+4uGJG+cB3oG/T5OVybC1GXttznfwBrjCDgr7EJCB3OmVQ8rc7cUP3BKKci7LOC1vKMLMImmoeenxY6Tb/2E1Il7M8/O2ZomF8ENfhipn");
        log.info("{}", HttpUtil.request(new HttpUtil.HttpParam("https://ynuf.aliapp.org/service/um.json", body), String.class));

        log.info("{}", HttpUtil.request(new HttpUtil.HttpParam("https://hdc1new.taobao.com/asyn.htm?pageId=1623961148&userId=1806053478"), String.class));

        FileUtil.createFile(path + "1806053478.jpg", false,
                HttpUtil.request(new HttpUtil.HttpParam("https://img.alicdn.com/imgextra/i1/1806053478/O1CN01euQBGt1bYy31tEYVV_!!1806053478.jpg_60x60q90.jpg"), byte[].class),
                true);

        log.info("{}", HttpUtil.request(new HttpUtil.HttpParam("https://seeds-darwin.xycdn.com/h5/getseeds?v=2.1.10", "{\"url\":\"m801.music.126.net/20190807133702/076a4356ca7e30befbcf7ff819b3ca59/jdyyaac/0e5a/545e/0e58/902c1c579ec40caf31f39212f8d21b02.m4a\",\"surl\":\"https://m801.music.126.net/20190807133702/076a4356ca7e30befbcf7ff819b3ca59/jdyyaac/0e5a/545e/0e58/902c1c579ec40caf31f39212f8d21b02.m4a\",\"sid\":\"2c6ad8f5-f7d3-b20b-de86-9a6391dcb029\",\"fs\":3240183,\"ofs\":1114112,\"type\":\"h5\",\"ver\":\"2.1.10\",\"peer_status\":[]}", null), String.class));

    }

}
