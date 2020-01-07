package com.common.collect.debug.spring;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.TemplateUtil;
import com.common.collect.container.arrange.ArrangeContext;
import com.common.collect.container.arrange.context.BizContext;
import com.common.collect.container.arrange.context.BizFunctionChain;
import com.common.collect.container.arrange.context.ConfigContext;
import com.common.collect.container.arrange.model.FunctionDefineModel;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.StringUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/4/11.
 */

@RestController
@RequestMapping("/back/door/arrange")
public class ArrangeController {

    private String arrange_key = "c26b094cc27346379266147682c41fc0";

    // /back/door/arrange/operate?type=get&bizKeys=biz_pagingActivity&functionKeys=&uuid=c26b094cc27346379266147682c41fc0
    // /back/door/arrange/operate?type=set&content=biz_define%3A%0A%20%20%23%20%E8%8E%B7%E5%8F%96%E5%85%A8%E9%83%A8%E6%95%B0%E6%8D%AE%0A%20%20biz_pagingActivity111%3A%0A%20%20%20%20arranges%3A%0A%20%20%20%20-%20type%3A%20function%0A%20%20%20%20%20%20name%3A%20fun_pagingBaseActivity%0A%20%20%20%20-%20type%3A%20function%0A%20%20%20%20%20%20name%3A%20fun_queryBaseConferenceByActivityIds%0A%20%20%20%20%20%20input_type%3A%20pass%0A%20%20%20%20-%20type%3A%20function%0A%20%20%20%20%20%20name%3A%20fun_queryBaseModuleByActivityIds%0A%20%20%20%20%20%20input_type%3A%20pass%0A%20%20%20%20-%20type%3A%20function%0A%20%20%20%20%20%20name%3A%20fun_queryBaseScheduleByActivityIds%0A%20%20%20%20%20%20input_type%3A%20pass%0A%20%20%20%20-%20type%3A%20function%0A%20%20%20%20%20%20name%3A%20fun_queryBaseDetailByActivityIds%0A%20%20%20%20%20%20input_type%3A%20pass%0A%20%20%20%20-%20type%3A%20function%0A%20%20%20%20%20%20name%3A%20fun_composeActivity%0A%20%20%20%20%20%20input_type%3A%20pass%0A&uuid=c26b094cc27346379266147682c41fc0
    @RequestMapping("/operate")
    public void generator(@RequestParam("type") String type,
            @RequestParam(value = "bizKeys", required = false) String bizKeys,
            @RequestParam(value = "functionKeys", required = false) String functionKeys,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "uuid") String uuid, HttpServletRequest request, HttpServletResponse response) {

        if (!uuid.equals(arrange_key)) {
            throw UnifiedException.gen("无权限访问此链接");
        }

        Map<String, BizContext> bizContextMap = new LinkedHashMap<>();
        Map<String, FunctionDefineModel> functionDefineModelMap = new LinkedHashMap<>();
        if ("get".equals(type)) {
            if (EmptyUtil.isNotEmpty(bizKeys)) {
                for (String bizKey : StringUtil.split2List(bizKeys, ",")) {
                    BizContext bizContext = BizContext.getBizContextByKey(bizKey);
                    if (bizContext != null) {
                        bizContextMap.put(bizKey, bizContext);
                        for (BizFunctionChain bizFunctionChain : bizContext.getBizFunctionChains()) {
                            functionDefineModelMap.put(bizFunctionChain.getFunctionKey(),
                                    ConfigContext.getFunctionByKey(bizFunctionChain.getFunctionKey()));
                        }
                    }
                }
            }
            if (EmptyUtil.isNotEmpty(functionKeys)) {
                for (String functionKey : StringUtil.split2List(functionKeys, ",")) {
                    functionDefineModelMap.put(functionKey, ConfigContext.getFunctionByKey(functionKey));
                }
            }
            if (EmptyUtil.isEmpty(bizContextMap) && EmptyUtil.isEmpty(functionDefineModelMap)) {
                bizContextMap = BizContext.getBizContextMap();
                functionDefineModelMap = ConfigContext.getFunctionDefineModelMap();
            }
        } else if ("set".equals(type)) {
            if (EmptyUtil.isEmpty(content)) {
                throw UnifiedException.gen("content 不能为空");
            }
            ArrangeContext.load(content);
        }
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write("<html><body>");
            out.write("<h1>操作成功</h1>");

            if ("get".equals(type)) {
                out.write("<h2><font color=\"red\">");
                out.write("function defines config :");
                out.write("</font></h2>");
                for (Map.Entry<String, FunctionDefineModel> entry : functionDefineModelMap.entrySet()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    Map<String, FunctionDefineModel> tempMap = new LinkedHashMap<>();
                    tempMap.put(entry.getKey(), entry.getValue());
                    map.put("functionDefines", tempMap);
                    String yaml = TemplateUtil.getStringByTemplate("/tpl/arrange", "function_define.tpl", map);
                    out.write("<pre>");
                    out.write(StringEscapeUtils.escapeXml11(yaml));
                    out.write("</pre>");
                }
                out.write("<h2><font color=\"red\">");
                out.write("biz defines config :");
                out.write("</font></h2>");
                for (Map.Entry<String, BizContext> entry : bizContextMap.entrySet()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    Map<String, BizContext> tempMap = new LinkedHashMap<>();
                    tempMap.put(entry.getKey(), entry.getValue());
                    map.put("bizDefines", tempMap);
                    String yaml = TemplateUtil.getStringByTemplate("/tpl/arrange", "biz_define.tpl", map);
                    out.write("<pre>");
                    out.write(StringEscapeUtils.escapeXml11(yaml));
                    out.write("</pre>");
                }
                out.write("<h2><font color=\"red\">");
                out.write("biz function chains config :");
                out.write("</font></h2>");
                for (Map.Entry<String, BizContext> entry : bizContextMap.entrySet()) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    Map<String, BizContext> tempMap = new LinkedHashMap<>();
                    tempMap.put(entry.getKey(), entry.getValue());
                    map.put("bizFunctionChains", tempMap);
                    String yaml = TemplateUtil.getStringByTemplate("/tpl/arrange", "biz_function_chain.tpl", map);
                    out.write("<pre>");
                    out.write(StringEscapeUtils.escapeXml11(yaml));
                    out.write("</pre>");
                }
            }

            out.write("</body></html>");
            out.flush();
        } catch (Exception ex) {
            throw UnifiedException.gen("输出内容出错", ex);
        }


    }


}
