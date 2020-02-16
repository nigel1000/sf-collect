package com.common.collect.framework.elastic;

import org.elasticsearch.client.RestHighLevelClient;

public interface IElasticConfig {

    RestHighLevelClient getElasticClient();

    String getIndex();

    default String getType() {
        return null;
    }

}
