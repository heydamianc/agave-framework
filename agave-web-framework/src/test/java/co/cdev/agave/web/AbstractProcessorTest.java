package co.cdev.agave.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jmock.Mockery;
import org.junit.Before;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;

public class AbstractProcessorTest {

    protected Mockery mockery;
    protected ServletContext servletContext;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    protected RoutingContext routingContext;
    protected RequestDispatcher requestDispatcher;
    protected HandlerDescriptor handlerDescriptor;
    
    @Before
    public void setUp() {
        mockery = new Mockery();
        
        servletContext = mockery.mock(ServletContext.class);
        request = mockery.mock(HttpServletRequest.class);
        response = mockery.mock(HttpServletResponse.class);
        session = mockery.mock(HttpSession.class);
        requestDispatcher = mockery.mock(RequestDispatcher.class);
        
        routingContext = new RoutingContext(servletContext, request, response, session);
    }
    
}
