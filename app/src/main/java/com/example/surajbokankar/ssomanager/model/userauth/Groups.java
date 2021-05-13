package com.example.surajbokankar.ssomanager.model.userauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;

/**
 * Created by suraj.bokankar on 22/9/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "filters",
        "name"
})
public class Groups {
    @JsonProperty("id")
    public String id;
    @JsonProperty("filters")
    public ArrayList<Filters> filtersArrayList=null;
    @JsonProperty("name")
    public String name;
    public Groups(){

    }
}
