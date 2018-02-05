package com.imaduddinaf.pertaminahealthassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.model.User;
import com.imaduddinaf.pertaminahealthassistant.network.APICallback;
import com.imaduddinaf.pertaminahealthassistant.network.APIManager;
import com.imaduddinaf.pertaminahealthassistant.network.service.AuthService;
import com.imaduddinaf.pertaminahealthassistant.network.service.StepsService;
import com.imaduddinaf.pertaminahealthassistant.shealth.model.StepDailyTrend;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Imaduddin Al Fikri on 05-Feb-18.
 */

public class UserSession {

    private static final String PREF_NAME = "pref";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private static UserSession instance = null;
    private User user = null;

    private UserSession() {}

    public static UserSession instance() {
        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public User getUser() {
        return user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void login(Context context, User user, String username, String password) {
        Log.d(Constant.DEBUG_TAG, "login");
        this.user = user;
        saveStringIntoPref(context, USERNAME_KEY, username);
        saveStringIntoPref(context, PASSWORD_KEY, password);
    }

    public void logout(Context context) {
        Log.d(Constant.DEBUG_TAG, "logout");
        user = null;
        clearPref(context);
    }

    public boolean isUserAlreadyLoggedInBefore(Context context) {
        String username = getStringFromPref(context, USERNAME_KEY);
        String password = getStringFromPref(context, PASSWORD_KEY);

        return username != null && password != null;
    }

    public void autoLogin(BaseActivity context, Consumer<User> onSuccess, Runnable onFailure) {
        if (!isUserAlreadyLoggedInBefore(context)) return;

        Log.d(Constant.DEBUG_TAG, "do autologin");

        String username = getStringFromPref(context, USERNAME_KEY);
        String password = getStringFromPref(context, PASSWORD_KEY);

        AuthService authService = APIManager.get().service().create(AuthService.class);
        Call<BaseResponse<User>> call = authService.auth(username, password);

        call.enqueue(new APICallback<BaseResponse<User>>(context, Constant.DEFAULT_LOADING_MESSAGE) {
            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    onSuccess.accept(user);
                    login(context, user, username, password);
                } else {
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                super.onFailure(call, t);
                onFailure.run();
//                logout(context);
            }
        });
    }

    private SharedPreferences getPref(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private SharedPreferences.Editor prefEditor(Context context) {
        return getPref(context).edit();
    }

    public void saveStringIntoPref(Context context, String key, String value) {
        Log.d(Constant.DEBUG_TAG, "save: " + key + " - " + value);
        prefEditor(context).putString(key, value);
        prefEditor(context).apply();
    }

    public String getStringFromPref(Context context, String key) {
        return getPref(context).getString(key, null);
    }

    public void clearPref(Context context) {
//        prefEditor(context).remove(USERNAME_KEY);
//        prefEditor(context).remove(PASSWORD_KEY);
        prefEditor(context).clear();
        prefEditor(context).apply();
    }

    public void updateSteps(StepDailyTrend stepDailyTrend) {
        if (!isLoggedIn()) return;

        Integer stepCount = stepDailyTrend.getTotalStep();
        Integer calorieCount = stepDailyTrend.getTotalCalorie().intValue();
        Integer distance = stepDailyTrend.getTotalDistance().intValue();

        Log.d(Constant.DEBUG_TAG, "distance: " + stepDailyTrend.getTotalDistance());

        StepsService.instance().updateStep(
                UserSession.instance().getUser().getID(),
                Helper.getFormattedTime(Constant.TODAY_START_UTC_TIME),
                stepCount,
                calorieCount,
                distance)
                .enqueue(new APICallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                        super.onResponse(call, response);
                        // empty on success
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                        super.onFailure(call, t);
                        // empty on failure
                    }
                });
    }
}
