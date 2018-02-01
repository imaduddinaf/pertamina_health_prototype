package com.imaduddinaf.pertaminahealthassistant.core;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class Helper {
    public static final String DEBUG_TAG = "PHA - DEBUG";
    public static final String ERROR_TAG = "PHA - ERROR";

    public static String getFormattedTime(long startTime) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd (E)", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(startTime);
    }
}
