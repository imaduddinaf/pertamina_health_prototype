package com.imaduddinaf.pertaminahealthassistant.core;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment
public class BaseFragment extends Fragment {

    private boolean isAfterViewsOrInjection = false;

    public boolean isAfterViewsOrInjection() {
        return isAfterViewsOrInjection;
    }

    public void setAfterViewsOrInjection(boolean afterViewsOrInjection) {
        isAfterViewsOrInjection = afterViewsOrInjection;
    }

    @AfterViews
    protected void afterViews() {
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
    public void onDetach() {
        super.onDetach();

        isAfterViewsOrInjection = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isAfterViewsOrInjection = false;
    }
}
