<%@ page contentType="application/xhtml+xml" %>
<%@ include file="/WEB-INF/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
    <title>
      <template:useFragment name="title" />
      <fmt:message key="applicationName" />
    </title>
    <style type="text/css">
      @import "${contextPath}/css/pastebin.css";
      <template:useFragment name="css" />
    </style>
    <script type="text/javascript" src="${contextPath}/js/prototype-1.6.0.2.js"></script>
    <script type="text/javascript" src="${contextPath}/js/pastebin.js"></script>
    <template:useFragment name="js" />
  </head>
  <body>
    <div id="content">
      <template:useFragment name="content" />
      <div id="footer">
        <p>&copy; 2009 <a href="http://damiancarrillo.org">Damian Carrillo</a>. <fmt:message key="allRightsReserved" /></p>
        <p><a href="http://code.google.com/p/agave-web-framework/"><img
              src="http://code.google.com/p/agave-web-framework/logo?logo_id=1253032226"
              alt="The Web on Agave"
              title="The Web on Agave" /></a></p>
      </div>
    </div>
    <div id="sidebar">
      <template:useFragment name="sidebarHeader" />
      <c:if test="${!empty overview.recentEntries}">
        <h3><fmt:message key="recentSnippets" /></h3>
        <ul>
          <c:forEach var="recentEntry" items="${overview.recentEntries}">
            <c:choose>
              <c:when test="${empty recentEntry.owner}">
                <li><a href="${contextPath}/${recentEntry.uniqueId}"><fmt:message key="from">
                      <fmt:param><fmt:message key="anonymous" /></fmt:param></fmt:message></a>
                </li>
              </c:when>
              <c:otherwise>
                <li><a href="${contextPath}/${recentEntry.uniqueId}"><fmt:message key="from">
                      <fmt:param><c:out value="${recentEntry.owner}" /></fmt:param></fmt:message></a></li>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </ul>
      </c:if>
    </div>
  </body>
</html>
