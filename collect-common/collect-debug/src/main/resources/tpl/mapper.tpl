<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${daoPackagePath}.${className}${daoSuffixName}">

    <sql id="tb">
        `${tableName}`
    </sql>

    <sql id="cols_all">
    <#list fieldInfos as vo>
        `${vo.dbName}`<#if vo_has_next>,</#if>
    </#list>
    </sql>

    <sql id="vals_all">
    <#list fieldInfos as vo>
        <#if insertDate2Now?seq_contains(vo.javaName)>
        <if test="item.${vo.javaName} != null ">${"#"}{item.${vo.javaName}}<#if vo_has_next>,</#if></if>
        <if test="item.${vo.javaName} == null ">now()<#if vo_has_next>,</#if></if>
        <#else>
        ${"#"}{item.${vo.javaName}}<#if vo_has_next>,</#if>
        </#if>
    </#list>
    </sql>

    <sql id="set_dynamic">
    <#list fieldInfos as vo>
        <if test="item.${vo.javaName} !=null">`${vo.dbName}` = ${"#"}{item.${vo.javaName}},</if>
    </#list>
    </sql>

    <#if sqlIds?seq_contains("create")>
    <insert id="create" parameterType="${domainPackagePath}.${className}" keyProperty="item.id" useGeneratedKeys="true">
        INSERT INTO
        <include refid="tb"/>
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <include refid="cols_all"/>
        </trim>
        VALUES
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <include refid="vals_all"/>
        </trim>
    </insert>
    </#if>
    <#if sqlIds?seq_contains("creates")>

    <insert id="creates" parameterType="list">
        INSERT INTO
        <include refid="tb"/>
        (<include refid="cols_all"/>)
        VALUES
        <foreach collection="list" item="item" index="index" separator=",">
            (<include refid="vals_all"/>)
        </foreach>
    </insert>
    </#if>
    <#if sqlIds?seq_contains("load")>

    <select id="load" parameterType="long" resultType="${domainPackagePath}.${className}">
        SELECT
        <include refid="cols_all"/>
        FROM
        <include refid="tb"/>
        WHERE id = ${"#"}{id}
    </select>
    </#if>
    <#if sqlIds?seq_contains("loads")>

    <select id="loads" resultType="${domainPackagePath}.${className}" parameterType="list">
        SELECT
        <include refid="cols_all"/>
        FROM
        <include refid="tb"/>
        WHERE id IN
        <foreach collection="list" item="id" index="index" open="(" separator="," close=")">
            ${"#"}{id}
        </foreach>
    </select>
    </#if>
    <#if sqlIds?seq_contains("delete")>

    <delete id="delete" parameterType="long">
        DELETE FROM
        <include refid="tb"/>
        WHERE id = ${"#"}{id}
    </delete>
    </#if>
    <#if sqlIds?seq_contains("deletes")>

    <delete id="deletes" parameterType="list">
        DELETE FROM
        <include refid="tb"/>
        WHERE id IN
        <foreach collection="list" index="index" item="id" open="(" separator="," close=")">
            ${"#"}{id}
        </foreach>
    </delete>
    </#if>
    <#if sqlIds?seq_contains("update")>

    <update id="update" parameterType="${domainPackagePath}.${className}">
        UPDATE
        <include refid="tb"/>
        <set>
            <include refid="set_dynamic"/>
        </set>
        WHERE id=${"#"}{item.id}
    </update>
    </#if>

</mapper>
