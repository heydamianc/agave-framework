package co.cdev.agave.guice;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Test;

import co.cdev.agave.AbstractFunctionalTest;
import co.cdev.agave.sample.SampleHandler;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

public class AgaveInjectionHandlerFunctionalTest extends AbstractFunctionalTest {

    protected AgaveInjectionFilter createSilentInjectionFilter() throws Exception {
        AgaveInjectionFilter filter = new AgaveInjectionFilter();
        
        Logger agaveFilterLogger = LogManager.getLogManager().getLogger("");
        if (agaveFilterLogger != null) {
            agaveFilterLogger.setLevel(Level.OFF);
        }
        
        return filter;
    }
    
    @Test
    public void testInit() throws Exception {
        AgaveInjectionFilter filter = createSilentInjectionFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(servletContext).getAttribute(AgaveInjectionFilter.PARENT_INJECTOR); will(returnValue(null));
        }});
        
        filter.init(filterConfig);
        
        assertNotNull(filter.getInjector());
        assertHandlerClassWasBound(filter.getInjector(), SampleHandler.class);
    }
    
    @Test
    public void testInjection() throws Exception {
        AgaveInjectionFilter filter = createSilentInjectionFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(servletContext).getAttribute(AgaveInjectionFilter.PARENT_INJECTOR); will(returnValue(null));
            allowing(request).getServletPath(); will(returnValue("/message"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            
            one(request).setAttribute("message", "Hello, world!");
        }});
        
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testInjection_withNonMatchingURIPattern() throws Exception {
        AgaveInjectionFilter filter = createSilentInjectionFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());

        context.checking(new Expectations() {{
            allowing(servletContext).getAttribute(AgaveInjectionFilter.PARENT_INJECTOR); will(returnValue(null));
            allowing(request).getServletPath(); will(returnValue("/messageNot"));
            allowing(request).getMethod(); will(returnValue("GET"));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
            allowing(filterChain).doFilter(request, response);
        }});
        
        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    protected void assertHandlerClassWasBound(Injector injector, Class<?> handlerClass) {
        boolean found = false;
        
        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
        for (Key<?> key : bindings.keySet()) {
            if (handlerClass.equals(key.getTypeLiteral().getRawType())) {
                found = true;
                break;
            }
        }
        
        Assert.assertTrue(found);
    }
    
}
