package com.common.collect.debug.dubbo.invoker;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/1/17.
 */
@Slf4j
public class InvokerFactory {

    private static final RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();

    public static <T> T getInstance(InvokerParam invokerParam) {
        String zkAddress = invokerParam.getZkAddress();
        URL registryUrl = URL.valueOf(zkAddress);
        registryUrl = registryUrl.addParameter(Constants.APPLICATION_KEY,invokerParam.getApplication());
        String registryUrlStr = registryUrl.toFullString();
        log.info("registryUrl:[{}]", registryUrlStr);
        Registry registry = ThreadLocalUtil.pull(registryUrlStr);
        if (registry == null) {
            registry = registryFactory.getRegistry(registryUrl);
            ThreadLocalUtil.push(registryUrlStr, registry);
        }
        if (registry == null) {
            throw UnifiedException.gen("没有找到 registry, registryUrl is " + registryUrl.toFullString());
        }
        URL url = new URL(invokerParam.getProtocol(), invokerParam.getHost(), invokerParam.getPort(), invokerParam.getMockClassName(), InvokerParam.getParams(invokerParam));
        String providerUrlStr = url.toFullString();
        log.info("url:[{}]", providerUrlStr);
        T retCache = ThreadLocalUtil.pull(providerUrlStr);
        if (retCache != null) {
            return retCache;
        }
        try {
            List<URL> providerUrls = registry.lookup(url);
            if (providerUrls != null && providerUrls.size() > 0) {
                for (int i = 0; i < providerUrls.size(); i++) {
                    URL providerUrl = providerUrls.get(i);
                    if (providerUrl.getProtocol().contains("jsonrpc")) {
                        continue;
                    }
                    log.info("before providerUrl:{}", providerUrl);
                    if (invokerParam.getIpMap().containsKey(providerUrl.getHost())) {
                        providerUrl = providerUrl.setAddress(invokerParam.getIpMap().get(providerUrl.getHost()));
                    }
                    log.info("after  providerUrl:{}", providerUrl);
                    try {
                        ApplicationConfig applicationConfig = new ApplicationConfig();
                        applicationConfig.setName(invokerParam.getApplication());
                        ReferenceConfig<T> referenceConfig = new ReferenceConfig<>();
                        Map<String, String> params = InvokerParam.getInvokerParam(providerUrl, invokerParam);
                        referenceConfig.setUrl(UrlUtils.parseURL(providerUrl.getAddress(), params).toString());
                        referenceConfig.setInterface(Class.forName(providerUrl.getServiceInterface()));
                        referenceConfig.setVersion(invokerParam.getVersion());
                        referenceConfig.setApplication(applicationConfig);
                        T ret = referenceConfig.get();
                        ThreadLocalUtil.push(providerUrlStr, ret);
                        return ret;
                    } catch (ClassNotFoundException ex) {
                        throw UnifiedException.gen("没有找到 class " + providerUrl.getServiceInterface());
                    } catch (Exception ex) {
                        log.error("无法访问 interface [{}] in {},尝试下一个", providerUrl.getServiceInterface(), providerUrl.getAddress(), ex);
                    }
                }
            }
        } catch (Exception ex) {
            throw UnifiedException.gen("registry.lookup 失败 ", ex);
        }
        throw UnifiedException.gen("没有找到匹配的接口类");
    }

}
