package com.imaduddinaf.pertaminahealthassistant.shealth.type;

import com.imaduddinaf.pertaminahealthassistant.Helper;

/**
 * Created by Imaduddin Al Fikri on 03-Feb-18.
 */

public class BaseSHealthType {
    private String[] types;

    public BaseSHealthType() {
        types = getAllTypes();
    }

    public BaseSHealthType(String[] types) {
        this.types = types;
    }

    public String[] getTypes() {
        return types;
    }

    public static String[] getAllTypes() {
        String[] allTypes = new String[]{};
        allTypes = Helper.concat(allTypes, StepCountType.DATA_TYPES);
        allTypes = Helper.concat(allTypes, HeartRateCountType.DATA_TYPES);
        allTypes = Helper.concat(allTypes, WeightCountType.DATA_TYPES);
        allTypes = Helper.concat(allTypes, SleepCountType.DATA_TYPES);

        return allTypes;
    }
}
