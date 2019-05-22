package collect.debug.docs.demo;

import com.common.collect.api.Response;
import com.common.collect.container.docs.DocsApi;
import com.common.collect.container.docs.DocsApiMethod;
import com.common.collect.container.docs.DocsMethodConfig;
import com.common.collect.container.docs.DocsMethodParamConfig;
import com.common.collect.container.docs.MethodParamType;
import com.common.collect.container.docs.SupportRequest;
import com.google.common.collect.Lists;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@DocsApi(rootDirName = "DocsDemo1", urlPrefix = "/DocsDemo1")
public class DocsDemo1 {

    @DocsApiMethod(
            nodeName = "method1.md",
            urlSuffix = "/method1",
            methodAuthor = "hznijianfeng",
            methodDesc = "测试方法",
            supportRequest = {SupportRequest.GET, SupportRequest.POST}
    )
    public DocsMethodConfig method1() {
        DocsMethodConfig docsMethodConfig = new DocsMethodConfig();
        docsMethodConfig.setMethodParamType(MethodParamType.REQUEST_PARAM);
        docsMethodConfig.addRequestParams(DocsMethodParamConfig.builder().paramName("name").paramType("String").paramDesc("name").defValue("name").required(false).build());
        docsMethodConfig.addRequestParams(DocsMethodParamConfig.builder().paramName("id").paramType("Integer").paramDesc("id").defValue(2).required(true).build());
        docsMethodConfig.putResponseBody("返回成功", Response.ok(Lists.newArrayList(new RetDemo(), new RetDemo())));
        docsMethodConfig.putResponseBody("返回失败", Response.fail("操作失败"));
        return docsMethodConfig;
    }


}
