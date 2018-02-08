package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Collection;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment(R.layout.fragment_home)
public class HomeFragment extends BaseFragment implements OnChartValueSelectedListener {

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

    @ViewById(R.id.chart_performance)
    BarChart chartPerformance;

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
    private ArrayList<UserStep> tmpStepTrend = new ArrayList<>();
    private ArrayList<SimpleUserAverageStep> tmpAverageTrend = new ArrayList<>();

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
        initChart();
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

    private void initChart() {
        chartPerformance.setOnChartValueSelectedListener(this);
        chartPerformance.getDescription().setEnabled(false);
        chartPerformance.setPinchZoom(false);
        chartPerformance.setDrawBarShadow(false);
        chartPerformance.setDrawGridBackground(false);

        Legend legend = chartPerformance.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(true);
        legend.setYOffset(0f);
        legend.setXOffset(5f);
        legend.setYEntrySpace(0f);
        legend.setTextSize(8f);

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

        BarDataSet averageSet, userSet;
        ArrayList<BarEntry> yValsAverageSet = new ArrayList<>();
        ArrayList<BarEntry> yValsUserSet = new ArrayList<>();

        int groupCount = 14;
        int startX = 0;
        int endX = startX + groupCount;

        float groupSpace = 0.08f;
        float barSpace = 0.01f;
        float barWidth = 0.1f;

        Log.d(Constant.DEBUG_TAG, "average count: " + tmpAverageTrend.size() + " - " + tmpStepTrend.size());

        int i = 0;
        for (SimpleUserAverageStep averageStep : tmpAverageTrend) {
            float date = (float) i;
            yValsAverageSet.add(new BarEntry(date, averageStep.getAverage().floatValue()));
            i++;
        }

        i = 0;
        for (UserStep userStep : tmpStepTrend) {
            float date = (float) i;
            yValsUserSet.add(new BarEntry(date, userStep.getStep().floatValue()));
            i++;
        }

        if (chartPerformance.getData() != null && chartPerformance.getData().getDataSetCount() > 0) {

            averageSet = (BarDataSet) chartPerformance.getData().getDataSetByIndex(0);
            userSet = (BarDataSet) chartPerformance.getData().getDataSetByIndex(1);
            averageSet.setValues(yValsAverageSet);
            userSet.setValues(yValsUserSet);
            chartPerformance.getData().notifyDataChanged();
            chartPerformance.notifyDataSetChanged();

        } else {
            averageSet = new BarDataSet(yValsAverageSet, "Rata-rata");
            averageSet.setColor(Color.rgb(104, 241, 175));
            userSet = new BarDataSet(yValsUserSet, UserSession.instance().getUser().getName());
            userSet.setColor(Color.rgb(255, 102, 0));

            BarData data = new BarData(averageSet, userSet);
            data.setValueFormatter(new LargeValueFormatter());

            chartPerformance.setData(data);
        }

        chartPerformance.getBarData().setBarWidth(barWidth);
        chartPerformance.getXAxis().setAxisMinimum(startX);

        chartPerformance.getXAxis()
                .setAxisMaximum(startX + chartPerformance.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chartPerformance.groupBars(startX, groupSpace, barSpace);
        chartPerformance.invalidate();
    }

    private void refreshView() {
        if (!isAfterViewsOrInjection()) {
            addIntoQueue(this::refreshView);
            Log.d(Constant.DEBUG_TAG, "add into queue");
            return;
        }

        // current steps
        tvAvgStepCount.setText(Helper.getStringOrEmpty(todayAllAvgStep));
        tvCurrentStepCount.setText(Helper.getStringOrEmpty(todayStep));

        // chart
        containerChartStep.setVisibility(View.GONE);
        if (stepTrendAll != null &&
                stepTrendAll.getUserSteps() != null &&
                !stepTrendAll.getUserSteps().isEmpty() &&
                stepTrendAll.getAverageSteps() != null &&
                !stepTrendAll.getAverageSteps().isEmpty() &&
                UserSession.instance().isLoggedIn()) {
            containerChartStep.setVisibility(View.VISIBLE);
            setChartData();
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

            // request a-month step performance
            StepsService.instance()
                    .getPerformance(user.getID(), 14)
                    .enqueue(new APICallback<BaseResponse<UserStepTrend>>() {
                        @Override
                        public void onResponse(Call<BaseResponse<UserStepTrend>> call, Response<BaseResponse<UserStepTrend>> response) {
                            super.onResponse(call, response);
                            if (response.body() != null && response.body().getData() != null) {
                                stepTrendAll = response.body().getData();

                                tmpStepTrend = stepTrendAll.getUserSteps();
                                tmpAverageTrend = stepTrendAll.getAverageSteps();

                                Collections.reverse(tmpStepTrend);
                                Collections.reverse(tmpAverageTrend);

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

    // Bar chart listener
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
