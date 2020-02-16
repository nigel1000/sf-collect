# Excel Componment
外界类似工具：easyPoi，easyExcel。
[excel 组件整体架构图](https://www.processon.com/view/link/59ccc3f0e4b0f600882fa4fd)
>内部工具相对于外部工具：

1. 可维护性强
2. 功能可定制化
3. 坑点可自理

>外部工具：

1. 功能较多
2. 扩展成本大

## 导入
>可使用注解：**@ExcelImport，@ExcelCheck，@ExcelConvert**

>建议使用姿势：

1. excel 导入 全部用 string 接收，获取List<String>
2. 对 List<String> 进行数据校验和数据转换成相应的 List<Object>
3. 如数据校验不合法，可设置 errorMsg，最后导出错误列表

>主要提供以下功能：

1. 从 excel 导入数据，每行对应一个 object，返回 List<Object>。可指定 from和end，即从第几行开始到第几行结束。
2. 支持 List 属性对应一行多列 excel 单元格数据
2. 提供导入数据量限制
3. 默认支持 excel单元格数据转换java类型 (Long|Integer|BigDecimal|Date|Boolean|String|List)，提供扩展机制，可自行重写
4. 支持对 excel单元格数据进行校验，并信息返回错误数据，类似：sheet 测试表 第1行 第二列 数据须符合...规则
5. 可指定快速失败，即一个数据有问题，解析即结束，亦可指定最大错误数值，在收集此数值后才结束解析。 
6. 提供给错误信息进行样式设置并导出文件。

## 导出
> 导出 100w 条数据，耗时 30s。

>可使用注解：**@ExcelExport，@ExcelConvert，@ExcelEntity**

>主要提供以下功能：

1. 支持 导出 List 对象到 excel。
2. 支持导出 xlsx 和 xls
3. 默认支持 java 类型(Long|Integer|BigDecimal|Date|Boolean|String|List) 转成 excel单元格 text 数据，提供扩展机制，可自行重写
4. 可指定导出样式数据
5. 支持从已有模板或者全新创建进行数据导出

## 注解详解
### @ExcelExport
> 导出时使用：进行excel单元格和java对象类的映射
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {

    // excel的colIndex 指定导出列
    int colIndex() default 0;

    // excel的title 导出此列的title
    String title() default "";

    // 默认不做配置 可扩展配置列宽 单元格样式 字体 是否隐藏等
    Class<? extends ICellConfig> cellConfig() default ICellConfig.class;

}
```
### @ExcelImport
> 导入时使用：进行excel单元格和java对象类的映射
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    // 譬如指定多列 8:22,0:5,24，
    // 会把第0到5列，第8列，22到24列合并成 List<dataType> 返回，顺序与下标顺序一致
    // 故多列时 field 类型必须是 List
    // 譬如指定单列 8 此时会把第8列转换成 field 类型返回
    String colIndex() default "";

    // 解析 colIndex 为 List<Integer> 可扩展适合使用的列下标解析器
    Class<? extends IColIndexParser> colIndexParser() default SplitColIndexParser.class;

    // 多列时生效即属性类型是List
    Class dataType() default String.class;

    // 多列时生效即属性类型是List 是否需要去重
    boolean duplicateRemove() default false;

    // excel的title 无实际使用意义 类似于备注使字段可以自解释
    String title() default "";

}
```
### @ExcelCheck
> 导入时使用：进行数据校验

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCheck {

    // 必填约束
    boolean required() default false;

    String requiredTips() default "定义属性不能为空而录入数据为空";

    // 单元格的字符串的最大长度或者数值的最大值
    // 作用与类型：String,Integer,Long,BigDecimal
    long max() default Long.MIN_VALUE;

    String maxTips() default "数值必须小于" + ExcelConstants.PLACEHOLDER_MAX + "或者字符必须少于" + ExcelConstants.PLACEHOLDER_MAX;

    // 作用与类型：String
    String regex() default "";

    String regexTips() default "正则表达式：" + ExcelConstants.PLACEHOLDER_REGEX;

    // 新增校验器 可自行扩展校验范围
    Class<? extends ICheckImportHandler>[] checkImportHandlers() default {};

}
```
> 提供以下错误提示占位符：
```
    // tips 占位符
    // convert
    public final static String PLACEHOLDER_DATE_PARSE = "#{date_parse}";
    // check
    public final static String PLACEHOLDER_MAX = "#{max_check}";
    public final static String PLACEHOLDER_REGEX = "#{regex_check}";
    // common
    public final static String PLACEHOLDER_CELL_VALUE = "#{cell_value}";
    public final static String PLACEHOLDER_ROW_NUM = "#{row_num}";
    public final static String PLACEHOLDER_COL_NUM = "#{col_num}";
    public final static String PLACEHOLDER_COL_TITLE = "#{col_title}";
    public final static String PLACEHOLDER_SHEET_NAME = "#{sheet_name}";
```
### @ExcelConvert
> 导入时使用：进行数据转换，String->Object，convertImportHandlers

> 导出时使用：进行数据转换，Object->String，convertExportHandlers
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelConvert {

    // 作用与类型：Date
    // 作用于 导入
    String dateParse() default "yyyy-MM-dd HH:mm:ss";

    // 占位符替换成 dateParse()的值
    String dateParseTips() default "日期格式需要满足：" + ExcelConstants.PLACEHOLDER_DATE_PARSE;

    // 作用与类型：Date
    // 作用于 导出
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    // 默认导出数据都转成 string
    // 新增导出转换器 遍历全部转换器 以最后一个不返回 null 的为准
    Class<? extends IConvertExportHandler>[] convertExportHandlers() default {ByTypeConvertExportHandler.class};

    // 默认导入值都为 string ，即 string 根据类型转成相应数据
    // 新增导入转换器 遍历全部转换器 以最后一个不返回 null 的为准
    Class<? extends IConvertImportHandler>[] convertImportHandlers() default {ByTypeConvertImportHandler.class};

}
```
### @ExcelEntity
> 提供导入导出全局配置类，此为类上的属性，会覆盖属性上的注解值

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 类上的配置会覆盖属性上的配置
public @interface ExcelEntity {
    // 覆盖 ExcelExport.cellConfig
    Class<? extends ICellConfig> cellConfig() default DefaultCellConfig.class;
    
    // 可扩展类的 bean 生成和获取方式自定义
    Class<? extends IBeanFactory> beanFactory() default SingletonBeanFactory.class;

}
```
## 使用用例
```java
import java.util.Arrays;public class TestCellConfig implements ICellConfig {

    @Override
    public ExcelCellConfigInfo pullCellConfig(Object value, ExcelSession excelSession, ExcelParam excelParam) {
        ICellConfig cellConfig =
                excelParam.getClassInfo().getExcelEntityModel().getBeanFactoryImpl().getBean(DefaultCellConfig.class);
        ExcelCellConfigInfo excelCellConfigInfo = cellConfig.pullCellConfig(value, excelSession, excelParam);
        excelCellConfigInfo.setColWidth(2500);
        return excelCellConfigInfo;
    }

}

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

@Slf4j
public class ExcelSessionTest {

    public static String path;

    static {
        path = ExcelSessionTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "src/test/resources";
        } else {
            path = path.substring(0, path.indexOf("target")) + "src/test/resources";
        }
    }

    public static void main(String[] args) {
        log.info("path:{}", path);
        try {
            new ExcelImportUtil("");
        } catch (Exception ex) {

        }
        Slf4jUtil.setLogLevel("debug");

        ExcelClient excelClient = new ExcelClient();

        List<ExcelComposeEO> corrects =
                excelClient.fileImport(new File(path + "/ExcelImport.xlsx"), ExcelComposeEO.class);
        print(corrects);

        try {
            List<ExcelComposeEO> errors =
                    excelClient.fileImport(new File(path + "/ExcelImportError.xlsx"), ExcelComposeEO.class);
            print(errors);
        } catch (Exception ex) {
            log.error("导出错误信息", ex);
        }

        ExcelComposeEO excelComposeEO = ExcelComposeEO.gen();
        long time = System.currentTimeMillis();
        excelClient.fileExport(ExcelComposeEO.class, "测试", (excelExportUtil -> {
            for (int i = 0; i < 1000000; i++) {
                excelExportUtil.exportForward(Arrays.asList(excelComposeEO), ExcelComposeEO.class);
            }
        }));
        // 新建 excel 100 万条数据 30秒
        log.info("耗时：{} 秒", (System.currentTimeMillis() - time) / 1000);

        time = System.currentTimeMillis();
        excelClient.fileTplExport("ExcelExportTpl.xlsx", (excelExportUtil -> {
            for (int i = 0; i < 1000000; i++) {
                excelExportUtil.exportForward(Arrays.asList(excelComposeEO), ExcelComposeEO.class);
            }
        }));
        // 用模版 100 万条数据 30秒
        log.info("耗时：{} 秒", (System.currentTimeMillis() - time) / 1000);
    }

    private static <T> void print(List<T> list) {
        for (T t : list) {
            log.info("{}", t);
        }
    }

}

```

