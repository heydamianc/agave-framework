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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.cdev.agave.exception.ConversionException;

/**
 * Converts a supplied date string into a {@code java.util.Date} and time by leveraging
 * {@code DateFormat.format()}'s short locale-specific implementation. 
 * 
 * <p>An example of its usage can be seen in this class's unit test:
 * 
 * <pre>@Test
 *public void testConvert() throws Exception {
 *    Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 8, 32).getTime();
 *    Assert.assertEquals(halloween, converter.convert("10/31/2008 8:32 am", Locale.US));
 *    Assert.assertEquals(halloween, converter.convert("10/31/2008 8:32am", Locale.US));
 *    Assert.assertEquals(halloween, converter.convert("10/31/2008     8:32AM    ", Locale.US));
 *    Assert.assertEquals(halloween, converter.convert("31/10/2008 8:32am", Locale.UK));
 *}</pre>
 * 
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class DateTimeParamConverter implements StringParamConverter<Date> {

    private static final Pattern timePattern = Pattern.compile("(.*)([AaPp][Mm]?)(.*)");

    @Override
    public Date convert(String input, Locale locale) throws ConversionException {
        try {
            String formattedInput = input;
            Matcher timeMatcher = timePattern.matcher(input);
            if (timeMatcher.matches() && timeMatcher.groupCount() >= 2) {
                formattedInput = timeMatcher.group(1).trim() + " " + timeMatcher.group(2);
            }
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
            return format.parse(formattedInput);
        } catch (ParseException ex) {
            throw new ConversionException("Could not convert " + input + " to a Date object (with time)", ex);
        }
    }

}
