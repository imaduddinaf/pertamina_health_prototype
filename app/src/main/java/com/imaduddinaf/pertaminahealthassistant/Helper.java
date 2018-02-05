package com.imaduddinaf.pertaminahealthassistant;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class Helper {

    public static String[] concat(String[] first, String second[]) {
        Collection<String> collection = new ArrayList<String>();
        collection.addAll(Arrays.asList(first));
        collection.addAll(Arrays.asList(second));

        return collection.toArray(new String[] {});
    }

    public static String getFormattedTime(long time) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd (E)", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(time);
    }

    public static Double getHourBetween(long startTime, long endTime) {
        long secs = (endTime - startTime) / 1000;
        Double hours = Double.valueOf(secs / 3600);
        secs = secs % 3600;
        Double mins = Double.valueOf(secs / 60);
        secs = secs % 60;

        return hours + (mins / 60);
    }
}
