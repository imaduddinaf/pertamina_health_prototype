package com.imaduddinaf.pertaminahealthassistant.shealth.reader;

import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;

import java.util.Iterator;
import java.util.function.Consumer;

public class WeightReader extends SHealthReader {

    public WeightReader(HealthDataStore store) {
        super(store);
    }

    public void readLastWeight(final long startTime, final long endTime, Consumer<Double> onSuccess) {

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.Weight.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        HealthConstants.Weight.WEIGHT
                })
                .setTimeAfter(startTime)
                .setTimeBefore(endTime)
                .setSort(HealthConstants.HeartRate.START_TIME, HealthDataResolver.SortOrder.DESC)
                .build();

        try {
            healthDataResolver.read(request).setResultListener(result -> {
                Double lastWeight = 0.0;
                try {
                    Iterator<HealthData> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        lastWeight = data.getDouble(HealthConstants.Weight.WEIGHT);
                        Log.d(Constant.DEBUG_TAG, "Got last weight " + data.getContentValues().valueSet().toString());
                    }
                } finally {
                    result.close();
                }

                if (onSuccess != null) {
                    onSuccess.accept(lastWeight);
                }
                Log.d(Constant.DEBUG_TAG, "Getting last weight success ");
            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting last weight fails.", e);
        }
    }
}
