package com.common.collect.container.excel.define.cell;

import com.common.collect.container.excel.ExcelSession;
import com.common.collect.container.excel.define.ICellConfig;
import com.common.collect.container.excel.pojo.ExcelParam;
import org.apache.poi.ss.usermodel.CellStyle;

import java.math.BigDecimal;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

public class DefaultCellConfig implements ICellConfig {

    @Override
    public ExcelCellConfigInfo pullCellConfig(Object value, ExcelSession excelSession, ExcelParam excelParam) {

        ExcelCellConfigInfo excelCellConfigInfo = new ExcelCellConfigInfo();

        excelCellConfigInfo.setColWidth(5000);
        excelCellConfigInfo.setHidden(false);
        // 配置样式
        CellStyle defaultCellStyle = excelSession.createDefaultCellStyle();
        if (value != null) {
            CellStyle numberCellStyle = excelSession.createDefaultDoubleCellStyle();
            if (value.getClass() == Double.class || value.getClass() == double.class) {
                defaultCellStyle = numberCellStyle;
            } else if (value.getClass() == Float.class || value.getClass() == float.class) {
                defaultCellStyle = numberCellStyle;
            } else if (value.getClass() == BigDecimal.class) {
                defaultCellStyle = numberCellStyle;
            }
        }
        excelCellConfigInfo.setCellStyle(defaultCellStyle);
        excelCellConfigInfo.vailSelf();
        return excelCellConfigInfo;
    }

}
