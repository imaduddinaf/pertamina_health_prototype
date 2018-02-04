package com.imaduddinaf.pertaminahealthassistant.shealth.reader;

import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.shealth.model.HeartRateData;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Imaduddin Al Fikri on 04-Feb-18.
 */

public class HeartRateReader extends SHealthReader {

    public HeartRateReader(HealthDataStore store) {
        super(store);
    }

    public void readLastHeartRate(final long startTime, final long endTime, Consumer<Double> onSuccess) {

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        HealthConstants.HeartRate.HEART_RATE
                })
                .setTimeAfter(startTime)
                .setTimeBefore(endTime)
                .setSort(HealthConstants.HeartRate.START_TIME, HealthDataResolver.SortOrder.DESC)
                .build();

        try {
            healthDataResolver.read(request).setResultListener(result -> {
                Double lastHeartRate = 0.0;
                try {
                    Iterator<HealthData> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        lastHeartRate = data.getDouble(HealthConstants.HeartRate.HEART_RATE);
                        Log.d(Constant.DEBUG_TAG, "Got heart rate binning " + data.getContentValues().valueSet().toString());
                    }
                } finally {
                    result.close();
                }

                if (onSuccess != null) {
                    onSuccess.accept(lastHeartRate);
                }
                Log.d(Constant.DEBUG_TAG, "Getting heart rate binning success ");
            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting heart rate binning fails.", e);
        }
    }

    public void readTotalHeartRate(final long startTime, final long endTime, Consumer<HeartRateData> onSuccess) {
        // Get sum of step counts by device
        HealthDataResolver.AggregateRequest request = new HealthDataResolver.AggregateRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.COUNT, HealthConstants.HeartRate.HEART_BEAT_COUNT, HealthConstants.HeartRate.HEART_BEAT_COUNT)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.AVG, HealthConstants.HeartRate.HEART_RATE, HealthConstants.HeartRate.HEART_RATE)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.AVG, HealthConstants.HeartRate.COMMENT, HealthConstants.HeartRate.COMMENT)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.MIN, HealthConstants.HeartRate.MIN, HealthConstants.HeartRate.MIN)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.MAX, HealthConstants.HeartRate.MAX, HealthConstants.HeartRate.MAX)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.SUM, HealthConstants.HeartRate.BINNING_DATA, HealthConstants.HeartRate.BINNING_DATA)
                .addGroup(HealthConstants.StepCount.DEVICE_UUID, ALIAS_DEVICE_UUID)
                .setLocalTimeRange(HealthConstants.HeartRate.START_TIME,
                        HealthConstants.HeartRate.TIME_OFFSET,
                        startTime,
                        endTime)
                .setSourceDevices(getDevicesUuid())
                .build();

        try {
            healthDataResolver.aggregate(request).setResultListener(result -> {
                HeartRateData heartRateData = new HeartRateData();

                try {
                    Iterator<HealthData> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        heartRateData = new HeartRateData(
                                data.getDouble(HealthConstants.HeartRate.HEART_BEAT_COUNT),
                                data.getDouble(HealthConstants.HeartRate.HEART_RATE),
                                data.getDouble(HealthConstants.HeartRate.MIN),
                                data.getDouble(HealthConstants.HeartRate.MAX),
                                data.getDouble(HealthConstants.HeartRate.COMMENT));
                        Log.d(Constant.DEBUG_TAG, "Got heart rate " + data.getContentValues().valueSet().toString());
                    }
                } finally {
                    result.close();
                }

                if (onSuccess != null) {
                    onSuccess.accept(heartRateData);
                }

                Log.d(Constant.DEBUG_TAG, "Getting heart rate success ");
            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting heart rate fails.", e);
        }
    }
}
