package com.imaduddinaf.pertaminahealthassistant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserStep extends SimpleUserStep implements Serializable {

    @SerializedName("distance")
    Integer distance;

    @SerializedName("calorie")
    Integer calorie;

    @SerializedName("speed")
    Double speed;

    @SerializedName("user")
    User user;

    @SerializedName("last_updated")
    String lastUpdatedString;

    public UserStep(Integer step) {
        super(step);
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getCalorie() {
        return calorie;
    }

    public User getUser() {
        return user;
    }

    public String getLastUpdatedString() {
        return lastUpdatedString;
    }

    public Double getSpeed() {
        return speed;
    }
}
