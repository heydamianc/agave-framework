<%@ include file="/WEB-INF/common.jsp" %>
<c:set var="title" scope="request">
  <fmt:message key="postedBy">
    <fmt:param>
      <c:choose>
        <c:when test="${empty snippet.owner}">
          <fmt:message key="anonymous" />
        </c:when>
        <c:otherwise>
          <c:out value="${snippet.owner}" />
        </c:otherwise>
      </c:choose>
    </fmt:param>
    <fmt:param>
      <fmt:formatDate type="both" value="${snippet.created}" />
    </fmt:param>
  </fmt:message>
</c:set>
<template:apply path="/WEB-INF/template.jsp">
  <template:supplyFragment name="title">
    <c:out value="${title}" /> -
  </template:supplyFragment>
  <template:supplyFragment name="css">
    <c:if test="${!empty snippet.syntaxLanguage}">
    @import "${contextPath}/css/syn-themes/sh_typical.min.css";
    </c:if>
  </template:supplyFragment>
  <template:supplyFragment name="js">
    <script type="text/javascript" src="${contextPath}/js/sh_main.min.js"></script>
    <c:if test="${!empty snippet.syntaxLanguage}">
      <script type="text/javascript" src="${contextPath}/js/syn/${snippet.syntaxLanguage}.min.js"></script>
    </c:if>
  </template:supplyFragment>
  <template:supplyFragment name="sidebarHeader">
    <div id="header">
      <h1><a href="${contextPath}/" title="<fmt:message key="backHome" />"><fmt:message key="applicationName" /></a></h1>
    </div>
  </template:supplyFragment>
  <template:supplyFragment name="content">
    <c:choose>
      <c:when test="${empty snippet}">
        <h3><fmt:message key="unableToFindSnippet" /></h3>
      </c:when>
      <c:otherwise>
        <h3><c:out value="${title}" /></h3>
        <c:if test="${snippet.privateSnippet}">
          <p class="warning"><fmt:message key="privateNotice" /></p>
        </c:if>
        <pre class="${snippet.syntaxLanguage}"><c:out value="${snippet.contents}" /></pre>
      </c:otherwise>
    </c:choose>
  </template:supplyFragment>
</template:apply>