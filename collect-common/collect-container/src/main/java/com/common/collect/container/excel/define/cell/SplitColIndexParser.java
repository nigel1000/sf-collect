package com.common.collect.container.excel.define.cell;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.IColIndexParser;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.SplitUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by nijianfeng on 2019/3/9.
 */
public class SplitColIndexParser implements IColIndexParser {

    @Override
    public List<Integer> parseColIndex(String colIndex) {

        List<Integer> result = Lists.newArrayList();
        for (String rangeIndex : SplitUtil.split2Array(colIndex.trim(), ",")) {
            List<Integer> indexs = SplitUtil.split(rangeIndex.trim(), ":", Integer::valueOf);
            if (indexs.size() == 1) {
                result.add(indexs.get(0));
            } else if (indexs.size() == 2 && indexs.get(1) >= indexs.get(0)) {
                for (int i = indexs.get(0); i <= indexs.get(indexs.size() - 1); i++) {
                    result.add(i);
                }
            } else {
                throw UnifiedException.gen(ExcelConstants.MODULE, rangeIndex + "为空或者不合法");
            }
        }
        if (EmptyUtil.isEmpty(result) || result.get(0) < 0) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "colIndex不能小于0");
        }

        return result;
    }
}
