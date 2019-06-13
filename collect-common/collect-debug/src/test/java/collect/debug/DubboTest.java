package collect.debug;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.debug.dubbo.invoker.InvokerParam;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by hznijianfeng on 2019/1/17.
 */

@Slf4j
public class DubboTest {

    private static String zkAddress;
    private static String group;
    private static InvokerParam invokerParam;
    private static String classPath = DubboTest.class.getResource("/").getPath();

    private static void initInvokerParam() {
        invokerParam = new InvokerParam();
        invokerParam.setGroup(group);
        invokerParam.setZkAddress(zkAddress);
        Map<String, String> ipMap = new HashMap<>();
        // 预发ip转换
        ipMap.put("10.177.247.163", "10.194.69.249");
        ipMap.put("10.177.247.164", "10.194.69.250");
        invokerParam.setIpMap(ipMap);
    }

    static {
        Properties properties = new Properties();
        try {
            classPath = classPath + "app_machine.properties";
            properties.load(new FileInputStream(classPath));
        } catch (IOException e) {
            throw UnifiedException.gen("属性文件获取失败", e);
        }

        zkAddress = properties.getProperty("zk.test");
        group = "stable_master";

        initInvokerParam();
    }

    public static void main(String[] args) {

        groupListProvider();

        System.exit(0);
    }

    public static void groupListProvider() {
//        invokerParam.setMockClass(IGroupListProvider.class);
//
//        IGroupListProvider groupListProvider = InvokerFactory.getInstance(invokerParam);
//
//        GroupListContext groupListContext = new GroupListContext();
//        groupListContext.setPageNo(1);
//        groupListContext.setPageSize(3);
//        GroupListRetDto result = ResponseUtil.parse(groupListProvider.pagingGroupList(groupListContext));
//
//        log.info("result:{}", JsonUtil.bean2jsonPretty(result));
    }

}
