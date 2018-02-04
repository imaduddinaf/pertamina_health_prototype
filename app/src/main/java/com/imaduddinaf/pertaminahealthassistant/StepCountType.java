package com.imaduddinaf.pertaminahealthassistant;

import com.samsung.android.sdk.healthdata.HealthConstants;

public class StepCountType extends BaseSHealthType {

    public static final String[] DATA_TYPES = new String[] {
            HealthConstants.StepCount.HEALTH_DATA_TYPE,
            StepCountReader.STEP_SUMMARY_DATA_TYPE_NAME
    };

    public StepCountType() {
        super(DATA_TYPES);
    }
}
