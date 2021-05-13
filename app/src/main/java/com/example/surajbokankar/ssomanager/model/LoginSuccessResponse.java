package com.example.surajbokankar.ssomanager.model;

import com.example.surajbokankar.ssomanager.model.userauth.LoginData;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suraj.bokankar on 16/10/18.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "isError",
        "data",
        "message"
})

public class LoginSuccessResponse {

    @JsonProperty("isError")
    private Boolean isError;
    @JsonProperty("data")
    private LoginData data;
    @JsonProperty("message")
    private String message;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("isError")
    public Boolean getIsError() {
        return isError;
    }

    @JsonProperty("isError")
    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    @JsonProperty("data")
    public LoginData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(LoginData data) {
        this.data = data;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public LoginSuccessResponse(){

    }
}
