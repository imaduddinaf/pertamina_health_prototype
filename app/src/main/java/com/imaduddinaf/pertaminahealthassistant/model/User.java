package com.imaduddinaf.pertaminahealthassistant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Imaduddin Al Fikri on 05-Feb-18.
 */

public class User extends BaseModel implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("jabatan")
    private String level;

    @SerializedName("photo_url")
    private String profilePhotoURL;

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }
}
