/**
 * Copyright (c) 2008, Damian Carrillo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of 
 *     conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *     conditions and the following disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *   * Neither the name of the copyright holder's organization nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without specific 
 *     prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package agave.internal;

import agave.conversion.Converter;
import agave.conversion.BooleanConverter;
import agave.sample.LoginForm;
import agave.sample.MultiForm;
import agave.sample.AliasedForm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class ParameterBinderTest {

    Mockery context; 
    final HttpServletRequest request;
    final HandlerDescriptor descriptor;
    final Map<String, Method> parameterSetters;
    final Map<String, String[]> parameterMap;
    final Map<String, Class<? extends Converter<?,?>>> parameterConverters;
   

    public ParameterBinderTest() throws Exception {
        context = new Mockery();
        request = context.mock(HttpServletRequest.class);
        descriptor = context.mock(HandlerDescriptor.class);
        parameterSetters = new HashMap<String, Method>();
        parameterMap = new HashMap<String, String[]>();
        parameterConverters = new HashMap<String, Class<? extends Converter<?,?>>>();
    }

    @Before
    public void setup() throws Exception {
        parameterSetters.clear();
        parameterMap.clear();
        parameterConverters.clear();
    }

    @Test
    public void testBindRequestParametersWithoutConversion() throws Exception {
        LoginForm form = new LoginForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        parameterSetters.put("username", LoginForm.class.getMethod("setUsername", String.class));
        parameterSetters.put("password", LoginForm.class.getMethod("setPassword", String.class));

        parameterMap.put("username", new String[] {"damiancarrillo"});
        parameterMap.put("password", new String[] {"unsung", "hero"});

        context.checking(new Expectations() {{
            allowing(descriptor).getParameterSetters(); will(returnValue(parameterSetters));
            allowing(descriptor).getParameterConverters(); will(returnValue(parameterConverters));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
        }});

        binder.bindRequestParameters(request);    

        Assert.assertEquals("damiancarrillo", form.getUsername());
        Assert.assertEquals("unsung", form.getPassword());
    }

    @Test
    public void testBindRequestParametersForStringArraysAndLists() throws Exception {
        MultiForm form = new MultiForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        parameterSetters.put("arrayValues", MultiForm.class.getMethod("setArrayValues", String[].class));
        parameterSetters.put("listValues", MultiForm.class.getMethod("setListValues", List.class));
        parameterMap.put("arrayValues", new String[] {"one", "two", "three"});
        parameterMap.put("listValues", new String[] {"a", "b", "c"});

        context.checking(new Expectations() {{
            allowing(descriptor).getParameterSetters(); will(returnValue(parameterSetters));
            allowing(descriptor).getParameterConverters(); will(returnValue(parameterConverters));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
        }});
     
        binder.bindRequestParameters(request);
      
        Assert.assertEquals(new String[] {"one", "two", "three"}, form.getArrayValues());
        Assert.assertEquals(3, form.getListValues().size());
        Assert.assertEquals("a", form.getListValues().get(0));
        Assert.assertEquals("b", form.getListValues().get(1));
        Assert.assertEquals("c", form.getListValues().get(2));
    }

    @Test
    public void testBindRequestParametersWithConversion() throws Exception {
        LoginForm form = new LoginForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        parameterSetters.put("remembered", LoginForm.class.getMethod("setRemembered", Boolean.class));   
        parameterMap.put("remembered", new String[] {"t"});
        parameterConverters.put("remembered", BooleanConverter.class);

        context.checking(new Expectations() {{
            allowing(descriptor).getParameterSetters(); will(returnValue(parameterSetters));
            allowing(descriptor).getParameterConverters(); will(returnValue(parameterConverters));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
        }});

        binder.bindRequestParameters(request);

        Assert.assertEquals(Boolean.TRUE, form.isRemembered());
    }

    @Test
    public void testBindRequestParametersWithAliasedSetters() throws Exception {
        AliasedForm form = new AliasedForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);
        
        // parameters are aliased
        parameterSetters.put("someAlias", AliasedForm.class.getMethod("setSomeProperty", String.class));
        parameterSetters.put("anotherAlias", AliasedForm.class.getMethod("setAnotherProperty", Boolean.class));

        parameterMap.put("someAlias", new String[] {"alpha"});
        parameterMap.put("anotherAlias", new String[] {"true"});

        parameterConverters.put("anotherAlias", BooleanConverter.class);
        
        context.checking(new Expectations() {{
            allowing(descriptor).getParameterSetters(); will(returnValue(parameterSetters));
            allowing(descriptor).getParameterConverters(); will(returnValue(parameterConverters));
            allowing(request).getParameterMap(); will(returnValue(parameterMap));
        }});

        binder.bindRequestParameters(request);

        // and then checked by the actuals
        Assert.assertEquals("alpha", form.getSomeProperty());
        Assert.assertEquals(Boolean.TRUE, form.getAnotherProperty());
    }

    @Test
    public void testBindURIParametersWithoutConversion() throws Exception {
        LoginForm form = new LoginForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        parameterSetters.put("username", LoginForm.class.getMethod("setUsername", String.class));
        parameterSetters.put("password", LoginForm.class.getMethod("setPassword", String.class));

        final URIPattern pattern = new URIPatternImpl("/uri-params/${username}/${password}");

        context.checking(new Expectations() {{
            allowing(descriptor).getParameterSetters(); will(returnValue(parameterSetters));
            allowing(descriptor).getParameterConverters(); will(returnValue(parameterConverters));
            allowing(descriptor).getPattern(); will(returnValue(pattern));
            allowing(request).getRequestURI(); will(returnValue("/uri-params/damian/ornery/"));
        }});
        
        binder.bindURIParameters(request);

        Assert.assertEquals("damian", form.getUsername());
        Assert.assertEquals("ornery", form.getPassword());
    }

    @Test
    public void testBindURIParametersWithConversion() throws Exception {
        LoginForm form = new LoginForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        parameterSetters.put("remembered", LoginForm.class.getMethod("setRemembered", Boolean.class));
        parameterConverters.put("remembered", BooleanConverter.class);

        final URIPattern pattern = new URIPatternImpl("/uri-params/${remembered}");

        context.checking(new Expectations() {{
            allowing(descriptor).getParameterSetters(); will(returnValue(parameterSetters));
            allowing(descriptor).getParameterConverters(); will(returnValue(parameterConverters));
            allowing(descriptor).getPattern(); will(returnValue(pattern));
            allowing(request).getRequestURI(); will(returnValue("/uri-params/true/"));
        }});
        
        binder.bindURIParameters(request);

        Assert.assertEquals(Boolean.TRUE, form.isRemembered());
    }

}
