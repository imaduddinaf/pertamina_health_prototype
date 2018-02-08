package com.imaduddinaf.pertaminahealthassistant.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.imaduddinaf.pertaminahealthassistant.Helper;
import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.UserSession;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.model.SimpleUserAverageStep;
import com.imaduddinaf.pertaminahealthassistant.model.User;
import com.imaduddinaf.pertaminahealthassistant.model.UserStep;
import com.imaduddinaf.pertaminahealthassistant.network.APICallback;
import com.imaduddinaf.pertaminahealthassistant.network.service.StepsService;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthPermissionManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthTrackerManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.HeartRateReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.SleepReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.StepCountReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.WeightReader;
import com.imaduddinaf.pertaminahealthassistant.shealth.type.BaseSHealthType;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

@EActivity(R.layout.activity_my_step)
public class MyStepActivity extends BaseActivity implements OnChartValueSelectedListener {

    // Step & date
    @ViewById(R.id.tv_step_count)
    TextView tvStepCount;

    @ViewById(R.id.tv_calorie_count)
    TextView tvCalorieCount;

    @ViewById(R.id.tv_date)
    TextView tvDate;

    // Chart
    @ViewById(R.id.container_chart_step)
    LinearLayout containerChartStep;

    @ViewById(R.id.chart_performance)
    BarChart chartPerformance;

    // Details
    @ViewById(R.id.tv_distance)
    TextView tvDistance;

    @ViewById(R.id.tv_calorie_count_detail)
    TextView tvCalorieCountDetail;

    @ViewById(R.id.tv_speed)
    TextView tvSpeed;

    private long currentTime;
    private Integer stepCount = 0;
    private Integer calorieCount = 0;
    private Integer distance = 0;
    private Double speed = 0.0;
    private ArrayList<UserStep> stepHistory = new ArrayList<>();

    private SHealthManager sHealthManager;
    private StepCountReader stepCountReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentTime = Constant.TODAY_START_UTC_TIME;

        sHealthManager = new SHealthManager(
                this,
                this,
                new SHealthPermissionManager(this,
                        (BaseActivity) this,
                        () -> {
                            //didGotPermission
                            requestStep();
                        },
                        () -> {
                            // didNotGotPermission
                            // empty
                        }
                ),
                new BaseSHealthType()
        );

