<%@ page contentType="application/xhtml+xml" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Guestbook</title>
  </head>
  <body>
    <h1>Guestbook</h1>
    <h2>Previous Messages</h2>
    <h2>Add a New Messag</h2>
    <form method="post" action="${contextPath}/create">
      <table>
        <tbody>
          <tr>
            <td>Subject</td>
            <td><input type="text" name="subject" /></td>
          </tr>
          <tr>
            <td>Contents</td>
            <td><textarea name="contents" /></td>
          </tr>
        </tbody>
      </table>
    </form>
  </body>
</html>