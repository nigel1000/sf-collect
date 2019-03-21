package com.common.collect.debug.mybatis;

import com.common.collect.api.excps.UnifiedException;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

public class TemplateUtil {

    public static String genTemplate(String classPathPrefixPath, String fileName, Map<String, Object> map) {
        try {
            // 获取文档模板
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setClassForTemplateLoading(TemplateUtil.class, classPathPrefixPath);
            Template template = configuration.getTemplate(fileName);
            // 填充模板生成输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer out = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            template.process(map, out);
            // 将输出流刷新到outputStream中
            out.flush();
            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw UnifiedException.gen("TemplateUtil 模板生成有问题!", ex);
        }
    }

}
