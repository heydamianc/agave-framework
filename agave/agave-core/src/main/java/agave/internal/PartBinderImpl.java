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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import agave.MultipartRequest;
import agave.conversion.Converter;
import agave.conversion.PartConverter;
import agave.exception.ConversionException;
import agave.exception.PartBindingException;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class PartBinderImpl implements PartBinder {

    private Object form;
    private HandlerDescriptor descriptor;

    public PartBinderImpl(Object form, HandlerDescriptor descriptor) {
        this.form = form;
        this.descriptor = descriptor;
    }
    
	public void bindParts(MultipartRequest multipartRequest) throws PartBindingException, ConversionException {
        for (String partName : multipartRequest.getParts().keySet()) {
            Part part = multipartRequest.getParts().get(partName);
            if (part != null) {
                Method partMutator = descriptor.getMutators().get(partName);
                if (partMutator != null) {
                    try {
	                    Class<? extends Converter<?,?>> converterClass = descriptor.getConverters().get(partName);
	                    if (converterClass == null) {
	                        if (partMutator.getParameterTypes().length > 0) {
	                            partMutator.invoke(form, part.getContents());
	                        }
	                    } else {
	                        Converter<?,?> converter = converterClass.newInstance();
	                        if (converter instanceof PartConverter) {
	                            partMutator.invoke(form, ((PartConverter<?>)converter).convert(part));
	                        } else {
	                            throw new ConversionException(partMutator, converterClass);
	                        }
	                    }
                    } catch (IllegalAccessException ex) {
                        throw new PartBindingException(partMutator, ex.getCause());
                    } catch (IllegalArgumentException ex) {
                        throw new PartBindingException(partMutator, ex.getCause());
                    } catch (InstantiationException ex) {
                        throw new PartBindingException(partMutator, ex.getCause());
                    } catch (InvocationTargetException ex) {
                        throw new PartBindingException(partMutator, ex.getCause());
                    }
                }
            }
        }
    
    }

}
