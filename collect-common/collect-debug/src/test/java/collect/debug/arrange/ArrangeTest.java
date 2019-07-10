package collect.debug.arrange;

import collect.debug.arrange.demo.ProductContext;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.aops.LogConstant;
import com.common.collect.container.arrange.ArrangeContext;
import com.common.collect.container.arrange.ArrangeRetContext;
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

    private static String path;

    static {
        path = ArrangeTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "logs/arrange/";
        } else {
            path = path.substring(0, path.indexOf("target")) + "logs/arrange/";
        }
    }

    public static void main(String[] args) throws IOException {
        // 启动 spring 容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");
        // 加载配置文件
        ArrangeContext.load(new ClassPathResource("arrange/function-define.yml").getInputStream());
        ArrangeContext.downLoadConfig(path);
        // 定义第一个入参
        ProductContext param = new ProductContext();
        param.setGoodsId(5882654L);

        // 启动一个业务调用链并传入入参
        ArrangeRetContext context = ArrangeContext.runBiz("biz_queryProductAll", JsonUtil.bean2json(param));

        log.info("最后一个入参：" + LogConstant.getObjString(context.getLastArg()));
        // 返回最后一个入参
        ProductContext in = context.getByIndexFromMap(context.getInputMap().size(), context.getInputMap());
        log.info("最后一个入参：" + LogConstant.getObjString(in));

        log.info("最后一个返回：" + LogConstant.getObjString(context.getLastRet()));
        // 返回最后一个返回
        ProductContext out = context.getByIndexFromMap(context.getOutputMap().size(), context.getOutputMap());
        log.info("最后一个返回：" + LogConstant.getObjString(out));

        context = ArrangeContext.runBiz("biz_fillProductSkuAndSale", JsonUtil.bean2json(out));
        log.info("最后一个入参：" + LogConstant.getObjString(context.getLastArg()));
        log.info("最后一个返回：" + LogConstant.getObjString(context.getLastRet()));

    }

}
