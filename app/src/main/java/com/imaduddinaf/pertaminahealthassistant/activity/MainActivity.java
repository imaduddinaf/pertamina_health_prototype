package com.imaduddinaf.pertaminahealthassistant.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

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
                    fragment = new HomeFragment_();
                    break;
                case R.id.navigation_profile:
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
    }

    private void getSHealthPermission() {
        if (!sHealthManager.isPermissionAcquired()) {
            sHealthManager.requestPermission();
        }
    }
}
