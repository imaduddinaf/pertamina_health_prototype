package com.imaduddinaf.pertaminahealthassistant.shealth.reader;

import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDevice;
import com.samsung.android.sdk.healthdata.HealthDeviceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Imaduddin Al Fikri on 04-Feb-18.
 */

public class SHealthReader {

    protected static final String ALIAS_DEVICE_UUID = "deviceuuid";

    protected HealthDataResolver healthDataResolver;
    protected HealthDataStore healthDataStore;

    public static class DeviceType {
        public static final Integer ALL = 0;
        public static final Integer GEAR = 360003;
        public static final Integer PHONE = 360001;

    }

    public SHealthReader(HealthDataStore store) {
        healthDataStore = store;
        healthDataResolver = new HealthDataResolver(store, null);
    }

    public Integer getTypeForStepAndHeart() {
        return DeviceType.GEAR;
    }

    public Integer getTypeForOthers() {
        return DeviceType.PHONE;
    }

    public List<String> getDevicesUuid(Integer deviceType) {

        HealthDeviceManager healthDeviceManager = new HealthDeviceManager(healthDataStore);
        List<HealthDevice> healthDeviceList = healthDeviceManager.getAllDevices();
        List<String> uuidList = new ArrayList<String>();

        for (HealthDevice device: healthDeviceList) {

            if (device.getGroup() == deviceType) {
                uuidList.add(device.getUuid());
            }
        }

        return uuidList;
    }
}
