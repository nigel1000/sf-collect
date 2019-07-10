package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by hznijianfeng on 2018/8/20.
 */

@Slf4j
public class FileUtil {

    public static String getString(File file) {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = new FileInputStream(file);
            String line; // 用来保存每行读取的内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            line = reader.readLine(); // 读取第一行
            while (line != null) { // 如果 line 为空说明读完了
                sb.append(line); // 将读到的内容添加到 buffer 中
                sb.append("\n"); // 添加换行符
                line = reader.readLine(); // 读取下一行
            }
            reader.close();
            is.close();
            return sb.toString();
        } catch (Exception ex) {
            throw UnifiedException.gen("读取文件失败", ex);
        }
    }

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
        return getFileNamePrefix(file.getName());
    }

    public static String getFileNameSuffix(File file) {
        if (file == null) {
            return "";
        }
        return getFileNameSuffix(file.getName());
    }

    public static String getFileNamePrefix(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    public static String getFileNameSuffix(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            return "";
        }
        return fileName.substring(index);
    }

    public static void createFile(String path, boolean isDir, byte[] contents, boolean isCover) {
        Path dirPath;
        if (isDir) {
            dirPath = Paths.get(path);
            if (!dirPath.toFile().isDirectory()) {
                throw UnifiedException.gen(StringUtil.format("{} 不是目录", path));
            }
        } else {
            dirPath = Paths.get(path);
            if (dirPath.toFile().isDirectory()) {
                throw UnifiedException.gen(StringUtil.format("{} 是目录", path));
            }
            dirPath = dirPath.getParent();
        }
        // 创建目录
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw UnifiedException.gen("目录生成失败");
            }
        }
        if (!isDir) {
            File file = Paths.get(path).toFile();
            if (file.exists()) {
                if (isCover) {
                    if (!file.delete()) {
                        throw UnifiedException.gen(path + "文件删除失败");
                    }
                } else {
                    return;
                }
            }
            try {
                Files.write(Files.createFile(Paths.get(path)), contents);
            } catch (IOException e) {
                throw UnifiedException.gen("生成文件失败", e);
            }
        }
    }
}
