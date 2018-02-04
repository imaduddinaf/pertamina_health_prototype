package com.imaduddinaf.pertaminahealthassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.core.Helper;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;

public class SHealthManager {

    private long currentStartTime;
    private HealthDataStore healthDataStore;
    private StepCountReader stepCountReader;
    private boolean isConnected = false;

    private Context context;
    private BaseActivity activityHolder;

    private SHealthPermissionManager sHealthPermissionManager;
    private BaseSHealthType baseSHealthType;

    public SHealthManager(Context context,
                          BaseActivity activityHolder,
                          SHealthPermissionManager sHealthPermissionManager,
                          BaseSHealthType baseSHealthType,
                          long currentStartTime) {

        this.context = context;
        this.activityHolder = activityHolder;
        this.sHealthPermissionManager = sHealthPermissionManager;
        this.baseSHealthType = baseSHealthType;
        this.currentStartTime = currentStartTime;

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        healthDataStore = new HealthDataStore(context, connectionListener);
        stepCountReader = new StepCountReader(healthDataStore);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public HealthDataStore getHealthDataStore() {
        return healthDataStore;
    }

    public SHealthPermissionManager getsHealthPermissionManager() {
        return sHealthPermissionManager;
    }

    public BaseSHealthType getBaseSHealthType() {
        return baseSHealthType;
    }

    public long getCurrentStartTime() {
        return currentStartTime;
    }

    public StepCountReader getStepCountReader() {
        return stepCountReader;
    }

    public void connectService() {
        healthDataStore.connectService();
    }

    public void disconnectService() {
        healthDataStore.disconnectService();
    }

    public boolean isPermissionAcquired() {
        return sHealthPermissionManager.isPermissionAcquired(healthDataStore,
                baseSHealthType.getTypes(),
                HealthPermissionManager.PermissionType.READ);
    }

    public void requestPermission() {
        sHealthPermissionManager.requestPermission(healthDataStore,
                baseSHealthType.getTypes(),
                HealthPermissionManager.PermissionType.READ);
    }

    // Connection Listener
    private final HealthDataStore.ConnectionListener connectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(Helper.DEBUG_TAG, "onConnected");
            isConnected = true;

            if (isPermissionAcquired()) {
                sHealthPermissionManager.getOnGotPermission().run();
            } else {
                requestPermission();
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(Helper.DEBUG_TAG, "onConnectionFailed");
            isConnected = false;
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(Helper.DEBUG_TAG, "onDisconnected");
            isConnected = false;
        }
    };

    private void showConnectionFailureDialog(final HealthConnectionErrorResult error) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        if (error.hasResolution()) {
            switch (error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    alert.setMessage(R.string.msg_req_install);
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    alert.setMessage(R.string.msg_req_upgrade);
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    alert.setMessage(R.string.msg_req_enable);
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    alert.setMessage(R.string.msg_req_agree);
                    break;
                default:
                    alert.setMessage(R.string.msg_req_available);
                    break;
            }
        } else {
            alert.setMessage(R.string.msg_conn_not_available);
        }

        alert.setPositiveButton(R.string.ok, (dialog, id) -> {
            if (error.hasResolution()) {
                error.resolve(activityHolder);
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton(R.string.cancel, null);
        }

        alert.show();
    }
}
