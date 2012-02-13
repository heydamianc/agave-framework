package co.cdev.agave.sample;

import java.io.IOException;

import javax.servlet.ServletException;

import co.cdev.agave.Param;
import co.cdev.agave.Route;
import co.cdev.agave.configuration.RoutingContext;
import co.cdev.agave.conversion.IntegerConverter;

public class SampleHandler {
    
    @Route("/login")
    public void login(RoutingContext context, LoginForm loginForm) throws ServletException, IOException {
        if ("damian".equals(loginForm.getUsername()) && "password".equals(loginForm.getPassword())) {
            context.getRequest().setAttribute("loggedIn", Boolean.TRUE);
        } else {
            context.getRequest().setAttribute("loggedIn", Boolean.FALSE);
        }
        context.getResponse().setStatus(400);
    }

    @Route("/aliased")
    public void aliased(RoutingContext context, AliasedForm aliasedForm) throws ServletException, IOException {
    }

    @Route("/uri-params/${username}/${password}/")
    public void uriParams(RoutingContext context, LoginForm loginForm) throws ServletException, IOException {
        context.getRequest().setAttribute("username", loginForm.getUsername());
        context.getRequest().setAttribute("password", loginForm.getPassword());
    }

    @Route("/throws/nullPointerException")
    public void throwsNullPointerException(RoutingContext context, LoginForm loginForm) throws ServletException, IOException {
        throw new NullPointerException();
    }

    @Route("/throws/ioException")
    public void throwsIOException(RoutingContext context, LoginForm loginForm) throws ServletException, IOException {
        throw new IOException();
    }
    
    @Route("/lacks/form")
    public void lacksForm(RoutingContext context) throws ServletException, IOException {
        context.getRequest().setAttribute("noErrors", Boolean.TRUE);
    }
    
    @Route("/has/named/params/${something}/${aNumber}")
    public void hasNamedParams(RoutingContext context, 
                               @Param("something") String something, 
                               @Param(name = "aNumber", converter = IntegerConverter.class) int aNumber) throws ServletException, IOException {
        context.getRequest().setAttribute("something", something);
        context.getRequest().setAttribute("aNumber", aNumber);
    }
    
    @Route("/overloaded")
    public void overloaded(RoutingContext context) {
        context.getRequest().setAttribute("overloadedWithNoAdditionalParams", Boolean.TRUE);
    }
    
    @Route("/overloaded/${param}")
    public void overloaded(RoutingContext context, @Param("param") String param) {
        context.getRequest().setAttribute("overloadedWithAdditionalParams", param);
    }
    
}
