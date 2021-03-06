package com.imaduddinaf.pertaminahealthassistant.core;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.imaduddinaf.pertaminahealthassistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EActivity(R.layout.activity_blank)
public class BaseActivity extends AppCompatActivity {

    private boolean isAfterViewsOrInjection = false;

    public boolean isAfterViewsOrInjection() {
        return isAfterViewsOrInjection;
    }

    public void setAfterViewsOrInjection(boolean afterViewsOrInjection) {
        isAfterViewsOrInjection = afterViewsOrInjection;
    }

    @AfterViews
    public void afterViews() {
        setTitle(getCustomTitle());

        if (shouldShowBackButton()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        isAfterViewsOrInjection = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        isAfterViewsOrInjection = false;
    }

    @Override
    public void onStop() {
        super.onStop();

        isAfterViewsOrInjection = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isAfterViewsOrInjection = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home && shouldShowBackButton()) finish();

        return super.onOptionsItemSelected(item);
    }

    public boolean shouldShowBackButton() {
        return true;
    }

    public String getCustomTitle() {
        return "Pertamina Health Assistant";
    }
}
