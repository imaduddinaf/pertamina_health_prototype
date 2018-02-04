package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.shealth.SHealthTrackerManager;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;
import com.samsung.android.sdk.shealth.tracker.TrackerManager;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment(R.layout.fragment_home)
public class HomeFragment extends BaseFragment {

    @ViewById(R.id.container_step_count)
    LinearLayout containerStepCount;

    @ViewById(R.id.tv_last_step_count)
    TextView tvLastStepCount;

    @ViewById(R.id.tv_step_count)
    TextView tvStepCount;

    private SHealthTrackerManager sHealthTrackerManager = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sHealthTrackerManager = new SHealthTrackerManager(this.getContext());
    }

    @Override
    protected void afterViews() {
        super.afterViews();
    }

    @Click(R.id.container_step_count)
    void tapOnContainerStepCount(View v) {
        sHealthTrackerManager.startActivity(this.getContext(), v, TrackerManager.TrackerId.HEART_RATE);
    }
}
