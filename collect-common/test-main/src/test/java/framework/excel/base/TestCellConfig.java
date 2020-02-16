package framework.excel.base;


import com.common.collect.framework.excel.ExcelSession;
import com.common.collect.framework.excel.context.ExcelContext;
import com.common.collect.framework.excel.define.ICellConfig;
import com.common.collect.framework.excel.define.cell.DefaultCellConfig;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

public class TestCellConfig implements ICellConfig {

    @Override
    public ExcelCellConfigInfo pullCellConfig(Object value, ExcelSession excelSession, ExcelContext excelContext) {
        ICellConfig cellConfig = excelContext.getBeanFactory().getBean(DefaultCellConfig.class);
        ExcelCellConfigInfo excelCellConfigInfo = cellConfig.pullCellConfig(value, excelSession, excelContext);
        excelCellConfigInfo.setColWidth(2500);
        return excelCellConfigInfo;
    }

}
