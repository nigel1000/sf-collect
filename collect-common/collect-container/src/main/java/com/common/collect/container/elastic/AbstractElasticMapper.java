package com.common.collect.container.elastic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.api.page.PageParam;
import com.common.collect.api.page.PageResult;
import com.common.collect.util.ConvertUtil;
import com.common.collect.util.EmptyUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by hznijianfeng on 2019/1/25.
 */

@Slf4j
public abstract class AbstractElasticMapper<T> implements IElasticMapper<T>, IElasticConfig {

    @Setter
    @Getter
    private boolean mapUnderLineCamelCaseConvert = true;

    @Override
    public Boolean index(Object docId, @NonNull T obj) {
        IndexRequest indexRequest = new IndexRequest(getIndex(), getType());
        indexRequest.source(toJSONString(obj), XContentType.JSON);
        if (docId != null) {
            indexRequest.id(String.valueOf(docId));
        }
        return index(indexRequest);
    }

    // 父子操作 api
    // https://www.elastic.co/guide/en/elasticsearch/reference/6.5/parent-join.html
    @Override
    public Boolean index(Object docId, Object routing, @NonNull T obj) {
        if (routing == null) {
            return index(docId, obj);
        }
        IndexRequest indexRequest = new IndexRequest(getIndex(), getType());
        indexRequest.source(toJSONString(obj), XContentType.JSON);
        indexRequest.routing(String.valueOf(routing));
        if (docId != null) {
            indexRequest.id(String.valueOf(docId));
        }
        return index(indexRequest);
    }

    private Boolean index(IndexRequest indexRequest) {
        try {
            indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
            IndexResponse indexResponse = getElasticClient().index(indexRequest, RequestOptions.DEFAULT);
            return indexResponse.getResult().equals(DocWriteResponse.Result.CREATED);
        } catch (Exception e) {
            throw UnifiedException.gen("es 插入文档失败:" + toJSONString(indexRequest.sourceAsMap()), e);
        }
    }

