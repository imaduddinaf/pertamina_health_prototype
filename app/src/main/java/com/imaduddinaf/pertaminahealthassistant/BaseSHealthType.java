package com.imaduddinaf.pertaminahealthassistant;

import com.samsung.android.sdk.healthdata.HealthPermissionManager;

/**
 * Created by Imaduddin Al Fikri on 03-Feb-18.
 */

public class BaseSHealthType {
    private String[] types;

    public BaseSHealthType(String[] types) {
        this.types = types;
    }

    public String[] getTypes() {
        return types;
    }
}
