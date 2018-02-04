package com.imaduddinaf.pertaminahealthassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    public static String getFormattedTime(long startTime) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd (E)", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(startTime);
    }
}
