/*
 * Copyright (c) 2006 - 2007 Damian Carrillo.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *	* Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 *	* Redistributions in binary form must reproduce the above copyright notice,
 *	  this list of conditions and the following disclaimer in the documentation
 *	  and/or other materials provided with the distribution.
 *	* Neither the name of the <ORGANIZATION> nor the names of its contributors
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
package agave.converters;

import agave.HandlerContext;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// TODO DELETE ME
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {F.class, G.class, H.class} = F(G(H(x)))
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 * @param T the type of the value supplied by the form
 * @since 1.0
 */
public class ConverterChain {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConverterChain.class);
	
	private LinkedList<Converter<?,?>> converters;
	
	/**
	 *
	 * @param converterClasses
	 */
	public ConverterChain(final Class<? extends Converter>[] converterClasses) 
	throws ConversionException {
		converters = new LinkedList<Converter<?,?>>();
		List<Class<? extends Converter>> reversedConverterClasses = 
			Arrays.asList(converterClasses);
		Collections.reverse(reversedConverterClasses);
		
		// validate the chain
		
		try {
			Class<?> returnType = null;
			Class<?> paramType = null;
			for (Class<? extends Converter> converterClass : reversedConverterClasses) {
					
				for (Method method : converterClass.getMethods()) {
					if (method.getName().equals("convert")) {
						Class<?>[] paramTypes = method.getParameterTypes();
						if (paramTypes != null && paramTypes.length >= 2) {
							returnType = method.getReturnType();
							
							if (paramType != null && !returnType.equals(paramType)) {
								StringBuilder msg = new StringBuilder();
								msg.append("The return type of ");
								msg.append(converterClass.getName());
								msg.append(" is not the same as the previous");
								msg.append(" converter's argument in the chain (");
								msg.append(returnType.getName());
								msg.append(" vs. ");
								msg.append(paramType.getName() + ")");
								throw new ConversionException(msg.toString());
							}
							
							paramType = paramTypes[1];
							Converter<?,?> instance = converterClass.newInstance();
							converters.addFirst(instance);
						}
						break;
					}
				}
				
			}
		} catch (InstantiationException ex) {
			throw new ConversionException(ex);
		} catch (IllegalAccessException ex) {
			throw new ConversionException(ex);
		}
	}
	
	/**
	 *
	 * @param context the HandlerContext in which to operate
	 * @param converters An array of converter classes
	 * @param initialValue the initial string value supplied by some form post
	 * @return the resultant object
	 */
	public Object convertAll(final HandlerContext context, final Object initialValue) 
	throws ConversionException {
		Class<?> paramType = null;
		
		Object value = initialValue;
		Method convertMethod = null;
		StringBuilder visual = new StringBuilder();

		try {
			for (Converter<?,?> converter : converters) {
				visual.append(converter.getClass().getName() + " ");
				for (Method method : converter.getClass().getMethods()) {
					if (method.getName().equals("convert")) {
						paramType = method.getParameterTypes()[1];
						value = method.invoke(converter, context, paramType.cast(value));
						break;
					}
				}
			}
			LOGGER.debug("Converter Chain: " + visual.toString());
		} catch (IllegalAccessException ex) {
			throw new ConversionException(ex);
		} catch (InvocationTargetException ex) {
			throw new ConversionException(ex);
		}
		
		return value;
	}
	
}
