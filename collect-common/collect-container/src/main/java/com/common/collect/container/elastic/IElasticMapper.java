package com.common.collect.container.elastic;

import com.common.collect.api.page.PageParam;
import com.common.collect.api.page.PageResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;
import java.util.function.Function;

public interface IElasticMapper<T> {

    // T 泛型的 class
    Class<T> getIndexClass();

    Boolean index(Object id, T t);

    Boolean index(Object id, Object parentId, T t);

    Boolean update(Object id, String field, Object object);

    Boolean update(Object id, T update);

    Boolean delete(Object id);

    Boolean delete(Object id, Object routing);

    T get(Object id);

    PageResult<T> query(QueryBuilder query, PageParam pageParam, List<SortBuilder> sortBuilders);

    PageResult<T> query(QueryBuilder query, String[] fetchSource, PageParam pageParam, List<SortBuilder> sortBuilders);

    PageResult<T> query(QueryBuilder query, String[] fetchSource, String[] routing, PageParam pageParam, List<SortBuilder> sortBuilders);

    PageResult<T> query(QueryBuilder query, String[] fetchSource, String[] routing, PageParam pageParam, List<SortBuilder> sortBuilders,
                        Function<SearchResponse, List<T>> resultMapping);

}
