package co.cdev.agave;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class support a profile of the functionality defined in the ISO/FDIS 
 * 8601:2000(E) date format. The international standard describes multiple ways
 * to specify a date, however this class is only concerned with specifying a date
 * with a year, month, date, and optionally a time specified as hours, minutes, 
 * and optionall seconds. The time may also have a time zone designator, specified
 * with a plus or minus and either 4 or 2 digits that follow to represent the 
 * hourly offset along with optional minutes.
 * 
 * The following examples should sufficiently illustrate the subset of the standard
 * that this class supports:
 * 
 * <pre>
 * YYYYMMDD                    19850412
 * YYYYDDD                     1985123 (123rd day of the year)
 * YYYYMMDDThh                 19850412T10
 * YYYYMMDDThhmm               19850412T1015
 * YYYYMMDDThhmmZ              19850412T1015Z
 * YYYYMMDDThhmmss             19850412T101530        
 * YYYYMMDDThhmmssZ            19850412T101530Z
 * YYYYMMDDThhmmss±hhmm        19850412T101530+0400   
 * YYYYMMDDThhmmss±hh          19850412T101530+04
 * YYYY-MM-DD                  1985-04-12
 * YYYY-DDD                    1985-123 (123rd day of the year)
 * YYYY-MM-DDThh               1985-04-12T10
 * YYYY-MM-DDThhZ              1985-04-12T10Z
 * YYYY-MM-DDThh:mm            1985-04-12T10:15
 * YYYY-MM-DDThh:mmZ           1985-04-12T10:15Z
 * YYYY-MM-DDThh:mm:ss         1985-04-12T10:15:30 
 * YYYY-MM-DDThh:mm:ssZ        1985-04-12T10:15:30Z
 * YYYY-MM-DDThh:mm:ss±hh:mm   1985-04-12T10:15:30+04:00
 * YYYY-MM-DDThh:mm:ss±hhmm    1985-04-12T10:15:30+0400
 * YYYY-MM-DDThh:mm:ss±hh      1985-04-12T10:15:30+04</pre>
 *
 * The date must be specified in full, and must be exactly 8 digits long, with an
 * optional dash to separate each component of the date. Optionally specify a date
 * as a year followed by the ordinal number of the day in the year. Also, you can 
 * specify the date as the year, followed by the week of the year, followed by the
 * day of the week, were Monday is 1, Tuesday is 2, ..., Sunday is 7.
 * 
 * A great degree of granularity can be left off in the time designator. Any missing
 * component indicates 00, eg. 18 implies 6 o'clock PM, with 0 seconds trailing. An
 * optional colon can denote different components of the time.
 * 
 * If the time zone is missing, that indicates UTC time (as does a 'Z'). It can 
 * be specified with a plus or a minus, followed by a trailing number of hours
 * or minutes. The specification is relative to UTC, and the granularity depends
 * on the number of specified digits. 
 *
 * Note that this also corresponds to RFC 3339 Timestamps: 
 *   
 *   http://www.ietf.org/rfc/rfc3339.txt
 *
 * @author <a href="damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class ISO8601DateFormat extends SimpleDateFormat {

    private static final long serialVersionUID = 1L;
    
    private final Locale locale;

    public ISO8601DateFormat(Locale locale) {
        super("yyyy-MM-dd'T'HH:mm:ssZ", locale);
        this.locale = locale;
        
        super.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Date parse(String input) throws ParseException {
        return (Date) parseObject(input);
    }

    @Override
    public Object parseObject(String input) throws ParseException {
        return parseObject(input, null);
    }

    @Override
    public Object parseObject(String input, ParsePosition parsePosition) {
        Date parsedDate = null;
        
        if (input != null && !"".equals(input)) {
            String formatString = null;
            String actualInput = null;
            
            String[] components = input.split("T");
            
            if (components.length > 0) {
                components[0] = components[0].replace("-", "");

                if (components[0].contains("W")) {
                    
                    // Date as year, week in year, day of week (starting with Monday as 1
                    // and ending with Sunday as 7
                    
                    formatString = "yyyy'W'wwEEE";
                    actualInput = components[0].substring(0, 7);
                    
                    DateFormatSymbols symbols = new DateFormatSymbols(locale);
                    int dayOfWeek = Integer.valueOf(components[0].substring(7, 8));
                    
                    if (dayOfWeek == 1) {
                        actualInput += symbols.getWeekdays()[Calendar.MONDAY];
                    } else if (dayOfWeek == 2) {
                        actualInput += symbols.getWeekdays()[Calendar.TUESDAY];
                    } else if (dayOfWeek == 3) {
                        actualInput += symbols.getWeekdays()[Calendar.WEDNESDAY];
                    } else if (dayOfWeek == 4) {
                        actualInput += symbols.getWeekdays()[Calendar.THURSDAY];
                    } else if (dayOfWeek == 5) {
                        actualInput += symbols.getWeekdays()[Calendar.FRIDAY];
                    } else if (dayOfWeek == 6) {
                        actualInput += symbols.getWeekdays()[Calendar.SATURDAY];
                    } else if (dayOfWeek == 7) {
                        actualInput += symbols.getWeekdays()[Calendar.SUNDAY];
                    }
                } else if (components[0].length() == 8) { 
                    
                    // Date as year, month, day
                    
                    formatString = "yyyyMMdd";
                    actualInput = components[0];
                } else if (components[0].length() == 7) {
                    
                    // Date as year and day of year
                    
                    formatString = "yyyyDDD";
                    actualInput = components[0];
                }
                
                if (formatString != null && actualInput != null) {
                    
                    // Optional Time and Time Zone
                    
                    if (components.length > 1) {
                        components[1] = components[1].replace(":", "");
                        components[1] = components[1].replace("Z", "");
                        
                        String timePart = null;
                        String timeZonePart = null;
                        
                        if (components[1].contains("+")) {
                            String[] timeComponents = components[1].split("\\+");
                            timePart = timeComponents[0];
                            timeZonePart = "+" + timeComponents[1];
                        } else if (components[1].contains("-")) {
                            String[] timeComponents = components[1].split("\\-");
                            timePart = timeComponents[0];
                            timeZonePart = "-" + timeComponents[1];
                        } else {
                            timePart = components[1];
                        }
                        
                        // Time
                        
                        if (timePart != null) {
                            while (timePart.length() < 4) {
                                timePart += "0";
                            }
                            
                            if (timePart.length() > 6) {
                                timePart = timePart.substring(0, 6);
                            }
                            
                            actualInput += timePart;
                            
                            if (timePart.length() == 4) {
                                formatString += "HHmm";
                            } else if (timePart.length() == 6) {
                                formatString += "HHmmss"; 
                            }
                            
                            // Time Zone
                        
                            if (timeZonePart != null) {
                                if (timeZonePart.length() == 1) {
                                    timeZonePart = null;
                                } else {
                                    while (timeZonePart.length() < 5) {
                                        timeZonePart += "0";
                                    }
                                    
                                    actualInput += timeZonePart;
                                    formatString += "Z";
                                }
                            }
                        }
                    }
                }
            }
            
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            
            try {
                parsedDate = format.parse(actualInput);
                parsedDate.setTime(parsedDate.getTime() / 1000 * 1000); // shave off milliseconds
            } catch (ParseException e) {
                // do nothing on invalid input
            }
        }
        
        return parsedDate;
    }



}
