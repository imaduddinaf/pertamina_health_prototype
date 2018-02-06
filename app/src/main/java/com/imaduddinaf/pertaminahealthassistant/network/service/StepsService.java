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
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public interface StepsService {

    public static StepsService instance() {
        return APIManager.get().service().create(StepsService.class);
    }

    @GET("rank.php")
    Call<BaseResponse<ArrayList<UserStep>>> getLeaderboard(@Query("date") String date);

    @GET("average_today.php")
    Call<BaseResponse<SimpleUserAverageStep>> getAvgAll(@Query("date") String date);

    @GET("my_step.php")
    Call<BaseResponse<ArrayList<UserStep>>> getTrend(@Query("id_user") long userID,
                                                     @Query("amount") Integer amount);

    @GET("my_step_today.php")
    Call<BaseResponse<UserStep>> getStep(@Query("id_user") long userID,
                                         @Query("date") String date);

    @GET("step_performance.php")
    Call<BaseResponse<UserStepTrend>> getPerformance(@Query("id_user") long userID,
                                                     @Query("amount") Integer amount);

    @FormUrlEncoded
    @POST("add_step.php")
    Call<BaseResponse<String>> updateStep(@Field("id_user") long userID,
                                          @Field("date") String date,
                                          @Field("step") Integer step,
                                          @Field("calorie") Integer calorie,
                                          @Field("distance") Integer distance,
                                          @Field("speed") Double speed);

}
