package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CustomerAddressPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMSUBSCRIBERADDRESSREL")
public class CustomerAddress {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESSID", nullable = false, length = 40)
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
    
    @JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
    
    @Column(name = "street_name")
	private String streetName;
	
	@Column(name = "house_no")
	private String houseNo;
    
    public CustomerAddress(CustomerAddressPojo customerAddressPojo) {
		this.id = customerAddressPojo.getId();
		this.addressType = customerAddressPojo.getAddressType();
		this.address1 = customerAddressPojo.getAddress1();
		this.address2 = customerAddressPojo.getAddress2();
		this.landmark = customerAddressPojo.getLandmark();
		this.areaId = customerAddressPojo.getAreaId();
		this.pincodeId = customerAddressPojo.getPincodeId();
		this.cityId = customerAddressPojo.getCityId();
		this.stateId = customerAddressPojo.getStateId();
		this.countryId = customerAddressPojo.getCountryId();
		this.customerId = customerAddressPojo.getCustomerId();
		this.fullAddress = customerAddressPojo.getFullAddress();
		this.isDelete = customerAddressPojo.getIsDelete();
		if(customerAddressPojo.getLeadMasterId() != null) {
			this.leadMaster = new LeadMaster(customerAddressPojo.getLeadMasterId());
		}
		this.streetName = customerAddressPojo.getStreetName();
		this.houseNo = customerAddressPojo.getHouseNo();
	}
}
