package com.imaduddinaf.pertaminahealthassistant.shealth.reader;

import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.shealth.model.StepDailyTrend;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDataUtil;
import com.samsung.android.sdk.healthdata.HealthDevice;
import com.samsung.android.sdk.healthdata.HealthDeviceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;

public class StepCountReader extends SHealthReader {
    public static final String STEP_SUMMARY_DATA_TYPE_NAME = "com.samsung.shealth.step_daily_trend";

    private static final String ALIAS_TOTAL_COUNT = "count";

    public StepCountReader(HealthDataStore store) {
        super(store);
    }

    public void readStepCount(final long startTime, Consumer<StepDailyTrend> onSuccess) {

        // Get sum of step counts by device
        HealthDataResolver.AggregateRequest request = new HealthDataResolver.AggregateRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.SUM, HealthConstants.StepCount.COUNT, HealthConstants.StepCount.COUNT)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.SUM, HealthConstants.StepCount.CALORIE, HealthConstants.StepCount.CALORIE)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.SUM, HealthConstants.StepCount.DISTANCE, HealthConstants.StepCount.DISTANCE)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.SUM, HealthConstants.StepCount.SPEED, HealthConstants.StepCount.SPEED)
                .addGroup(HealthConstants.StepCount.DEVICE_UUID, ALIAS_DEVICE_UUID)
                .setLocalTimeRange(HealthConstants.StepCount.START_TIME, HealthConstants.StepCount.TIME_OFFSET,
                        startTime, startTime + Constant.ONE_DAY)
                .setSort(ALIAS_TOTAL_COUNT, HealthDataResolver.SortOrder.DESC)
                .setSourceDevices(getDevicesUuid(getTypeForStepAndHeart()))
                .build();

        try {
            healthDataResolver.aggregate(request).setResultListener(result -> {
                StepDailyTrend stepDailyTrend = new StepDailyTrend();
                String deviceUuid = null;

                try {
                    Iterator<HealthData> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        stepDailyTrend = new StepDailyTrend(
                                data.getInt(HealthConstants.StepCount.COUNT),
                                data.getDouble(HealthConstants.StepCount.CALORIE),
                                data.getDouble(HealthConstants.StepCount.SPEED),
                                data.getDouble(HealthConstants.StepCount.DISTANCE));
                        deviceUuid = data.getString(HealthConstants.StepCount.UUID);
                        Log.d(Constant.DEBUG_TAG, "got health data " + data.getContentValues().valueSet().toString());
                    }
                } finally {
                    result.close();
                }

                if (onSuccess != null) {
                    onSuccess.accept(stepDailyTrend);
                }

                Log.d(Constant.DEBUG_TAG, "Getting step count success " + stepDailyTrend.getTotalStep() + " - " + stepDailyTrend.getTotalCalorie());
            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting step count fails.", e);
        }
    }
}

