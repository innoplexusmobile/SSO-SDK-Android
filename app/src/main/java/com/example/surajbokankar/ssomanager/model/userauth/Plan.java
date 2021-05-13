package com.example.surajbokankar.ssomanager.model.userauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by suraj.bokankar on 15/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "is_trial_period",
        "trial_period_duration",
        "description",
        "end_date",
        "order_id",
        "cost",
        "total_users",
        "instant_billing_mandatory",
        "duration",
        "is_recurring",
        "is_recursive",
        "plan_id",
        "name",
        "amount",
        "start_date",
        "duration_unit",
        "trial_period_duration_unit",
        "skip_billing",
        "billing_type",
        "is_extend_trial_pending",
        "allow_trial_period_extention",
        "is_admin"
})
public class Plan {


    @JsonProperty("is_trial_period")
    public Boolean isTrialPeriod;
    @JsonProperty("trial_period_duration")
    public Integer trialPeriodDuration;
    @JsonProperty("description")
    public String description;
    @JsonProperty("end_date")
    public String endDate;
    @JsonProperty("order_id")
    public Object orderId;
    @JsonProperty("cost")
    public Integer cost;
    @JsonProperty("total_users")
    public Integer totalUsers;
    @JsonProperty("instant_billing_mandatory")
    public Boolean instantBillingMandatory;
    @JsonProperty("duration")
    public Integer duration;
    @JsonProperty("is_recurring")
    public Boolean isRecurring;
    @JsonProperty("is_recursive")
    public Boolean isRecursive;
    @JsonProperty("plan_id")
    public String planId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("amount")
    public Integer amount;
    @JsonProperty("start_date")
    public String startDate;
    @JsonProperty("duration_unit")
    public String durationUnit;
    @JsonProperty("trial_period_duration_unit")
    public String trialPeriodDurationUnit;
    @JsonProperty("skip_billing")
    public Boolean skipBilling;
    @JsonProperty("billing_type")
    public String billingType;
    @JsonProperty("is_extend_trial_pending")
    public Boolean isExtendTrialPending;
    @JsonProperty("allow_trial_period_extention")
    public Boolean allowTrialPeriodExtention;
    @JsonProperty("is_admin")
    public Boolean isAdmin;
}
