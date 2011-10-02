# The Agave Web Framework

A simple and lightweight framework for creating Java web applications

## Getting Started

*Prerequisites*: Java 5+ and Maven 2+

Use the `agave-archetype` to create a baseline web application for you with the following command:

    mvn archetype:generate -DarchetypeCatalog=http://agave-web-framework.googlecode.com/svn/maven2 \
                           -DgroupId=org.sample \
                           -DartifactId=sampleProject

It is possible to accomplish the same result from within an IDE, but I will leave that as an exercise 
left to the reader. When you execute the previous command, you will be using a remotely hosted catalog 
of archetypes, so you will have to select the primary archetype to use, then enter the revision number 
when it prompts you to do so. After that, you will have a Maven project in a directory named 
`sampleProject`.

At this point, you have a fully working Agave web project. Change into the `sampleProject` directory, 
and execute the following command:

    mvn jetty:run-war

Maven will download a few jetty dependencies and then start the Jetty servlet container. Then, from a 
web browser, navigate to [http://localhost:8080/sampleProject](http://localhost:8080/sampleProject) and 
you will see a "Hello, world!" page that confirms that you have everything set up correctly. The command 
you just entered compiles the code, packages a webapp, then uses the 
[jetty:run-war](http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin) plugin to run the packaged 
webapp. You can see the result of this in the `target` directory, with `sampleProject-1.0-SNAPSHOT.war` 
and the associated exploded webapp dir of `sampleProject-1.0-SNAPSHOT`.  

## Setting the `classesDirectory` Parameter

Although you have a working webapp at this point, there is a deficiency in running your webapp with the 
`run-war` goal while you are actively devloping it if you are using JSPs in your view tier. Changes 
made to your JSPs will not be immediately visible because you will have to package the webapp up again, 
whereas Jetty will respond to this by reloading the context. This is a good characteristic, but the 
process can be streamlined. Open your `./src/main/webapp/WEB-INF/web.xml` file and add the following 
initialization parameter to the `AgaveFilter`:

    <filter>
      <filter-name>AgaveFilter</filter-name>
      <filter-class>agave.AgaveFilter</filter-class>
      <init-param>
        <param-name>classesDirectory</param-name>
        <param-value>./target/classes/</param-value>
      </init-param>
    </filter>

Without the `classesDirectory` initialization parameter, the classes are loaded from `/WEB-INF/classes/` 
directory, which is what the servlet specification designates as the standard for war files. Adding the 
initialization parameter effectively tells Agave to build its internal configuration from the classes 
that are loaded from a specific directory. This is necessary because the Jetty plugin is smart enough to 
run the webapp without actually having to package it, and since maven does not package the project as a 
war file, the actual classes that are used to run the webapp are those that end up in the directory that 
you point to with the `param-value`. _Although this is convenient during development, the webapp that 
is generated will not work once it is deployed outside of your development environment, unless the path 
to the classes directory matches that of the exploded webapp's classes directory._ So, prior to releasing 
the webapp upstream, remove the `init-param` or just use the `run-war` goal to run the webapp. Now that 
you have read the disclaimer, run the webapp with the following goal:

    mvn jetty:run

Once again, visit [http://localhost:8080/sampleProject](http://localhost:8080/sampleProject). Then, 
open `./src/main/webapp/WEB-INF/jsp/index.jsp` and add a new paragraph or some visible change to it. 
Since the Jetty plugin is running over a monitored filesystem, your changes will be immediately available 
and visible after you have saved your file. Also, changes made to any Java files will require a 
recompilation before they are available. So if you have the Maven process running Jetty in the background, 
issuing a `mvn compile` command will cause Jetty to reload the context.

## Additional Information

### Request Handlers

Agave concerns itself with user data-binding and method invocation. The problem of method invocation is 
in determining where an entry point to the web application is; similar to how a `main(String[] args)` 
method is the entry point when invoking a class on the command line. When using Agave, a handler method 
is the solution to this problem. You will have multiple handler methods that serve as entry points into 
the web application and each entry point is indicated with a unique URI pattern. The pattern is one of
the argumentst to a `@HandlesRequestsTo` annotation, and by placing this annotation on a method, you 
effectively identify that method as being a handler method.

### Handler Method Signature

The method signature is actually quite loose. It can be of the form: 
`public [void|java.net.URL|agave.Destination] methodName(HandlerContext context(, FormClass form)?)`, 
where some examples of valid handler method signatures are:

  * `public void handle(HandlerContext context) throws IOException`
  * `public void handle(HandlerContext context, HandlerForm form) throws ServletException`
  * `public Destination login(HandlerContext context, AuthenticationForm form)`
  * `public URL agreeOrRedirectToGoogle(HandlerContext context, AgreementForm form)`

### The `@HandlesRequestsTo` Annotation

You indicate which methods should field requests with the `@HandlesRequestsTo` annotation. The 
`@HandlesRequestsTo` annotation can take a URI pattern and an optional HTTP method to match against. The 
URI pattern indicates which URL the handler method will field, based on the URL constructed by combining 
the protocol, port, context, and URI. The HTTP method is optional and may be used to define a more 
granular HTTP reques matching scheme. The URI pattern can be used in the following fashion (note that 
`http://localhost:8080/app/` will be used because a development environment is assumed, but these URI 
patterns will work with any context deployed to any hostname running on any port): 
<dl>
  <dt><tt>@HandlesRequestsTo(&quot;/&quot;)</tt></dt>
  <dd>Fields requests to the root of the context. Use this along with the welcome file element in your 
      web.xml to immediately bootstrap the framework instead of having to hit a JSP or HTML page and 
      have it redirect into an entry handler.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/&quot;)</tt></dt>
  <dd>Same as before, except the URI pattern is explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/&quot;, method=HttpMethod.GET)</tt></dt>
  <dd>Same as before, except the URI pattern and HTTP method are explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(&quot;/some/uri&quot;)</tt></dt>
  <dd>Fields requests to <tt>http://localhost:8080/app/some/uri</tt>, 
      <tt>http://localhost:8080/app/some/uri/</tt>, 
      <tt>http://localhost:8080/app/some/uri?param1=one&amp;param2=two</tt>, etc.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/some/uri&quot;)</tt></dt>
  <dd>The same as before, except the URI pattern is explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/some/uri&quot;, method=HttpMethod.GET)</tt></dt>
  <dd>The same as before, except the URI pattern and HTTP method are explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(&quot;/some/uri/${param1}/&quot;)</tt></dt>
  <dd>Fields requests to <tt>http://localhost:8080/app/some/uri/duck</tt>, 
      <tt>http://localhost:8080/app/some/uri/duck/</tt>, 
      <tt>http://localhost:8080/app/some/uri/goose</tt>, and also has the side effect of using the last
      segment as a parameter value, which will be bound to a form object.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/some/uri/${param1}/&quot;)</tt></dt>
  <dd>The same as before, except the URI pattern is explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/some/uri/${param1}/&quot;, method=HttpMethod.POST)</tt></dt>
  <dd>The same as before, except the URI pattern and HTTP method are explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(&quot;/*/hunt&quot;)</tt></dt>
  <dd>Fields requests to <tt>http://localhost:8080/app/duck/hunt</tt>, 
      <tt>http://localhost:8080/app/redOctober/hunt</tt>, but not 
      <tt>http://localhost:8080/app/red/october/hunt</tt>. The portion of the URI matched by the 
      wildcard is not used as a parameter value; it is strictly used for matching purposes.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/*/hunt&quot;)</tt></dt>
  <dd>The same as before, except the URI pattern is explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(uri=&quot;/*/hunt&quot;, method=HttpMethod.GET)</tt></dt>
  <dd>The same as before, except the URI pattern and HTTP method are explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(&quot;/**/hunt&quot;)</tt></dt>
  <dd>Fields requests to <tt>http://localhost:8080/app/duck/hunt</tt>, 
      <tt>http://localhost:8080/app/redOctober/hunt</tt>, and 
      <tt>http://localhost:8080/app/app/red/october/hunt</tt> but not 
      <tt>http://localhost:8080/app/duck/hunt/x</tt>. The portion of the URI matched by the wildcard is 
      not used as a parameter value; it is strictly used for matching purposes.</dd>
  <dt><tt>@HandlesRequestsTo(uri&quot;/**/hunt&quot;)</tt></dt>
  <dd>The same as before, except the URI pattern is explicitly specified.</dd>
  <dt><tt>@HandlesRequestsTo(uri&quot;/**/hunt&quot;, method=HttpMethod.GET)</tt></dt>
  <dd>The same as before, except the URI pattern and HTTP method are explicitly specified.</dd>
