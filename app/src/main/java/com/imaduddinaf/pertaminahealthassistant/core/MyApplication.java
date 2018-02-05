package com.imaduddinaf.pertaminahealthassistant.core;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.imaduddinaf.pertaminahealthassistant.Constant;

import okhttp3.OkHttpClient;

/**
 * Created by Imaduddin Al Fikri on 05-Feb-18.
 */

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        Fresco.initialize(this);
    }
}
