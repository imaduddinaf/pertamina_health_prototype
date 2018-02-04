package com.imaduddinaf.pertaminahealthassistant;

import java.util.List;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class StepDailyTrend {
    private Integer totalStep = 0;
    private Double totalCalorie = 0.0;
    private Double totalSpeed = 0.0;
    private Double totalDistance = 0.0;

    private List<StepCountReader.StepBinningData> details = null;

    public StepDailyTrend() {

    }

    public StepDailyTrend(Integer totalStep,
                          Double totalCalorie,
                          Double totalSpeed,
                          Double totalDistance,
                          List<StepCountReader.StepBinningData> details) {
        this.totalStep = totalStep;
        this.totalCalorie = totalCalorie;
        this.totalSpeed = totalSpeed;
        this.totalDistance = totalDistance;
        this.details = details;
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

    public List<StepCountReader.StepBinningData> getDetails() {
        return details;
    }
}
