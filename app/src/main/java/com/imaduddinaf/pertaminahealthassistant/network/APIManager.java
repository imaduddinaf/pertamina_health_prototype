package com.imaduddinaf.pertaminahealthassistant.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class APIManager {
    public static String BASE_URL = "";
    private static APIManager mInstance = null;
    private Retrofit retrofit = null;

    private APIManager() {}

    public static APIManager get() {
        if (mInstance == null) {
            mInstance = new APIManager();
        }

        return mInstance;
    }

    public Retrofit service() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
