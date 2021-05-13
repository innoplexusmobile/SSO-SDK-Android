package com.example.surajbokankar.ssomanager.model.userauth;

import com.example.surajbokankar.ssomanager.model.Permission;
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
        "id",
        "is_editable",
        "name",
        "role_type","permissions"
})
public class Role {
    @JsonProperty("id")
    public String id;
    @JsonProperty("is_editable")
    public Boolean isEditable;
    @JsonProperty("name")
    public String name;
    @JsonProperty("role_type")
    public String roleType;
    @JsonProperty("permissions")
    public ArrayList<Permission> permission=new ArrayList<>();

    public Role(){

    }

}
