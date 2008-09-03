<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Pastebin</title>
		<style type="text/css">
			@import "${contextPath}/css/structure.css";
			@import "${contextPath}/css/scheme.css";
			<#if snippet.syntaxLanguage != ''>
			@import "${contextPath}/css/SyntaxHighlighter.css";
			</#if>
		</style>
		<script type="text/javascript" src="${contextPath}/js/prototype-1.6.0.2.js"></script>
		<script type="text/javascript" src="${contextPath}/js/pastebin.js"></script>
		<script type="text/javascript" src="${contextPath}/js/syntax/shCore.js"></script>
		<#if snippet.syntaxLanguage == 'c++'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushCpp.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'c#'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushCSharp.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'css'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushCss.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'delphi'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushDelphi.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'html'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushXml.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'java'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushJava.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'javascript'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushJScript.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'php'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushPhp.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'python'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushPython.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'ruby'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushRuby.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'sql'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushSql.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'vb'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushVb.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'java'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushJava.js"></script>
		</#if>
		<#if snippet.syntaxLanguage == 'xml'>
		<script type="text/javascript" src="${contextPath}/js/syntax/shBrushXml.js"></script>
		</#if>
		</head>
		<body>
			<div id="main">
				<div id="top">
					<div class="pad">
						<h1><a href="${contextPath}/">Pastebin</a></h1>
					</div>
				</div>
				<div id="contentwrap" class="equalize">
					<div id="content">
						<div class="pad">
							<h3>Posted by ${snippet.owner} on ${snippet.created?datetime}</h3>
							<pre name="code" class="${snippet.syntaxLanguage}:nocontrols">${snippet.contents}</pre>
						</div>
					</div>
				</div>
				<div id="right" class="equalize">
					<div class="pad">
						<#include "./overview.ftl">
					</div>
				</div>
				<div id="foot">
					Copyright &copy; 2008 Damian Carrillo - 
					<a href="http://agave-web-framework.googlecode.com/">Agave Web Framework</a>
				</div>
			</div>
		</div>
		<script language="javascript">
			dp.SyntaxHighlighter.HighlightAll('code');
		</script>
	</body>
</html>
