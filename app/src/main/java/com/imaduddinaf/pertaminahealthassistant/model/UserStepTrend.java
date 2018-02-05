package com.imaduddinaf.pertaminahealthassistant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Imaduddin Al Fikri on 05-Feb-18.
 */

public class UserStepTrend extends BaseModel implements Serializable {

    @SerializedName("user_steps")
    ArrayList<UserStep> userSteps;

    @SerializedName("average_steps")
    ArrayList<SimpleUserAverageStep> averageSteps;

    public ArrayList<UserStep> getUserSteps() {
        return userSteps;
    }

    public ArrayList<SimpleUserAverageStep> getAverageSteps() {
        return averageSteps;
    }
}
