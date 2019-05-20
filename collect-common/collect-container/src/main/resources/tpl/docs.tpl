
## 作者
```
姓名：${docsMethodConfig.methodAuthor!""}
```

## 请求
```
请求功能：${docsMethodConfig.methodDesc!""}
支持请求：${docsMethodConfig.supportRequest!""}
请求 url：${docsMethodConfig.requestUrl!""}
```

## 入参
入参类型：${docsMethodConfig.methodParamType}
<#if docsMethodConfig.methodParamType=="REQUEST_PARAM">

|属性名|类型|是否必填|默认值|参数描述|
|:---:|:---:|:---:|:---:|:---:|
<#list docsMethodConfig.requestParams as vo>
| ${vo.paramName!""} | ${vo.paramType!""} | ${vo.required!""} | ${vo.defValue!""} | ${vo.paramDesc!""} |
</#list>
</#if>
<#if docsMethodConfig.methodParamType=="REQUEST_BODY">
```json
${docsMethodConfig.requestBody!""}
```
</#if>

## 返回
```json
${docsMethodConfig.responseBody!""}
```

