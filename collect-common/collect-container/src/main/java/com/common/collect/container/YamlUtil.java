package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * Created by nijianfeng on 2019/7/6.
 */
public class YamlUtil {


    public static LinkedHashMap parse(@NonNull Object yaml) {
        InputStream inputStream;
        if (yaml instanceof File) {
            try {
                inputStream = new FileInputStream((File) yaml);
            } catch (Exception e) {
                throw UnifiedException.gen("文件流获取失败", e);
            }
        } else if (yaml instanceof String) {
            try {
                inputStream = new FileInputStream(new File((String) yaml));
            } catch (Exception e) {
                try {
                    inputStream = HttpUtil.getInputStream((String) yaml);
                } catch (Exception ex) {
                    inputStream = new ByteArrayInputStream(((String) yaml).getBytes());
                }
            }
        } else if (yaml instanceof InputStream) {
            inputStream = (InputStream) yaml;
        } else {
            throw UnifiedException.gen("不支持此类型入参" + yaml.getClass().getName());
        }
        //初始化Yaml解析器
        Yaml parser = new Yaml();
        //读入文件
        Object result;
        try {
            result = parser.load(inputStream);
        } catch (Exception e) {
            throw UnifiedException.gen("解析 yaml 失败", e);
        }
        return (LinkedHashMap) result;
    }

}
