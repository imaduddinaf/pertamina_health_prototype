package com.imaduddinaf.pertaminahealthassistant.shealth.model;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class HeartRateData {
    private Double heartBeatCount = 0.0;
    private Double heartRate = 0.0;
    private Double minHeartRate = 0.0;
    private Double maxHeartRate = 0.0;
    private Double comment = 0.0;

    public HeartRateData() {

    }

    public HeartRateData(Double heartBeatCount,
                         Double heartRate,
                         Double minHeartRate,
                         Double maxHeartRate,
                         Double comment) {
        this.heartBeatCount = heartBeatCount;
        this.heartRate = heartRate;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.comment = comment;
    }

    public Double getHeartBeatCount() {
        return heartBeatCount;
    }

    public Double getHeartRate() {
        return heartRate;
    }

    public Double getMinHeartRate() {
        return minHeartRate;
    }

    public Double getMaxHeartRate() {
        return maxHeartRate;
    }
}
