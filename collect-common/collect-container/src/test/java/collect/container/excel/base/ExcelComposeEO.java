package collect.container.excel.base;

import com.common.collect.container.excel.annotations.*;
import com.common.collect.container.excel.define.cell.DefaultCellConfig;
import com.common.collect.container.excel.extension.OptionCheckImportHandler;
import com.common.collect.container.excel.extension.OptionConvertExportHandler;
import com.common.collect.container.excel.extension.OptionConvertImportHandler;
import com.common.collect.container.excel.extension.OptionYesNo;
import com.common.collect.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Data
@ExcelEntity(cellConfig = DefaultCellConfig.class)
public class ExcelComposeEO implements Serializable {
    private static final long serialVersionUID = 2673802046675941279L;

    @ExcelImport(colIndex = "0", title = "字符串")
    @ExcelCheck(regex = "[a-z]", required = true)
    @ExcelExport(colIndex = 0, title = "字符串")
    private String stringValue;

    @ExcelImport(colIndex = "1", title = "数字1")
    @ExcelCheck(max = 100, required = true)
    @ExcelExport(colIndex = 1, title = "数字1", cellConfig = TestCellConfig.class)
    private Integer intValue;

    @ExcelImport(colIndex = "2", title = "数字2")
    @ExcelCheck(max = 100, required = true)
    @ExcelExport(colIndex = 2, title = "数字2")
    private Long longValue;

    @ExcelImport(colIndex = "3", title = "数字3")
    @ExcelCheck(max = 100, required = true)
    @ExcelExport(colIndex = 3, title = "数字3")
    private BigDecimal bigDecimalValue;

    @ExcelImport(colIndex = "4:5,3,0:1", duplicateRemove = true, dataType = String.class)
    @ExcelCheck(max = 100, required = true)
    private List<String> stringListValue;

    @ExcelImport(colIndex = "4", title = "option")
    @ExcelCheck(max = 100, required = true, checkImportHandlers = {OptionCheckImportHandler.class})
    @ExcelExport(colIndex = 4, title = "option")
    @ExcelConvert(convertExportHandlers = {OptionConvertExportHandler.class},
            convertImportHandlers = {OptionConvertImportHandler.class})
    private OptionYesNo optionYesNo;

    @ExcelImport(colIndex = "5", title = "date")
    @ExcelCheck(required = true)
    @ExcelExport(colIndex = 5, title = "date")
    private Date date;

    @ExcelImport(colIndex = "5", title = "dateExcelConvert")
    @ExcelCheck(required = true)
    @ExcelConvert(dateFormat = "yyyy-MM-dd HH")
    @ExcelExport(colIndex = 6, title = "dateExcelConvert")
    private Date dateExcelConvert;

    public static ExcelComposeEO gen() {
        ExcelComposeEO excelComposeEO = new ExcelComposeEO();
        excelComposeEO.setBigDecimalValue(BigDecimal.ONE);
        excelComposeEO.setDate(DateUtil.now());
        excelComposeEO.setDateExcelConvert(DateUtil.now());
        excelComposeEO.setIntValue(2);
        excelComposeEO.setLongValue(3L);
        excelComposeEO.setStringValue("测试");
        excelComposeEO.setOptionYesNo(OptionYesNo.YES);
        return excelComposeEO;
    }

}
