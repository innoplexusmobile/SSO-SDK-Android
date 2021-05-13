package com.example.surajbokankar.ssomanager.model.userauth;

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
        "values","name"
})
public class Filters {
    @JsonProperty("values")
    public ArrayList<String> valuesList=null;
    @JsonProperty("name")
    public String name=null;

    public Filters(){}

}
