package com.yellowtwig.utils.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by marcprive on 06-14-15.
 */
public class TimeZoneUtil {
    static String convertToLocalString( Date date ){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm Z", new Locale("nl", "NL"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
        String formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }
}
