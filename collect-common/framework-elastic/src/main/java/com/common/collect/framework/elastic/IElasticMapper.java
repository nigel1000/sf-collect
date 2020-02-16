package com.common.collect.framework.elastic;

import com.common.collect.lib.api.page.PageParam;
import com.common.collect.lib.api.page.PageResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;
import java.util.function.Function;

public interface IElasticMapper<T> {

    Boolean index(Object id, Object routing, T t);

    Boolean update(Object id, Object routing, T update);

    Boolean delete(Object id, Object routing);

    T get(Object id, Object routing);

    PageResult<T> paging(QueryBuilder query, String[] fetchSource, String[] routing, PageParam pageParam, List<SortBuilder> sortBuilders,
                        Function<SearchResponse, List<T>> resultMapping);


}
