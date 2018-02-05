package com.imaduddinaf.pertaminahealthassistant.network.service;

import com.imaduddinaf.pertaminahealthassistant.UserSession;
import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.model.SimpleUserAverageStep;
import com.imaduddinaf.pertaminahealthassistant.model.UserStep;
import com.imaduddinaf.pertaminahealthassistant.model.UserStepTrend;
import com.imaduddinaf.pertaminahealthassistant.network.APIManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public interface StepsService {

    public static StepsService instance() {
        return APIManager.get().service().create(StepsService.class);
    }

    @FormUrlEncoded
    @POST("rank.php")
    Call<BaseResponse<ArrayList<UserStep>>> getLeaderboard(@Field("date") String date);

    @FormUrlEncoded
    @POST("average_today.php")
    Call<BaseResponse<SimpleUserAverageStep>> getAvgAll(@Field("date") String date);

    @FormUrlEncoded
    @POST("my_step.php")
    Call<BaseResponse<UserStepTrend>> getTrend(@Field("id_user") long userID,
                                               @Field("amount") Integer amount);

    @FormUrlEncoded
    @POST("add_step.php")
    Call<BaseResponse<String>> updateStep(@Field("id_user") long userID,
                                          @Field("date") String date,
                                          @Field("step") Integer step,
                                          @Field("calorie") Integer calorie,
                                          @Field("distance") Integer distance);

}
