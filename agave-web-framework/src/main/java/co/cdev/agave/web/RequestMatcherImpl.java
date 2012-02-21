package co.cdev.agave.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.HttpMethod;
import co.cdev.agave.configuration.Config;
import co.cdev.agave.configuration.HandlerDescriptor;
import co.cdev.agave.configuration.ParamDescriptor;

public final class RequestMatcherImpl implements RequestMatcher {

    private Config config;
    
    public RequestMatcherImpl(Config config) {
        this.config = config;
    }

    @Override
    public HandlerDescriptor findMatch(HttpServletRequest request) {
        for (HandlerDescriptor handlerDescriptor : config) {
            URIPatternMatcher patternMatcher = new URIPatternMatcherImpl(handlerDescriptor.getURIPattern());
            boolean matches = request != null && request.getMethod() != null && patternMatcher.matches(request);
            
            if (matches) {
                HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
                
                matches &= handlerDescriptor.getHttpMethod().matches(method);
                
                if (!handlerDescriptor.getParamDescriptors().isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> requestParams = request.getParameterMap();
                    
                    URIParamExtractor extractor = new URIParamExtractorImpl(handlerDescriptor.getURIPattern());
                    Map<String, String> uriParams = extractor.extractParams(request);
                    
                    for (ParamDescriptor paramDescriptor : handlerDescriptor.getParamDescriptors()) {
                        String paramName = paramDescriptor.getName();
                        matches &= requestParams.containsKey(paramName) || uriParams.containsKey(paramName);
                    }
                }
            }
            
            if (matches) {
                return handlerDescriptor;
            }
        }
        return null;
    }
    
}
