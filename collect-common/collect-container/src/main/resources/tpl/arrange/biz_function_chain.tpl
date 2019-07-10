
biz_function_chains:
<#if bizFunctionChains??>
    <#list bizFunctionChains?keys as key>
  ${key!}:
        <#list bizFunctionChains[key].bizFunctionChains as bizFunctionChain>
    ${bizFunctionChain.functionKey!}:
      method: ${bizFunctionChain.method!}
      functionInKeep: ${bizFunctionChain.functionInKeep?c}
      functionOutKeep: ${bizFunctionChain.functionOutKeep?c}
      functionMethodOutFromEnum: ${bizFunctionChain.functionMethodOutFromEnum}
      lastOutToCurrentIn:
            <#list bizFunctionChain.inOutMap?keys as inOut>
        - ${inOut!} -> ${bizFunctionChain.inOutMap[inOut]!}
            </#list>
        </#list>

    </#list>
</#if>