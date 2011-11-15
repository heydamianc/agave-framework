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
package co.cdev.agave.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import co.cdev.agave.exception.DuplicateDescriptorException;

/**
 * A repository used to group all registered handlers. Handlers are registered
 * by means of scanning the classpath for classes that have methods annotated
 * with the {@code HandlesRequestsTo} annotation.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public final class HandlerRegistryImpl implements HandlerRegistry {

    private Collection<HandlerMethodDescriptor> descriptors;

    public HandlerRegistryImpl() {
        descriptors = new TreeSet<HandlerMethodDescriptor>();
    }

    public HandlerRegistryImpl(Collection<HandlerMethodDescriptor> descriptors)
            throws DuplicateDescriptorException {
        this();
        addAllDescriptors(descriptors);
    }

    /**
     * Adds handlers descriptors to a sorted set of descriptors. The set is
     * sorted according to the specificity of the URIPattern.
     * 
     * @param descriptor
     *            the HandlerDescriptor to be added.
     * @see agave.internal.URIPattern#compareTo(URIPattern) for the algorithm
     *      used in determining the specificity
     */
    @Override
    public void addDescriptor(HandlerMethodDescriptor descriptor)
            throws DuplicateDescriptorException {
        for (HandlerMethodDescriptor existingDescriptor : descriptors) {
            if (existingDescriptor.equals(descriptor)) {
                throw new DuplicateDescriptorException(existingDescriptor, descriptor);
            }
        }
        descriptors.add(descriptor);
    }

    @Override
    public void addAllDescriptors(Collection<HandlerMethodDescriptor> descriptors)
            throws DuplicateDescriptorException {
        for (HandlerMethodDescriptor descriptor : descriptors) {
            addDescriptor(descriptor);
        }
    }

    @Override
    public HandlerMethodDescriptor findMatch(HttpServletRequest request) {
        for (HandlerMethodDescriptor descriptor : descriptors) {
            if (descriptor.matches(request)) {                
                return descriptor;
            }
        }
        return null;
    }

    @Override
    public Collection<HandlerMethodDescriptor> getDescriptors() {
        return Collections.unmodifiableCollection(descriptors);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("HandlerRegistryImpl [descriptors=");
        
        Iterator<HandlerMethodDescriptor> itr = descriptors.iterator();
        
         while (itr.hasNext()) {
             s.append(itr.next().toString());
             
             if (itr.hasNext()) {
                 s.append(",");
             }
         }
        
        s.append("]");
        
        return s.toString();
    }
    
}
