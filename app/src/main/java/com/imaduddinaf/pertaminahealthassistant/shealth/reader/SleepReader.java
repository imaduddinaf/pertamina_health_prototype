package com.imaduddinaf.pertaminahealthassistant.shealth.reader;

import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.Helper;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by Imaduddin Al Fikri on 04-Feb-18.
 */

public class SleepReader extends SHealthReader {

    public SleepReader(HealthDataStore store) {
        super(store);
    }

    public void readLastSleep(final long startTime, final long endTime, Consumer<Double> onSuccess) {

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.Sleep.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        HealthConstants.Sleep.START_TIME,
                        HealthConstants.Sleep.END_TIME
                })
                .setTimeAfter(startTime)
                .setTimeBefore(endTime)
                .setSort(HealthConstants.Sleep.START_TIME, HealthDataResolver.SortOrder.DESC)
                .build();

        try {
            healthDataResolver.read(request).setResultListener(result -> {
                long startSleepTime = 0;
                long endSleepTime = 0;
                Double sleepTime = 0.0;
                try {
                    Iterator<HealthData> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        startSleepTime = data.getLong(HealthConstants.Sleep.START_TIME);
                        endSleepTime = data.getLong(HealthConstants.Sleep.END_TIME);
                        Log.d(Constant.DEBUG_TAG, "Got last sleep " + data.getContentValues().valueSet().toString());
                    }
                } finally {
                    result.close();
                }

                if (startSleepTime > 0 && endSleepTime > 0 && endSleepTime > startSleepTime) {
                    sleepTime = Helper.getHourBetween(startSleepTime, endSleepTime);
                }

                if (onSuccess != null) {
                    onSuccess.accept(sleepTime > 0 ? sleepTime : 0);
                }
                Log.d(Constant.DEBUG_TAG, "Getting last sleep success ");
            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting last sleep fails.", e);
        }
    }
}
