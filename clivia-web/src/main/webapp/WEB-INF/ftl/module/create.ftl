<#function type ct key=0>
  <#local dn = " DEFAULT NULL"/>
  <#if key gt 0>
    <#local dn = " NOT NULL"/>
  </#if>
  <#if ct=="id">
    <#return "CHAR(36)"+dn/>
  </#if>
  <#if ct=="int">
    <#return "INT DEFAULT 0"/>
  </#if>
  <#if ct=="date">
    <#return "DATE"+dn/>
  </#if>
  <#if ct=="datetime">
    <#return "DATETIME"+dn/>
  </#if>
  <#if ct=="text">
    <#return "TEXT"+dn/>
  </#if>
  <#return "VARCHAR(255)"+dn/>
</#function>
<#assign key=0/>
DROP TABLE IF EXISTS t_${data.name};
CREATE TABLE t_${data.name}
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
<#list data.columns as column>
  c_${column.name} ${type(column.type, column.key)} COMMENT '${column.explain}',
  <#if column.key?? && column.key gt 0>
    <#assign key++/>
  </#if>
</#list>

  PRIMARY KEY pk(c_id) USING HASH<#if key gt 0>,</#if>
<#list data.columns as column>
  <#if column.key?? && column.key gt 0>
    <#assign key--/>
    <#if column.key==1>
  KEY k_${column.name}(c_${column.name}) USING HASH<#if key gt 0>,</#if>
    <#elseif column.key==2>
  UNIQUE KEY uk_${column.name}(c_${column.name}) USING HASH<#if key gt 0>,</#if>
    </#if>
  </#if>
</#list>
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;