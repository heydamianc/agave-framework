package co.cdev.agave.configuration;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.URIPattern;

public interface HandlerDescriptor extends Comparable<HandlerDescriptor>, Serializable {
    
    public Class<?> getHandlerClass();
    public Method getHandlerMethod();
    public URIPattern getURIPattern();
    public HttpMethod getHttpMethod();
    public Class<?> getFormClass();
    public String getWorkflowName();
    public List<ParamDescriptor> getParamDescriptors();
    public boolean initiatesWorkflow();
    public boolean completesWorkflow();

    @Override
    public boolean equals(Object that);

    @Override
    public int hashCode();

}
