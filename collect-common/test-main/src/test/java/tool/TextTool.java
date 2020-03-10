package tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hznijianfeng on 2020/3/10.
 */

public class TextTool {

    public static void main(String[] args) throws Exception {
        InputStream is = new FileInputStream(new File("D:\\download\\music_liveshow8088_schema.sql"));
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        int num = 1;
        while (line != null) { // 如果 line 为空说明读完了
            if (line.contains("id") &&
                    !line.contains("bigint") &&
                    !line.contains("ENGINE=InnoDB") &&
                    !line.contains("varchar(") &&
                    !line.contains("CREATE TABLE") &&
                    !line.contains("` text") &&
                    !line.contains("KEY `") &&
                    !line.contains("PRIMARY KEY")) {
                System.out.println(num + ":" + line);
            }
            line = reader.readLine(); // 读取下一行
            num++;
        }
        reader.close();
        is.close();
    }

}
