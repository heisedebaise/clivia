(this.webpackJsonpui=this.webpackJsonpui||[]).push([[0],{133:function(e,t,n){},153:function(e,t,n){},235:function(e,t,n){},237:function(e,t,n){"use strict";n.r(t);var s=n(6),a=n(0),r=n.n(a),i=n(24),c=n.n(i),o=(n(133),n(70),n(40)),l=(n(101),n(48)),d=(n(136),n(19)),p=(n(138),n(69)),j=(n(140),n(38)),u=n(99),h=n(32),m=n(35),b=n(34),g=n(127),O=(n(143),n(68)),f=function(e,t){return v(e,t).then((function(e){return null===e?null:0===e.code?(e.message&&O.b.success(e.message),e.data):(O.b.warn("["+e.code+"]"+e.message),null)}))},v=function e(t,n){return fetch(""+t,{method:"POST",headers:x(),body:JSON.stringify(n)}).then((function(t){return e.loader&&e.loader.setState({loading:!1}),t.ok?t.json():(O.b.warn("["+t.status+"]"+t.statusText),null)}))},x=function(){var e={"Content-Type":"application/json"};return y(e,!0),e},y=function(e,t){t&&v.loader&&v.loader.setState({loading:!0});var n=localStorage.getItem("photon-session-id");if(!n){for(n="";n.length<64;)n+=Math.random().toString(36).substring(2);n=n.substring(0,64),localStorage.setItem("photon-session-id",n)}e["photon-session-id"]=n},S=function(e){Object(m.a)(n,e);var t=Object(b.a)(n);function n(){var e;Object(h.a)(this,n);for(var a=arguments.length,r=new Array(a),i=0;i<a;i++)r[i]=arguments[i];return(e=t.call.apply(t,[this].concat(r))).request="const post = (url, body) => fetch(url, {\n    method: 'POST',\n    headers: header(),\n    body: JSON.stringify(body)\n}).then(response => {\n    if (response.ok) return response.json();\n\n    message.warn('[' + response.status + ']' + response.statusText);\n\n    return null;\n});\n\nconst header = () => {\n    let header = {\n        'Content-Type': 'application/json'\n    };\n    psid(header, true);\n\n    return header;\n}\n\nconst psid = (header) => {\n    let psid = localStorage.getItem('photon-session-id');\n    if (!psid) {\n        psid = '';\n        while (psid.length < 64) psid += Math.random().toString(36).substring(2);\n        psid = psid.substring(0, 64);\n        localStorage.setItem('photon-session-id', psid);\n    }\n    header['photon-session-id'] = psid;\n}",e.response='{\n    "code": "\u7f16\u7801\uff0c0\u8868\u793a\u6210\u529f\uff0c\u5927\u4e8e0\u8868\u793a\u5931\u8d25\u3002",\n    "data": "\u6570\u636e\uff0ccode===0\u65f6\u8fd4\u56de\u3002",\n    "message": "\u5931\u8d25\u4fe1\u606f\u8bf4\u660e\uff0ccode>0\u65f6\u8fd4\u56de\u3002"\n}',e.render=function(){return Object(s.jsxs)(o.b,{direction:"vertical",style:{width:"100%"},children:[Object(s.jsx)("div",{children:"\u53c2\u8003\u793a\u4f8b"}),Object(s.jsx)("pre",{children:e.request}),Object(s.jsx)("div",{children:"\u8fd4\u56de\u7ed3\u679c"}),Object(s.jsx)("pre",{children:e.response})]})},e}return n}(r.a.Component),N=(n(146),n(57)),w=(n(239),n(56)),I=n(122),k=n.n(I),C=(n(153),function(e){Object(m.a)(n,e);var t=Object(b.a)(n);function n(e){var a;Object(h.a)(this,n),(a=t.call(this,e)).componentDidMount=function(){a.change()},a.render=function(){return Object(s.jsxs)(o.b,{direction:"vertical",children:[Object(s.jsxs)(w.a,{direction:"vertical",current:6,children:[Object(s.jsx)(w.a.Step,{title:"\u6dfb\u52a0\u7b7e\u540d\u65f6\u95f4\u6233\u53c2\u6570",description:"\u83b7\u53d6\u5f53\u524d\u65f6\u95f4\u6233\uff0c\u7cbe\u786e\u5230\u6beb\u79d2\uff0c\u53c2\u6570\u540d\u4e3a\uff1asign-time\u3002",icon:1}),Object(s.jsx)(w.a.Step,{title:"\u53c2\u6570\u540d\u6392\u5e8f",description:"\u5c06\u6240\u6709\u53c2\u6570\u6839\u636e\u53c2\u6570\u540dASCII\u7801\u5347\u5e8f\u6392\u5217\u3002",icon:2}),Object(s.jsx)(w.a.Step,{title:"\u62fc\u63a5\u53c2\u6570",description:"\u7528\u7b49\u53f7'='\u62fc\u63a5\u53c2\u6570\u540d\u4e0e\u503c\uff0c\u5e76\u7528'&'\u7b26\u53f7\u8fde\u63a5\uff0c\u5982\uff1aa=1&b=2&c=3&sign-time=1234567890123",icon:3}),Object(s.jsx)(w.a.Step,{title:"\u6dfb\u52a0\u5bc6\u94a5",description:"\u6dfb\u52a0'&secret={secret value}'",icon:4}),Object(s.jsx)(w.a.Step,{title:"\u8ba1\u7b97MD5\u503c",description:"\u8ba1\u7b97\u5b57\u7b26\u4e32\u7684MD5\u503c\uff0c\u8f6c\u5316\u4e3a\u5c0f\u5199\u5b57\u6bcd\uff0c\u5373\u4e3a\u7b7e\u540d\u503c\uff0c\u7b7e\u540d\u53c2\u6570\u540d\u4e3asign\u3002",icon:5})]}),Object(s.jsx)("div",{children:"\u793a\u4f8b"}),Object(s.jsxs)("div",{className:"sign-form",children:[Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:"\u53c2\u6570\u540d"}),Object(s.jsx)("div",{className:"sign-form-value",children:"\u53c2\u6570\u503c"})]}),Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:Object(s.jsx)(N.a,{id:"name-1",onChange:a.change,defaultValue:"a"})}),Object(s.jsx)("div",{className:"sign-form-value",children:Object(s.jsx)(N.a,{id:"value-1",onChange:a.change,defaultValue:"1"})})]}),Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:Object(s.jsx)(N.a,{id:"name-2",onChange:a.change,defaultValue:"b"})}),Object(s.jsx)("div",{className:"sign-form-value",children:Object(s.jsx)(N.a,{id:"value-2",onChange:a.change,defaultValue:"2"})})]}),Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:Object(s.jsx)(N.a,{id:"name-3",onChange:a.change,defaultValue:"c"})}),Object(s.jsx)("div",{className:"sign-form-value",children:Object(s.jsx)(N.a,{id:"value-3",onChange:a.change,defaultValue:"3"})})]}),Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:"sign-time"}),Object(s.jsx)("div",{className:"sign-form-value",children:a.state.time})]}),Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:"secret"}),Object(s.jsx)("div",{className:"sign-form-value",children:a.state.secret})]}),Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:"\u7b7e\u540d\u660e\u6587"}),Object(s.jsx)("div",{className:"sign-form-value sign-form-plain",children:a.state.plain})]}),Object(s.jsxs)("div",{className:"sign-form-line",children:[Object(s.jsx)("div",{className:"sign-form-label",children:"sign"}),Object(s.jsx)("div",{className:"sign-form-value",children:a.state.sign})]})]})]})},a.change=function(){for(var e=[],t=1;t<=3;t++)e.push(document.querySelector("#name-"+t).value+"="+document.querySelector("#value-"+t).value);var n=(new Date).getTime();e.push("sign-time="+n),e.sort(),e.push("secret="+a.state.secret);var s=e.join("&");a.setState({time:n,plain:s,sign:k()(s)})};for(var r="";r.length<32;)r+=Math.random().toString(36).substring(2);return r=r.substring(0,32),a.state={time:"",secret:r,plain:"",sign:""},a}return n}(r.a.Component)),q=(n(238),n(126)),T=(n(103),n(37)),M=n(67),D=n(42),E=function(e){Object(m.a)(n,e);var t=Object(b.a)(n);function n(){var e;Object(h.a)(this,n);for(var a=arguments.length,r=new Array(a),i=0;i<a;i++)r[i]=arguments[i];return(e=t.call.apply(t,[this].concat(r))).render=function(){return Object(s.jsx)(q.a,{title:function(){return Object(s.jsx)("div",{className:"api-title",children:e.props.header?"\u5934\u4fe1\u606f":"\u53c2\u6570"})},columns:[{title:e.props.header?"\u5934\u540d\u79f0":"\u53c2\u6570\u540d",dataIndex:"name",key:"name"},{title:"\u7c7b\u578b",dataIndex:"type",key:"type"},{title:"\u5fc5\u987b",dataIndex:"require",key:"require",render:function(e){return e?Object(s.jsx)(M.a,{}):Object(s.jsx)(D.a,{})}},{title:"\u8bf4\u660e",dataIndex:"description",key:"description"}],dataSource:e.props.data,rowKey:"name",pagination:!1,locale:{emptyText:Object(s.jsx)(T.a,{image:T.a.PRESENTED_IMAGE_SIMPLE,description:e.props.header?"\u65e0\u9700\u5934\u4fe1\u606f":"\u65e0\u9700\u53c2\u6570"})}})},e}return n}(r.a.Component),A=(n(233),n(98)),B=function(e){Object(m.a)(n,e);var t=Object(b.a)(n);function n(){var e;Object(h.a)(this,n);for(var a=arguments.length,r=new Array(a),i=0;i<a;i++)r[i]=arguments[i];return(e=t.call.apply(t,[this].concat(r))).responseFile='{\n    "path": "\u6587\u4ef6URI\u5730\u5740",\n    "fileName": "\u539f\u6587\u4ef6\u540d",\n    "fileSize": "\u6587\u4ef6\u5927\u5c0f",\n    "success": "true-\u6210\u529f\uff1bfalse-\u5931\u8d25"\n}',e.responseBase64='{\n    "code": 0,\n    "data": {\n        "path": "\u6587\u4ef6URI\u5730\u5740",\n        "fileName": "\u539f\u6587\u4ef6\u540d",\n        "fileSize": "\u6587\u4ef6\u5927\u5c0f",\n        "success": "true-\u6210\u529f\uff1bfalse-\u5931\u8d25"\n    }\n}',e.render=function(){return Object(s.jsxs)(o.b,{direction:"vertical",style:{width:"100%"},children:[Object(s.jsx)(A.a,{dashed:!0,children:"\u6587\u4ef6\u65b9\u5f0f\u4e0a\u4f20"}),Object(s.jsx)(l.a,{type:"info",message:"\u63a5\u53e3\u5730\u5740\uff1a"+e.props.url+"/photon/ctrl-http/upload"}),Object(s.jsx)(E,{header:!0,data:e.props.meta.headers}),Object(s.jsx)(E,{header:!1,data:[{name:e.props.meta.upload,type:"file",require:!0,description:"\u4e0a\u4f20\u6587\u4ef6\u3002"}]}),Object(s.jsx)("div",{children:"\u8fd4\u56de\u7ed3\u679c"}),Object(s.jsx)("pre",{children:e.responseFile}),Object(s.jsx)(A.a,{dashed:!0,children:"Base64\u65b9\u5f0f\u4e0a\u4f20"}),Object(s.jsx)(l.a,{type:"info",message:"\u63a5\u53e3\u5730\u5740\uff1a"+e.props.url+"/photon/ctrl/upload"}),Object(s.jsx)(E,{header:!0,data:e.props.meta.headers}),Object(s.jsx)(E,{header:!1,data:[{name:"name",type:"string",require:!0,description:"\u56fa\u5b9a\u4e3a\uff1a"+e.props.meta.upload+"\u3002"},{name:"contentType",type:"string",require:!0,description:"\u6587\u4ef6\u683c\u5f0f\uff0c\u5982\uff1aimage/jpeg\u3002"},{name:"fileName",type:"string",require:!0,description:"\u539f\u6587\u4ef6\u540d\u3002"},{name:"base64",type:"string",require:!0,description:"BASE64\u7f16\u7801\u540e\u7684\u6587\u4ef6\u5185\u5bb9\uff0c\u4e0d\u5305\u542bContent-Type\u3002"}]}),Object(s.jsx)("div",{children:"\u8fd4\u56de\u7ed3\u679c"}),Object(s.jsx)("pre",{children:e.responseBase64}),Object(s.jsx)(A.a,{dashed:!0,children:"cURL\u65b9\u5f0f\u4e0a\u4f20"}),Object(s.jsx)(l.a,{type:"info",message:"\u63a5\u53e3\u5730\u5740\uff1a"+e.props.url+"/photon/ctrl-http/upload"}),Object(s.jsx)("div",{children:"\u53d1\u9001"}),Object(s.jsxs)("pre",{children:["curl -H 'photon-session-id: ",localStorage.getItem("photon-session-id"),"' -F '",e.props.meta.upload,"=@/path/to/file' ",e.props.url,"/photon/ctrl-http/upload"]}),Object(s.jsx)("div",{children:"\u8fd4\u56de\u7ed3\u679c"}),Object(s.jsx)("pre",{children:e.responseFile})]})},e}return n}(r.a.Component),P=function(e){Object(m.a)(n,e);var t=Object(b.a)(n);function n(){var e;Object(h.a)(this,n);for(var a=arguments.length,r=new Array(a),i=0;i<a;i++)r[i]=arguments[i];return(e=t.call.apply(t,[this].concat(r))).render=function(){return Object(s.jsxs)(o.b,{direction:"vertical",style:{width:"100%"},children:[Object(s.jsx)(l.a,{type:"info",message:"\u63a5\u53e3\u5730\u5740\uff1a"+e.props.url+"/keyvalue/object"}),Object(s.jsx)(E,{header:!0,data:[{name:"photon-session-id",type:"string",require:!0,description:"\u7528\u6237SESSION ID\u503c\uff0c\u5982\uff1a"+localStorage.getItem("photon-session-id")+"\u3002"}]}),Object(s.jsx)(E,{header:!1,data:[{name:"key",type:"string",require:!0,description:"\u56fa\u5b9a\u4e3a\uff1a\u3010"+e.props.meta.prefix+"\u3011\u3002"}]}),Object(s.jsx)("div",{children:"\u8fd4\u56de\u7ed3\u679c"}),Object(s.jsx)("pre",{children:"{\n"+e.props.meta.keys.map((function(e){return'    "'+e.key+'":"'+e.description+'",\n'})).join("")+"}"})]})},e}return n}(r.a.Component),V=(n(235),function(e){Object(m.a)(n,e);var t=Object(b.a)(n);function n(e){var a;return Object(h.a)(this,n),(a=t.call(this,e)).componentDidMount=function(){f("/keyvalue/object",{key:"setting.global."}).then((function(e){null!==e&&(document.title=e["setting.global.console.title"]||"Clivia API",a.setState({logo:e["setting.global.console.logo"]}))})),f("/user/sign").then((function(e){return a.setState({user:e})})),f("/api/get").then((function(e){if(null!==e){var t,n=Object(u.a)(e);try{for(n.s();!(t=n.n()).done;){var s,r=t.value,i=Object(u.a)(r.services);try{for(i.s();!(s=i.n()).done;){var c=s.value;c.psid&&(c.headers||(c.headers=[]),c.headers.push({name:"photon-session-id",type:"string",require:!0,description:"\u7528\u6237SESSION ID\u503c\uff0c\u5982\uff1a"+localStorage.getItem("photon-session-id")+"\u3002"})),c.sign&&(c.parameters||(c.parameters=[]),c.parameters.push({name:"sign-time",type:"long",description:"\u7b7e\u540d\u65f6\u95f4\u6233\uff0c\u7cbe\u786e\u5230\u6beb\u79d2\u3002"}),c.parameters.push({name:"sign",type:"string",description:"\u53c2\u6570\u7b7e\u540d\uff0c\u89c4\u5219\u53c2\u8003\u3010\u901a\u7528->\u53c2\u6570\u7b7e\u540d\u3011\u9875\u3002"})),r.model&&("model"===c.response?c.response=r.model:1===c.response.length&&"model"===c.response[0]?c.response="["+r.model+"]":"pagination"===c.response&&(c.parameters||(c.parameters=[]),c.parameters.push({name:"pageSize",type:"int",description:"\u6bcf\u9875\u663e\u793a\u8bb0\u5f55\u6570\uff0c\u9ed8\u8ba4\uff1a20\u3002"}),c.parameters.push({name:"pageNum",type:"int",description:"\u5f53\u524d\u663e\u793a\u9875\u6570\u3002"}),c.response='{\n    "count":"\u8bb0\u5f55\u603b\u6570\u3002",\n    "size":"\u6bcf\u9875\u6700\u5927\u663e\u793a\u8bb0\u5f55\u6570\u3002",\n    "number":"\u5f53\u524d\u663e\u793a\u9875\u6570\u3002",\n    "page":"\u603b\u9875\u6570\u3002",\n    "list":[\n        '+r.model.replace(/\n {4}/g,"\n            ").replace("\n}","\n        }")+"\n    ]\n}"))}}catch(j){i.e(j)}finally{i.f()}}}catch(j){n.e(j)}finally{n.f()}var o,l=[{name:"\u901a\u7528",services:[{name:"HTTP\u8bf7\u6c42",page:"request"},{name:"\u53c2\u6570\u7b7e\u540d",page:"sign"}]}],d=Object(u.a)(e);try{for(d.s();!(o=d.n()).done;){var p=o.value;l.push(p)}}catch(j){d.e(j)}finally{d.f()}a.setState({data:l},(function(){return a.show({key:"0-0-0"})}))}}))},a.render=function(){return Object(s.jsx)(d.a,{locale:g.a,children:Object(s.jsxs)(p.a,{style:{minHeight:"100vh"},children:[Object(s.jsxs)(p.a.Sider,{children:[Object(s.jsx)("div",{className:"api-logo",children:a.props.logo?[Object(s.jsx)("img",{src:(e=a.props.logo,""+e),alt:""},"img"),Object(s.jsx)("div",{},"div")]:null}),Object(s.jsx)("div",{className:"api-menu",children:Object(s.jsx)(j.a,{onClick:a.show,mode:"inline",theme:"dark",defaultOpenKeys:["0-0"],defaultSelectedKeys:[a.state.item.uri?"0-0":"0-0-0"],children:a.menu(a.state.data,"0")})}),Object(s.jsxs)("div",{className:"api-copyright",children:["clivia-api \xa9 ",(new Date).getFullYear()]})]}),Object(s.jsxs)(p.a,{children:[Object(s.jsx)(p.a.Header,{className:"api-header"}),Object(s.jsx)(p.a.Content,{children:Object(s.jsx)("div",{className:"api-body",children:a.body()})})]})]})});var e},a.menu=function(e,t){var n=[];if(0===e.length)return n;for(var r=0;r<e.length;r++){var i=t+"-"+r,c=e[r];c.services?n.push(Object(s.jsx)(j.a.SubMenu,{title:Object(s.jsx)("span",{children:c.name}),children:a.menu(c.services,i)},i)):(a.map[i]=c,n.push(Object(s.jsx)(j.a.Item,{children:c.name},i)))}return n},a.show=function(e){a.setState({item:a.map[e.key]})},a.body=function(){if(a.state.item.page)switch(a.state.item.page){case"request":return Object(s.jsx)(S,{});case"sign":return Object(s.jsx)(C,{});case"upload":return Object(s.jsx)(B,{url:a.url,meta:a.state.item});case"setting":return Object(s.jsx)(P,{url:a.url,meta:a.state.item});default:return Object(s.jsx)("div",{})}return Object(s.jsxs)(o.b,{direction:"vertical",style:{width:"100%"},children:[Object(s.jsx)(l.a,{type:"info",message:"\u63a5\u53e3\u5730\u5740\uff1a"+a.url+a.state.item.uri}),Object(s.jsx)(E,{header:!0,data:a.state.item.headers}),Object(s.jsx)(E,{header:!1,data:a.state.item.parameters}),Object(s.jsx)("div",{className:"api-response-title",children:"\u8fd4\u56de"}),Object(s.jsx)("pre",{children:a.state.item.response})]})},a.state={logo:"",user:{},data:[],item:{}},a.map={},a.url=window.location.href,a.url=a.url.substring(0,a.url.length-window.location.pathname.length),a}return n}(r.a.Component));Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));c.a.render(Object(s.jsx)(r.a.StrictMode,{children:Object(s.jsx)(V,{})}),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()})).catch((function(e){console.error(e.message)}))}},[[237,1,2]]]);
//# sourceMappingURL=main.5b3b84b4.chunk.js.map