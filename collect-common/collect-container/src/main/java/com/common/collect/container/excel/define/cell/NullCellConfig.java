package com.common.collect.container.excel.define.cell;

import com.common.collect.container.excel.ExcelSession;
import com.common.collect.container.excel.context.ExcelContext;
import com.common.collect.container.excel.define.ICellConfig;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

public class NullCellConfig implements ICellConfig {

    @Override
    public ExcelCellConfigInfo pullCellConfig(Object value, ExcelSession excelSession, ExcelContext excelContext) {
        return null;
    }

}
