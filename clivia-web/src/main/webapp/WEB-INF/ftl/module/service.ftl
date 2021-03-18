package ${data.pkg};
<#if data.execute?contains("query") || data.execute?contains("user")>

import com.alibaba.fastjson.JSONObject;
</#if>

public interface ${data.name}Service {
<#if data.execute?contains("query")>

    JSONObject query(<#if data.searchArg??>${data.searchArg?keep_before_last(",")}</#if>);
</#if>
<#if data.execute?contains("user")>

    JSONObject user(<#if data.searchArg??>${data.searchArg?keep_before_last(",")}</#if>);
</#if>
<#if data.execute?contains("save")>

    void save(${data.name}Model ${data.lowerName});
</#if>
<#if data.execute?contains("delete")>

    void delete(String id);
</#if>
}
