package com.imaduddinaf.pertaminahealthassistant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Imaduddin Al Fikri on 05-Feb-18.
 */

public class BaseResponse<T> implements Serializable {

    @SerializedName("status")
    String status = "";

    @SerializedName("message")
    String message = "";

    @SerializedName("data")
    T data;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}
