package com.common.collect.debug.dubbo;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.debug.telnet.TelnetUtil;
import com.common.collect.util.SplitUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.util.Iterator;
import java.util.List;

/**
 * Created by nijianfeng on 2018/10/20.
 */
@Slf4j
public class DubboUtil {

    public static GenericService getGenericService(@NonNull String className, String group, String version,
                                                   @NonNull String zkAddress, @NonNull Integer zkPort) {
        // 当前应用的信息
        ApplicationConfig application = new ApplicationConfig();
        application.setName("dubbo-invoke");
        // 获取注册中心信息
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(zkAddress);
        registryConfig.setPort(zkPort);
        registryConfig.setProtocol("zookeeper");
        // 获取服务的代理对象
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(application);
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setGeneric(true);
        referenceConfig.setInterface(className);
        referenceConfig.setRetries(0);
        referenceConfig.setTimeout(1000);
        if (StringUtils.isNotEmpty(group)) {
            referenceConfig.setGroup(group);
        }
        if (StringUtils.isNotEmpty(version)) {
            referenceConfig.setVersion(version);
        }
        // 调用远程服务
        // 缺省 ReferenceConfigCache 把相同服务 Group、接口、版本的 ReferenceConfig 认为是相同，缓存一份。
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        // cache.get方法中会缓存 Reference对象，并且调用ReferenceConfig.get方法启动ReferenceConfig
        return cache.get(referenceConfig);
    }

    public static URL getUrlByGenericService(@NonNull String className, String group, String version,
                                             @NonNull String zkAddress, @NonNull Integer zkPort) {

        getGenericService(className, group, version, zkAddress, zkPort);

        Iterator<Invoker<?>> iterator = DubboProtocol.getDubboProtocol().getInvokers().iterator();
        while (iterator.hasNext()) {
            Invoker invoker = iterator.next();
            return invoker.getUrl();
        }

        throw UnifiedException.gen("没有此服务接口 ");
    }

    public static URL getUrlByZookeeper(@NonNull String className, String group, String version,
                                        @NonNull String zkAddress, @NonNull Integer zkPort, @NonNull String methodName) {

        ZkClient zkClient = new ZkClient(zkAddress + ":" + zkPort, 10000);
        List<String> urls = zkClient.getChildren("/dubbo/" + className + "/providers");
        zkClient.close();
        for (String url : urls) {
            URL result = URL.valueOf(URL.decode(url));
            if (!group.equals(result.getParameter("group"))) {
                continue;
            }
            if (!version.equals(result.getParameter("version"))) {
                continue;
            }
            if (!SplitUtil.split2StringByComma(result.getParameter("methods")).contains(methodName)) {
                throw UnifiedException.gen(" 此服务接口没有此方法 ");
            }
            return result;
        }

        throw UnifiedException.gen(" 没有此服务接口 ");
    }

    public static String getResultByTelnet(@NonNull String className, @NonNull String methodName,
                                           @NonNull String methodParam, URL url) {
        try {
            // 发送命令
            StringBuilder command = new StringBuilder();
            command.append("invoke").append(" ");
            command.append(className).append(".");
            command.append(methodName).append("(");
            command.append(methodParam).append(")");
            return TelnetUtil.command(url.getHost(), url.getPort(), command.toString());
        } catch (Exception ex) {
            throw UnifiedException.gen("telnet 连接失败", ex);
        }
    }

}
