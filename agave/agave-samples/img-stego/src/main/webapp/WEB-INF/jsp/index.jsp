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
    <fieldset>
      <legend>Obscure Text in an Image</legend>
      <form 
        action="${pageContext.request.contextPath}/obscure" 
        method="post"
        enctype="multipart/form-data">
        <table>
          <tbody>
            <tr>
              <td><label for="payload">Textual Payload:</label></td>
              <td><textarea id="payload" name="payload" cols="80" rows="5"/></td>
            </tr>
            <tr>
              <td><label for="carrier">Carrier Image:</label></td>
              <td><input id="encodeCarrier" type="file" name="carrier" /></td>
            </tr>
          </tbody>
        </table>
        <input class="submissionButton" type="submit" value="Obscure the Payload!" />
      </form>
    </fieldset>
    <fieldset>
      <legend>Extract Obscured Text from an Image</legend>
      <form 
        action="${pageContext.request.contextPath}/extract" 
        method="post"
        enctype="multipart/form-data">
        <table>
          <tbody>
            <tr>
              <td><label for="carrier">Carrier Image:</label></td>
              <td><input id="extractCarrier" type="file" name="carrier" /></td>
            </tr>
          </tbody>
        </table>
        <input class="submissionButton" type="submit" value="Extract the Payload!" />
      </form>
    </fieldset>
    <div id="footer">
      Copyright &copy; 2007-2008 Damian Carrillo. All rights reserved.
    </div>
  </body>
</html>