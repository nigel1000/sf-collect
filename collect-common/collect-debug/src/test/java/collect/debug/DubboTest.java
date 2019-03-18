package collect.debug;

import com.common.collect.api.excps.UnifiedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by hznijianfeng on 2019/1/17.
 */

@Slf4j
public class DubboTest {

    private static String zkAddress;
    private static String group;

    static {
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("app_env.properties");
        } catch (IOException e) {
            throw UnifiedException.gen("属性文件获取失败", e);
        }

        zkAddress = properties.getProperty("zk." + "testjd");
        group = "env_jd";

//        InvokerParam.serviceDubboIp = properties.getProperty("env_jd" + ".ip.hz");
//        InvokerParam.serviceDubboPort = properties.getProperty("env_jd" + ".dubbo.port");

    }

    public static void main(String[] args) {

        DemoFacade();

        System.exit(0);
    }

    public static void DemoFacade() {
//        InvokerParam invokerParam = new InvokerParam();
//        invokerParam.setGroup(group);
//        invokerParam.setZkAddress(zkAddress);
//        invokerParam.setMockClass(DemoFacade.class);
//
//        DemoFacade demoFacade = InvokerFactory.getInstance(invokerParam);
//
//        Object result = demoFacade.getDemo();
//
//        log.info("result:{}", result);
    }

}
