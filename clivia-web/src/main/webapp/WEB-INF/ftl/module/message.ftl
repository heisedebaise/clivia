<#list data.columns as column>
${data.prefix}.${column.name}=${codec.unicode(column.explain)}
</#list>