</dl>

### URI Pattern Specifics

In the examples above, you can see that the URI pattern can be parameterized. The pattern may have the 
following tokens:

<dl>
  <dt>Literal Tokens</dt>
  <dd>Literal tokens are the basis of URI patterns, and should be used whenever you know precisely 
      what URI should be matched. A literal token can be any string, but it should conform to 
      <a href="http://rfc.sunsite.dk/rfc/rfc1738.html" rel="nofollow">RFC 1738</a>, or see Brian 
      Wilson&#x27;s <a href="http://www.blooberry.com/indexdot/html/topics/urlencoding.htm#whatwhy" 
      rel="nofollow">overview</a> for an easier to interpret format.</dd>
  <dt><tt>*</tt> Wildcard</dt>
  <dd>A single asterisk wildcard whose actual value in the URI pattern can be any whole token. In other 
      words, the following is not allowed: <tt>/some/uri/First*Rest/</tt>, and each <tt>*</tt> token 
      should be surrounded by forward slashes (as in <tt>/*/</tt>). Use the single asterisk wildcard 
      when you want to field requests to multiple URLs with one method, but do not care about the value 
      supplied in place of the wildcard.</dd>
  <dt><tt>**</tt> Wildcard</dt>
  <dd><div>A double asterisk wildcard whose actual value in the URI pattern can be any series of whole 
      tokens. In other words, the following is not allowed: <tt>/some/uri/First**Rest/</tt>, and each 
      <tt>**</tt> token should be surrounded by forward slashes (as in <tt>/**/</tt>). Use the double 
      asterisk wildcard when you want to field requests to multiple URLs with one method, but do not 
      care about the values supplied in place of the wildcard.</dd>
