package com.imaduddinaf.pertaminahealthassistant.shealth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.imaduddinaf.pertaminahealthassistant.Constant;
import com.samsung.android.sdk.shealth.Shealth;
import com.samsung.android.sdk.shealth.tracker.TrackerManager;

/**
 * Created by Imaduddin Al Fikri on 04-Feb-18.
 */

public class SHealthTrackerManager {

    private TrackerManager trackerManager = null;

    public SHealthTrackerManager(Context context) {
        Shealth shealth = new Shealth();
        try {
            shealth.initialize(context);
            if (shealth.isFeatureEnabled(Shealth.FEATURE_TRACKER_TILE, Shealth.FEATURE_TRACKER_LAUNCH_EXTENDED)) {
                trackerManager = new TrackerManager(context);
            } else {
                Log.d(Constant.DEBUG_TAG, "SHealth should be upgraded");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.SHEALTH_STORE_URL));
                context.startActivity(intent);
            }
        } catch (IllegalStateException e) {
            Log.e(Constant.ERROR_TAG, e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            Log.e(Constant.ERROR_TAG, e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public TrackerManager getTrackerManager() {
        return trackerManager;
    }

    public void startActivity(Context context, View view, String trackerID) {
        try {
            trackerManager.startActivity((Activity)view.getContext(), trackerID);
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
