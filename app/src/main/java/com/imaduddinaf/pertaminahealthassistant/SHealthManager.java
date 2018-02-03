package com.imaduddinaf.pertaminahealthassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.core.Helper;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Imaduddin Al Fikri on 03-Feb-18.
 */

public class SHealthManager {

    private long currentStartTime;
    private HealthDataStore healthDataStore;
    private StepCountReader stepCountReader;

    private Context context;
    private BaseActivity activityHolder;

    private SHealthPermissionManager sHealthPermissionManager;

    private Consumer<Integer> onStepCountChanges;
    private Consumer<List<StepCountReader.StepBinningData>> onStepBinningDataChanges;

    public SHealthManager(Context context,
                          SHealthPermissionManager sHealthPermissionManager,
                          long currentStartTime,
                          Consumer<Integer> onStepCountChanges,
                          Consumer<List<StepCountReader.StepBinningData>> onStepBinningDataChanges) {

        this.context = context;
        this.sHealthPermissionManager = sHealthPermissionManager;
        this.currentStartTime = currentStartTime;
        this.onStepCountChanges = onStepCountChanges;
        this.onStepBinningDataChanges = onStepBinningDataChanges;

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        healthDataStore = new HealthDataStore(context, connectionListener);
        stepCountReader = new StepCountReader(healthDataStore, stepCountObserver);
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

    // Connection Listener
    private final HealthDataStore.ConnectionListener connectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(Helper.DEBUG_TAG, "onConnected");
            String[] dataTypes = new String[] {
                    HealthConstants.StepCount.HEALTH_DATA_TYPE,
                    StepCountReader.STEP_SUMMARY_DATA_TYPE_NAME
            };
            if (sHealthPermissionManager.isPermissionAcquired(healthDataStore,
                    dataTypes,
                    HealthPermissionManager.PermissionType.READ)) {
                stepCountReader.requestDailyStepCount(currentStartTime);
            } else {
                sHealthPermissionManager.requestPermission(healthDataStore,
                        dataTypes,
                        HealthPermissionManager.PermissionType.READ);
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(Helper.DEBUG_TAG, "onConnectionFailed");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(Helper.DEBUG_TAG, "onDisconnected");
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

    // Step Count Reader
    private final StepCountReader.StepCountObserver stepCountObserver = new StepCountReader.StepCountObserver() {
        @Override
        public void onChanged(int count) {
            onStepCountChanges.accept(count);
        }

        @Override
        public void onBinningDataChanged(List<StepCountReader.StepBinningData> stepBinningDataList) {
            onStepBinningDataChanges.accept(stepBinningDataList);
        }
    };
}
