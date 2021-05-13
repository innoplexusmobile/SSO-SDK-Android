package com.example.surajbokankar.ssomanager.model.signup;

import com.example.surajbokankar.ssomanager.model.Permission;
import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by suraj.bokankar on 20/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "success",
        "message","responseData","info",
        "filters",
        "otp_enabled",
        "callback_url",
        "permissions"

})
public class AuthResponseData {
    @JsonProperty("status")
    public String status;
    @JsonProperty("success")
    public  boolean succcess;
    @JsonProperty("message")
    public String message;
    @JsonProperty("info")
    public UserInfo info=null;
    @JsonProperty("filters")
    public Object filters;
    @JsonProperty("otp_enabled")
    public Boolean otpEnabled;
    @JsonProperty("callback_url")
    public String callbackUrl;
    @JsonProperty("permissions")
    public Permission permissions;

    public AuthResponseData(){

    }


}
