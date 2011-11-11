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
package co.cdev.agave.conversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import co.cdev.agave.exception.ConversionException;

/**
 * Converts a supplied date string into a {@code java.util.Date} by leveraging
 * {@code DateFormat.format()}'s short locale-specific implementation. 
 * 
 * <p>An example of its usage can be seen in this class's unit test:
 * 
 * <pre>@Test
 *public void testConvert() throws Exception {
 *    Assert.assertEquals(new GregorianCalendar(2008, Calendar.OCTOBER, 31).getTime(),
 *        dateConverter.convert("10/31/2008", Locale.US));
 *    Assert.assertEquals(new GregorianCalendar(1982, Calendar.MAY, 7).getTime(),
 *        dateConverter.convert("7/5/82", Locale.UK));
 *}</pre>
 *</p>
 *
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class DateParamConverter implements StringParamConverter<Date> {

    public Date convert(String input, Locale locale) throws ConversionException {
        try {
            DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            return format.parse(input);
        } catch (ParseException ex) {
            throw new ConversionException("Could not convert " + input + " to a Date object", ex);
        }
    }

}
