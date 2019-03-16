package collect.container.excel.base;

import com.common.collect.container.excel.ExcelSession;
import com.common.collect.container.excel.define.ICellConfig;
import com.common.collect.container.excel.define.cell.DefaultCellConfig;
import com.common.collect.container.excel.pojo.ExcelParam;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

public class TestCellConfig implements ICellConfig {

    @Override
    public ExcelCellConfigInfo pullCellConfig(Object value, ExcelSession excelSession, ExcelParam excelParam) {
        ICellConfig cellConfig =
                excelParam.getClassInfo().getExcelEntityModel().getBeanFactoryImpl().getBean(DefaultCellConfig.class);
        ExcelCellConfigInfo excelCellConfigInfo = cellConfig.pullCellConfig(value, excelSession, excelParam);
        excelCellConfigInfo.setColWidth(2500);
        return excelCellConfigInfo;
    }

}
