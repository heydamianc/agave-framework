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

import agave.exception.ConversionException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:damianarrillo@gmail.com">Damian Carrillo</a>
 */
public class DateTimeConverterTest {

    private DateTimeConverter converter;

    @Before
    public void setup() throws Exception {
        converter = new DateTimeConverter();
    }

    @Test
    public void testConvert() throws Exception {
        Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 8, 32).getTime();
        Assert.assertEquals(halloween, converter.convert("10/31/2008 8:32 am", Locale.US));
        Assert.assertEquals(halloween, converter.convert("10/31/2008 8:32am", Locale.US));
        Assert.assertEquals(halloween, converter.convert("10/31/2008     8:32AM    ", Locale.US));
        Assert.assertEquals(halloween, converter.convert("31/10/2008 8:32am", Locale.UK));
    }

    @Test(expected = ConversionException.class)
    public void testConvertWithInvalidDate() throws Exception {
        converter.convert("this is not a valid date", Locale.getDefault());
    }

}
