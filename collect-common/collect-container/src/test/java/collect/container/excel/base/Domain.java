package collect.container.excel.base;

import com.common.collect.container.excel.annotations.ExcelImport;
import lombok.Data;

import java.util.List;

@Data
public class Domain {
    @ExcelImport(colIndex = "0")
    private String s1;
    @ExcelImport(colIndex = "1")
    private String s2;
    @ExcelImport(colIndex = "2")
    private String s3;
    @ExcelImport(colIndex = "3")
    private String s4;
    @ExcelImport(colIndex = "4")
    private String s5;
    @ExcelImport(colIndex = "5")
    private String s6;
    @ExcelImport(colIndex = "6")
    private String s7;
    @ExcelImport(colIndex = "2:4")
    private List<String> s24;
}