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
    final Map<String, Method> mutators;
    final Map<String, String[]> parameters;
    final Map<String, Class<? extends Converter<?,?>>> converters;
   

    public ParameterBinderTest() throws Exception {
        context = new Mockery();
        request = context.mock(HttpServletRequest.class);
        descriptor = context.mock(HandlerDescriptor.class);
        mutators = new HashMap<String, Method>();
        parameters = new HashMap<String, String[]>();
        converters = new HashMap<String, Class<? extends Converter<?,?>>>();
    }

    @Before
    public void setup() throws Exception {
        mutators.clear();
        parameters.clear();
        converters.clear();
    }

    @Test
    public void testBindRequestParametersWithoutConversion() throws Exception {
        LoginForm form = new LoginForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        mutators.put("username", LoginForm.class.getMethod("setUsername", String.class));
        mutators.put("password", LoginForm.class.getMethod("setPassword", String.class));

        parameters.put("username", new String[] {"damiancarrillo"});
        parameters.put("password", new String[] {"unsung", "hero"});

        context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
            allowing(request).getParameterMap(); will(returnValue(parameters));
        }});

        binder.bindRequestParameters(request);    

        Assert.assertEquals("damiancarrillo", form.getUsername());
        Assert.assertEquals("unsung", form.getPassword());
    }

    @Test
    public void testBindRequestParametersForStringArraysAndLists() throws Exception {
        MultiForm form = new MultiForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        mutators.put("arrayValues", MultiForm.class.getMethod("setArrayValues", String[].class));
        mutators.put("listValues", MultiForm.class.getMethod("setListValues", List.class));
        parameters.put("arrayValues", new String[] {"one", "two", "three"});
        parameters.put("listValues", new String[] {"a", "b", "c"});

        context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
            allowing(request).getParameterMap(); will(returnValue(parameters));
        }});
     
        binder.bindRequestParameters(request);
      
        Assert.assertArrayEquals(new String[] {"one", "two", "three"}, form.getArrayValues());
        Assert.assertEquals(3, form.getListValues().size());
        Assert.assertEquals("a", form.getListValues().get(0));
        Assert.assertEquals("b", form.getListValues().get(1));
        Assert.assertEquals("c", form.getListValues().get(2));
    }

    @Test
    public void testBindRequestParametersWithConversion() throws Exception {
        LoginForm form = new LoginForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);

        mutators.put("remembered", LoginForm.class.getMethod("setRemembered", Boolean.class));   
        parameters.put("remembered", new String[] {"t"});
        converters.put("remembered", BooleanConverter.class);

        context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
            allowing(request).getParameterMap(); will(returnValue(parameters));
        }});

        binder.bindRequestParameters(request);

        Assert.assertEquals(Boolean.TRUE, form.isRemembered());
    }

    @Test
    public void testBindRequestParametersWithAliasedSetters() throws Exception {
        AliasedForm form = new AliasedForm();
        ParameterBinder binder = new ParameterBinderImpl(form, descriptor);
        
        // parameters are aliased
        mutators.put("someAlias", AliasedForm.class.getMethod("setSomeProperty", String.class));
        mutators.put("anotherAlias", AliasedForm.class.getMethod("setAnotherProperty", Boolean.class));

        parameters.put("someAlias", new String[] {"alpha"});
        parameters.put("anotherAlias", new String[] {"true"});

        converters.put("anotherAlias", BooleanConverter.class);
        
        context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
            allowing(request).getParameterMap(); will(returnValue(parameters));
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

        mutators.put("username", LoginForm.class.getMethod("setUsername", String.class));
        mutators.put("password", LoginForm.class.getMethod("setPassword", String.class));

        final URIPattern pattern = new URIPatternImpl("/uri-params/${username}/${password}");

        context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
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

        mutators.put("remembered", LoginForm.class.getMethod("setRemembered", Boolean.class));
        converters.put("remembered", BooleanConverter.class);

        final URIPattern pattern = new URIPatternImpl("/uri-params/${remembered}");

        context.checking(new Expectations() {{
            allowing(descriptor).getMutators(); will(returnValue(mutators));
            allowing(descriptor).getConverters(); will(returnValue(converters));
            allowing(descriptor).getPattern(); will(returnValue(pattern));
            allowing(request).getRequestURI(); will(returnValue("/uri-params/true/"));
        }});
        
        binder.bindURIParameters(request);

        Assert.assertEquals(Boolean.TRUE, form.isRemembered());
    }

}
