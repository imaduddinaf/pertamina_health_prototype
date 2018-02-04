package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.imaduddinaf.pertaminahealthassistant.Helper;
import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;
import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.StepCountReader;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment(R.layout.fragment_my_step)
public class MyStepFragment extends BaseFragment{

    @ViewById(R.id.tv_step_count)
    TextView tvStepCount;

    @ViewById(R.id.tv_date)
    TextView tvDate;

    @ViewById(R.id.lv_details)
    ListView lvDetails;

    private long currentStartTime;
    private HealthDataStore healthDataStore;
    private StepCountReader stepCountReader;
    private StepDetailListAdapter stepDetailListAdapter;

    public MyStepFragment() {
        // Required empty public constructor
    }

    public static MyStepFragment newInstance() {
        MyStepFragment fragment = new MyStepFragment_();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the start time of today in local
        currentStartTime = Constant.TODAY_START_UTC_TIME;

        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a HealthDataStore instance and set its listener
        healthDataStore = new HealthDataStore(this.getContext(), connectionListener);

        // Request the connection to the health data store
        healthDataStore.connectService();
//        stepCountReader = new StepCountReader(healthDataStore, stepCountObserver);
    }

    @Override
    protected void afterViews() {
        super.afterViews();

        tvDate.setText(Helper.getFormattedTime(currentStartTime));

        stepDetailListAdapter = new StepDetailListAdapter();
        lvDetails.setAdapter(stepDetailListAdapter);
    }

    @Override
    public void onDetach() {
        healthDataStore.disconnectService();
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        stepCountReader.requestDailyStepCount(currentStartTime);
    }

    // Button Listener
    @Click(R.id.button_date_before)
    void clickOnDateBefore() {
        currentStartTime -= Constant.ONE_DAY;
        tvDate.setText(Helper.getFormattedTime(currentStartTime));
        stepDetailListAdapter.changeDataSet(Collections.<StepCountReader.StepBinningData>emptyList());
        stepCountReader.requestDailyStepCount(currentStartTime);
    }

    @Click(R.id.button_date_next)
    void clickOnDateNext() {
        currentStartTime += Constant.ONE_DAY;
        tvDate.setText(Helper.getFormattedTime(currentStartTime));
        stepDetailListAdapter.changeDataSet(Collections.emptyList());
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
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setTitle(R.string.notice)
                .setMessage(R.string.msg_perm_acquired)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showConnectionFailureDialog(final HealthConnectionErrorResult error) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());

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
                error.resolve(this.getActivity());
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
            Log.e(Constant.ERROR_TAG, "Permission request fails.", e);
        }
        return false;
    }

    private void requestPermission() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(healthDataStore);
        try {
            // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(generatePermissionKeySet(), this.getActivity())
                    .setResultListener(mPermissionListener);
        } catch (Exception e) {
            Log.e(Constant.ERROR_TAG, "Permission setting fails.", e);
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
            Log.d(Constant.DEBUG_TAG, "onConnected");
            if (isPermissionAcquired()) {
                stepCountReader.requestDailyStepCount(currentStartTime);
            } else {
                requestPermission();
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(Constant.DEBUG_TAG, "onConnectionFailed");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(Constant.DEBUG_TAG, "onDisconnected");
        }
    };

    private void updateStepCountView(final String count) {
        tvStepCount.setText(count);
    }

    private void updateBinningChartView(List<StepCountReader.StepBinningData> stepBinningDataList) {
        // the following code will be replaced with chart drawing code
        Log.d(Constant.DEBUG_TAG, "updateBinningChartView");
        stepDetailListAdapter.changeDataSet(stepBinningDataList);
        for (StepCountReader.StepBinningData data : stepBinningDataList) {
            Log.d(Constant.DEBUG_TAG, "TIME : " + data.time + "  COUNT : " + data.count);
        }
    }

    // List Adapter
    private class StepDetailListAdapter extends BaseAdapter {

        private List<StepCountReader.StepBinningData> dataList = new ArrayList<>();

        void changeDataSet(List<StepCountReader.StepBinningData> dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(dataList.get(position).count + " steps");
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(dataList.get(position).time);
            return convertView;
        }
    }
}
