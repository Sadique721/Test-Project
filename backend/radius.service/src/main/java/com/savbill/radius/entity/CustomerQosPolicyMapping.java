package com.savbill.radius.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.radius.helper.CustomerQosPolicyMappingDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBLTCUSTOMERQOSPOLICYMAPPING")
@NoArgsConstructor
public class CustomerQosPolicyMapping {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated Qos Policy Mapping Id")
    @Column (name="qospolicymappingid", nullable = false)
	private Long qosPolicyMappingId;
	
	@ApiModelProperty(notes = "The is Customer Id")
    @Column (name="custid", nullable = false)
	private Long custId;

	@ApiModelProperty(notes = "This is Upload Qos")
    @Column (name="uploadqos", nullable = false)
    private Long uploadQos;
	
	@ApiModelProperty(notes = "This is Download Qos")
    @Column (name="downloadqos", nullable = false)
    private Long downloadQos;
	
	@ApiModelProperty(notes = "This is Qos From")
    @Column (name="qosfrom", nullable = false)
    private Long qosFrom;
	
	@ApiModelProperty(notes = "This is Qos To")
    @Column (name="qosto", nullable = false)
    private Long qosTo;
	
	@ApiModelProperty(notes = "This is mvno id")
    @Column (name="mvnoid", nullable = false)
    private Integer mvnoId;
	
	public Long getQosPolicyMappingId() {
		return qosPolicyMappingId;
	}
	public void setQosPolicyMappingId(Long qosPolicyMappingId) {
		this.qosPolicyMappingId = qosPolicyMappingId;
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
	public Integer getMvnoId() {
		return mvnoId;
	}
	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}
	
	public Long getCustId() {
		return custId;
	}
	public void setCustId(Long custId) {
		this.custId = custId;
	}
	
	public CustomerQosPolicyMapping(CustomerQosPolicyMappingDto customerQosPolicyMappingDto, Integer mvnoId)
	{
		this.custId = customerQosPolicyMappingDto.getCustId();
		this.downloadQos = customerQosPolicyMappingDto.getDownloadQos();
		this.uploadQos = customerQosPolicyMappingDto.getUploadQos();
		this.qosFrom = customerQosPolicyMappingDto.getQosFrom();
		this.qosTo = customerQosPolicyMappingDto.getQosTo();
		this.mvnoId = mvnoId;
	}

}
