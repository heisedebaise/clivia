<!DOCTYPE html><#t>
<html><#t>
  <head><#t>
    <meta charset="UTF-8"><#t>
    <link rel="icon" href="/favicon.ico"><#t>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"><#t>
    <title>${data.title}</title><#t>
    <style type="text/css"><#t>
      html,body {<#t>
        margin: 0;<#t>
        padding: 0;<#t>
      }<#t>
      .bold {<#t>
        font-weight: bold;<#t>
      }<#t>
      .italic {<#t>
        font-style: italic;<#t>
      }<#t>
      .underline {<#t>
        text-decoration: underline;<#t>
      }<#t>
      .linethrough {<#t>
        text-decoration: line-through;<#t>
      }<#t>
      .divider {<#t>
        padding: 0 0.5rem;<#t>
        border-top: 1px solid #e5e5e5;<#t>
      }<#t>
    </style><#t>
  </head><#t>
  <body><#t>
  <#list data.lines as line>
    <#if line.tag=="h1">
      <@text "h1" line.texts/>
    <#elseif line.tag=="h2">
      <@text "h2" line.texts/>
    <#elseif line.tag=="h3">
      <@text "h3" line.texts/>
    <#elseif line.tag=="text">
      <@text "p" line.texts/>
    <#elseif line.tag=="divider">
      <div class="divider"><#t>
        <div></div><#t>
      </div><#t>
    </#if>
  </#list>
  </body><#t>
</html><#t>
<#macro text tag texts>
  <${tag}><#t>
    <#list texts as text>
      <span class="${text.style}">${text.text}</span><#t>
    </#list>
  </${tag}><#t>
</#macro>