        stepCountReader = new StepCountReader(sHealthManager.getHealthDataStore());
        requestData();
    }

    @Override
    public void afterViews() {
        super.afterViews();

        sHealthManager.connectService();
        initChart();
        refreshView();
    }

    @Override
    public void onDestroy() {
        sHealthManager.disconnectService();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sHealthManager.isConnected() && sHealthManager.isPermissionAcquired()) {
            requestData();
        } else {
            sHealthManager.connectService();
        }

        refreshView();
    }

    // Button Listener
    @Click(R.id.button_date_before)
    void clickOnDateBefore() {

        if (!UserSession.instance().isLoggedIn()) {
            Helper.showNeedLoginToast(this);
            return;
        }

        long newTime = currentTime - Constant.ONE_DAY;

        // max 30 days before
        if (newTime < Constant.TODAY_START_UTC_TIME - (30 * Constant.ONE_DAY)) return;

        currentTime = newTime;
        requestData();
        refreshView();
    }

    @Click(R.id.button_date_next)
    void clickOnDateNext() {
        long newTime = currentTime + Constant.ONE_DAY;

        // max today
        if (newTime > Constant.TODAY_START_UTC_TIME) return;

        currentTime = newTime;
        requestData();
        refreshView();
    }

    private void initChart() {
        chartPerformance.setOnChartValueSelectedListener(this);
        chartPerformance.getDescription().setEnabled(false);
        chartPerformance.setPinchZoom(false);
        chartPerformance.setDrawBarShadow(false);
        chartPerformance.setDrawGridBackground(false);

        Legend legend = chartPerformance.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = chartPerformance.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });

        YAxis leftAxis = chartPerformance.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });

        chartPerformance.getAxisRight().setEnabled(false);
    }

    private void setChartData() {
        if (!UserSession.instance().isLoggedIn()) return;

        BarDataSet stepSet;
        ArrayList<BarEntry> yValsStepSet = new ArrayList<>();

        int i = 0;
        for (UserStep stepHistory: stepHistory) {
            float date = (float) i;
            yValsStepSet.add(new BarEntry(date, stepHistory.getStep().floatValue()));
            i++;
        }

        if (chartPerformance.getData() != null && chartPerformance.getData().getDataSetCount() > 0) {

            stepSet = (BarDataSet) chartPerformance.getData().getDataSetByIndex(0);
            stepSet.setValues(yValsStepSet);
            chartPerformance.getData().notifyDataChanged();
            chartPerformance.notifyDataSetChanged();

        } else {
            stepSet = new BarDataSet(yValsStepSet, UserSession.instance().getUser().getName());
            stepSet.setColor(Color.rgb(104, 241, 175));

            BarData data = new BarData(stepSet);
            data.setValueFormatter(new LargeValueFormatter());

            chartPerformance.setData(data);
            chartPerformance.setFitBars(true);
        }

        chartPerformance.invalidate();
    }

    private void refreshView() {
        if (!isAfterViewsOrInjection()) return;

        tvDate.setText(Helper.getFormattedTime(currentTime, Helper.DateFormat.COMPLETE));

        tvStepCount.setText(Helper.getStringOrEmpty(stepCount));
        tvCalorieCount.setText(Helper.getStringOrEmpty(calorieCount));

        Double distanceInKm = Double.valueOf(distance / 1000);
        if (distanceInKm > 0.1) {
            tvDistance.setText(Helper.getStringOrEmpty(distanceInKm, "km"));
        } else {
            tvDistance.setText(Helper.getStringOrEmpty(distance, "meter"));
        }

        tvCalorieCountDetail.setText(Helper.getStringOrEmpty(calorieCount, "kalori"));
        Double speedInKmHour = speed / (1000 * 3600);

        if (speedInKmHour > 0.09)  {
            tvSpeed.setText(Helper.getStringOrEmpty(speedInKmHour, "#.##", "km/jam"));
        } else {
            tvSpeed.setText(Helper.getStringOrEmpty(speed, null, "meter/detik"));
        }

        if (!stepHistory.isEmpty()) {
            containerChartStep.setVisibility(View.VISIBLE);
            setChartData();
        } else {
            containerChartStep.setVisibility(View.GONE);
        }
    }

    private void requestData() {
        if (currentTime == Constant.TODAY_START_UTC_TIME) {
            if (sHealthManager.isConnected() && sHealthManager.isPermissionAcquired()) {
                requestStep();
            } else {
                sHealthManager.connectService();
            }
        } else {
            requestStepFromBE();
        }

        requestStepHistory();
    }

    private void requestStep() {

        resetData();

        stepCountReader.readStepCount(currentTime, stepDailyTrend -> {
            stepCount = stepDailyTrend.getTotalStep();
            calorieCount = stepDailyTrend.getTotalCalorie().intValue();
            distance = stepDailyTrend.getTotalDistance().intValue();
            speed = stepDailyTrend.getTotalSpeed();

            UserSession.instance().updateSteps(stepDailyTrend);

            refreshView();
        });
    }

    private void resetData() {
        stepCount = 0;
        calorieCount = 0;
        distance = 0;
        speed = 0.0;
    }

    private void requestStepFromBE() {
        resetData();

        if (!UserSession.instance().isLoggedIn()) return;

        User user = UserSession.instance().getUser();

        // request yesterday step
        StepsService.instance()
                .getStep(user.getID(), Helper.getFormattedTime(currentTime))
                .enqueue(new APICallback<BaseResponse<UserStep>>(this, Constant.DEFAULT_LOADING_MESSAGE) {
                    @Override
                    public void onResponse(Call<BaseResponse<UserStep>> call, Response<BaseResponse<UserStep>> response) {
                        super.onResponse(call, response);

                        if (response.body() != null && response.body().getData() != null) {
                            stepCount = response.body().getData().getStep();
                            calorieCount = response.body().getData().getCalorie();
                            distance = response.body().getData().getDistance();
                            speed = response.body().getData().getSpeed();

                            refreshView();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<UserStep>> call, Throwable t) {
                        super.onFailure(call, t);
                        // empty on failure
                    }
                });
    }

    private void requestStepHistory() {
        if (!UserSession.instance().isLoggedIn()) return;

        User user = UserSession.instance().getUser();

        StepsService.instance()
                .getTrend(user.getID(), 28)
                .enqueue(new APICallback<BaseResponse<ArrayList<UserStep>>>(this, Constant.DEFAULT_LOADING_MESSAGE) {
                    @Override
                    public void onResponse(Call<BaseResponse<ArrayList<UserStep>>> call, Response<BaseResponse<ArrayList<UserStep>>> response) {
                        super.onResponse(call, response);

                        if (response.body() != null && response.body().getData() != null) {
                            stepHistory = response.body().getData();
                            Collections.reverse(stepHistory);

                            refreshView();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ArrayList<UserStep>>> call, Throwable t) {
                        super.onFailure(call, t);
                        // empty on failure
                    }
                });
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
