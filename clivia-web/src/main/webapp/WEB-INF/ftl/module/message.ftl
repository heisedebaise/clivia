<#list data.columns as column>
${data.beanName}.${column.name}=${codec.unicode(column.explain)}
</#list>