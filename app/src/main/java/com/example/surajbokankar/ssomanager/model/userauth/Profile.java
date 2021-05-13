
package com.example.surajbokankar.ssomanager.model.userauth;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "created_by",
    "created_on",
    "deactivate_reason",
    "delete_reason",
    "deleted_by",
    "deleted_on",
    "department_id",
    "department_name",
    "designation_id",
    "designation_name",
    "email_signature",
    "first_name",
    "id",
    "image_url",
    "last_login_date",
    "last_name",
    "login_count",
    "organization",
    "organization_id",
    "organization_name",
    "profile_url",
    "status",
    "updated_by",
    "updated_on",
    "groupId",
    "group",
    "isIndividualUser",
    "isGroupAdmin",
    "isOrgAdmin"
})
public class Profile {

    @JsonProperty("created_by")
    private Object createdBy;
    @JsonProperty("created_on")
    private String createdOn;
    @JsonProperty("deactivate_reason")
    private Object deactivateReason;
    @JsonProperty("delete_reason")
    private Object deleteReason;
    @JsonProperty("deleted_by")
    private Object deletedBy;
    @JsonProperty("deleted_on")
    private String deletedOn;
    @JsonProperty("department_id")
    private Object departmentId;
    @JsonProperty("department_name")
    private Object departmentName;
    @JsonProperty("designation_id")
    private Object designationId;
    @JsonProperty("designation_name")
    private Object designationName;
    @JsonProperty("email_signature")
    private Object emailSignature;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("id")
    private String id;
    @JsonProperty("image_url")
    private Object imageUrl;
    @JsonProperty("last_login_date")
    private String lastLoginDate;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("login_count")
    private Integer loginCount;
    @JsonProperty("organization")
    private String organization;
    @JsonProperty("organization_id")
    private String organizationId;
    @JsonProperty("organization_name")
    private String organizationName;
    @JsonProperty("profile_url")
    private Object profileUrl;
    @JsonProperty("status")
    private String status;
    @JsonProperty("updated_by")
    private Object updatedBy;
    @JsonProperty("updated_on")
    private String updatedOn;
    @JsonProperty("groupId")
    private String groupId;
    @JsonProperty("group")
    private String group;
    @JsonProperty("isIndividualUser")
    private Boolean isIndividualUser;
    @JsonProperty("isGroupAdmin")
    private Boolean isGroupAdmin;
    @JsonProperty("isOrgAdmin")
    private Boolean isOrgAdmin;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("created_by")
    public Object getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("created_by")
    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("created_on")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("created_on")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @JsonProperty("deactivate_reason")
    public Object getDeactivateReason() {
        return deactivateReason;
    }

    @JsonProperty("deactivate_reason")
    public void setDeactivateReason(Object deactivateReason) {
        this.deactivateReason = deactivateReason;
    }

    @JsonProperty("delete_reason")
    public Object getDeleteReason() {
        return deleteReason;
    }

    @JsonProperty("delete_reason")
    public void setDeleteReason(Object deleteReason) {
        this.deleteReason = deleteReason;
    }

    @JsonProperty("deleted_by")
    public Object getDeletedBy() {
        return deletedBy;
    }

    @JsonProperty("deleted_by")
    public void setDeletedBy(Object deletedBy) {
        this.deletedBy = deletedBy;
    }

    @JsonProperty("deleted_on")
    public String getDeletedOn() {
        return deletedOn;
    }

    @JsonProperty("deleted_on")
    public void setDeletedOn(String deletedOn) {
        this.deletedOn = deletedOn;
    }

    @JsonProperty("department_id")
    public Object getDepartmentId() {
        return departmentId;
    }

    @JsonProperty("department_id")
    public void setDepartmentId(Object departmentId) {
        this.departmentId = departmentId;
    }

    @JsonProperty("department_name")
    public Object getDepartmentName() {
        return departmentName;
    }

    @JsonProperty("department_name")
    public void setDepartmentName(Object departmentName) {
        this.departmentName = departmentName;
    }

    @JsonProperty("designation_id")
    public Object getDesignationId() {
        return designationId;
    }

    @JsonProperty("designation_id")
    public void setDesignationId(Object designationId) {
        this.designationId = designationId;
    }

    @JsonProperty("designation_name")
    public Object getDesignationName() {
        return designationName;
    }

    @JsonProperty("designation_name")
    public void setDesignationName(Object designationName) {
        this.designationName = designationName;
    }

    @JsonProperty("email_signature")
    public Object getEmailSignature() {
        return emailSignature;
    }

    @JsonProperty("email_signature")
    public void setEmailSignature(Object emailSignature) {
        this.emailSignature = emailSignature;
    }

    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("image_url")
    public Object getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("image_url")
    public void setImageUrl(Object imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("last_login_date")
    public String getLastLoginDate() {
        return lastLoginDate;
    }

    @JsonProperty("last_login_date")
    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("login_count")
    public Integer getLoginCount() {
        return loginCount;
    }

    @JsonProperty("login_count")
    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    @JsonProperty("organization")
    public String getOrganization() {
        return organization;
    }

    @JsonProperty("organization")
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @JsonProperty("organization_id")
    public String getOrganizationId() {
        return organizationId;
    }

    @JsonProperty("organization_id")
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("organization_name")
    public String getOrganizationName() {
        return organizationName;
    }

    @JsonProperty("organization_name")
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    @JsonProperty("profile_url")
    public Object getProfileUrl() {
        return profileUrl;
    }

    @JsonProperty("profile_url")
    public void setProfileUrl(Object profileUrl) {
        this.profileUrl = profileUrl;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("updated_by")
    public Object getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updated_by")
    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonProperty("updated_on")
    public String getUpdatedOn() {
        return updatedOn;
    }

    @JsonProperty("updated_on")
    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    @JsonProperty("groupId")
    public String getGroupId() {
        return groupId;
    }

    @JsonProperty("groupId")
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("group")
    public String getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(String group) {
        this.group = group;
    }

    @JsonProperty("isIndividualUser")
    public Boolean getIsIndividualUser() {
        return isIndividualUser;
    }

    @JsonProperty("isIndividualUser")
    public void setIsIndividualUser(Boolean isIndividualUser) {
        this.isIndividualUser = isIndividualUser;
    }

    @JsonProperty("isGroupAdmin")
    public Boolean getIsGroupAdmin() {
        return isGroupAdmin;
    }

    @JsonProperty("isGroupAdmin")
    public void setIsGroupAdmin(Boolean isGroupAdmin) {
        this.isGroupAdmin = isGroupAdmin;
    }

    @JsonProperty("isOrgAdmin")
    public Boolean getIsOrgAdmin() {
        return isOrgAdmin;
    }

    @JsonProperty("isOrgAdmin")
    public void setIsOrgAdmin(Boolean isOrgAdmin) {
        this.isOrgAdmin = isOrgAdmin;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
