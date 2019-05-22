
## 作者
```
姓名：${tplContext.methodAuthor!""}
```

## 请求
```
请求功能：${tplContext.methodDesc!""}
支持请求：${tplContext.supportRequest!""}
请求 url：${tplContext.requestUrl!""}
```

## 入参
入参类型：${tplContext.methodParamType}
<#if tplContext.methodParamType=="REQUEST_PARAM">

|属性名|类型|是否必填|默认值|参数描述|
|:---:|:---:|:---:|:---:|:---:|
<#list tplContext.requestParams as vo>
| ${vo.paramName!""} | ${vo.paramType!""} | ${vo.required!""} | ${vo.defValue!""} | ${vo.paramDesc!""} |
</#list>
</#if>
<#if tplContext.methodParamType=="REQUEST_BODY">
```json
${tplContext.requestBody!""}
```

<#if tplContext.showComment>
## 入参字段描述
```json
${tplContext.requestBodyComment!""}
```
</#if>

</#if>

<#if tplContext.responseBody?exists>
## 返回
    <#list tplContext.responseBody?keys as key>
### ${key}
```json
${tplContext.responseBody[key]}
```
    </#list>

<#if tplContext.showComment>
## 返回字段描述
    <#list tplContext.responseBodyComment?keys as key>
### ${key}
```json
${tplContext.responseBodyComment[key]}
```
    </#list>
</#if>

</#if>


