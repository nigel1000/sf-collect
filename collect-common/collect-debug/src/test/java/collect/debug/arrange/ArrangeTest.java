package collect.debug.arrange;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.aops.LogConstant;
import com.common.collect.container.arrange.ArrangeContext;
import com.common.collect.container.arrange.ArrangeRetContext;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Created by nijianfeng on 2019/7/6.
 */

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
