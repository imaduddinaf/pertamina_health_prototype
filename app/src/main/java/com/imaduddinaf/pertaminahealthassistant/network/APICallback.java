package com.imaduddinaf.pertaminahealthassistant.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Imaduddin Al Fikri on 05-Feb-18.
 */

public class APICallback<T> implements Callback<T> {

    private ProgressDialog progressDialog = null;

    public APICallback(BaseActivity context, String loadingMessage) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);

        if (loadingMessage != null && !progressDialog.isShowing()) {
            progressDialog.setMessage(loadingMessage);
            progressDialog.show();
        }
    }

    public APICallback() {
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
}
