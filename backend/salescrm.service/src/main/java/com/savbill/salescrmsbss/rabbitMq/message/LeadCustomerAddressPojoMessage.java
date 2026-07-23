package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.CustomerAddressPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustomerAddressPojoMessage {

	private Integer id;

    private String addressType;

    private String address1;

    private String address2;

    private String landmark;

    private Integer areaId;
    
    private Integer pincodeId;

    private Integer cityId;

    private Integer stateId;

    private Integer countryId;

    private Integer customerId;

    private String fullAddress;

    private Boolean isDelete;
    
	private Long leadMasterId;
	
	private String streetName;

	private String houseNo;
	
	public LeadCustomerAddressPojoMessage(CustomerAddressPojo customerAddress) {
		this.id = customerAddress.getId();
		this.addressType = customerAddress.getAddressType();
		this.address1 = customerAddress.getAddress1();
		this.address2 = customerAddress.getAddress2();
		this.landmark = customerAddress.getLandmark();
		this.areaId = customerAddress.getAreaId();
		this.pincodeId = customerAddress.getPincodeId();
		this.cityId = customerAddress.getCityId();
		this.stateId = customerAddress.getStateId();
		this.countryId = customerAddress.getCountryId();
		this.customerId = customerAddress.getCustomerId();
		this.fullAddress = customerAddress.getFullAddress();
		this.isDelete = customerAddress.getIsDelete();
		this.leadMasterId = customerAddress.getLeadMasterId();
		this.streetName = customerAddress.getStreetName();
		this.houseNo = customerAddress.getHouseNo();
	}
}
