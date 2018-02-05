package com.imaduddinaf.pertaminahealthassistant;

import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class Constant {
    public static final String SHEALTH_STORE_URL = "market://details?id=com.sec.android.app.shealth";
    public static final String DEBUG_TAG = "PHA - DEBUG";
    public static final String ERROR_TAG = "PHA - ERROR";
    public static final String DEFAULT_LOADING_MESSAGE = "Harap tunggu...";

    public static final long TODAY_START_UTC_TIME;
    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    static {
        TODAY_START_UTC_TIME = getTodayStartUtcTime();
    }

    private static long getTodayStartUtcTime() {
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Log.d(Constant.DEBUG_TAG, "Today : " + today.getTimeInMillis());

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    public static long today() {
        return TODAY_START_UTC_TIME;
    }

    public static long yesterday() {
        return today() - ONE_DAY;
    }

    public static long endOfToday() {
        return today() + ONE_DAY;
    }

    public static long lastMonth() {
        return today() - (30 * ONE_DAY);
    }
}
