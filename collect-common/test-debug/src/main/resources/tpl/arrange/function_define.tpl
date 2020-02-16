
functions_define:
<#if functionDefines??>
    <#list functionDefines?keys as key>
  ${key!}:
    function_in_keep: ${functionDefines[key].functionInKeep?c}
    function_out_keep: ${functionDefines[key].functionOutKeep?c}
    function_clazz_type: ${functionDefines[key].functionClazzType!}
    function_clazz_key: ${functionDefines[key].functionClazzKey!}
    function_method_name: ${functionDefines[key].functionMethodName!}
    function_method_type: ${functionDefines[key].functionMethodType!}
    function_method_in_clazz: ${functionDefines[key].functionMethodInClazz!}
    function_method_in_fields:
        <#list functionDefines[key].functionMethodInFields as functionMethodInField>
    - ${functionMethodInField!}
        </#list>
    function_method_out_from: ${functionDefines[key].functionMethodOutFrom!}
    function_method_out_fields:
        <#list functionDefines[key].functionMethodOutFields as functionMethodOutField>
    - ${functionMethodOutField!}
        </#list>

    </#list>
</#if>

