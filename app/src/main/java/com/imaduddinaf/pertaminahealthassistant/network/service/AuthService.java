package com.imaduddinaf.pertaminahealthassistant.network.service;

import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {

    @FormUrlEncoded
    @POST("login.php")
    Call<BaseResponse<User>> auth(@Field("username") String username, @Field("password") String password);
}
