<%@ include file="/WEB-INF/common.jsp" %>
<template:apply path="/WEB-INF/template.jsp">
  <template:supplyFragment name="sidebarHeader">
    <div id="header">
      <h1><fmt:message key="applicationName" /></h1>
    </div>
  </template:supplyFragment>
  <template:supplyFragment name="content">
    <p><fmt:message key="introduction" /></p>
    <h2><fmt:message key="getStarted" /></h2>
    <p><fmt:message key="supplyTheFollowingDetails" /></p>
    <form action="${contextPath}/create" id="createForm" method="post">
      <input type="hidden" name="snippetId" id="snippetId" />
      <table>
        <tbody>
          <tr>
            <td colspan="2">
              <textarea id="contents" name="contents" cols="80" rows="15"><fmt:message key="enterSnippetContents" /></textarea>
            </td>
          </tr>
          <tr>
            <td><label for="owner"><fmt:message key="ownerLabel" /></label></td>
            <td><input type="text" name="owner" size="40" /></td>
          </tr>
          <tr>
            <td><label for="syntaxLanguage"><fmt:message key="languageLabel" /></label></td>
            <td>
              <select id="syntaxLanguage" name="syntaxLanguage">
                <option value="" />
                <option value="sh_bison">Bison</option>
                <option value="sh_c">C</option>
                <option value="sh_cpp">C++</option>
                <option value="sh_csharp">C#</option>
                <option value="sh_changelog">Changelog</option>
                <option value="sh_css">CSS</option>
                <option value="sh_desktop">Desktop File</option>
                <option value="sh_diff">Diff</option>
                <option value="sh_flex">Flex</option>
                <option value="sh_glsl">GLSL</option>
                <option value="sh_haxe">Haxe</option>
                <option value="sh_html">HTML</option>
                <option value="sh_java">Java</option>
                <option value="sh_javascript">Javascript</option>
                <option value="sh_javascript_dom">Javascript w/ DOM</option>
                <option value="sh_latex">Latex</option>
                <option value="sh_ldap">LDAP</option>
                <option value="sh_log">Log File</option>
                <option value="sh_lsm">Linux Software Map</option>
                <option value="sh_m4">M4</option>
                <option value="sh_makefile">Makefile</option>
                <option value="sh_caml">OCaml</option>
                <option value="sh_oracle">Oracle</option>
                <option value="sh_pascal">Pascal</option>
                <option value="sh_perl">Perl</option>
                <option value="sh_php">PHP</option>
                <option value="sh_prolog">Prolog</option>
                <option value="sh_properties">Properties File</option>
                <option value="sh_python">Python</option>
                <option value="sh_ruby">Ruby</option>
                <option value="sh_scala">Scala</option>
                <option value="sh_sh">Shell Script</option>
                <option value="sh_slang">Slang</option>
                <option value="sh_sml">SML</option>
                <option value="sh_spec">Spec</option>
                <option value="sh_sql">SQL</option>
                <option value="sh_tcl">TCL</option>
                <option value="sh_xml">XML</option>
                <option value="sh_xorg">Xorg Configuration</option>
              </select>
            </td>
          </tr>
          <tr>
            <td><label for="expiration"><fmt:message key="expirationLabel" /></label></td>
            <td>
              <select id="expiration" name="expiration">
                <option value="hour"><fmt:message key="hourOption" /></option>
                <option value="day"><fmt:message key="dayOption" /></option>
                <option value="week"><fmt:message key="weekOption" /></option>
                <option value="month"><fmt:message key="monthOption" /></option>
                <option value="forever"><fmt:message key="neverOption" /></option>
              </select>
            </td>
          </tr>
          <tr>
            <td><label for="privateSnippet"><fmt:message key="privateLabel" /></label></td>
            <td><input type="checkbox" id="privateSnippet" name="privateSnippet" />
          </tr>
          <tr>
            <td colspan="2">
              <input type="submit" value="Create Snippet" />
            </td>
          </tr>
        </tbody>
      </table>
    </form>
  </template:supplyFragment>
</template:apply>