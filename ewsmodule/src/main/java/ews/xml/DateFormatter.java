package ews.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by marcprive on 05-25-15.
 */
public class DateFormatter {
    private static String formatDate(Date date, String format) {
        final DateFormat utcFormatter = createDateFormat(format);
        return utcFormatter.format(date);
    }

    private static DateFormat createDateFormat(String format) {
        final DateFormat utcFormatter = new SimpleDateFormat(format);
        utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormatter;
    }
}
