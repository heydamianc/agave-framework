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
      The following shows the difference between the original image and the one carrying the encoded payload.
      If you look closely you can see how added and dropped bits from the payload affect the colors in the 
      obscured image.  Try saving the obscured image and 
      <a href="${pageContext.request.contextPath}/">decoding the payload</a> now.
    </p>
    <table class="imgTable">
      <thead>
        <tr>
          <th>Original Image</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>
            <img src="${filename}" alt="Original image" />
          </td>
        </tr>
      </tbody>
    </table>
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
    <div id="footer">
      Copyright &copy; 2007-2008 Damian Carrillo. All rights reserved.
    </div>
  </body>
</html>