</dl>

In some cases, you may end up with a nondeterministic URI pattern where there is no way to effectively 
pick a handler to field HTTP requests for it. The following combination of URI patterns are 
nondeterministic:

  * `/some/uri/**/*`
  * `/some/uri/**/${param1}`

As you can imagine, the double asterisk (`**`) is eagerly matching virtual directory tokens, and with 
another unspecified value that follows it, there is no way of knowing when to stop eagerly matching. 
This sort of thing should be avoided, and you should only place the double asterisk at the end of a 
URI pattern or with a specified token immediately afterward, like so:

  * `/some/uri/**/stop/*`
  * `/some/uri/**/stop/${param1}`

### Form Objects

The problem of having user input bound to a Java object is solved in Agave with forms. Forms are simple 
POJOs that have fields named similar to the names of form inputs, plus mutators to set those values in 
an encapsulation-safe way. Your HTML can have forms of both content type 
`application/x-www-form-urlencoded` and `multipart/form-data`, and Agave will populate the form fields 
correctly for both. Agave correctly interprets a `multipart/form-data` form with a custom 
`HttpServletRequest` implementation that parses the input stream of the submitted `ServletRequest`. 
This process extracts the textual data as strings and the binary data as the content for a temporary 
file. The form is then populated with the extracted strings and files so you can manipulate them. 
The custom implementation also replaces the `request.getParameter(...)` method implementation, so the 
correct object is returned whenever you call it (the servlet implementation lacks this ability for a 
multipart form).

### The `@ConvertWith` Annotation

URL encoded form objects have a default data type of string for all the fields that correspond to form 
inputs. This is because the only type of request parameter is a string parameter. You can, however, 
convert these strings into other objects with a given set of converters or by creating your own custom 
converter. The converters provided by Agave are:

<dl>
  <dt><tt>agave.conversion.BooleanConverter</tt></dt>
  <dd>Used to convert input from a <tt>String</tt> into a <tt>Boolean</tt> object. Valid inputs that 
      resolve to <tt>true</tt> are: <tt>true</tt>, <tt>t</tt>, <tt>1</tt>, <tt>on</tt>, <tt>yes</tt>, 
      and <tt>y</tt>. Valid inputs that resolve to <tt>false</tt> are: <tt>false</tt>, <tt>f</tt>, 
      <tt>0</tt>, <tt>off</tt>, <tt>no</tt>, and <tt>n</tt>.</dd>
  <dt><tt>agave.conversion.BufferedImageConverter</tt></dt>
  <dd>Used to convert input <tt>agave.Part</tt> into an editable
      <tt>java.awt.image.BufferedImage</tt>.</dd>
  <dt><tt>agave.conversion.ByteConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Byte</tt> object by calling 
      <tt>Byte.parseByte()</tt> on the input string.</dd>
  <dt><tt>agave.conversion.CharacterConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Character</tt> object by taking the first 
      Unicode code point of the input string.</dd>
  <dt><tt>agave.conversion.DateConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Date</tt> object without taking time into 
      account. Leverages The <a href="http://download.oracle.com/javase/6/docs/api/java/text/DateFormat.html#getDateInstance(int,%20java.util.Locale)"
      rel="nofollow">locale specific DateFormat</a> to interpret a date (eg. <tt>05/07/93</tt> in the 
      US locale).</dd>
  <dt><tt>agave.conversion.DoubleConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Double</tt> object by calling 
      <tt>Double.parseDouble()</tt> on the input string.</dd>
  <dt><tt>agave.conversion.FloatConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Float</tt> object by calling 
      <tt>Float.parseFloat()</tt> on the input string.</dd>
  <dt><tt>agave.conversion.IntegerConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Integer</tt> object by calling 
      <tt>Integer.parseInt()</tt> on the input string.</dd>
  <dt><tt>agave.conversion.LongConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Long</tt> object by calling 
      <tt>Long.parseLong()</tt> on the input string.</dd>
  <dt><tt>agave.conversion.ShortConverter</tt></dt>
  <dd>Used to convert an input <tt>String</tt> into a <tt>Short</tt> object by calling 
      <tt>Short.parseShort()</tt> on the input string.</dd>
