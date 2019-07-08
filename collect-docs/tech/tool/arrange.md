## 扩展点和使用点

### 使用目标

- 产品提起上线工单内容为业务定义，开发进行语法审核上预发，测试回归测试通过预发，leader审核上线，测试回归通过。

### 扩展设计

- 功能上可以加 事务或者降级等标签，看后续使用情况
- 业务上可以按需打上标签以控制逻辑调用

## 解决的问题

- 人员迭代导致的代码冗余，你我他都有自己的实现
- 不能动态的改变业务逻辑，可能在关键地方会使用开关
- 需求迭代排期时间长，需要大量的人力
- 产品基本不直掉现有能力，不能给甲方一个预估且较的时间

## 解决的思路

- 在现有的能力下，组合各类服务以完成相应业务
- 业务逻辑可以动态的调整变更，亦可实现不回滚完成逻辑切换
- 一个业务就是一个调用链，使用者只需关注入参和返回

## 实现目标&原理

### 目标

编排已有功能，进行组合，形成调用链，确定入参后执行获取结果，实现搭积木式的开发模式。  

### 原理

1. 加载 功能&业务 配置 到内存
2. 解析配置形成以业务key为主键，功能调用链为值的模型
3. 提供 runBiz 开启业务 和 获取返回 的入口

## 实现示例

### 功能&业务定义

一个业务形成一个调用功能或者业务的链。即业务中可嵌套业务。

#### 功能的核心要素

- 功能的id即key
- 功能的描述
- 是否需要保存参数和返回
- 功能类的获取方式，通过spring容器或者反射
-  功能类中的具体功能，即类中方法
- 当前只支持一个入参或者没有入参，即入参必须用一个自定义对象包裹，若有多个入参复杂度太高 
- 入参对象中的哪些属性作为输入
- 确定**从入参中还是返回中**的属性作为下一个功能的入参属性
- 确定**哪些**属性作为下一个功能的入参属性



```yaml
functions_define:
  # 功能 key
  # 对输入的参数进行叠加 返回 计算数字out2 和 总和out1
  test_function:
    # 是否保存输入和返回
    function_in_keep: true
    function_out_keep: true
    # 功能 类 优先使用 function_clazz_by_spring，次之 function_clazz_by_reflect
    function_clazz_by_spring: functionClazz
    function_clazz_by_reflect: collect.debug.arrange.FunctionClazz
    # 功能 方法
    function_method: testFunction
    # 功能 方法 入参少于等于 1
    function_method_type: inputLessEqualOne
    function_method_in_clazz: collect.debug.arrange.FunctionTestContext
    # 入参属性 可为空 必须是 function_method_in_clazz 的 field
    function_method_in_fields:
    - in
    # 导出来自 入参 input | 返回 output
    function_method_out_from: output
    # 返回属性 可为空 必须是 返回对象|入参对象 的 field，生成 json 作为 下一个 function_name 的 function_method_in_json
    function_method_out_fields:
    - out2

```

#### 业务的核心要素

- 业务key即业务名称
- 调用的功能或者业务的key
- 定义功能或者业务的第一个功能的入参是从上一个返回的哪个属性中来

```yaml

biz_define:
  # 业务 1
  compose_biz_1:
    arranges:
    - type: function
      name: test_function
    # 类型是 biz 的 input 不能为空
    - type: biz
      name: sub_biz
      input: [out2->in]
    - type: function
      name: test_function
      input: [out2->in]
    - type: biz
      name: sub_biz
      input: [out2->in]
    - type: biz
      name: sub_biz
      input: [out2->in]
  # 业务 2
  sub_biz:
    arranges:
    - type: function
      name: test_function
  # 业务 3
  compose_biz_2:
    arranges:
    - type: function
      name: test_function
    # 类型是 biz 的 input 不能为空
    - type: biz
      name: sub_biz
      input: [out2->in]
    - type: function
      name: test_function
      input: [out2->in]
    - type: biz
      name: compose_biz_1
      input: [out2->in]

```

### 开始一个调用

可以动态加载功能&业务定义，覆盖已有定义，动态改变调用业务逻辑

```java
@Slf4j
public class ArrangeTest {

    public static void main(String[] args) throws IOException {
        // 启动 spring 容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");

        // 加载配置文件
        ArrangeContext.load(new ClassPathResource("arrange/function-define.yml").getInputStream());

      	// 定义第一个入参
        FunctionTestContext param = new FunctionTestContext();
        param.setIn(Lists.newArrayList(0));

      	// 启动一个业务调用链并传入入参
//        ArrangeRetContext context = ArrangeContext.runBiz("compose_biz_2", null);
        ArrangeRetContext context = ArrangeContext.runBiz("compose_biz_2", JsonUtil.bean2json(param));

      	// 返回最后一个功能的返回
        log.info(JsonUtil.bean2jsonPretty(context));
        FunctionTestContext ret = context.getLastRet();
        log.info(LogConstant.getObjString(ret));

        // 返回随意一个功能返回
        ret = context.getByIndexFromMap(1, context.getOutputMap());
        log.info(LogConstant.getObjString(ret));

    }

}
```









