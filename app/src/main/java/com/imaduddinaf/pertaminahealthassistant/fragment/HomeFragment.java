package com.imaduddinaf.pertaminahealthassistant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imaduddinaf.pertaminahealthassistant.R;
import com.imaduddinaf.pertaminahealthassistant.core.BaseFragment;
import com.imaduddinaf.pertaminahealthassistant.core.Helper;
import com.samsung.android.sdk.shealth.Shealth;
import com.samsung.android.sdk.shealth.tracker.TrackerManager;
import com.samsung.android.sdk.shealth.tracker.TrackerTileManager;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Imaduddin Al Fikri on 31-Jan-18.
 */

@EFragment(R.layout.fragment_home)
public class HomeFragment extends BaseFragment {
    private static final String STORE_URL = "market://details?id=com.sec.android.app.shealth";

    private TrackerManager mTrackerManager = null;

    @ViewById(R.id.container_step_count)
    LinearLayout containerStepCount;

    @ViewById(R.id.tv_last_step_count)
    TextView tvLastStepCount;

    @ViewById(R.id.tv_step_count)
    TextView tvStepCount;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shealth shealth = new Shealth();
        try {
            shealth.initialize(this.getContext());
            if (shealth.isFeatureEnabled(Shealth.FEATURE_TRACKER_TILE, Shealth.FEATURE_TRACKER_LAUNCH_EXTENDED)) {
                mTrackerManager = new TrackerManager(this.getContext());
            } else {
                Log.d(Helper.DEBUG_TAG, "SHealth should be upgraded");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(STORE_URL));
                this.startActivity(intent);
            }
        } catch (IllegalStateException e) {
            Log.e(Helper.ERROR_TAG, e.toString());
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            Log.e(Helper.ERROR_TAG, e.toString());
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Click(R.id.container_step_count)
    void tapOnContainerStepCount(View v) {
        try {
            mTrackerManager.startActivity((Activity)v.getContext(), TrackerManager.TrackerId.HEART_RATE);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
