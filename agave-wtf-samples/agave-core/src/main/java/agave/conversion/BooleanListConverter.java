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
package agave.conversion;

import java.util.ArrayList;
import java.util.List;

import agave.exception.ConversionException;

/**
 * Converts an input {@code String} into a {@code Boolean} object.
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class BooleanListConverter implements ListConverter<Boolean> {

    BooleanConverter converter = new BooleanConverter();

    /**
     * Performs the conversion.
     * @param inputs the input parameters as a {@code String}. Supported inputs are (none are case sensitive): 
     *  <p>
     *    <strong>Input values that resolve to {@code Boolean.TRUE}:</strong>
     *    <ul>
     *      <li>true</li>
     *      <li>t</li>
     *      <li>1</li>
     *    </ul>
     *  </p>
     *  <p>
     *    <strong>Input values that resolve to {@code Boolean.FALSE}:</strong>
     *    <ul>
     *      <li>false</li>
     *      <li>f</li>
     *      <li>0</li>
     *    </ul>
     *  </p>
     * @return a {@code Boolean} object List that is guaranteed to never be null
     * @throws ConversionException 
     */ 
    public List<Boolean> convert(String[] inputs) throws ConversionException {
        List<Boolean> convertedValues = new ArrayList<Boolean>();
        if (inputs != null) {
            for (String input : inputs) {
                convertedValues.add(converter.convert(input));
            }
        }
        return convertedValues;
    }

}
