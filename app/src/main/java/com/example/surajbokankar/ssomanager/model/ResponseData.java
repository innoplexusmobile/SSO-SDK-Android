package com.example.surajbokankar.ssomanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.ResponseCache;

/**
 * Created by suraj.bokankar on 15/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "info",
        "filters",
        "otp_enabled",
        "callback_url",
        "success",
        "permissions"
        ,"is_user_consent"
})
public class ResponseData {
    @JsonProperty("info")
    public UserInfo info=null;
    @JsonProperty("filters")
    public Object filters;
    @JsonProperty("otp_enabled")
    public Boolean otpEnabled;
    @JsonProperty("callback_url")
    public String callbackUrl;
    @JsonProperty("success")
    public Boolean success;
    @JsonProperty("permissions")
    public Permission permissions;
    @JsonProperty("is_user_consent")
    public boolean is_user_consent=false;


    @Override
    public String toString() {
        return info.toString()+callbackUrl+permissions.toString();
    }

    public ResponseData(){

    }
}
