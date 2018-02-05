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

    private static final String PROPERTY_TIME = "day_time";
    private static final String PROPERTY_COUNT = "count";
    private static final String PROPERTY_BINNING_DATA = "binning_data";
    private static final String ALIAS_TOTAL_COUNT = "count";
    private static final String ALIAS_BINNING_TIME = "binning_time";

    public StepCountReader(HealthDataStore store) {
        super(store);
    }

    // Get the daily total step count of a specified day
    public void requestDailyStepCount(long startTime) {
        if (startTime >= Constant.TODAY_START_UTC_TIME) {
            // Get today step count
            readStepCount(startTime, null);
        } else {
            // Get historical step count
            readStepDailyTrend(startTime, null);
        }
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
                                data.getDouble(HealthConstants.StepCount.DISTANCE),
                                null);
                        deviceUuid = data.getString(HealthConstants.StepCount.UUID);
                        Log.d(Constant.DEBUG_TAG, "got health data " + data.getContentValues().valueSet().toString());
                    }
                } finally {
                    result.close();
                }

                if (onSuccess != null) {
                    onSuccess.accept(stepDailyTrend);
                }

                if (deviceUuid != null) {
                    readStepCountBinning(startTime, deviceUuid);
                }

                Log.d(Constant.DEBUG_TAG, "Getting step count success " + stepDailyTrend.getTotalStep() + " - " + stepDailyTrend.getTotalCalorie());
            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting step count fails.", e);
        }
    }

    public void readStepDailyTrend(final long startTime, Consumer<StepDailyTrend> onSuccess) {

        HealthDataResolver.Filter filter = HealthDataResolver.Filter.and(HealthDataResolver.Filter.eq(PROPERTY_TIME, startTime),
                // filtering source type "combined(-2)"
                HealthDataResolver.Filter.eq("source_type", -2));

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(STEP_SUMMARY_DATA_TYPE_NAME)
                .setProperties(new String[]{
                        PROPERTY_COUNT,
                        PROPERTY_BINNING_DATA})
                .setFilter(filter)
                .setSourceDevices(getDevicesUuid(getTypeForStepAndHeart()))
                .build();

        try {
            healthDataResolver.read(request).setResultListener(result -> {
                int totalCount = 0;
                List<StepBinningData> binningDataList = Collections.emptyList();

                try {
                    Iterator<HealthData> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        totalCount = data.getInt(PROPERTY_COUNT);
                        byte[] binningData = data.getBlob(PROPERTY_BINNING_DATA);
                        binningDataList = getBinningData(binningData);
                        Log.d(Constant.DEBUG_TAG, "Got step trend binning " + data.getContentValues().valueSet().toString());
                    }
                } finally {
                    result.close();
                }

                if (onSuccess != null) {
                    onSuccess.accept(new StepDailyTrend(
                            totalCount,
                            null,
                            null,
                            null,
                            binningDataList));
                }

                Log.d(Constant.DEBUG_TAG, "Getting daily step trend success " + totalCount);

            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting daily step trend fails.", e);
        }
    }

    private void readStepCountBinning(final long startTime, String deviceUuid) {

        HealthDataResolver.Filter filter = HealthDataResolver.Filter.eq(HealthConstants.StepCount.DEVICE_UUID, deviceUuid);

        // Get 10 minute binning data of a particular device
        HealthDataResolver.AggregateRequest request = new HealthDataResolver.AggregateRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .addFunction(HealthDataResolver.AggregateRequest.AggregateFunction.SUM, HealthConstants.StepCount.COUNT, ALIAS_TOTAL_COUNT)
                .setTimeGroup(HealthDataResolver.AggregateRequest.TimeGroupUnit.MINUTELY, 10, HealthConstants.StepCount.START_TIME,
                        HealthConstants.StepCount.TIME_OFFSET, ALIAS_BINNING_TIME)
                .setLocalTimeRange(HealthConstants.StepCount.START_TIME, HealthConstants.StepCount.TIME_OFFSET,
                        startTime, startTime + Constant.ONE_DAY)
                .setFilter(filter)
                .setSort(ALIAS_BINNING_TIME, HealthDataResolver.SortOrder.ASC)
                .setSourceDevices(getDevicesUuid(getTypeForStepAndHeart()))
                .build();

        try {
            healthDataResolver.aggregate(request).setResultListener(result -> {

                List<StepBinningData> binningCountArray = new ArrayList<>();

                try {
                    for (HealthData data : result) {
                        String binningTime = data.getString(ALIAS_BINNING_TIME);
                        int binningCount = data.getInt(ALIAS_TOTAL_COUNT);

                        if (binningTime !=null) {
                            binningCountArray.add(new StepBinningData(binningTime.split(" ")[1], binningCount));
                        }
                        Log.d(Constant.DEBUG_TAG, "Got step count binning " + data.getContentValues().valueSet().toString());
                    }

//                    if (stepCountObserver != null) {
//                        stepCountObserver.onBinningDataChanged(binningCountArray);
//                    }

                    Log.d(Constant.DEBUG_TAG, "Getting step binning data success.");
                } finally {
                    result.close();
                }
            });
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Getting step binning data fails.", e);
        }
    }

    private static List<StepBinningData> getBinningData(byte[] zip) {
        // decompress ZIP
        List<StepBinningData> binningDataList = HealthDataUtil.getStructuredDataList(zip, StepBinningData.class);
        for (int i = binningDataList.size() - 1; i >= 0; i--) {
            if (binningDataList.get(i).count == 0) {
                binningDataList.remove(i);
            } else {
                binningDataList.get(i).time = String.format(Locale.US, "%02d:%02d", i / 6, (i % 6) * 10);
            }
        }

        return binningDataList;
    }

    public static class StepBinningData {
        public String time;
        public final int count;

        public StepBinningData(String time, int count) {
            this.time = time;
            this.count = count;
        }
    }
}

