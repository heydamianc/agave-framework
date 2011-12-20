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
package co.cdev.agave.sample;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import co.cdev.agave.Destination;
import co.cdev.agave.Destinations;
import co.cdev.agave.RoutingContext;
import co.cdev.agave.Route;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class SayHandler {

    @Route("/say/${phrase}")
    public Destination say(RoutingContext context, SayForm form) {
        Destination dest = Destinations.create("/say.jsp");
        dest.addParameter("said", form.getPhrase());
        return dest;
    }
    
    @Route("/whisper/${phrase}")
    public Destination whisper(RoutingContext context, SayForm form) {
        Destination dest = Destinations.redirect("/whisper.jsp");
        dest.addParameter("said", form.getPhrase());
        dest.addParameter("how", "very softly & sweetly");
        return dest;
    }
    
    @Route("/proclaim/${phrase}")
    public URI proclaim(RoutingContext context) throws URISyntaxException {
        return new URI("http", "//www.utexas.edu/", null);
    }
    
    @Route("/shout/${phrase}")
    public void shout(RoutingContext context) {
        context.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
    
}
