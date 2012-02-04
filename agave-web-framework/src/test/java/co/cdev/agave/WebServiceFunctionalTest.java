/*
 * Copyright (c) 2011, Damian Carrillo
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
package co.cdev.agave;

import java.util.Collection;
import java.util.HashMap;

import org.jmock.Expectations;
import org.junit.Test;

import co.cdev.agave.sample.MovieHandler.Movie;

/**
 * 
 * @author <a href="damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class WebServiceFunctionalTest extends AbstractFunctionalTest {

    @Test
    public void testCollectionList() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();

        emulateServletContainer(new HashMap<String, String[]>());
        expectRequest("/movies", HttpMethod.GET);
        expectRequestAttributeValue(Collection.class);
        expectForward("/WEB-INF/movies/list.jsp");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testCollectionReplace() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());
        expectRequest("/movies", HttpMethod.PUT);
        expectRequestAttributeValue(Boolean.class);
        expectForward("/WEB-INF/movies/replace.jsp");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testCollectionCreate() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());
        expectRequest("/movies", HttpMethod.POST);
        expectRequestAttributeValue(Movie.class);
        expectForward("/WEB-INF/movies/create.jsp");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testCollectionDelete() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());
        expectRequest("/movies", HttpMethod.DELETE);
        expectRequestAttributeValue(Boolean.class);
        expectForward("/WEB-INF/movies/delete.jsp");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testElementRetrieve() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());
        expectRequest("/movies/Rocky%20II", HttpMethod.GET);
        expectRequestAttributeValue(Movie.class);
        expectForward("/WEB-INF/movie/display.jsp");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testElementReplace() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());
        expectRequest("/movies/Rocky%20II", HttpMethod.PUT);
        expectRequestAttributeValue(Movie.class);
        expectForward("/WEB-INF/movie/replace.jsp");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    @Test
    public void testElementDelete() throws Exception {
        AgaveFilter filter = createSilentAgaveFilter();
        
        emulateServletContainer(new HashMap<String, String[]>());
        expectRequest("/movies/Rocky%20II", HttpMethod.DELETE);
        expectRequestAttributeValue(Movie.class);
        expectForward("/WEB-INF/movie/delete.jsp");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    public void expectRequest(final String uri, final HttpMethod method) {
        context.checking(new Expectations() {{
            allowing(request).getServletPath(); will(returnValue(uri));
            allowing(request).getMethod(); will(returnValue(method.name()));
            allowing(request).getContentType(); will(returnValue("application/x-www-form-urlencoded"));
        }});
    }
    
    public void expectRequestAttributeValue(final Class<?> valueType) {
        context.checking(new Expectations() {{
            one(request).setAttribute(with(any(String.class)), with(any(valueType)));
        }});
    }
    
    public void expectForward(final String path) {
        context.checking(new Expectations() {{
            allowing(response).isCommitted(); will(returnValue(false));
            allowing(request).getRequestDispatcher(path);
        }});
    }
	
}
