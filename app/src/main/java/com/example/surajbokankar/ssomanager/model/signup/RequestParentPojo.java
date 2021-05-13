package com.example.surajbokankar.ssomanager.model.signup;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        "responseData","errorMessage","errorCode"

})
public class RequestParentPojo {

    @JsonProperty("responseData")
    public AuthResponseData authResponseData;
    @JsonProperty("errorMessage")
    public String errorMessage;
    @JsonProperty("errorCode")
    public int errorCode;
}
