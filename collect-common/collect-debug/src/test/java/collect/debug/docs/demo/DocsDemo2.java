package collect.debug.docs.demo;

import com.common.collect.api.Response;
import com.common.collect.container.docs.DocsApi;
import com.common.collect.container.docs.DocsApiMethod;
import com.common.collect.container.docs.DocsMethodConfig;
import com.common.collect.container.docs.DocsMethodParamConfig;
import com.common.collect.container.docs.MethodParamType;
import com.common.collect.container.docs.SupportRequest;
import org.assertj.core.util.Lists;

import java.math.BigDecimal;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@DocsApi(rootDirName = "DocsDemo2", urlPrefix = "/DocsDemo2")
public class DocsDemo2 {

    @DocsApiMethod(
            nodeName = "method2.md",
            urlSuffix = "/method2",
            methodAuthor = "hznijianfeng",
            methodDesc = "测试方法",
            supportRequest = {SupportRequest.GET, SupportRequest.POST}
    )
    public DocsMethodConfig method2() {
        DocsMethodConfig docsMethodConfig = new DocsMethodConfig();
        docsMethodConfig.setMethodParamType(MethodParamType.REQUEST_PARAM);
        docsMethodConfig.setRequestParams(Lists.newArrayList(
                DocsMethodParamConfig.builder().defValue(new BigDecimal(22)).paramName("id").paramType("BigDecimal").required(true).build()
        ));
        docsMethodConfig.setResponseBody(Response.ok("success"));
        return docsMethodConfig;
    }

    @DocsApiMethod(
            nodeName = "method22.md",
            urlSuffix = "/method22",
            methodAuthor = "hznijianfeng",
            methodDesc = "测试方法",
            supportRequest = {SupportRequest.GET, SupportRequest.POST}
    )
    public DocsMethodConfig method22() {
        DocsMethodConfig docsMethodConfig = new DocsMethodConfig();
        docsMethodConfig.setMethodParamType(MethodParamType.REQUEST_BODY);
        docsMethodConfig.setRequestBody(Lists.newArrayList(Lists.newArrayList(23, 45), 2, new BigDecimal(88), 4));
        docsMethodConfig.setResponseBody(Response.ok(new BigDecimal(29)));
        return docsMethodConfig;
    }

}
