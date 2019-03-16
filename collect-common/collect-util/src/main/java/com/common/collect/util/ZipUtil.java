package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
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
            return PathUtil.tailEndSeparator(prefixPath);
        }
    }

    public static byte[] zip(@NonNull List<ZipModel> zipModels) {
        log.info("压缩文件的文件列表 {}", FunctionUtil.valueList(zipModels, ZipModel::getAbsolutePath));
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream _zipOut = new ZipOutputStream(bos);

            for (ZipModel zipModel : zipModels) {
                if (PathUtil.hasPathSpecial(zipModel.getPrefixPath())
                        || PathUtil.hasFileSpecial(zipModel.getFileName())) {
                    log.info("此文件路径有特殊字符 absolutePath:{}", zipModel.getAbsolutePath());
                    continue;
                }
                _zipOut.putNextEntry(new ZipEntry(zipModel.getPrefixPath()));
                _zipOut.closeEntry();
                byte[] _byte = zipModel.getFileBytes();
                if (_byte != null) {
                    _zipOut.putNextEntry(new ZipEntry(zipModel.getAbsolutePath()));
                    _zipOut.write(_byte, 0, _byte.length);
                    _zipOut.closeEntry();
                }
                log.info("压缩文件 absolutePath:{} 成功!", zipModel.getAbsolutePath());
            }
            _zipOut.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw UnifiedException.gen("压缩文件", "压缩文件失败", e);
        }
    }

}

