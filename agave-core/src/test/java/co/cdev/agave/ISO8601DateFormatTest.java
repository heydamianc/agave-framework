package co.cdev.agave;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

public class ISO8601DateFormatTest {

    private DateFormat dateFormat;
    
    @Before
    public void setUp() {
       dateFormat = new ISO8601DateFormat(Locale.US);
    }
    
    @Test
    public void testFormat() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        cal.set(2012, Calendar.JANUARY, 12, 8, 0, 0);
        
        assertEquals("2012-01-12T08:00:00+0000", dateFormat.format(cal.getTime()));
        
        cal.set(2012, Calendar.JANUARY, 23, 22, 0, 0);
        
        assertEquals("2012-01-23T22:00:00+0000", dateFormat.format(cal.getTime()));
    }
    
    @Test
    public void testConvert() throws ParseException {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        cal.set(2012, Calendar.JANUARY, 23, 22, 0, 0);
        
        Date a = cal.getTime();
        a.setTime(a.getTime() / 1000 * 1000);
        
        Date b = (Date) dateFormat.parse("2012-01-23T22:00:00+0000");
        
        assertEquals(a, b);
    }
    
}
