package com.savbill.integrationsystem.deviceveri.model;

import java.time.LocalDateTime;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class CustomerServiceMappingDTO extends Auditable<Long> implements IBaseDto{
    private Long id;
    private Long custid;
    private Long serviceid;
    private Long leasecircuitid;
    private Long purchaseorderId;
    private String connectionNo;
    private String invoiceFormat;
    private Integer isDeleted;
    private String invoice_type;
    private String leaseCircuitName;
    private String circuitStatus;
    private Long cafNo;
    private String uploadCaf;
    private String customerName;
    private Long accountNumber;
    private String typeOfLink;
    private LocalDateTime linkInstallationDate;
    private LocalDateTime linkAcceptanceDate;
    private LocalDateTime purchaseOrderDate;
    private Long partnerId;
    private LocalDateTime expiryDate;
    private Long distance;
    private String distancUnit;
    private Long bandwidth;
    private String uploadQOS;
    private String downloadQOS;
    private String linkRouterLocation;
    private String linkPortType;
    private Long planId;
    private String newbandwidthType;
    private String linkRouterName;
    private Long circuitBillingId;
    private String pop;
    private String terminationAddress;
    private String note;
    private String contactPerson;
    private String mobileNumber;
    private String landlineNumber;
    private String emailId;
    private String billing_Cycle;
    private String billingType;
    private String billable;
    private String billingGroup;
    private String payable;
    private String enableProcessing;
    private String deposite;
    private String poNumber;
    private String fullName;
    private String organisation;
    private String address1;
    private String address2;
    private String city;
    private String zipcode;
    private String state;
    private String country;
    private String status;
    private String sDiscountType;
    private String serviceAreaType;
    private Double sDiscount;
    private Long billable_cust_id;
    private Integer isDelete;
    private Long mvnoid;
    private Long buid;
    private LocalDateTime discountExpiryDate;
    private LocalDateTime newDiscountExpiryDate;
    private Double newDiscount;
    private String newDiscountType;
    private String remarks;
    private Long nextTeamHirMapping;
    private Long nextStaff;
    private String branch;
    private String connectionType;
    private Long custservicemappingid;
	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Long getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMvnoId(Long mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
