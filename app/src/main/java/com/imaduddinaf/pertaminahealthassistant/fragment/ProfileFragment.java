package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.SHealthManager;
import com.imaduddinaf.pertaminahealthassistant.SHealthPermissionManager;
import com.imaduddinaf.pertaminahealthassistant.StepCountReader;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;
import com.imaduddinaf.pertaminahealthassistant.core.Helper;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
    SHealthManager sHealthManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sHealthManager = new SHealthManager(
                this.getContext(),
                new SHealthPermissionManager(this.getContext(),
                        (BaseActivity) this.getActivity(),
                        this::didGotPermission,
                        this::didNotGotPermission),
                StepCountReader.TODAY_START_UTC_TIME,
                this::didStepCountChanges,
                this::didStepBinningDataChanges
        );
    }

    void didGotPermission() {

    }

    void didNotGotPermission() {

    }

    void didStepCountChanges(Integer count) {

    }

    void didStepBinningDataChanges(List<StepCountReader.StepBinningData> list) {

    }

    @AfterViews
    void afterViews() {

    }
}