</dl>

### The Agave Filter

The `AgaveFilter` routes requests to handlers whenever a URI pattern is matched. It can be extended 
and its protected methods overridden for maximum control, or any of the following initialization params 
can be supplied:

### The `classesDirectory` init-param

The `classesDirectory` parameter is used to override where Agave looks for classes.  With all technical 
details aside, Agave scans a directory tree for class files and detects any classes that can be used to 
process requests. It does this by interpreting the bytecode and targeting the `HandlesRequestsTo` 
annotation. So, you use the `classesDirectory` parameter to specify which directory to scan for classes, 
relative to the deployment directory. This is typically only good for a development environment.

Sample usage:

    <web-app>
      ...
      <filter>
        <filter-name>AgaveFilter</filter-name>
        <filter-class>agave.AgaveFilter</filter-class>
        <init-param>
          <param-name>classesDirectory</param-name>
          <!-- Adds support for running "mvn jetty:run" -->
          <param-value>${basedir}/target/classes/</param-value>
        </init-param>
      </filter>
      ...
    </web-app>

### The `lifecycleHooks` init-param

The `lifecycleHooks` parameter is used to hook into the lifecycle of Agave. This is good for implementing 
security measures, or for doing universal preparation, etc. The value of this parameter is used by 
`Class.forName()`, so it has to be a fully qualified class name.

Sample usage:

    <web-app>
      ...
      <filter>
        <filter-name>AgaveFilter</filter-name>
        <filter-class>agave.AgaveFilter</filter-class>
        <init-param>
          <param-name>lifecycleHooks</param-name>
          <param-value>com.domain.package.ClassName</param-value>
        </init-param>
      </filter>
      ...
    </web-app>

### The `instanceCreator` init-param

The `instanceCreator` parameter is used as a way to integrate with dependency injection frameworks, or 
create instances of objects with factories. The default implementation uses reflection to create 
instances of forms and handlers by calling the default constructor, so both handler classes and form 
classes must have a default constructor when you do not supply this parameter.

Sample usage:

    <web-app>
      ...
      <filter>
        <filter-name>AgaveFilter</filter-name>
        <filter-class>agave.AgaveFilter</filter-class>
        <init-param>
          <param-name>lifecycleHooks</param-name>
          <param-value>com.domain.package.DefaultInstanceFactory</param-value>
        </init-param>
      </filter>
      ...
    </web-app>

## Frequently Asked Questions

* Why do I get a stack overflow when "/" is configured as my welcome file?
  * You most likely have configured the welcome file in the `web.xml` to be the root of the context 
    ("/") but have not configured a handler to handle this request. I am often guilty of this because 
    I personally like to programmatically bootstrap the framework by catching requests to the welcome 
    file and not having to perform a redirect just to arrive in controller code. In fact, the 
    `WelcomeHandler` generated through the archetype is configured to work this way.
  * Another reason might be if you are leveraging the *jetty:run* goal but have not configured the 
    classes directory as a parameter to the `AgaveFilter`.  Either use the *jetty:run-war* goal or 
    configure the classes directory. This is necessary because if you run *jetty:run* there will be 
    no classes directory to scan for handlers (the default is /WEB-INF/classes), and the servlet 
    container will keep forwarding to itself: `root → welcome file → root → welcome file → ad 
    infinitum`.

## License

All content is released under the 
[BSD license](https://github.com/damiancarrillo/agave-web-framework/blob/master/LICENSE.markdown). 
See [this page](http://www.linfo.org/bsdlicense.html) for a plain-text description of what this means.
