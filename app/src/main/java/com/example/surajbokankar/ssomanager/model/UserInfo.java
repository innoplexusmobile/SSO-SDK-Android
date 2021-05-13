package com.example.surajbokankar.ssomanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;

/**
 * Created by suraj.bokankar on 9/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id","organization_id","organization","first_name","last_name","email","image_url","status","callback_url"
,"created_on","deleted_on","updated_on","updated_by","created_by","deleted_by","delete_reason","deactivate_reason","last_login_date","login_count"})
public class UserInfo {
    @JsonProperty("id")
    public String id;
    @JsonProperty("organization_id")
    public String organization_id;
    @JsonProperty("organization")
    public String organization;
    @JsonProperty("first_name")
    public String first_name;
    @JsonProperty("last_name")
    public String last_name;
    @JsonProperty("email")
    public String email;
    @JsonProperty("image_url")
    public String image_url;
    @JsonProperty("status")
    public String status;
    @JsonProperty("callback_url")
    public String callback_url;
    @JsonProperty("created_on")
    public String created_on;
    @JsonProperty("deleted_on")
    public String deleted_on;
    @JsonProperty("updated_on")
    public String updated_on;
    @JsonProperty("updated_by")
    public String updated_by;
    @JsonProperty("created_by")
    public String created_by;
    @JsonProperty("deleted_by")
    public String deleted_by;
    @JsonProperty("delete_reason")
    public String delete_reason;
    @JsonProperty("deactivate_reason")
    public String deactivate_reason;
    @JsonProperty("last_login_date")
    public String last_login_date;
    @JsonProperty("login_count")
    public String login_count;

    public String passWord;

    public  boolean isOtpEnabled=false;

}
