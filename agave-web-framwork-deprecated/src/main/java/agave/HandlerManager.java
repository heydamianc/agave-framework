/*
 * Copyright (c) 2005 - 2007 Damian Carrillo.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *	* Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 *	* Redistributions in binary form must reproduce the above copyright notice,
 *	  this list of conditions and the following disclaimer in the documentation
 *	  and/or other materials provided with the distribution.
 *	* Neither the name of the <ORGANIZATION> nor the names of its contributors
 *	  may be used to endorse or promote products derived from this software
 *	  without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package agave;

import agave.annotations.ContentType;
import agave.annotations.Converters;
import agave.annotations.Path;
import agave.annotations.PositionalParameters;
import agave.annotations.Required;
import agave.converters.ConverterChain;
import java.io.BufferedReader;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class HandlerManager implements Filter {

	private Logger LOGGER = LoggerFactory.getLogger(HandlerManager.class);

	private static final String CLASS_SUFFIX = ".class";
	private static final String CLASSDIR = "/WEB-INF/classes/";
	private static final String DEBUG = "debug";
	private static final String GET = "get";

	public static final String DEFAULT_CONTENT_TYPE = ContentType.APPLICATION_XHTML_XML;

	private FilterConfig filterConfig = null;
	private Map<String, String> configuration;
	protected Map<String, Class<? extends ResourceHandler>> resourceHandlers;
	protected Map<String, Class<? extends FormHandler>> formHandlers;

	private boolean debugMode = false;

	/**
	 * Init method for this filter
	 * @param filterConfig
	 */
	public void init(FilterConfig filterConfig) {
		Date start = new Date();
		LOGGER.info("Initializing Framework Execution Environment");

		String debugModeValue = filterConfig.getInitParameter(DEBUG);
		if ("true".equalsIgnoreCase(debugModeValue)) {
			debugMode = true;
		}

		this.filterConfig = filterConfig;
		configuration = new HashMap<String, String>();

		Enumeration initParameters = filterConfig.getInitParameterNames();
		while (initParameters.hasMoreElements()) {
			String configKey = (String)initParameters.nextElement();
			String configValue = filterConfig.getInitParameter(configKey);
			configuration.put(configKey, configValue);
		}

		resourceHandlers = new LinkedHashMap<String, Class<? extends ResourceHandler>>();
		formHandlers = new LinkedHashMap<String, Class<? extends FormHandler>>();

		String classDirPath = filterConfig.getServletContext().getRealPath(CLASSDIR);
		File root = new File(filterConfig.getServletContext().getRealPath(CLASSDIR));

		// Discover and map the resource handlers
		Set<Class<? extends ResourceHandler>> resourceHandlerClasses = new HashSet<Class<? extends ResourceHandler>>();

		discoverClasses(classDirPath, root, ResourceHandler.class, resourceHandlerClasses);
		mapResourceHandlers(resourceHandlerClasses);

		// Discover and map the form handlers
		Set<Class<? extends FormHandler>> formHandlerClasses = new HashSet<Class<? extends FormHandler>>();
		discoverClasses(classDirPath, root, FormHandler.class, formHandlerClasses);
		mapFormHandlers(formHandlerClasses);
		Date end = new Date();
		LOGGER.info("Initialization took " + (end.getTime() - start.getTime()) + "ms");
	}

	/**
	 *
	 * @param req
	 * @param resp
	 * @param chain The filter chain we are processing
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet error occurs
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
			HttpServletRequest request = (HttpServletRequest)req;
			HttpServletResponse response = (HttpServletResponse)resp;

			if (LOGGER.isDebugEnabled()) {
				String msg = "Displaying request headers (disable this by " + "removing the debug parameter to HandlerManager in the web.xml)";
				LOGGER.debug(msg);
			}

			Enumeration headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = (String)headerNames.nextElement();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(headerName + ": " + request.getHeader(headerName));
				}
			}

			String requestedPath = request.getRequestURI();
			String contextPath = request.getContextPath();
			String method = request.getMethod();

			if (!contextPath.equals("/") && requestedPath.startsWith(contextPath)) {
				requestedPath = requestedPath.substring(contextPath.length());
			}

			if (method.equals("POST")) {
				String matchedPath = matchPath(requestedPath, formHandlers.keySet());
				if (matchedPath != null) {
					handlePost(new HandlerContext(configuration, filterConfig.getServletContext(), request, response, requestedPath, matchedPath, resourceHandlers, formHandlers));

					// do not continue processing filter chain
					return;
				}
			} else if (method.equals("GET")) {
				String matchedPath = matchPath(requestedPath, resourceHandlers.keySet());
				if (matchedPath != null) {
					handleGet(new HandlerContext(configuration, filterConfig.getServletContext(), request, response, requestedPath, matchedPath, resourceHandlers, formHandlers));

					// do not continue processing filter chain
					return;
				}
			}
		}

		chain.doFilter(req, resp);
	}

	/**
	 * Destroy method for this filter.
	 */
	public void destroy() {
		filterConfig = null;
		configuration = null;
		resourceHandlers = null;
		formHandlers = null;
	}

	/**
	 *
	 * @param context
	 * @throws agave.HandlerException
	 * @throws java.io.IOException
	 */
	protected void handlePost(HandlerContext context) throws HandlerException, IOException {
		Class<? extends FormHandler> handlerClass = formHandlers.get(context.getMatchedPath());
		LOGGER.info("Handling POST for path " + context.getRequestedPath() + " with " + handlerClass.getName());

		try {
			Constructor<? extends FormHandler> constructor = handlerClass.getConstructor();
			FormHandler handler = constructor.newInstance();

			// Assign the query string parameters to their matching properties
			Map<String, String> params = new HashMap<String, String>();

			// Interpret any posted files (header name is case insensitive)
			String ctHeader = context.getRequest().getHeader("Content-Type");

			if (ctHeader != null && ctHeader.trim().startsWith("multipart")) {
				InputStream in = context.getRequest().getInputStream();
				Pattern bp = Pattern.compile("^.*boundary=(.*)$");
				Matcher bm = bp.matcher(ctHeader);

				String boundary = null;
				if (bm.matches() && bm.groupCount() >= 1) {
					boundary = bm.group(1);
					SimpleMultipartInterpreter mi = new SimpleMultipartInterpreter(in, boundary);
					params.putAll(mi.getParameters());
					
					bindParameters(context, handlerClass, handler, mi.getFiles());
				} else {
					throw new IOException("Unable to process multipart form post: part boundary was malformed (in the Content-Type header)");
				}
			} else {
				Enumeration requestParamNames = context.getRequest().getParameterNames();
				while (requestParamNames.hasMoreElements()) {
					String requestParamName = (String)requestParamNames.nextElement();
					String requestParamValue = context.getRequest().getParameter(requestParamName);

					LOGGER.debug("Request parameter: " + requestParamName + " = " + requestParamValue);

					if (requestParamValue != null) {
						params.put(requestParamName, requestParamValue);
					}
				}
			}

			bindParameters(context, handlerClass, handler, params);
			String path = handler.process(context);
			if (path.startsWith("/")) {
				context.getResponse().sendRedirect(context.getRequest().getContextPath() + path);
			} else {
				context.getResponse().sendRedirect(path);
			}
		} catch (Exception ex) {
			throw new HandlerException(ex);
		}
	}

	/**
	 *
	 * @param context
	 * @throws java.io.IOException
	 * @throws agave.HandlerException
	 */
	protected void handleGet(HandlerContext context) throws HandlerException, IOException {
		Class<? extends ResourceHandler> handlerClass = resourceHandlers.get(context.getMatchedPath());

		LOGGER.info("Handling GET for path " + context.getRequestedPath() + " with " + handlerClass.getName());

		try {
			Constructor<? extends ResourceHandler> constructor = handlerClass.getConstructor();
			ResourceHandler handler = constructor.newInstance();

			// Assign the query string parameters to their matching properties
			Map<String, String> params = new HashMap<String, String>();
			Enumeration requestParamNames = context.getRequest().getParameterNames();

			while (requestParamNames.hasMoreElements()) {
				String requestParamName = (String)requestParamNames.nextElement();
				String requestParamValue = context.getRequest().getParameter(requestParamName);
				if (requestParamValue != null) {
					params.put(requestParamName, requestParamValue);
				}
			}

			// Assign any positional parameters
			PositionalParameters positionalParams = handlerClass.getAnnotation(PositionalParameters.class);

			String paramStr = context.getRequestedPath().replaceFirst(context.getMatchedPath(), "");
			if (paramStr.startsWith("/")) {
				paramStr = paramStr.substring(1);
			}

			if (positionalParams != null && paramStr != null && !paramStr.equals("") && !paramStr.equals("/")) {

				String[] paramValues = paramStr.split("\\s*/\\s*");
				String[] paramNames = positionalParams.value();

				if (paramValues.length < paramNames.length) {
					throw new HandlerException("Missing one or more positional parameters.");
				}

				for (int i = 0; i < paramNames.length; i++) {
					params.put(paramNames[i], paramValues[i]);
				}
			}

			bindParameters(context, handlerClass, handler, params);
			ContentType contentTypeAnn = handlerClass.getAnnotation(ContentType.class);
			String contentType = (contentTypeAnn == null) ? DEFAULT_CONTENT_TYPE : contentTypeAnn.value();
			context.getResponse().setContentType(contentType);

			// have the handler render itself
			handler.render(context);
		} catch (Exception ex) {
			throw new HandlerException(ex);
		}
	}

	/**
	 * Rewrites a path so that it is turned into a fully qualified class name.
	 * @param file the file whose name will be rewritten
	 * @param root the root path string
	 * @return the rewritten class name
	 */
	protected String rewriteClassName(String root, File file) {
		if (!root.endsWith("/")) {
			root = root + "/";
		}
		String fileName = file.getAbsolutePath();
		fileName = fileName.replace(root, "");
		fileName = fileName.replace(".class", "");
		fileName = fileName.replace("/", ".");

		return fileName;
	}

	/**
	 * Find the longest path that matches the supplied path amongst the set of
	 * mapped handlers.
	 * @param path The requested path
	 * @param keys The set of handlers mapped to paths
	 * @return The longest matched path
	 */
	protected String matchPath(final String path, final Set<String> keys) {
		int matchLength = 0;
		String longestMatch = null;

		for (String key : keys) {
			if (path.startsWith(key)) {
				if (key.length() > matchLength) {
					matchLength = key.length();
					longestMatch = key;
				}
			}
		}

		return longestMatch;
	}

	/**
	 * Bind a possibly converted value of some sort to a property in a class.
	 * @param context The context in which the handler operates in
	 * @param handlerClass The handler class
	 * @param handler The instance of the handlerClass that has been created
	 *		  for each request.
	 * @param params The parameters to set on the handler instance
	 * @throws agave.HandlerException if any exception occurs
	 */
	protected <T> void bindParameters(HandlerContext context, Class<? extends Handler> handlerClass, Handler handler, Map<String, T> params) throws HandlerException {
		for (String paramName : params.keySet()) {
			T paramValue = params.get(paramName);
			if (paramValue != null && !paramValue.equals("")) {
				try {
					String setterName = "set" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);

					Method[] methods = handlerClass.getMethods();
					Method setter = null;

					// Find the method named by the setterName above
					for (Method m : methods) {
						if (m.getName().equals(setterName)) {
							setter = m;
							break;
						}
					}

					if (setter != null) {
						// If there is a converter on the setter, use it
						Converters converters = setter.getAnnotation(Converters.class);
						if (converters != null) {
							ConverterChain chain = new ConverterChain(converters.value());
							setter.invoke(handler, chain.convertAll(context, paramValue));
						} else {
							setter.invoke(handler, paramValue);
							LOGGER.debug("Calling setter \'" + setter.getName() + "\' with paramater " + paramValue.toString());
						}
					} else {
						throw new NoSuchMethodException("Could not find setter " + setterName + " on class " + handlerClass.getName());
					}
				} catch (Exception ex) {
					throw new HandlerException("Could not bind positional parameter \'" + paramName + "\'", ex);
				}
			}
		}

		try {
			for (Method method : handlerClass.getMethods()) {
				if (method.getName().startsWith(GET) && method.isAnnotationPresent(Required.class) && method.invoke(handler) == null) {
					throw new HandlerException("Missing required value returned from: " + method.getName());
				}
			}
		} catch (InvocationTargetException ex) {
			throw new HandlerException(ex);
		} catch (IllegalArgumentException ex) {
			throw new HandlerException(ex);
		} catch (IllegalAccessException ex) {
			throw new HandlerException(ex);
		}
	}


	/**
	 * Discovers classes the are children of the supplied target class.	 This
	 * method is recursive.
	 * @param classDir The base directory to scan for classes.	This is laid out
	 *		in the Servlet specification.
	 * @param root The file whose children to inspect
	 * @param target The target class
	 * @param matches A set of classes that are children of the target class
	 */
	protected <T> void discoverClasses(String classDir, File root, Class<T> target, Set<Class<? extends T>> matches) {

		if (root.isDirectory()) {
			for (File file : root.listFiles()) {
				discoverClasses(classDir, file, target, matches);
			}
		} else {
			if (root.exists() && root.canRead() && root.getPath().endsWith(CLASS_SUFFIX)) {
				String className = rewriteClassName(classDir, root);
				try {
					Class<?> candidate = Class.forName(className);

					if (target.isAssignableFrom(candidate)) {
						// Class heirarchy is tested for so the following should not be
						// a problem
						@SuppressWarnings(value = "unchecked")
						Class<T> match = (Class<T>)candidate;

						matches.add(match);
					}
				} catch (ClassNotFoundException ex) {
					LOGGER.error("Could not call Class.forName()", ex);
				}
			}
		}
	}

	/**
	 * Map the discovered resource handles by inspecting their {@code Path}
	 * annotation.
	 * @param handlers a {@code Set} of discovered resource handler classes
	 */
	protected void mapResourceHandlers(Set<Class<? extends ResourceHandler>> handlers) {
		LOGGER.info("[Resource Handlers]");

		if (debugMode) {
			resourceHandlers.put("/info", InfoPage.class);
			String msg = "Mapping /info -> " + InfoPage.class.getName() + "(Disable this particular handler by removing the debug " + " parameter to the HandlerManager in web.xml)";
			LOGGER.info(msg);
		}

		for (Class<? extends ResourceHandler> handler : handlers) {
			if (handler.isAnnotationPresent(Path.class)) {
				Path pathAnnotation = handler.getAnnotation(Path.class);
				String mappedPath = pathAnnotation.value();
				resourceHandlers.put(mappedPath, handler);
				LOGGER.info("Mapping " + mappedPath + " -> " + handler.getName());
			}
		}
	}

	/**
	 * Map the discovered form handlers by inspecting their {@code Path}
	 * annotation.
	 * @param handlers a {@code Set} of discovered form handler classes
	 */
	protected void mapFormHandlers(Set<Class<? extends FormHandler>> handlers) {
		LOGGER.info("[Form Handlers]");

		for (Class<? extends FormHandler> handler : handlers) {
			if (handler.isAnnotationPresent(Path.class)) {
				Path pathAnnotation = handler.getAnnotation(Path.class);
				String mappedPath = pathAnnotation.value();
				formHandlers.put(mappedPath, handler);
				LOGGER.info("Mapping " + mappedPath + " -> " + handler.getName());
			}
		}
	}
}