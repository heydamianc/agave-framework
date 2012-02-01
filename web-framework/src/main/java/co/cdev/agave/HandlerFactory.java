package co.cdev.agave;

import javax.servlet.ServletContext;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.exception.HandlerException;

/**
 * Creates instances of handlers for the {@link AgaveFilter}. The default
 * implementation of this is {@link agave.internal.HandlerFactoryImpl},
 * but you can override it by specifying an initialization param to the
 * {@link AgaveFilter}. An example of this is:
 * 
 * <pre>
 * &lt;web-app&gt;
 * ...
 * &lt;filter&gt;
 *   &lt;filter-name>AgaveFilter&lt;/filter-name&gt;
 *   &lt;filter-class>agave.AgaveFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;handlerFactory&lt;/param-name&gt;
 *     &lt;param-value&gt;com.domain.package.DefaultHandlerFactory&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * ...
 * &lt;/web-app&gt;
 * </pre>
 * 
 * Note that only a single value is supported, so there is no way to have
 * multiple {@code LifecycleHooks}s, unless the value named by the parameter
 * fronts multiple others. This is intentional, and was designed to be this way
 * so that the conceptual overhead of using Agave is shallow.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public interface HandlerFactory {

    /**
     * Initializes this {@code HandlerFactory} if necessary. This method is
     * called in the {@link AgaveFilter#init(javax.servlet.FilterConfig)}
     * method, so it is an effective way to set up a mechanism for providing
     * dependency injection or hooking into an IOC library.
     */
    public void initialize();

    /**
     * Creates instances of handler objects.
     * 
     * @param handlerContext
     *            the global context object
     * @param descriptor
     *            The {@link HandlerDescriptor} that describes the request
     *            handler
     * @return an instance of the described request handler
     * @throws HandlerException
     *             if construction fails
     */
    public Object createHandlerInstance(ServletContext servletContext,
            HandlerDescriptor descriptor) throws HandlerException;
}
