package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Dictionary",description = "This is data transfer object for Dictionary which is used to create new dictionary")
public class DictionaryDto 
{
	@ApiModelProperty(notes = "Name of the vendor",required=true)
	private String vendor;
	@ApiModelProperty(notes = "Id of the vendor",required=true)
	private String vendorId;
	@ApiModelProperty(notes = "Type of the vendor",allowableValues = "VENDOR,STANDARD",  value = "This field accept value only : VENDOR or STANDARD",required = true)
	private VendorType vendorType;
	
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public VendorType getVendorType() {
		return vendorType;
	}
	public void setVendorType(VendorType vendorType) {
		this.vendorType = vendorType;
	}
}
