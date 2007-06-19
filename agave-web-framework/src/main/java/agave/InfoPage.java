/*
 * Copyright (c) 2006 - 2007 Damian Carrillo.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *	o Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 *	o Redistributions in binary form must reproduce the above copyright notice,
 *	  this list of conditions and the following disclaimer in the documentation
 *	  and/or other materials provided with the distribution.
 *	o Neither the name of the <ORGANIZATION> nor the names of its contributors
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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import agave.annotations.ContentType;
import agave.annotations.Path;
import agave.annotations.PositionalParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders the Agave information page.
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 * @since 1.0
 */
@Path("/info") // this annotation doesn't really do anything
@ContentType(ContentType.APPLICATION_XHTML_XML)
public final class InfoPage implements ResourceHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoPage.class);
    
    /**
     * A representation of a positional parameter.
     */
    private final class PositionalParameterDescriptor {
        
        int position;
        String name;
        String type;
        
        public void setPosition(final int position) {this.position = position;}
        public int getPosition() {return position;}
        
        public void setName(final String name) {this.name = name;}
        public String getName() {return name;}
        
        public void setType(final String type) {this.type = type;}
        public String getType() {return type;}
    }
    
    /**
     * A representation of an abstract handler.
     */
    private abstract class HandlerDescriptor {
        private String path;
        private String pathURL;
        private String className;
        
        public void setPath(final String path) {this.path = path;}
        public String getPath() {return path;}
        
        public void setPathURL(final String pathURL) {this.pathURL = pathURL;}
        public String getPathURL() {return pathURL;}
        
        public void setClassName(final String className) {this.className = className;}
        public String getClassName() {return className;}
    }
    
    /**
     * A representation of a resource handler.
     */
    private final class ResourceHandlerDescriptor extends HandlerDescriptor {
        
        private String contentType;
        private List<PositionalParameterDescriptor> positionalParameters;
        
        public void setContentType(final String contentType) {this.contentType = contentType;}
        public String getContentType() {return contentType;}
        
        public void setPositionalParameters(final List<PositionalParameterDescriptor> positionalParameters) {
            this.positionalParameters = positionalParameters;
        }
        
        public List<PositionalParameterDescriptor> getPositionalParameters() {
            return positionalParameters;
        }
    }
    
    /**
     * A representation of a form field.
     */
    private final class FormField {
        private String name;
        private String type;
        
        public void setName(String name) {this.name = name;}
        public String getName() {return name;}
        
        public void setType(String type) {this.type = type;}
        public String getType() {return type;}
    }
    
    /**
     * A representation of a form handler.
     */
    private final class FormHandlerDescriptor extends HandlerDescriptor {
        public List<FormField> fields;
        
        public void setFields(final List<FormField> fields) {this.fields = fields;}
        public List<FormField> getFields() {return fields;}
    }
    
    /**
     * Renders the Agave information page.
     * @param context The resource servletContext
     * @throws java.io.IOException
     */
    public void render(final HandlerContext context) throws IOException {
        PrintWriter out = context.getResponse().getWriter();
        XHTMLTemplateGroup group = new XHTMLTemplateGroup("info");
        StringTemplate info = group.getInstanceOf("InfoPage");
        info.setAttribute("defaultContentType", HandlerManager.DEFAULT_CONTENT_TYPE);
        
        List<ResourceHandlerDescriptor> resources = new ArrayList<ResourceHandlerDescriptor>();
        
        for (String path : context.getResourceHandlers().keySet()) {
            ResourceHandlerDescriptor descriptor = new ResourceHandlerDescriptor();
            
            // Set the paths
            descriptor.setPath(path);
            descriptor.setPathURL(context.getRequest().getContextPath() + path);
            
            // Set the class name
            Class<? extends ResourceHandler> handler = context.getResourceHandlers().get(path);
            descriptor.setClassName(handler.getName());
            
            // Set the content type
            ContentType contentType = handler.getAnnotation(ContentType.class);
            descriptor.setContentType((contentType == null) ? null : contentType.value());
            
            // Set the positional parameters
            PositionalParameters pps = handler.getAnnotation(PositionalParameters.class);
            if (pps != null) {
                List<PositionalParameterDescriptor> ppds =
                        new ArrayList<PositionalParameterDescriptor>();
                
                String[] positionalParameters = pps.value();
                for (int i = 0; i < positionalParameters.length; i++) {
                    String name = pps.value()[i];
                    PositionalParameterDescriptor ppd = new PositionalParameterDescriptor();
                    ppd.setPosition(i);
                    ppd.setName(name);
                    try {
                        ppd.setType(handler.getDeclaredField(name).getType().getName());
                    } catch (NoSuchFieldException ex) {
                        // swallow
                    }
                    ppds.add(ppd);
                }
                descriptor.setPositionalParameters(ppds);
            }
            resources.add(descriptor);
        }
        info.setAttribute("resources", resources);
        
        List<FormHandlerDescriptor> forms = new ArrayList<FormHandlerDescriptor>();
        for (String path : context.getFormHandlers().keySet()) {
            FormHandlerDescriptor descriptor = new FormHandlerDescriptor();
            
            // Set the paths
            descriptor.setPath(path);
            descriptor.setPathURL(context.getRequest().getContextPath() + path);
            
            // Set the class name
            Class<? extends FormHandler> handler = context.getFormHandlers().get(path);
            descriptor.setClassName(handler.getName());
            
            // Set the available form fields
            List<FormField> fields = new ArrayList<FormField>();
            for (Method method : handler.getDeclaredMethods()) {
                String name = method.getName();
                if (name.startsWith("set")) {
                    FormField field = new FormField();
                    field.setName(name.substring(3,4).toLowerCase() + name.substring(4));
                    field.setType(method.getParameterTypes()[0].getName());
                    fields.add(field);
                }
            }
            descriptor.setFields(fields);
            forms.add(descriptor);
        }
        
        info.setAttribute("forms", forms);
        out.println(info.toString());
        out.close();
    }
    
}
