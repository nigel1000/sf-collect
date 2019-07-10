
biz_define:
<#if bizDefines??>
    <#list bizDefines?keys as key>
  ${key!}:
    arranges:
        <#list bizDefines[key].bizDefineModel.arranges as arrange>
    - type: ${arrange.type!}
      name: ${arrange.name!}
      input_type: ${arrange.inputType!}
      input_mappings:
            <#list arrange.inputMappings as mapping>
      - ${mapping!}
            </#list>
      input_excludes:
            <#list arrange.inputExcludes as exclude>
      - ${exclude!}
            </#list>
        </#list>

    </#list>
</#if>


