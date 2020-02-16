package com.common.collect.framework.excel.context;

import lombok.Data;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

@Data
public class EventModelContext {

    // 是否刚开始读 sheet
    private boolean sheetStart;

    // 为 0 时表示没有设置填充属性，读取列数和 excel 实际列数相符
    // 不为 0 时，rows 里面的 col 列数为 needReadColNum 列
    private int needReadColNum;

    // 当前 sheet 下标
    private int curSheetIndex;

    // 当前 sheet 读取的总行数
    private int sheetAlreadyReadRowNum;

    // 读取的数据
    private List<List<String>> rows;

}
