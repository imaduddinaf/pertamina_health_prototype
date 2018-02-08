package com.imaduddinaf.pertaminahealthassistant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SimpleUserStep extends BaseModel implements Serializable {

    @SerializedName("step")
    Integer step;

    @SerializedName("date")
    String dateString;

    public SimpleUserStep(Integer step) {
        this.step = step;
    }

    public Integer getStep() {
        return step;
    }

    public String getDateString() {
        return dateString;
    }
}
