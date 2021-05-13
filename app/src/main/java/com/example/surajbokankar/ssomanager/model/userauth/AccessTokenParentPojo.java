package com.example.surajbokankar.ssomanager.model.userauth;

import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;

/**
 * Created by suraj.bokankar on 15/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "_key",
        "_id",
        "_rev",
        "accessToken",
        "createdOn",
        "expires_in",
        "expiryDate",
        "filters",
        "is_tour_period",
        "permissions",
        "plan",
        "profile",
        "refreshToken",
        "roles","groups"
})
public class AccessTokenParentPojo {
    @JsonProperty("_key")
    public String key;
    @JsonProperty("_id")
    public String id;
    @JsonProperty("_rev")
    public String rev;
    @JsonProperty("accessToken")
    public String accessToken=null;
    @JsonProperty("createdOn")
    public String createdOn;
    @JsonProperty("expires_in")
    public Integer expiresIn;
    @JsonProperty("expiryDate")
    public String expiryDate;
    @JsonProperty("filters")
    public Filters filters;
    @JsonProperty("is_tour_period")
    public Boolean isTourPeriod;
    @JsonProperty("permissions")
    public ArrayList<String> permissions = null;
    @JsonProperty("plan")
    public Plan plan;
    @JsonProperty("profile")
    public UserInfo profile;
    @JsonProperty("refreshToken")
    public String refreshToken=null;
    @JsonProperty("roles")
    public ArrayList<Role> roleList= null;
    @JsonProperty("groups")
    public ArrayList<Groups> groupsArrayList=null;


    @Override
    public String toString() {
        return key +"\t"+id+"\t"+rev+"\t"+createdOn+"\n"+expiresIn+"\t"+expiryDate+"\n"+filters+"\t"+isTourPeriod+"\t"+accessToken;
    }
}
