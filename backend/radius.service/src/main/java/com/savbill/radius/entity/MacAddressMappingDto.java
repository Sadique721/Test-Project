package com.savbill.radius.entity;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

public class MacAddressMappingDto {
	@ApiModelProperty(notes = "This is user name of customer Id")
    private Long customerId;
	
	@ApiModelProperty(notes = "This is user name of customer Mac Address")
    private String macAddress;


	private Timestamp macRetentionDate;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Timestamp getMacRetentionDate() {
		return macRetentionDate;
	}

	public void setMacRetentionDate(Timestamp macRetentionDate) {
		this.macRetentionDate = macRetentionDate;
	}
}
