package com.imaduddinaf.pertaminahealthassistant.activity;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.core.Helper;
import com.imaduddinaf.pertaminahealthassistant.StepCountReader;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EActivity(R.layout.activity_my_step)
public class MyStepActivity extends AppCompatActivity {

    @ViewById(R.id.tv_step_count)
    TextView tvStepCount;

    @ViewById(R.id.tv_date)
    TextView tvDate;

    private long currentStartTime;
    private HealthDataStore healthDataStore;
    private StepCountReader stepCountReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentStartTime = StepCountReader.TODAY_START_UTC_TIME;

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a HealthDataStore instance and set its listener
        healthDataStore = new HealthDataStore(this, connectionListener);

        // Request the connection to the health data store
        healthDataStore.connectService();
        stepCountReader = new StepCountReader(healthDataStore, stepCountObserver);
    }

    @AfterViews
    public void afterViews() {
        tvDate.setText(Helper.getFormattedTime(currentStartTime));
    }

    @Override
    public void onDestroy() {
        healthDataStore.disconnectService();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        stepCountReader.requestDailyStepCount(currentStartTime);
    }

    // Button Listener
    @Click(R.id.button_date_before)
    void clickOnDateBefore() {
        currentStartTime -= StepCountReader.ONE_DAY;
        tvDate.setText(Helper.getFormattedTime(currentStartTime));
        stepCountReader.requestDailyStepCount(currentStartTime);
    }

    @Click(R.id.button_date_next)
    void clickOnDateNext() {
        currentStartTime += StepCountReader.ONE_DAY;
        tvDate.setText(Helper.getFormattedTime(currentStartTime));
        stepCountReader.requestDailyStepCount(currentStartTime);
    }

    // Here below are boilerplate codes that can be optimized

    // Permission Listener
    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                @Override
                public void onResult(HealthPermissionManager.PermissionResult result) {
                    Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();
                    // Show a permission alarm and clear step count if permissions are not acquired
                    if (resultMap.values().contains(Boolean.FALSE)) {
                        updateStepCountView("");
                        showPermissionAlarmDialog();
                    } else {
                        // Get the daily step count of a particular day and display it
                        stepCountReader.requestDailyStepCount(currentStartTime);
                    }
                }
            };

    private void showPermissionAlarmDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.notice)
                .setMessage(R.string.msg_perm_acquired)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showConnectionFailureDialog(final HealthConnectionErrorResult error) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

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
                error.resolve(this);
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton(R.string.cancel, null);
        }

        alert.show();
    }

    private boolean isPermissionAcquired() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(healthDataStore);
        try {
            // Check whether the permissions that this application needs are acquired
            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(generatePermissionKeySet());
            return !resultMap.values().contains(Boolean.FALSE);
        } catch (Exception e) {
            Log.e(Helper.ERROR_TAG, "Permission request fails.", e);
        }
        return false;
    }

    private void requestPermission() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(healthDataStore);
        try {
            // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(generatePermissionKeySet(), this)
                    .setResultListener(mPermissionListener);
        } catch (Exception e) {
            Log.e(Helper.ERROR_TAG, "Permission setting fails.", e);
        }
    }

    private Set<HealthPermissionManager.PermissionKey> generatePermissionKeySet() {
        Set<HealthPermissionManager.PermissionKey> pmsKeySet = new HashSet<>();
        pmsKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        pmsKeySet.add(new HealthPermissionManager.PermissionKey(StepCountReader.STEP_SUMMARY_DATA_TYPE_NAME, HealthPermissionManager.PermissionType.READ));
        return pmsKeySet;
    }

    // Connection Listener
    private final HealthDataStore.ConnectionListener connectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(Helper.DEBUG_TAG, "onConnected");
            if (isPermissionAcquired()) {
                stepCountReader.requestDailyStepCount(currentStartTime);
            } else {
                requestPermission();
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

    // Step Count Reader
    private final StepCountReader.StepCountObserver stepCountObserver= new StepCountReader.StepCountObserver() {
        @Override
        public void onChanged(int count) {
            updateStepCountView(String.valueOf(count));
        }

        @Override
        public void onBinningDataChanged(List<StepCountReader.StepBinningData> stepBinningDataList) {
//            updateBinningChartView(stepBinningDataList);
        }
    };

    private void updateStepCountView(final String count) {
        tvStepCount.setText(count);
    }

}
