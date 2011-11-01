<%@ page contentType="application/xhtml+xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
    <title>Image Steganography</title>
    <style type="text/css">
      @import "${pageContext.request.contextPath}/css/structure.css";
      @import "${pageContext.request.contextPath}/css/theme.css";
    </style>
  </head>
  <body>
    <h1>Image Steganography</h1>
    <p>
      The following shows the carrier image and the payload extracted from it.  
    </p>
    <table class="imgTable">
      <thead>
        <tr>
          <th>Image &amp; Obscured Payload</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>
            <img src="${encodedFilename}" alt="Image carrying the payload" />
          </td>
        </tr>
      </tbody>
    </table>
    <table class="imgTable">
      <thead>
        <tr>
          <th>Plaintext Payload</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>${extractedPayload}</td>
        </tr>
      </tbody>
    </table>
    <div id="footer">
      Copyright &copy; 2007-2008 Damian Carrillo. All rights reserved.
    </div>
  </body>
</html>