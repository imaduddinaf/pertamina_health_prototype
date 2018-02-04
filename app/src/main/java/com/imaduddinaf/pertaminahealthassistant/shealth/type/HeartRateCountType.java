package com.imaduddinaf.pertaminahealthassistant.shealth.type;

import com.samsung.android.sdk.healthdata.HealthConstants;

public class HeartRateCountType extends BaseSHealthType {

    public static final String[] DATA_TYPES = new String[] {
            HealthConstants.HeartRate.HEALTH_DATA_TYPE
    };

    public HeartRateCountType() {
        super(DATA_TYPES);
    }
}
