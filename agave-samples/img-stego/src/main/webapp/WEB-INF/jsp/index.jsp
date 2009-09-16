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
    <h2>Background</h2>
    <p>
      Steganography is a method of obscuring a message 
      in plain sight.  A primitive form of this was the shrinking of text in the days of 
      lore, and that eventually evolved into the microdot in the WWI/WWII days. Since then, 
      computers have opened the doors to a wider range of steganographic techniques that completely 
      obscure the payload.  Common examples include hiding text in audio, images, and
      videos in such a way that the quality of the targeted multimedia is slightly 
      decreased so that text can be inserted and later extracted.
    </p>
    <h2>Application</h2>
    <p>
      Now, to test this out, you need two things.  One is a messsage, called the 
      <em>payload</em>.  The second is the <em>carrier</em>, and in this case the 
      <em>carrier</em> is an image.  So, after you have uploaded an image and supplied a
      <em>payload</em>, you will be provided a resultant image that is hiding the message.
    </p>    
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
        <div>
          <input class="submissionButton" type="submit" value="Obscure the Payload!" />
        </div>
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
        <div>
          <input class="submissionButton" type="submit" value="Extract the Payload!" />
        </div>
      </form>
    </fieldset>
    <div id="source">
      <strong>View the source:</strong>
      <a href="http://code.google.com/p/agave-web-framework/source/browse/trunk/agave-samples/img-stego/src/main/java/agave/samples/StegoHandler.java">Handler</a> |
      <a href="http://code.google.com/p/agave-web-framework/source/browse/trunk/agave-samples/img-stego/src/main/webapp/WEB-INF/jsp/index.jsp">JSP</a> | 
      <a href="http://code.google.com/p/agave-web-framework/source/browse/trunk/agave-samples/img-stego/src/main/java/agave/samples/StegoServiceImpl.java">Stego Service</a>
    </div>
    <div id="footer">
      <p>&copy; 2007-2009 <a href="http://damiancarrillo.org">Damian Carrillo</a>.  All rights reserved.</p>
      <p><a href="http://code.google.com/p/agave-web-framework/"><img
              src="http://code.google.com/p/agave-web-framework/logo?logo_id=1253032226"
              alt="The Web on Agave"
              title="The Web on Agave" /></a></p>
    </div>
  </body>
</html>
