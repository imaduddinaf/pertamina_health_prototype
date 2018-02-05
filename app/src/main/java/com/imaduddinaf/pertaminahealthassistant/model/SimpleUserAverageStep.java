package com.imaduddinaf.pertaminahealthassistant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SimpleUserAverageStep extends BaseModel implements Serializable {

    @SerializedName("average")
    Double average;

    @SerializedName("date")
    String dateString;

    public Double getAverage() {
        return average;
    }

    public String getDateString() {
        return dateString;
    }
}
