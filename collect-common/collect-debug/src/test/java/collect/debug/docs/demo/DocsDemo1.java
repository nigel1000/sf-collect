package collect.debug.docs.demo;

import com.common.collect.api.Response;
import com.common.collect.container.docs.DocsApi;
import com.common.collect.container.docs.DocsApiMethod;
import com.common.collect.container.docs.DocsMethodConfig;
import com.common.collect.container.docs.DocsMethodParamConfig;
import com.common.collect.container.docs.MethodParamType;
import com.common.collect.container.docs.SupportRequest;
import com.common.collect.util.DateUtil;
import org.assertj.core.util.Lists;

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
        docsMethodConfig.setRequestParams(Lists.newArrayList(
                DocsMethodParamConfig.builder().defValue(2).paramName("id").paramType("Integer").paramDesc("id").required(true).build(),
                DocsMethodParamConfig.builder().defValue("name").paramName("name").paramType("String").paramDesc("name").required(false).build()
        ));
        docsMethodConfig.setResponseBody(Response.ok(DateUtil.now()));
        return docsMethodConfig;
    }


}
