package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.SHealthManager;
import com.imaduddinaf.pertaminahealthassistant.SHealthPermissionManager;
import com.imaduddinaf.pertaminahealthassistant.StepCountType;
import com.imaduddinaf.pertaminahealthassistant.StepCountReader;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;

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
    SHealthManager sHealthStepCountManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sHealthStepCountManager = new SHealthManager(
                this.getContext(),
                (BaseActivity) this.getActivity(),
                new SHealthPermissionManager(this.getContext(),
                        (BaseActivity) this.getActivity(),
                        () -> {
                            //didGotPermission
                            requestStepCount();
                        },
                        () -> {
                            // didNotGotPermission
                            // empty
                        }
                ),
                new StepCountType(),
                StepCountReader.TODAY_START_UTC_TIME
        );
    }

    @Override
    protected void afterViews() {
        super.afterViews();

        requestStepCount();
    }

    @Override
    public void onDetach() {
        disconnectServices();

        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sHealthStepCountManager.isConnected()) {
            requestStepCount();
        } else {
            sHealthStepCountManager.connectService();
        }
    }

    private void connectServices() {
        sHealthStepCountManager.connectService();
    }

    private void disconnectServices() {
        sHealthStepCountManager.disconnectService();
    }

    private void requestStepCount() {
        long today = StepCountReader.TODAY_START_UTC_TIME;
        long yesterday = today - StepCountReader.ONE_DAY;

        sHealthStepCountManager.getStepCountReader().readStepCount(today, stepDailyTrend -> {
            if (isAfterViewsOrInjection()) {
                tvStepCount.setText("" + stepDailyTrend.getTotalStep());
                tvCalorieCount.setText("" + stepDailyTrend.getTotalCalorie().intValue());
            }
        });

        sHealthStepCountManager.getStepCountReader().readStepCount(yesterday, stepDailyTrend -> {
            if (isAfterViewsOrInjection()) {
                tvLastStepCount.setText("" + stepDailyTrend.getTotalStep());
                tvLastCalorieCount.setText("" + stepDailyTrend.getTotalCalorie().intValue());
            }
        });
    }
}
