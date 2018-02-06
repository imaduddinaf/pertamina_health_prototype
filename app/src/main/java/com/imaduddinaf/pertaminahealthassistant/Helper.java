package com.imaduddinaf.pertaminahealthassistant;

import android.widget.Toast;

import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import static com.imaduddinaf.pertaminahealthassistant.Helper.DateFormat.SIMPLE_REVERSED_WITH_DASH;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class Helper {

    public static final String UNDERCONSTRUCTION_TEXT = "Fitur sedang dalam tahap pengembangan";
    public static final String NEED_LOGIN_TEXT = "Silahkan login terlebih dahulu";

    public static class DateFormat {
        public static String SIMPLE_REVERSED_WITH_DASH = "yyyy-MM-dd";
        public static String COMPLETE = "EEEE, d MMMM yyyy";
    }

    public static String[] concat(String[] first, String second[]) {
        Collection<String> collection = new ArrayList<String>();
        collection.addAll(Arrays.asList(first));
        collection.addAll(Arrays.asList(second));

        return collection.toArray(new String[] {});
    }

    public static String getFormattedTime(long time) {
        return getFormattedTime(time, null);
    }

    public static String getFormattedTime(long time, String format) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format != null && !format.isEmpty() ? format : DateFormat.SIMPLE_REVERSED_WITH_DASH, Locale.forLanguageTag("id-ID"));
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

    public static void showUnderConstructionToast(BaseActivity activity) {
        showMessage(activity, UNDERCONSTRUCTION_TEXT);
    }

    public static void showNeedLoginToast(BaseActivity activity) {
        showMessage(activity, NEED_LOGIN_TEXT);
    }

    public static void showMessage(BaseActivity activity, String message) {
        Toast.makeText(activity,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getStringOrEmpty(Integer value) {
        return getStringOrEmpty(value, null);
    }

    public static String getStringOrEmpty(Integer value, String tail) {
        String displayedTail = tail == null || tail.isEmpty() ? "" : " " + tail;
        return value != null && value > 0 ? value.toString() + displayedTail : "-";
    }

    public static String getStringOrEmpty(Double value) {
        return getStringOrEmpty(value, null);
    }

    public static String getStringOrEmpty(Double value, String format) {
        return getStringOrEmpty(value, format, null);
    }

    public static String getStringOrEmpty(Double value, String format, String tail) {
        String displayedTail = tail == null || tail.isEmpty() ? "" : " " + tail;
        DecimalFormat decimalFormat = new DecimalFormat(format != null && !format.isEmpty() ? format : "#.#");

        return value > 0 ? decimalFormat.format(value) + displayedTail : "-";
    }
}
