package com.imaduddinaf.pertaminahealthassistant.shealth.type;

import com.samsung.android.sdk.healthdata.HealthConstants;

public class SleepCountType extends BaseSHealthType {

    public static final String[] DATA_TYPES = new String[] {
            HealthConstants.Sleep.HEALTH_DATA_TYPE
    };

    public SleepCountType() {
        super(DATA_TYPES);
    }
}
