package com.imaduddinaf.pertaminahealthassistant.core;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.imaduddinaf.pertaminahealthassistant.Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;

@EFragment
public class BaseFragment extends Fragment {

    private boolean isAfterViewsOrInjection = false;
    private ArrayList<Runnable> queue = new ArrayList<>();

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

        for (Runnable runnable: queue) {
            Log.d(Constant.DEBUG_TAG, "run queue");
            runnable.run();
        }

        queue.clear();
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

    protected void addIntoQueue(Runnable runnable) {
        queue.add(runnable);
    }
}