    @Override
    public Boolean update(@NonNull Object docId, @NonNull String field, @NonNull Object object) {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), getType(), String.valueOf(docId));
        Map<String, Object> doc = Maps.newHashMap();
        doc.put(field, object);
        updateRequest.doc(toJSONString(doc), XContentType.JSON);
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
        try {
            UpdateResponse updateResponse = getElasticClient().update(updateRequest, RequestOptions.DEFAULT);
            return updateResponse.getResult().equals(DocWriteResponse.Result.UPDATED);
        } catch (Exception e) {
            log.warn("docId:[{}],field:[{}],object:[{}]", docId, field, object);
            throw UnifiedException.gen("es 更新文档失败:" + toJSONString(doc), e);
        }
    }

    @Override
    public Boolean update(@NonNull Object docId, @NonNull T update) {
        UpdateRequest updateRequest = new UpdateRequest(getIndex(), getType(), String.valueOf(docId));
        updateRequest.doc(toJSONString(update), XContentType.JSON);
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);

        try {
            UpdateResponse updateResponse = getElasticClient().update(updateRequest, RequestOptions.DEFAULT);
            return updateResponse.getResult().equals(DocWriteResponse.Result.UPDATED);
        } catch (Exception e) {
            log.warn("docId:[{}],update:[{}]", docId, update);
            throw UnifiedException.gen("es 更新文档失败:" + toJSONString(update), e);
        }
    }

    @Override
    public Boolean delete(@NonNull Object docId) {
        DeleteRequest deleteRequest = new DeleteRequest(getIndex(), getType(), String.valueOf(docId));
        deleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
        try {
            DeleteResponse deleteResponse = getElasticClient().delete(deleteRequest, RequestOptions.DEFAULT);
            return deleteResponse.getResult().equals(DocWriteResponse.Result.DELETED);
        } catch (Exception e) {
            throw UnifiedException.gen("es 删除文档失败", e);
        }
    }

    @Override
    public Boolean delete(@NonNull Object docId, Object routing) {
        if(routing == null){
            return delete(docId);
        }
        DeleteRequest deleteRequest = new DeleteRequest(getIndex(), getType(), String.valueOf(docId));
        deleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
        deleteRequest.routing(routing.toString());
        try {
            DeleteResponse deleteResponse = getElasticClient().delete(deleteRequest, RequestOptions.DEFAULT);
            return deleteResponse.getResult().equals(DocWriteResponse.Result.DELETED);
        } catch (Exception e) {
            throw UnifiedException.gen("es 删除文档失败", e);
        }
    }

    @Override
    public T get(Object docId) {
        GetRequest getRequest = new GetRequest(getIndex(), getType(), String.valueOf(docId));
        try {
            GetResponse getResponse = getElasticClient().get(getRequest, RequestOptions.DEFAULT);
            return parse(toJSONString(getResponse.getSourceAsMap()), getIndexClass());
        } catch (Exception e) {
            throw UnifiedException.gen("es 获取文档失败，文档:id" + String.valueOf(docId), e);
        }
    }

    @Override
    public PageResult<T> query(@NonNull QueryBuilder queryBuilders, PageParam pageParam,
                               List<SortBuilder> sortBuilders) {
        return query(queryBuilders, null, pageParam, sortBuilders);
    }

    @Override
    public PageResult<T> query(@NonNull QueryBuilder queryBuilders, String[] fetchSource, PageParam pageParam,
                               List<SortBuilder> sortBuilders) {
        return query(queryBuilders, fetchSource, null, pageParam, sortBuilders, defaultResultMapping);
    }

    @Override
    public PageResult<T> query(@NonNull QueryBuilder queryBuilders, String[] fetchSource, String[] routing, PageParam pageParam,
                               List<SortBuilder> sortBuilders) {
        return query(queryBuilders, fetchSource, routing, pageParam, sortBuilders, defaultResultMapping);
    }

    @Override
    public PageResult<T> query(QueryBuilder queryBuilders, String[] fetchSource, String[] routing, PageParam pageParam, List<SortBuilder> sortBuilders,
                               Function<SearchResponse, List<T>> resultMapping) {

        if (resultMapping == null) {
            throw UnifiedException.gen("es 查询必须设置映射函数");
        }

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageParam.getOffset());
        sourceBuilder.size(pageParam.getLimit());
        sourceBuilder.query(queryBuilders);
        if (fetchSource != null) {
            sourceBuilder.fetchSource(fetchSource, null);
        }
        if (EmptyUtil.isNotEmpty(sortBuilders)) {
            for (SortBuilder sortBuilder : sortBuilders) {
                sourceBuilder.sort(sortBuilder);
            }
            sourceBuilder.sort(SortBuilders.fieldSort("_id").order(SortOrder.DESC));
        }

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(getIndex());
        searchRequest.types(getType());
        searchRequest.source(sourceBuilder);
        if (routing != null) {
            searchRequest.routing(routing);
        }

        log.debug("es 查询 json :\r\n{}\r\n", sourceBuilder.toString());
        try {
            SearchResponse searchResponse = getElasticClient().search(searchRequest, RequestOptions.DEFAULT);
            return PageResult.gen((int) searchResponse.getHits().getTotalHits(), resultMapping.apply(searchResponse), pageParam);
        } catch (Exception e) {
            log.warn("sourceBuilder:[{}]", toJSONString(sourceBuilder));
            throw UnifiedException.gen("es 查询失败", e);
        }
    }


    protected String toJSONString(Object object) {
        SerializeFilter[] serializeFilters = null;
        if (mapUnderLineCamelCaseConvert) {
            serializeFilters = new SerializeFilter[]{name2UnderLineFilter, valueFilter};
        }
        return JSON.toJSONString(object, serializeFilters, SerializerFeature.DisableCircularReferenceDetect);
    }

    protected T parse(String text, Class<T> clazz) {
        SerializeFilter[] serializeFilters = null;
        if (mapUnderLineCamelCaseConvert) {
            serializeFilters = new SerializeFilter[]{name2CamelCaseFilter, valueFilter};
        }
        return JSON.parseObject(
                JSON.toJSONString(JSON.parse(text), serializeFilters, SerializerFeature.DisableCircularReferenceDetect),
                clazz);
    }

    protected Function<SearchResponse, List<T>> defaultResultMapping = (response) -> {
        List<T> result = Lists.newArrayList();
        for (SearchHit searchHit : response.getHits()) {
            result.add(parse(toJSONString(searchHit.getSourceAsMap()), getIndexClass()));
        }
        return result;
    };

    protected NameFilter name2UnderLineFilter = (object, name, value) -> ConvertUtil.camel2Underline(name);

    protected NameFilter name2CamelCaseFilter = (object, name, value) -> ConvertUtil.underline2Camel(name);

    protected ValueFilter valueFilter = (object, name, value) -> value;

}
