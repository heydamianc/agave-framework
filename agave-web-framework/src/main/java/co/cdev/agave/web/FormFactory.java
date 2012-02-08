package co.cdev.agave.web;

import javax.servlet.ServletContext;

import co.cdev.agave.configuration.HandlerDescriptor;

/**
 * Creates instances of forms for the {@link AgaveFilter}. The default
 * implementation of this is {@link agave.internal.FormFactoryImpl}, but you can
 * override it by specifying an initialization param to the {@link AgaveFilter}.
 * An example of this is:
 * 
 * <pre>
 * &lt;web-app&gt;
 * ...
 * &lt;filter&gt;
 *   &lt;filter-name>AgaveFilter&lt;/filter-name&gt;
 *   &lt;filter-class>agave.AgaveFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;formFactory&lt;/param-name&gt;
 *     &lt;param-value&gt;com.domain.package.DefaultFormFactory&lt;/param-value&gt;
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
public interface FormFactory {

    /**
     * Initializes this {@code FormFactory} if necessary. This method is called
     * in the {@link AgaveFilter#init(javax.servlet.FilterConfig)} method, so it
     * is an effective way to set up a mechanism for providing dependency
     * injection or hooking into an IOC library.
     */
    public void initialize();

    /**
     * Creates a new instance of a form object for the form class.
     * 
     * @param handlerContext
     *            the global context object
     * @param descriptor
     *            the handler descriptor that describes which form to
     *            instantiate
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws FormError
     *             when a form instance failed to be instantiated
     */
    public Object createFormInstance(ServletContext servletContext,
            HandlerDescriptor descriptor) throws FormException;
}
