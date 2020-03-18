package com.common.collect.framework.docs;

import com.common.collect.lib.api.Response;
import com.common.collect.lib.api.docs.DocsField;
import com.common.collect.lib.api.docs.DocsFieldExclude;
import com.common.collect.lib.api.docs.DocsMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/3/17.
 */
@Slf4j
@RequestMapping(value = "/back/door/docs", method = {RequestMethod.GET, RequestMethod.POST})
public class DocsDemo {

    @DocsMethod(name = "各类入参", desc = "hznijianfeng")
    @RequestMapping(value = "/request", method = {RequestMethod.GET, RequestMethod.POST})
    public void request(
            @DocsField(desc = " baseType") @RequestParam(name = "baseType", defaultValue = "基本类型") String base,
            DocsDemo.DocObject objType,
            String[] arrayBase,
            String[][] arrayBases,
            List<String> listBase,
            List<List<String>> listBases,
            List<DocsDemo.DocObjectSimpleSub> listObjType,
            List<List<DocsDemo.DocObjectSimpleSub>> listObjTypes,
            DocsDemo.DocObjectSimpleSub[] arrayObjType,
            DocsDemo.DocObjectSimpleSub[][] arrayObjTypes,
            // Object Map 不解析，当有 @DocsField 注解时会按注解的内容输出
            @DocsField(defaultValue = "有注解DocsField才处理", desc = "此类型的object不处理")
                    Object objTypeParse,
            List<Object> listObjNoParse,
            HttpServletResponse response,
            HttpServletRequest request,
            MultipartFile file
    ) {
    }

    @DocsMethod(name = "返回基本类型", desc = "hznijianfeng")
    @RequestMapping(value = "/responseBase", method = {RequestMethod.GET, RequestMethod.POST})
    public Long responseBase() {
        return null;
    }

    @DocsMethod(name = "返回对象类型不处理", desc = "hznijianfeng")
    @RequestMapping(value = "/responseObjNoParse", method = {RequestMethod.GET, RequestMethod.POST})
    public Object responseObjNoParse() {
        return null;
    }

    @DocsMethod(name = "返回对象类型处理", desc = "hznijianfeng")
    @RequestMapping(value = "/responseObjParse", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<DocsDemo.DocObject> responseObjParse() {
        return null;
    }

    @DocsMethod(name = "返回数组基本类型", desc = "hznijianfeng")
    @RequestMapping(value = "/responseArrayBase", method = {RequestMethod.GET, RequestMethod.POST})
    public List<String> responseArrayBase() {
        return null;
    }

    @DocsMethod(name = "返回数组对象类型处理", desc = "hznijianfeng")
    @RequestMapping(value = "/responseArrayObjParse", method = {RequestMethod.GET, RequestMethod.POST})
    public List<DocsDemo.DocObject> responseArrayObjParse() {
        return null;
    }

    @DocsMethod(name = "返回数组对象类型不处理", desc = "hznijianfeng")
    @RequestMapping(value = "/responseArrayObjNoParse", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Object> responseArrayObjNoParse() {
        return null;
    }

    @Data
    public static class DocObject {
        private String docObject;
        private DocsDemo.DocObjectSub docObjectSub;
        private DocsDemo.DocObjectSimpleSub[] docObjectSimpleSubs;
    }

    @Data
    public static class DocObjectSimpleSub {
        @DocsField(desc = "bool 值", required = false)
        private Boolean bool;
        @DocsField(defaultValue = "1234567890")
        private Integer intNum;
        @DocsFieldExclude
        private Date dateExclude;
    }

    @Data
    public static class DocObjectSub {
        @DocsField(desc = "bool 值", required = false)
        private Boolean bool;
        @DocsField(desc = "long 值")
        private Long longNum;
        @DocsField(defaultValue = "1234567890")
        private Integer intNum;
        private Float floatNum;
        private Double doubleNum;
        private Byte byteNum;
        private Short shortNum;
        private BigDecimal bigDecimalNum;
        private Character chatStr;
        private String str;
        @DocsFieldExclude
        private Date dateExclude;
        private Date date;
    }

}
