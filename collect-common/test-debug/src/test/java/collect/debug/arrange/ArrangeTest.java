package collect.debug.arrange;

import collect.debug.arrange.demo.ProductContext;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.FileUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import com.common.collect.test.debug.arrange.ArrangeContext;
import com.common.collect.test.debug.arrange.ArrangeRetContext;
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
        ArrangeContext.load(FileUtil.getString(new ClassPathResource("arrange/function-define.yml").getFile()));
        // 定义第一个入参
        ProductContext param = new ProductContext();
        param.setGoodsId(5882654L);

        // 启动一个业务调用链并传入入参
        ArrangeRetContext context = ArrangeContext.runBiz("biz_queryProductAll", JsonUtil.bean2json(param));

        log.info("最后一个入参：{}", JsonUtil.bean2json(context.getLastArg()));
        // 返回最后一个入参
        if (EmptyUtil.isNotEmpty(context.getInputMap())) {
            ProductContext in = context.getByIndexFromMap(context.getInputMap().size(), context.getInputMap());
            log.info("最后一个入参：{}", in);
        }

        log.info("最后一个返回：{}", JsonUtil.bean2json(context.getLastRet()));
        // 返回最后一个返回
        if (EmptyUtil.isNotEmpty(context.getOutputMap())) {
            ProductContext out = context.getByIndexFromMap(context.getOutputMap().size(), context.getOutputMap());
            log.info("最后一个返回：{}", out);
        }

        context = ArrangeContext.runBiz("biz_fillProductSkuAndSale", JsonUtil.bean2json(context.getLastRet()));
        log.info("最后一个入参：{}", JsonUtil.bean2json(context.getLastArg()));
        log.info("最后一个返回：{}", JsonUtil.bean2json(context.getLastRet()));

    }

}
