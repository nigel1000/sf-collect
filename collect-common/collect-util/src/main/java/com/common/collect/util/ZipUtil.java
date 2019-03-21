package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by hznijianfeng on 2018/8/25.
 */

@Slf4j
public class ZipUtil {

    @Data
    @Builder
    public static class ZipModel {
        private byte[] fileBytes;
        private String fileName;
        private String prefixPath;

        public String getAbsolutePath() {
            return getPrefixPath() + fileName;
        }

        public String getPrefixPath() {
            if (prefixPath == null) {
                return "";
            }
            return PathUtil.tailEndSeparator(prefixPath);
        }
    }

    public static List<ZipModel> fromFiles(List<File> files) {
        List<ZipUtil.ZipModel> zipModels = new ArrayList<>();
        for (File file : files) {
            try {
                ZipUtil.ZipModel zipModel = ZipUtil.ZipModel.builder()
                        .fileBytes(com.common.collect.util.FileUtil
                                .getBytes(new FileInputStream(file)))
                        .fileName(file.getName()).build();
                zipModels.add(zipModel);
            } catch (IOException e) {
                throw UnifiedException.gen(file.getAbsolutePath() + " 文件异常", e);
            }
        }
        return zipModels;
    }

    public static byte[] zip(@NonNull List<ZipModel> zipModels) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream _zipOut = new ZipOutputStream(bos);
            Set<String> zipPaths = new HashSet<>();
            for (ZipModel zipModel : zipModels) {
                String prefixPath = zipModel.getPrefixPath();
                String absolutePath = zipModel.getAbsolutePath();
                if (PathUtil.hasPathSpecial(prefixPath)
                        || PathUtil.hasFileSpecial(zipModel.getFileName())) {
                    log.info("此文件路径有特殊字符 absolutePath:{}", absolutePath);
                    continue;
                }
                if (!zipPaths.contains(prefixPath)) {
                    _zipOut.putNextEntry(new ZipEntry(prefixPath));
                    _zipOut.closeEntry();
                    zipPaths.add(prefixPath);
                }
                byte[] _byte = zipModel.getFileBytes();
                if (_byte != null && !zipPaths.contains(absolutePath)) {
                    _zipOut.putNextEntry(new ZipEntry(absolutePath));
                    _zipOut.write(_byte, 0, _byte.length);
                    _zipOut.closeEntry();
                    zipPaths.add(absolutePath);
                }
            }
            _zipOut.close();
            log.info("压缩文件成功的文件列表:{} !", zipPaths);
            return bos.toByteArray();
        } catch (IOException e) {
            throw UnifiedException.gen("压缩文件", "压缩文件失败", e);
        }
    }

}


