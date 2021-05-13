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
@JsonPropertyOrder({
        "total",
        "permissions","unique_code","permission_type","permission"
})
public class Permission {
    @JsonProperty("total")
    public Integer total;
    @JsonProperty("permissions")
    public ArrayList<String> permissions = null;
    @JsonProperty("permission")
    public String permissionName;
    @JsonProperty("unique_code")
    public String unique_code;
    @JsonProperty("permission_type")
    public String permission_type;
}
