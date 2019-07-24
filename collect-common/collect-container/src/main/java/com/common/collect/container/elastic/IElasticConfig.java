package com.common.collect.container.elastic;

import org.elasticsearch.client.RestHighLevelClient;

public interface IElasticConfig {

    RestHighLevelClient getElasticClient();

    String getIndex();

    String getType();

    default JoinField getJoinField() {
        return null;
    }

}
