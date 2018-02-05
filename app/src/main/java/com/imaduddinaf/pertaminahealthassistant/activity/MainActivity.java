package com.imaduddinaf.pertaminahealthassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.imaduddinaf.pertaminahealthassistant.UserSession;
import com.imaduddinaf.pertaminahealthassistant.shealth.type.BaseSHealthType;
import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthManager;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthPermissionManager;
import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;
import com.imaduddinaf.pertaminahealthassistant.fragment.HomeFragment_;
import com.imaduddinaf.pertaminahealthassistant.fragment.ProfileFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import okhttp3.OkHttpClient;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private BaseFragment fragment;
    private FragmentManager fragmentManager;

    private SHealthManager sHealthManager;

    @ViewById(R.id.bottom_navigation)
    public BottomNavigationView navigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (fragment.getClass() == HomeFragment_.class) return true;

                    fragment = new HomeFragment_();

                    break;
                case R.id.navigation_profile:
                    if (!UserSession.instance().isLoggedIn()) {
                        Intent myIntent = new Intent(getApplicationContext(), LoginActivity_.class);
                        getApplicationContext().startActivity(myIntent);
                        return false;
                    }

                    if (fragment.getClass() == ProfileFragment_.class) return true;

                    fragment = new ProfileFragment_();

                    break;
            }
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content, fragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Fragment
        fragment = new HomeFragment_();
        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment).commit();

        doAfterAppLaunch();
    }

    @AfterViews
    void initViews() {

        // Init Bottom Navigation View
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean shouldShowBackButton() {
        return false;
    }

    private void doAfterAppLaunch() {
        // get samsung health permission
        sHealthManager = new SHealthManager(
                this,
                this,
                new SHealthPermissionManager(
                        this,
                        this,
                        () -> {
                            //didGotPermission
                        },
                        () -> {
                            getSHealthPermission();
                        }
                ),
                new BaseSHealthType()
        );

        sHealthManager.connectService();

        if (UserSession.instance().isUserAlreadyLoggedInBefore(this)) {
            Log.d(Constant.DEBUG_TAG, "can autologin");

            UserSession.instance().autoLogin(
                    this,
                    user -> {

                    },
                    () -> {
                        // empty on failure
                    }
            );
        } else {
            Log.d(Constant.DEBUG_TAG, "cant autologin");
        }
    }

    private void getSHealthPermission() {
        if (!sHealthManager.isPermissionAcquired()) {
            sHealthManager.requestPermission();
        }
    }
}
