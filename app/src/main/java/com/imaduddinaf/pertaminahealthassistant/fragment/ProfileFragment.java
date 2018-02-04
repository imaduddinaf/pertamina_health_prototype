package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imaduddinaf.pertaminahealthassistant.shealth.reader.HeartRateReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.type.BaseSHealthType;
import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthPermissionManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthTrackerManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.StepCountReader;
import com.imaduddinaf.pertaminahealthassistant.activity.MyStepActivity_;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;
import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.samsung.android.sdk.shealth.tracker.TrackerManager;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends BaseFragment {

    // Step & calorie cards
    @ViewById(R.id.container_step_calorie)
    LinearLayout containerStepCalorie;

    @ViewById(R.id.container_last_step_calorie)
    LinearLayout containerLastStepCalorie;

    @ViewById(R.id.tv_last_step_count)
    TextView tvLastStepCount;

    @ViewById(R.id.tv_last_calorie_count)
    TextView tvLastCalorieCount;

    @ViewById(R.id.container_current_step_calorie)
    LinearLayout containerCurrentStepCalorie;

    @ViewById(R.id.tv_step_count)
    TextView tvStepCount;

    @ViewById(R.id.tv_calorie_count)
    TextView tvCalorieCount;

    // Heart rate card
    @ViewById(R.id.container_heart_rate)
    LinearLayout containerHeartRate;

    @ViewById(R.id.tv_heart_rate_count)
    TextView tvHeartRateCount;

    @ViewById(R.id.tv_avg_heart_rate_count)
    TextView tvAvgHeartRateCount;

    // Weight & sleep cards
    @ViewById(R.id.container_weight_sleep)
    LinearLayout containerWeightSleep;

    @ViewById(R.id.container_weight)
    RelativeLayout containerWeight;

    @ViewById(R.id.tv_weight_count)
    TextView tvWeightCount;

    @ViewById(R.id.container_sleep)
    RelativeLayout containerSleep;

    @ViewById(R.id.tv_sleep_count)
    TextView tvSleepCount;

    // Managers
    private SHealthManager sHealthManager;
    private SHealthTrackerManager sHealthTrackerManager = null;

    // Reader
    private StepCountReader stepCountReader;
    private HeartRateReader heartRateReader;
//    private StepCountReader stepCountReader;
//    private StepCountReader stepCountReader;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sHealthManager = new SHealthManager(
                this.getContext(),
                (BaseActivity) this.getActivity(),
                new SHealthPermissionManager(this.getContext(),
                        (BaseActivity) this.getActivity(),
                        () -> {
                            //didGotPermission
                            requestAllData();
                        },
                        () -> {
                            // didNotGotPermission
                            // empty
                        }
                ),
                new BaseSHealthType()
        );

        stepCountReader = new StepCountReader(sHealthManager.getHealthDataStore());
        heartRateReader= new HeartRateReader(sHealthManager.getHealthDataStore());

        sHealthTrackerManager = new SHealthTrackerManager(this.getContext());
    }

    @Override
    protected void afterViews() {
        super.afterViews();

        connectServices();
    }

    @Override
    public void onDetach() {
        disconnectServices();

        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sHealthManager.isConnected() && sHealthManager.isPermissionAcquired()) {
            requestAllData();
        } else {
            connectServices();
        }
    }

    private void connectServices() {
        sHealthManager.connectService();
    }

    private void disconnectServices() {
        sHealthManager.disconnectService();
    }

    private void requestAllData() {
        requestStepCount();
        requestHeartRate();
    }

    private void requestStepCount() {
        long today = Constant.TODAY_START_UTC_TIME;
        long yesterday = today - Constant.ONE_DAY;

        stepCountReader.readStepCount(today, stepDailyTrend -> {
            if (isAfterViewsOrInjection()) {
                tvStepCount.setText("" + stepDailyTrend.getTotalStep());
                tvCalorieCount.setText("" + stepDailyTrend.getTotalCalorie().intValue());
            }
        });

        stepCountReader.readStepCount(yesterday, stepDailyTrend -> {
            if (isAfterViewsOrInjection()) {
                tvLastStepCount.setText("" + stepDailyTrend.getTotalStep());
                tvLastCalorieCount.setText("" + stepDailyTrend.getTotalCalorie().intValue());
            }
        });
    }

    private void requestHeartRate() {
        long startTime = Constant.TODAY_START_UTC_TIME;
        long endTime = startTime + Constant.ONE_DAY;
        heartRateReader.readTotalHeartRate(startTime, endTime, heartRateData -> {
            if (isAfterViewsOrInjection()) {
                tvAvgHeartRateCount.setText("" + heartRateData.getHeartRate().intValue());
            }
        });

        heartRateReader.readLastHeartRate(startTime, endTime, heartRate -> {
            if (isAfterViewsOrInjection()) {
                tvHeartRateCount.setText("" + heartRate.intValue());
            }
        });
    }

    private void goToMyStep() {
        Intent myIntent = new Intent(this.getActivity(), MyStepActivity_.class);
        this.getActivity().startActivity(myIntent);
    }

    @Click(R.id.container_last_step_calorie)
    void tapOnContainerLastStepCalorie(View v) {
        goToMyStep();
    }

    @Click(R.id.container_step_calorie)
    void tapOnContainerStepCalorie(View v) {
        goToMyStep();
    }

    @Click(R.id.container_heart_rate)
    void tapOnContainerHeartRate(View v) {
        sHealthTrackerManager.startActivity(this.getContext(), v, TrackerManager.TrackerId.HEART_RATE);
    }

    @Click(R.id.container_weight)
    void tapOnContainerWeight(View v) {
        sHealthTrackerManager.startActivity(this.getContext(), v, TrackerManager.TrackerId.WEIGHT);
    }

    @Click(R.id.container_sleep)
    void tapOnContainerSleep(View v) {
        sHealthTrackerManager.startActivity(this.getContext(), v, TrackerManager.TrackerId.SLEEP);
    }
}
