package com.imaduddinaf.pertaminahealthassistant.shealth.model;

import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.shealth.reader.StepCountReader;

import java.util.List;

public class StepDailyTrend {
    private Integer totalStep = 0;
    private Double totalCalorie = 0.0;
    private Double totalSpeed = 0.0;
    private Double totalDistance = 0.0;

    public StepDailyTrend() {

    }

    public StepDailyTrend(Integer totalStep,
                          Double totalCalorie,
                          Double totalSpeed,
                          Double totalDistance) {
        this.totalStep = totalStep;
        this.totalCalorie = totalCalorie;
        this.totalSpeed = totalSpeed;
        this.totalDistance = totalDistance;
    }

    public Integer getTotalStep() {
        return totalStep;
    }

    public Double getTotalCalorie() {
        return totalCalorie;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public Double getTotalSpeed() {
        return totalSpeed;
    }

}
