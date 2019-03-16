package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Created by hznijianfeng on 2018/8/20.
 */

@Slf4j
public class FileUtil {

    public static byte[] getBytes(@NonNull InputStream inputStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            inputStream.close();
            return outStream.toByteArray();
        } catch (Exception ex) {
            throw UnifiedException.gen("获取 bytes 失败", ex);
        }
    }

    public static File saveTempFile(@NonNull byte[] contents, @NonNull String namePrefix, @NonNull String nameSuffix) {
        File tempFile;
        try {
            tempFile = File.createTempFile(namePrefix, nameSuffix);
            OutputStream out = new FileOutputStream(tempFile);
            out.write(contents);
            out.flush();
            out.close();
        } catch (Exception ex) {
            throw UnifiedException.gen("往临时目录文件写字节失败", ex);
        }
        return tempFile;
    }


    public static File saveTempFile(InputStream inputStream, @NonNull String namePrefix, @NonNull String nameSuffix) {
        return saveTempFile(getBytes(inputStream), namePrefix, nameSuffix);
    }

    public static File saveTempFile(@NonNull String namePrefix, @NonNull String nameSuffix) {
        File tempFile;
        try {
            tempFile = File.createTempFile(namePrefix, nameSuffix);
        } catch (Exception ex) {
            throw UnifiedException.gen("往临时目录生成文件失败", ex);
        }
        return tempFile;
    }

    public static String getFileNamePrefix(File file) {
        if (file == null) {
            return "";
        }
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileNameSuffix(File file) {
        if (file == null) {
            return "";
        }
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf("."));
    }

}
