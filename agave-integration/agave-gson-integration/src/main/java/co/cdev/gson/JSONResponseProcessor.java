package co.cdev.gson;

import java.io.IOException;

import javax.servlet.ServletException;

import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.RoutingContext;
import co.cdev.agave.web.HTTPResponse;
import co.cdev.agave.web.HTTPResponseProcessor;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class JSONResponseProcessor extends HTTPResponseProcessor {

    private final Gson gson;
    
    public JSONResponseProcessor(Gson gson) {
        this.gson = gson;
    }

    @Override
    public boolean canProcessResult(Object result, RoutingContext routingContext, HandlerDescriptor handlerDescriptor) {
        return result != null && JSONResponse.class.isAssignableFrom(result.getClass());
    }

    @Override
    protected void processMessageBody(HTTPResponse response, RoutingContext routingContext, HandlerDescriptor handlerDescriptor) 
            throws ServletException {
        try {
            gson.toJson(response.getMessageBody(), routingContext.getResponse().getWriter());
        } catch (JsonIOException e) {
            throw new ServletException(e);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

}
