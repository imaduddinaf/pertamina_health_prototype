package com.imaduddinaf.pertaminahealthassistant.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

public class APIManager {
    public static String BASE_URL = "https://pertaminahealth.000webhostapp.com/Monitoring/api/";
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
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }
}
