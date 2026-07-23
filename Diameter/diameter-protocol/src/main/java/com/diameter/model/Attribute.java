package com.diameter.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblm_attribute")
public class Attribute {
	@Id
    private String id;
    private int attributeId;
    private String name;
    private String mandatory;
    private String protectedFlag;
    private String encryption;
    private String type;
    private String status;
    private String dictionaryType;
    private Integer minimum;
    private Integer maximum;
    private Integer attributeVendorId;
    private String parentAttributeId;
    private String vendorId;
    private LocalDateTime  createdDate;
    private String createdBy;
    private LocalDateTime  modifiedDate;
    private String modifiedBy;
    private String regex;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMandatory() {
		return mandatory;
	}
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}
	public String getProtectedFlag() {
		return protectedFlag;
	}
	public void setProtectedFlag(String protectedFlag) {
		this.protectedFlag = protectedFlag;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDictionaryType() {
		return dictionaryType;
	}
	public void setDictionaryType(String dictionaryType) {
		this.dictionaryType = dictionaryType;
	}
	public Integer getMinimum() {
		return minimum;
	}
	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
	}
	public Integer getMaximum() {
		return maximum;
	}
	public void setMaximum(Integer maximum) {
		this.maximum = maximum;
	}
	public Integer getAttributeVendorId() {
		return attributeVendorId;
	}
	public void setAttributeVendorId(Integer attributeVendorId) {
		this.attributeVendorId = attributeVendorId;
	}
	public String getParentAttributeId() {
		return parentAttributeId;
	}
	public void setParentAttributeId(String parentAttributeId) {
		this.parentAttributeId = parentAttributeId;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
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
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	@Override
	public String toString() {
		return "Attribute [id=" + id + ", attributeId=" + attributeId + ", name=" + name + ", mandatory=" + mandatory
				+ ", protectedFlag=" + protectedFlag + ", encryption=" + encryption + ", type=" + type + ", status="
				+ status + ", dictionaryType=" + dictionaryType + ", minimum=" + minimum + ", maximum=" + maximum
				+ ", attributeVendorId=" + attributeVendorId + ", parentAttributeId=" + parentAttributeId
				+ ", vendorId=" + vendorId + ", createdDate=" + createdDate + ", createdBy=" + createdBy
				+ ", modifiedDate=" + modifiedDate + ", modifiedBy=" + modifiedBy + ", regex=" + regex
				+ "]";
	}
}

