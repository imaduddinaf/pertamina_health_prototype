package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.imaduddinaf.pertaminahealthassistant.Helper;
import com.imaduddinaf.pertaminahealthassistant.UserSession;
import com.imaduddinaf.pertaminahealthassistant.activity.MainActivity;
import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.model.SimpleUserStep;
import com.imaduddinaf.pertaminahealthassistant.model.User;
import com.imaduddinaf.pertaminahealthassistant.model.UserStep;
import com.imaduddinaf.pertaminahealthassistant.model.UserStepTrend;
import com.imaduddinaf.pertaminahealthassistant.network.APICallback;
import com.imaduddinaf.pertaminahealthassistant.network.service.StepsService;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.HeartRateReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.SleepReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.WeightReader;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends BaseFragment {

    @ViewById(R.id.container_profile_all)
    LinearLayout containerProfileAll;

    // User Profile
    @ViewById(R.id.container_user)
    RelativeLayout containerUser;

    @ViewById(R.id.tv_user_name)
    TextView tvUserName;

    @ViewById(R.id.tv_user_level)
    TextView tvUserLevel;

    @ViewById(R.id.iv_user_image)
    SimpleDraweeView ivUserImage;

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
    private WeightReader weightReader;
    private SleepReader sleepReader;

    private Integer stepCount = 0;
    private Integer calorieCount = 0;
    private Integer lastStepCount = 0;
    private Integer lastCalorieCount = 0;
    private Integer heartRate = 0;
    private Integer avgHeartRate = 0;
    private Integer weightAmount = 0;
    private Double sleepAmount = 0.0;

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
        weightReader = new WeightReader(sHealthManager.getHealthDataStore());
        sleepReader= new SleepReader(sHealthManager.getHealthDataStore());

        sHealthTrackerManager = new SHealthTrackerManager(this.getContext());

        requestYesterdaySteps();
    }

    @Override
    protected void afterViews() {
        super.afterViews();

        connectServices();
        setupUser();
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

        requestYesterdaySteps();
        setupUser();
    }

    @Override
    public String getCustomTitle() {
        return "Profil";
    }

    private void setupUser() {
        if (UserSession.instance().isLoggedIn()) {
            User user = UserSession.instance().getUser();

            containerProfileAll.setVisibility(View.VISIBLE);

            tvUserName.setText(user.getName());
            tvUserLevel.setText(user.getLevel());

            ivUserImage.setImageURI(user.getProfilePhotoURL());
        } else {
            containerProfileAll.setVisibility(View.GONE);
        }
    }

    private void connectServices() {
        sHealthManager.connectService();
    }

    private void disconnectServices() {
        sHealthManager.disconnectService();
    }

    private void refreshView() {
        if (!isAfterViewsOrInjection()) return;

        tvStepCount.setText(Helper.getStringOrEmpty(stepCount));
        tvCalorieCount.setText(Helper.getStringOrEmpty(calorieCount));

        tvLastStepCount.setText(Helper.getStringOrEmpty(lastStepCount));
        tvLastCalorieCount.setText(Helper.getStringOrEmpty(lastCalorieCount));

        tvAvgHeartRateCount.setText(Helper.getStringOrEmpty(avgHeartRate));
        tvHeartRateCount.setText(Helper.getStringOrEmpty(heartRate));

        tvWeightCount.setText(Helper.getStringOrEmpty(weightAmount));
        tvSleepCount.setText(Helper.getStringOrEmpty(sleepAmount));
    }

    private void requestAllData() {
        requestStepCount();
        requestHeartRate();
        requestWeight();
        requestSleep();
    }

    private void requestYesterdaySteps() {
        if (!UserSession.instance().isLoggedIn()) return;

        User user = UserSession.instance().getUser();

        // request yesterday step
        StepsService.instance()
                .getTrend(user.getID(), 2)
                .enqueue(new APICallback<BaseResponse<UserStepTrend>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<UserStepTrend>> call, Response<BaseResponse<UserStepTrend>> response) {
                        super.onResponse(call, response);

                        if (response.body() != null &&
                                response.body().getData() != null &&
                                response.body().getData().getUserSteps() != null) {
                            ArrayList<UserStep> steps = response.body().getData().getUserSteps();

                            if (steps.size() == 2) {
                                lastStepCount = steps.get(1).getStep();
                                lastCalorieCount = steps.get(1).getCalorie();
                            }

                            refreshView();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<UserStepTrend>> call, Throwable t) {
                        super.onFailure(call, t);
                        // empty on failure
                    }
                });
    }

    private void requestStepCount() {
        stepCountReader.readStepCount(Constant.today(), stepDailyTrend -> {
            stepCount = stepDailyTrend.getTotalStep();
            calorieCount = stepDailyTrend.getTotalCalorie().intValue();

            UserSession.instance().updateSteps(stepDailyTrend);

            refreshView();
        });
    }

    private void requestHeartRate() {
        heartRateReader.readTotalHeartRate(Constant.today(), Constant.endOfToday(), heartRateData -> {
            avgHeartRate = heartRateData.getHeartRate().intValue();

            refreshView();
        });

        heartRateReader.readLastHeartRate(Constant.lastMonth(), Constant.endOfToday(), heartRate -> {
            this.heartRate = heartRate.intValue();

            refreshView();
        });
    }

    private void requestWeight() {
        weightReader.readLastWeight(Constant.lastMonth(), Constant.endOfToday(), lastWeight -> {
            weightAmount = lastWeight.intValue();

            refreshView();
        });
    }

    private void requestSleep() {
        sleepReader.readLastSleep(Constant.lastMonth(), Constant.endOfToday(), sleepTime -> {
            sleepAmount = sleepTime;

            refreshView();
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

    @Click(R.id.button_sickness)
    void tapOnSicknessHistory(View v) {
        Helper.showUnderConstructionToast(this.getActivityHolder());
    }

    @Click(R.id.button_options)
    void tapOnOptions(View v) {
        Helper.showUnderConstructionToast(this.getActivityHolder());
    }

    @Click(R.id.button_logout)
    void tapOnLogout(View v) {
        UserSession.instance().logout(this.getActivityHolder());
        Helper.showMessage(this.getActivityHolder(), "Logout berhasil");

        MainActivity mainActivity = (MainActivity) this.getActivityHolder();
        View homeAction = mainActivity.navigationView.findViewById(R.id.navigation_home);
        homeAction.performClick();
    }
}
