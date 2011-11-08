/*
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
package co.cdev.agave;

import co.cdev.agave.internal.DestinationImpl;

/**
 * A factory class for creating {@link Destination}s.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 * 
 * @see Destination
 */
public final class Destinations {

    /**
     * Creates a new {@link Destination} within the deployed context.  The destination will be 
     * redirected to if the HTTP request method was a POST, otherwise it will be forwarded to.
     * 
     * @param path the desired destination path relative to the deployed context
     * @return a constructed {@link Destination}
     */
    public static Destination create(String path) {
        return new DestinationImpl(path);
    }

    /**
     * Creates a new {@link Destination} within the deployed context that will be redirected to. 
     * 
     * @param path the desired destination path relative to the deployed context (eg: {@code /section/resource}}
     * @return a constructed {@link Destination}
     */
    public static Destination redirect(String path) {
        return new DestinationImpl(path, true);
    }

    /**
     * Creates a new {@link Destination} within the deployed context that will be forwarded to.
     * 
     * @param path the desired resource relative to the context path to forward to (eg: {@code /WEB-INF/jsp/index.jsp}) 
     * @return a constructed {@link Destination}
     */
    public static Destination forward(String path) {
        return new DestinationImpl(path, false);
    }
}
