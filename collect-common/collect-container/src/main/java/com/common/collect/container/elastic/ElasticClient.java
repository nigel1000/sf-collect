package com.common.collect.container.elastic;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.SplitUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class ElasticClient {

    private boolean enableClientCache = true;
    private Map<String, RestHighLevelClient> clientMap = new ConcurrentHashMap<>();

    // test
    private String host;
    // 2800
    private String port;
    // http
    private String schema;

    private int CONNECT_TIME_OUT = 10 * 1000;
    private int SOCKET_TIME_OUT = 10 * 1000;
    private int CONNECTION_REQUEST_TIME_OUT = 10 * 1000;

    private int MAX_CONNECT_NUM = 100;
    private int MAX_CONNECT_PER_ROUTE = 100;

    public ElasticClient() {
    }

    public ElasticClient(String host, String port, String schema) {
        this.host = host;
        this.port = port;
        this.schema = schema;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        String key = host + port + schema;

        if (enableClientCache) {
            RestHighLevelClient clientCache = clientMap.get(key);
            if (clientCache != null) {
                return clientCache;
            }
        }

        List<String> hosts = SplitUtil.split2StringByComma(host.trim());
        HttpHost[] httpHosts = new HttpHost[hosts.size()];
        int count = 0;
        for (String h : hosts) {
            httpHosts[count] = new HttpHost(h, Integer.valueOf(port), schema);
            count++;
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);
        // 主要关于异步 httpclient 的连接延时配置
        builder.setRequestConfigCallback((t) -> {
            t.setConnectTimeout(CONNECT_TIME_OUT);
            t.setSocketTimeout(SOCKET_TIME_OUT);
            t.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
            return t;
        });
        // 主要关于异步 httpclient 的连接数配置
        builder.setHttpClientConfigCallback((t) -> {
            t.setMaxConnTotal(MAX_CONNECT_NUM);
            t.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);
            return t;
        });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        try {
            if (!client.ping(RequestOptions.DEFAULT)) {
                throw UnifiedException.gen("RestHighLevelClient ping 失败");
            }
        } catch (Exception ex) {
            throw UnifiedException.gen("RestHighLevelClient 初始化失败", ex);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clearCache(key);
        }));

        if (enableClientCache) {
            clientMap.put(key, client);
        }

        return client;
    }

    public void destroy(Closeable client) {
        try {
            client.close();
            client = null;
        } catch (IOException e) {
            log.warn("es 客户端关闭失败", e);
            client = null;
        }
    }

    public void clearCache(String key) {
        if (EmptyUtil.isBlank(key)) {
            return;
        }
        RestHighLevelClient client = clientMap.get(key);
        destroy(client);
        clientMap.remove(key);
    }

}
