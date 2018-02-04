package com.imaduddinaf.pertaminahealthassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.imaduddinaf.pertaminahealthassistant.core.BaseActivity;
import com.imaduddinaf.pertaminahealthassistant.core.Helper;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Imaduddin Al Fikri on 03-Feb-18.
 */

public class SHealthPermissionManager {

    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> permissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                @Override
                public void onResult(HealthPermissionManager.PermissionResult result) {
                    Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

                    if (resultMap.values().contains(Boolean.FALSE)) {
                        onNotGotPermission.run();
                        showPermissionAlarmDialog();
                    } else {
                        onGotPermission.run();
                    }
                }
            };

    private Runnable onGotPermission;
    private Runnable onNotGotPermission;
    private Context context;
    private BaseActivity activityHolder;

    public SHealthPermissionManager(Context context,
                                    BaseActivity activityHolder,
                                    Runnable onGotPermission,
                                    Runnable onNotGotPermission) {
        this.context = context;
        this.activityHolder = activityHolder;
        this.onGotPermission = onGotPermission;
        this.onNotGotPermission = onNotGotPermission;
    }

    public Runnable getOnGotPermission() {
        return onGotPermission;
    }

    public Runnable getOnNotGotPermission() {
        return onNotGotPermission;
    }

    private void showPermissionAlarmDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.notice)
                .setMessage(R.string.msg_perm_acquired)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public Set<HealthPermissionManager.PermissionKey> generatePermissionKeySet(String[] dataTypes,
                                                                               HealthPermissionManager.PermissionType permissionType) {
        Set<HealthPermissionManager.PermissionKey> pmsKeySet = new HashSet<>();

        for (String dataType: dataTypes) {
            pmsKeySet.add(new HealthPermissionManager.PermissionKey(dataType, permissionType));
        }

        return pmsKeySet;
    }

    public boolean isPermissionAcquired(HealthDataStore healthDataStore,
                                        String[] dataTypes,
                                        HealthPermissionManager.PermissionType permissionType) {
        HealthPermissionManager pmsManager = new HealthPermissionManager(healthDataStore);
        try {
            // Check whether the permissions that this application needs are acquired
            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(generatePermissionKeySet(dataTypes, permissionType));
            return !resultMap.values().contains(Boolean.FALSE);
        } catch (Exception e) {
            Log.e(Helper.ERROR_TAG, "Permission request fails.", e);
        }
        return false;
    }

    public void requestPermission(HealthDataStore healthDataStore,
                                  String[] dataTypes,
                                  HealthPermissionManager.PermissionType permissionType) {
        HealthPermissionManager pmsManager = new HealthPermissionManager(healthDataStore);
        try {
            // Show user permission UI for allowing user to change options
            pmsManager.requestPermissions(generatePermissionKeySet(dataTypes, permissionType), activityHolder)
                    .setResultListener(permissionListener);
        } catch (Exception e) {
            Log.e(Helper.ERROR_TAG, "Permission setting fails.", e);
        }
    }
}
