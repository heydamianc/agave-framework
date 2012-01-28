package co.cdev.agave.conversion;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ISO8601DateTimeParamConverterTest {
    
    private ISO8601DateTimeParamConverter converter;
    private Locale locale;
    
    @Before
    public void setUp() {
        converter = new ISO8601DateTimeParamConverter();
        locale = new Locale("UTC");
    }
    
    @After
    public void tearDown() {
        converter = null;
        locale = null;
    }

    @Test
    public void testDate_withNoSeparators() throws Exception {
        Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 0, 0).getTime();
        
        assertEquals(halloween, converter.convert("20081031", locale));
        assertEquals(halloween, converter.convert("2008305", locale));
    }
    
    @Test
    public void testDate_withSeparators() throws Exception {
        Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 0, 0).getTime();
        
        assertEquals(halloween, converter.convert("2008-10-31", locale));
        assertEquals(halloween, converter.convert("2008-305", locale));
    }
    
    @Test
    public void testDate_withOnlyHoursSpecifiedAndNoSeparators() throws Exception {
        Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 20, 0).getTime();
        
        assertEquals(halloween, converter.convert("20081031T20", locale));
        assertEquals(halloween, converter.convert("2008305T20", locale));
    }
    
    @Test
    public void testDate_withOnlyHoursSpecified() throws Exception {
        Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 20, 0).getTime();
        
        assertEquals(halloween, converter.convert("2008-10-31T20", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20Z", locale));
        assertEquals(halloween, converter.convert("2008-305T20", locale)); 
        assertEquals(halloween, converter.convert("2008-305T20Z", locale));
    }
    
    @Test
    public void testDate_withHoursAndMinutesSpecified() throws Exception {
        Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 20, 35).getTime();
        
        assertEquals(halloween, converter.convert("20081031T2035", locale)); 
        assertEquals(halloween, converter.convert("2008-10-31T2035", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35", locale));
        assertEquals(halloween, converter.convert("20081031T20:35", locale)); 
        assertEquals(halloween, converter.convert("2008-10-31T20:35Z", locale));
        assertEquals(halloween, converter.convert("20081031T20:35Z", locale));
        
        assertEquals(halloween, converter.convert("2008305T2035", locale)); 
        assertEquals(halloween, converter.convert("2008-305T2035", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35", locale));
        assertEquals(halloween, converter.convert("2008305T20:35", locale)); 
        assertEquals(halloween, converter.convert("2008-305T20:35Z", locale));
        assertEquals(halloween, converter.convert("2008305T20:35Z", locale));
    }
    
    @Test
    public void testDate_withHoursMinutesAndSecondsSpecified() throws Exception {
        Date halloween = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 20, 35, 13).getTime();
        
        assertEquals(halloween, converter.convert("20081031T203513", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13", locale));
        assertEquals(halloween, converter.convert("2008-10-31T203513", locale));
        assertEquals(halloween, converter.convert("20081031T20:35:13", locale));
        assertEquals(halloween, converter.convert("20081031T2035:13", locale));
        assertEquals(halloween, converter.convert("20081031T20:3513", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13Z", locale));
        assertEquals(halloween, converter.convert("2008-10-31T203513Z", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:3513Z", locale));
        assertEquals(halloween, converter.convert("2008-10-31T2035:13Z", locale));
        assertEquals(halloween, converter.convert("20081031T20:35:13Z", locale));
        assertEquals(halloween, converter.convert("20081031T2035:13Z", locale));
        assertEquals(halloween, converter.convert("20081031T20:3513Z", locale));
        
        assertEquals(halloween, converter.convert("2008305T203513", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13", locale));
        assertEquals(halloween, converter.convert("2008-305T203513", locale));
        assertEquals(halloween, converter.convert("2008305T20:35:13", locale));
        assertEquals(halloween, converter.convert("2008305T2035:13", locale));
        assertEquals(halloween, converter.convert("2008305T20:3513", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13Z", locale));
        assertEquals(halloween, converter.convert("2008-305T203513Z", locale));
        assertEquals(halloween, converter.convert("2008-305T20:3513Z", locale));
        assertEquals(halloween, converter.convert("2008-305T2035:13Z", locale));
        assertEquals(halloween, converter.convert("2008305T20:35:13Z", locale));
        assertEquals(halloween, converter.convert("2008305T2035:13Z", locale));
        assertEquals(halloween, converter.convert("2008305T20:3513Z", locale));
    }
    
    @Test
    public void testDate_withHoursMinutesSecondsAndTimeZoneSpecified() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(2008, Calendar.OCTOBER, 31, 20, 35, 13);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date halloween = cal.getTime();
        
        assertEquals(halloween, converter.convert("20081031T203513-00", locale));
        assertEquals(halloween, converter.convert("20081031T203513-0000", locale));
        assertEquals(halloween, converter.convert("20081031T203513+00", locale));
        assertEquals(halloween, converter.convert("20081031T203513+0000", locale));
        assertEquals(halloween, converter.convert("20081031T193513-01", locale));
        assertEquals(halloween, converter.convert("20081031T193513-0100", locale));
        assertEquals(halloween, converter.convert("20081031T213513+01", locale));
        assertEquals(halloween, converter.convert("20081031T213513+0100", locale));
        assertEquals(halloween, converter.convert("2008-10-31T203513-00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T203513-0000", locale));
        assertEquals(halloween, converter.convert("2008-10-31T203513+00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T203513+0000", locale));
        assertEquals(halloween, converter.convert("2008-10-31T193513-01", locale));
        assertEquals(halloween, converter.convert("2008-10-31T193513-0100", locale));
        assertEquals(halloween, converter.convert("2008-10-31T213513+01", locale));
        assertEquals(halloween, converter.convert("2008-10-31T213513+0100", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:3513-00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:3513-0000", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:3513+00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:3513+0000", locale));
        assertEquals(halloween, converter.convert("2008-10-31T19:3513-01", locale));
        assertEquals(halloween, converter.convert("2008-10-31T19:3513-0100", locale));
        assertEquals(halloween, converter.convert("2008-10-31T21:3513+01", locale));
        assertEquals(halloween, converter.convert("2008-10-31T21:3513+0100", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13-00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13-0000", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13+00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13+0000", locale));
        assertEquals(halloween, converter.convert("2008-10-31T19:35:13-01", locale));
        assertEquals(halloween, converter.convert("2008-10-31T19:35:13-0100", locale));
        assertEquals(halloween, converter.convert("2008-10-31T21:35:13+01", locale));
        assertEquals(halloween, converter.convert("2008-10-31T21:35:13+0100", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13-00:00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T20:35:13+00:00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T19:35:13-01:00", locale));
        assertEquals(halloween, converter.convert("2008-10-31T21:35:13+01:00", locale));
        
        assertEquals(halloween, converter.convert("2008305T203513-00", locale));
        assertEquals(halloween, converter.convert("2008305T203513-0000", locale));
        assertEquals(halloween, converter.convert("2008305T203513+00", locale));
        assertEquals(halloween, converter.convert("2008305T203513+0000", locale));
        assertEquals(halloween, converter.convert("2008305T193513-01", locale));
        assertEquals(halloween, converter.convert("2008305T193513-0100", locale));
        assertEquals(halloween, converter.convert("2008305T213513+01", locale));
        assertEquals(halloween, converter.convert("2008305T213513+0100", locale));
        assertEquals(halloween, converter.convert("2008-305T203513-00", locale));
        assertEquals(halloween, converter.convert("2008-305T203513-0000", locale));
        assertEquals(halloween, converter.convert("2008-305T203513+00", locale));
        assertEquals(halloween, converter.convert("2008-305T203513+0000", locale));
        assertEquals(halloween, converter.convert("2008-305T193513-01", locale));
        assertEquals(halloween, converter.convert("2008-305T193513-0100", locale));
        assertEquals(halloween, converter.convert("2008-305T213513+01", locale));
        assertEquals(halloween, converter.convert("2008-305T213513+0100", locale));
        assertEquals(halloween, converter.convert("2008-305T20:3513-00", locale));
        assertEquals(halloween, converter.convert("2008-305T20:3513-0000", locale));
        assertEquals(halloween, converter.convert("2008-305T20:3513+00", locale));
        assertEquals(halloween, converter.convert("2008-305T20:3513+0000", locale));
        assertEquals(halloween, converter.convert("2008-305T19:3513-01", locale));
        assertEquals(halloween, converter.convert("2008-305T19:3513-0100", locale));
        assertEquals(halloween, converter.convert("2008-305T21:3513+01", locale));
        assertEquals(halloween, converter.convert("2008-305T21:3513+0100", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13-00", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13-0000", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13+00", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13+0000", locale));
        assertEquals(halloween, converter.convert("2008-305T19:35:13-01", locale));
        assertEquals(halloween, converter.convert("2008-305T19:35:13-0100", locale));
        assertEquals(halloween, converter.convert("2008-305T21:35:13+01", locale));
        assertEquals(halloween, converter.convert("2008-305T21:35:13+0100", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13-00:00", locale));
        assertEquals(halloween, converter.convert("2008-305T20:35:13+00:00", locale));
        assertEquals(halloween, converter.convert("2008-305T19:35:13-01:00", locale));
        assertEquals(halloween, converter.convert("2008-305T21:35:13+01:00", locale));
    }
}
