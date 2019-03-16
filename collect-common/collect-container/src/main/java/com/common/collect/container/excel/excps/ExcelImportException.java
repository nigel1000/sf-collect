package com.common.collect.container.excel.excps;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.excel.pojo.ExcelSheetInfo;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Slf4j
public class ExcelImportException extends RuntimeException {

    @Getter
    private List<ExcelSheetInfo> infoList = Lists.newArrayList();

    private int collectCount;

    public ExcelImportException(int collectCount) {
        this.collectCount = collectCount;
    }

    public boolean addInfo(ExcelSheetInfo info) {
        if (info == null) {
            return true;
        }
        if (infoList.size() < collectCount) {
            infoList.add(info);
            return true;
        }
        return false;
    }

    public boolean addInfo(List<ExcelSheetInfo> infos) {
        for (ExcelSheetInfo info : infos) {
            if (!addInfo(info)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmptyInfo() {
        return infoList.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(System.lineSeparator())
                .append("excel 导入解析错误，返回错误数：")
                .append(infoList.size())
                .append("，详细内容如下:")
                .append(System.lineSeparator());
        for (ExcelSheetInfo info : infoList) {
            sb.append(JsonUtil.bean2json(info));
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

}

