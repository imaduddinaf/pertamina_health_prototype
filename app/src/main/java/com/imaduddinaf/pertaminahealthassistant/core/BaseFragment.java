package com.imaduddinaf.pertaminahealthassistant.core;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

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

        getActivityHolder().setTitle(getCustomTitle());
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

    public BaseActivity getActivityHolder() {
        return (BaseActivity) this.getActivity();
    }

    public String getCustomTitle() {
        return getActivityHolder().getCustomTitle();
    }

}
