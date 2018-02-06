package com.imaduddinaf.pertaminahealthassistant.network.service;

import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.network.APIManager;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Imaduddin Al Fikri on 05-Feb-18.
 */

public interface FactService {

    public static FactService instance() {
        return APIManager.get().service().create(FactService.class);
    }

    @GET("get_message.php")
    Call<BaseResponse<String>> getFact(@Query("date") String date);
}
