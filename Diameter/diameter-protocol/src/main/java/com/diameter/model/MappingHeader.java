package com.diameter.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MappingHeader {
	private String id;
	
	@NotBlank(message = "requestType is mandatory")
    private String requestType;
	
	@NotBlank(message = "responseType is mandatory")
    private String responseType;
	
	@NotBlank(message = "application is mandatory")
	private String application;
	
	@NotNull(message = "vendorId is mandatory")
	private int vendorId = 0;
	
    private boolean enabled;
    private String description;

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime modifiedDate;
    private String modifiedBy;
    private boolean includeStandardAvp;

    private List<MappingDetail> details;
    
    private List<AvpCondition> avpConditions;

	/**
	 * Optional CC request type
	 * INITIAL / UPDATE / TERMINATE
	 */
	private String ccRequestType;

	/**
	 * Optional peer list
	 */
	private List<String> peer;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public boolean isIncludeStandardAvp() {
		return includeStandardAvp;
	}

	public void setIncludeStandardAvp(boolean includeStandardAvp) {
		this.includeStandardAvp = includeStandardAvp;
	}

	public List<MappingDetail> getDetails() {
		return details;
	}

	public void setDetails(List<MappingDetail> details) {
		this.details = details;
	}

	public String getCcRequestType() {
		return ccRequestType;
	}

	public void setCcRequestType(String ccRequestType) {
		this.ccRequestType = ccRequestType;
	}

	public List<String> getPeer() {
		return peer;
	}

	public void setPeer(List<String> peer) {
		this.peer = peer;
	}
	
	public List<AvpCondition> getAvpConditions() {
		return avpConditions;
	}

	public void setAvpConditions(List<AvpCondition> avpConditions) {
		this.avpConditions = avpConditions;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingHeader [id=");
		builder.append(id);
		builder.append(", requestType=");
		builder.append(requestType);
		builder.append(", responseType=");
		builder.append(responseType);
		builder.append(", application=");
		builder.append(application);
		builder.append(", vendorId=");
		builder.append(vendorId);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", description=");
		builder.append(description);
		builder.append(", createdDate=");
		builder.append(createdDate);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", modifiedDate=");
		builder.append(modifiedDate);
		builder.append(", modifiedBy=");
		builder.append(modifiedBy);
		builder.append(", includeStandardAvp=");
		builder.append(includeStandardAvp);
		builder.append(", details=");
		builder.append(details);
		builder.append(", avpConditions=");
		builder.append(avpConditions);
		builder.append(", ccRequestType=");
		builder.append(ccRequestType);
		builder.append(", peer=");
		builder.append(peer);
		builder.append("]");
		return builder.toString();
	}
}