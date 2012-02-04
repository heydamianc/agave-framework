package co.cdev.agave.guice;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.configuration.HandlerDescriptor;

import com.google.inject.Injector;

public class InjectionHandlerFactoryTest {

    private Mockery mockery;
    private Injector injector;
    private HandlerDescriptor handlerDescriptor;
    private ServletContext servletContext;
    private InjectionHandlerFactory injectionHandlerFactory;
    
    @Before
    public void setUp() {
        mockery = new Mockery();
        injector = mockery.mock(Injector.class);
        handlerDescriptor = mockery.mock(HandlerDescriptor.class);
        servletContext = mockery.mock(ServletContext.class);
        injectionHandlerFactory = new InjectionHandlerFactory();
        injectionHandlerFactory.setInjector(injector);
    }
    
    @Test
    public void testCreateHandlerInstance() throws Exception{
        mockery.checking(new Expectations() {{
            one(handlerDescriptor).getHandlerClass(); will(returnValue(AgaveInjectionFilter.class));
            one(injector).getInstance(AgaveInjectionFilter.class);
        }});
        
        injectionHandlerFactory.createHandlerInstance(servletContext, handlerDescriptor);
    }
    
}
