package com.imaduddinaf.pertaminahealthassistant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.Helper;
import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.UserSession;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.model.BaseResponse;
import com.imaduddinaf.pertaminahealthassistant.model.User;
import com.imaduddinaf.pertaminahealthassistant.network.APICallback;
import com.imaduddinaf.pertaminahealthassistant.network.APIManager;
import com.imaduddinaf.pertaminahealthassistant.network.service.AuthService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.et_username)
    EditText edUsername;

    @ViewById(R.id.et_password)
    EditText edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public String getCustomTitle() {
        return "Login";
    }

    @Click(R.id.button_login)
    void login() {
        String username = "fahmikurniawan";//edUsername.getText().toString();
        String password = "fahmi";//edPassword.getText().toString();
        password = Helper.md5(password);

        Log.d(Constant.DEBUG_TAG, username + " - " + password);

        AuthService authService = APIManager.get().service().create(AuthService.class);
        Call<BaseResponse<User>> call = authService.auth(username, password);

        String finalPassword = password;
        call.enqueue(new APICallback<BaseResponse<User>>(this, Constant.DEFAULT_LOADING_MESSAGE) {
            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                super.onResponse(call, response);
                onSuccessLogin(response.body(), username, finalPassword);
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                super.onFailure(call, t);
                onFailureLogin();
            }
        });
    }

    @Click(R.id.button_register)
    void register() {
        Helper.showUnderConstructionToast(this);
    }

    private void onSuccessLogin(BaseResponse<User> response, String username, String password) {

        if (response != null && response.getData() != null) {
            Log.d(Constant.DEBUG_TAG, response.getMessage());
            Helper.showMessage(this, "Login berhasil");
            UserSession.instance().login(getApplicationContext(),
                    response.getData(),
                    username,
                    password);
            finish();
        } else {
            onFailureConnection();
        }
    }

    private void onFailureLogin() {
        Helper.showMessage(this, "Terjadi kesalahan pada username atau password");
    }

    private void onFailureConnection() {
        Helper.showMessage(this, "Terjadi kesalahan pada server");
    }
}
