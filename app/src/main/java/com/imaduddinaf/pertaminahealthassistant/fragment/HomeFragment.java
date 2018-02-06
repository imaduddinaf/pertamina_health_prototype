package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.Helper;
import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.UserSession;
import com.imaduddinaf.pertaminahealthassistant.activity.MyStepActivity_;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.model.SimpleUserAverageStep;
import com.imaduddinaf.pertaminahealthassistant.model.User;
import com.imaduddinaf.pertaminahealthassistant.model.UserStep;
import com.imaduddinaf.pertaminahealthassistant.model.UserStepTrend;
import com.imaduddinaf.pertaminahealthassistant.network.APICallback;
import com.imaduddinaf.pertaminahealthassistant.network.service.FactService;
import com.imaduddinaf.pertaminahealthassistant.network.service.StepsService;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthPermissionManager;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.StepCountReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.type.BaseSHealthType;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment(R.layout.fragment_home)
public class HomeFragment extends BaseFragment {

    // Step count
    @ViewById(R.id.container_step_count)
    LinearLayout containerStepCount;

    @ViewById(R.id.tv_current_step_count)
    TextView tvCurrentStepCount;

    @ViewById(R.id.tv_avg_step_count)
    TextView tvAvgStepCount;

    // Chart
    @ViewById(R.id.container_chart_step)
    LinearLayout containerChartStep;

    // Today fact
    @ViewById(R.id.container_today_fact)
    LinearLayout containerTodayFact;

    @ViewById(R.id.tv_today_fact)
    TextView tvTodayFact;

    private SHealthManager sHealthManager;
    private StepCountReader stepCountReader;

    private String todayFact = "";
    private Integer todayAllAvgStep = 0;
    private Integer todayStep = 0;
    private UserStepTrend stepTrendAll;

    public HomeFragment() {
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
                            requestTodayStep();
                        },
                        () -> {
                            // didNotGotPermission
                            // empty
                        }
                ),
                new BaseSHealthType()
        );

        stepCountReader = new StepCountReader(sHealthManager.getHealthDataStore());
    }

    @Override
    protected void afterViews() {
        super.afterViews();

        sHealthManager.connectService();
        refreshView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sHealthManager.isConnected() && sHealthManager.isPermissionAcquired()) {
            requestTodayStep();
        } else {
            sHealthManager.connectService();
        }

        refreshRequest();
        refreshView();
    }

    @Override
    public void onDetach() {
        sHealthManager.disconnectService();

        super.onDetach();
    }

    @Click(R.id.container_step_count)
    void tapOnContainerStepCount(View v) {
        Intent myIntent = new Intent(this.getActivity(), MyStepActivity_.class);
        this.getActivity().startActivity(myIntent);
    }

    private void refreshView() {
        if (!isAfterViewsOrInjection()) return;

        // current steps
        tvAvgStepCount.setText(Helper.getStringOrEmpty(todayAllAvgStep));
        tvCurrentStepCount.setText(Helper.getStringOrEmpty(todayStep));

        // chart
        if (stepTrendAll != null &&
                stepTrendAll.getUserSteps() != null &&
                !stepTrendAll.getUserSteps().isEmpty() &&
                stepTrendAll.getAverageSteps() != null &&
                !stepTrendAll.getAverageSteps().isEmpty()) {
            containerChartStep.setVisibility(View.VISIBLE);
        } else {
            containerChartStep.setVisibility(View.GONE);
        }

        // today fact
        if (!todayFact.isEmpty()) {
            containerTodayFact.setVisibility(View.VISIBLE);
            tvTodayFact.setText(todayFact);
        } else {
            containerTodayFact.setVisibility(View.GONE);
        }
    }

    private void requestTodayStep() {
        stepCountReader.readStepCount(Constant.today(), stepDailyTrend -> {
            todayStep = stepDailyTrend.getTotalStep();
            UserSession.instance().updateSteps(stepDailyTrend);

            refreshView();
        });
    }

    private void refreshRequest() {
        // request average of all
        Log.d(Constant.DEBUG_TAG, "today: " + Helper.getFormattedTime(Constant.TODAY_START_UTC_TIME));
        StepsService.instance()
                .getAvgAll(Helper.getFormattedTime(Constant.today()))
                .enqueue(new APICallback<BaseResponse<SimpleUserAverageStep>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<SimpleUserAverageStep>> call, Response<BaseResponse<SimpleUserAverageStep>> response) {
                        super.onResponse(call, response);
                        if (response.body() != null &&
                                response.body().getData() != null &&
                                response.body().getData().getAverage() != null) {
                            todayAllAvgStep = response.body().getData().getAverage().intValue();
                            refreshView();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<SimpleUserAverageStep>> call, Throwable t) {
                        super.onFailure(call, t);
                        // empty on failure
                    }
                });

        // request today fact
        FactService.instance()
                .getFact("")
                .enqueue(new APICallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                        super.onResponse(call, response);
                        if (response.body() != null && response.body().getData() != null) {
                            todayFact = response.body().getData();
                            refreshView();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                        super.onFailure(call, t);
                        // empty on failure
                    }
                });

        if (UserSession.instance().isLoggedIn()) {
            Log.d(Constant.DEBUG_TAG, "do request on user's");
            User user = UserSession.instance().getUser();

            // request a-month step
            StepsService.instance()
                    .getPerformance(user.getID(), 30)
                    .enqueue(new APICallback<BaseResponse<UserStepTrend>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<UserStepTrend>> call, Response<BaseResponse<UserStepTrend>> response) {
                            super.onResponse(call, response);
                            if (response.body() != null && response.body().getData() != null) {
                                stepTrendAll = response.body().getData();
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
    }
}
