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

    public SHealthReader(HealthDataStore store) {
        healthDataStore = store;
        healthDataResolver = new HealthDataResolver(store, null);
    }

    public List<String> getDevicesUuid() {

        HealthDeviceManager healthDeviceManager = new HealthDeviceManager(healthDataStore);
        List<HealthDevice> healthDeviceList = healthDeviceManager.getAllDevices();
        List<String> uuidList = new ArrayList<String>();

        Log.d(Constant.DEBUG_TAG, "samsung health device list: ");

        for (HealthDevice device: healthDeviceList) {
            Log.d(Constant.DEBUG_TAG, "name: " + device.getCustomName());
            Log.d(Constant.DEBUG_TAG, "uuid: " + device.getUuid());
            Log.d(Constant.DEBUG_TAG, "group: " + device.getGroup());
            Log.d(Constant.DEBUG_TAG, "model: " + device.getModel());
            Log.d(Constant.DEBUG_TAG, "==============================");

            uuidList.add(device.getUuid());

            // only Gear S3
//            if (device.getGroup() == 360003) {
//                uuidList.add(device.getUuid());
//            }
        }

        Log.d(Constant.DEBUG_TAG, "uuids: " + uuidList);

        return uuidList;
    }
}
