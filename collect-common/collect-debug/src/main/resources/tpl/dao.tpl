package ${daoPackagePath};

import java.util.List;
import org.apache.ibatis.annotations.Param;
import ${domainPackagePath}.${className};

/**
 * Created by ${author} on ${date}.
 */

public interface ${className}${daoSuffixName} {
    <#if sqlIds?seq_contains("create")>

    Integer create(@Param("item") ${className} item);
    </#if>
    <#if sqlIds?seq_contains("creates")>

    Integer creates(List<${className}> items);
    </#if>
    <#if sqlIds?seq_contains("delete")>

    Integer delete(@Param("id") Long id);
    </#if>
    <#if sqlIds?seq_contains("deletes")>

    Integer deletes(List<Long> ids);
    </#if>
    <#if sqlIds?seq_contains("load")>

    ${className} load(@Param("id") Long id);
    </#if>
    <#if sqlIds?seq_contains("loads")>

    List<${className}> loads(List<Long> ids);
    </#if>
    <#if sqlIds?seq_contains("update")>

    Integer update(@Param("item") ${className} item);
    </#if>

}