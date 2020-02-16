package com.common.collect.framework.docs.demo;

import com.common.collect.lib.api.Response;
import com.common.collect.lib.api.docs.DocsField;
import com.common.collect.lib.api.docs.DocsFieldExclude;
import com.common.collect.lib.api.docs.DocsMethod;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DemoController {

    @DocsMethod(id = "1", name = "各类入参", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/docs/request", method = {RequestMethod.GET, RequestMethod.POST})
    public void request(
            @DocsField(desc = " baseType") @RequestParam(name = "baseType", defaultValue = "基本类型") String base,
            DocObject objType,
            String[] arrayBase,
            String[][] arrayBases,
            List<String> listBase,
            List<List<String>> listBases,
            List<DocObjectSimpleSub> listObjType,
            List<List<DocObjectSimpleSub>> listObjTypes,
            DocObjectSimpleSub[] arrayObjType,
            DocObjectSimpleSub[][] arrayObjTypes,
            // Object Map 不解析，当有 @DocsField 注解时会按注解的内容输出
            @DocsField(value = "有注解DocsField才处理", desc = "此类型的object不处理") Object objTypeParse,
            Map<String, String> objTypeNoParse,
            List<Object> listObjNoParse,
            HttpServletResponse response,
            HttpServletRequest request,
            MultipartFile file
    ) {
    }

    @DocsMethod(id = "2", name = "返回基本类型", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/docs/response", method = {RequestMethod.GET, RequestMethod.POST})
    public Long responseBase() {
        return null;
    }

    @DocsMethod(id = "3", name = "返回对象类型不处理", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/docs/response", method = {RequestMethod.GET, RequestMethod.POST})
    public Object responseObjNoParse() {
        return null;
    }

    @DocsMethod(id = "4", name = "返回对象类型处理", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/docs/response", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<DocObject> responseObjParse() {
        return null;
    }

    @DocsMethod(id = "5", name = "返回数组基本类型", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/docs/response", method = {RequestMethod.GET, RequestMethod.POST})
    public List<String> responseArrayBase() {
        return null;
    }

    @DocsMethod(id = "5", name = "返回数组对象类型处理", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/docs/response", method = {RequestMethod.GET, RequestMethod.POST})
    public List<DocObject> responseArrayObjParse() {
        return null;
    }

    @DocsMethod(id = "5", name = "返回数组对象类型不处理", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/back/door/docs/response", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Object> responseArrayObjNoParse() {
        return null;
    }

    @Data
    public static class DocObject {
        private String docObject;
        private DocObjectSub docObjectSub;
        private DocObjectSimpleSub[] docObjectSimpleSubs;
    }

    @Data
    public static class DocObjectSimpleSub {
        @DocsField(desc = "bool 值", required = false)
        private Boolean bool;
        @DocsField(value = "1234567890")
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
        @DocsField(value = "1234567890")
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



