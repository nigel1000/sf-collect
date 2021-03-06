package com.common.collect.framework.excel.define.cell;

import com.common.collect.framework.excel.base.ExcelConstants;
import com.common.collect.framework.excel.define.IColIndexParser;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.FunctionUtil;
import com.common.collect.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijianfeng on 2019/3/9.
 */
public class SplitColIndexParser implements IColIndexParser {

    @Override
    public List<Integer> parseColIndex(String colIndex) {

        List<Integer> result = new ArrayList<>();
        for (String rangeIndex : StringUtil.split2List(colIndex.trim(), ",")) {
            List<Integer> indexs = FunctionUtil.valueList(StringUtil.split2List(rangeIndex.trim(), ":"), Integer::valueOf);
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
