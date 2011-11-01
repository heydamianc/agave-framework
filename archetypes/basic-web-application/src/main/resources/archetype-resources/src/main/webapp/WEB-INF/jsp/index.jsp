<%@ page contentType="application/xhtml+xml" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
    <title>Hello, ${requestScope.world}!</title>
    <style type="text/css">
      @import "${contextPath}/css/structure.css";
      @import "${contextPath}/css/theme.css";
    </style>
  </head>
  <body>
    <h1>Hello, ${requestScope.world}!</h1>
  </body>
</html>
