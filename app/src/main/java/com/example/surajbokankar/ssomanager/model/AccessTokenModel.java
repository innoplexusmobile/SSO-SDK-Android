package com.example.surajbokankar.ssomanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;

/**
 * Created by suraj.bokankar on 14/3/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"accessToken","refreshToken","usePermissions"})

public class AccessTokenModel {
    @JsonProperty("accessToken")
    public String accessToken;
    @JsonProperty("refreshToken")
    public String refreshToken;
    @JsonProperty("usePermissions")
    public ArrayList<String> userPermissions=null;

    public AccessTokenModel(){

    }
}
