package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CustomerQosPolicyMapping",description = "This is data transfer object for Customer QosPolicyMapping which is used to add new Customer Qos Policy Mapping")
public class CustomerQosPolicyMappingDto {

	@ApiModelProperty(notes = "This is customer Id")
    private Long custId;
	
	@ApiModelProperty(notes = "This is Upload Qos")
    private Long uploadQos;
	
	@ApiModelProperty(notes = "This is Download Qos")
    private Long downloadQos;
	
	@ApiModelProperty(notes = "This is Qos From")
    private Long qosFrom;
	
	@ApiModelProperty(notes = "This is Qos To")
    private Long qosTo;


	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public Long getUploadQos() {
		return uploadQos;
	}

	public void setUploadQos(Long uploadQos) {
		this.uploadQos = uploadQos;
	}

	public Long getDownloadQos() {
		return downloadQos;
	}

	public void setDownloadQos(Long downloadQos) {
		this.downloadQos = downloadQos;
	}

	public Long getQosFrom() {
		return qosFrom;
	}

	public void setQosFrom(Long qosFrom) {
		this.qosFrom = qosFrom;
	}

	public Long getQosTo() {
		return qosTo;
	}

	public void setQosTo(Long qosTo) {
		this.qosTo = qosTo;
	}


}
