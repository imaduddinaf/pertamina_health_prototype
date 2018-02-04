package com.imaduddinaf.pertaminahealthassistant.shealth.type;

import com.samsung.android.sdk.healthdata.HealthConstants;

public class WeightCountType extends BaseSHealthType {

    public static final String[] DATA_TYPES = new String[] {
            HealthConstants.Weight.HEALTH_DATA_TYPE
    };

    public WeightCountType() {
        super(DATA_TYPES);
    }
}
