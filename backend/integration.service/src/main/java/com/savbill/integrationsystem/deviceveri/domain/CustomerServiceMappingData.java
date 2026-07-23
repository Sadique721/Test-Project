package com.savbill.integrationsystem.deviceveri.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.integrationsystem.core.data.IBaseData;

import lombok.Data;

@Data
@Entity
@Table(name = "tbltcustomerservicemapping")
public class CustomerServiceMappingData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 40)
    private Long id;
	
	@Column(name = "custid")
    private Long custid;
	
	@Column(name = "serviceid")
    private Long serviceid;
	
	@Column(name = "leasecircuitid")
    private Long leasecircuitid;
	
	@Column(name = "purchaseorder_id")
    private Long purchaseorderId;
	
	@Column(name = "connection_no")
    private String connectionNo;
	
	@Column(name = "invoice_format")
    private String invoiceFormat;
	
	/*
	@Column(name = "CREATEDATE")
    private LocalDateTime createDate;
	
	@Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime lastModifiedDate;
	
	@Column(name = "createbyname")
    private String createbyname;
	
	@Column(name = "updatebyname")
    private String updatebyname;
	
	@Column(name = "CREATEDBYSTAFFID")
    private Long createdStaffId;
	
	@Column(name = "LASTMODIFIEDBYSTAFFID")
    private Long LastModifiedStaffId;
	*/
	
	@Column(name = "is_deleted")
    private Integer isDeleted;

	@Column(name = "invoice_type")
    private String invoice_type;
	
	@Column(name = "lease_circuit_name")
    private String leaseCircuitName;
	
	@Column(name = "circuit_status")
    private String circuitStatus;

	@Column(name = "caf_no")
    private Long cafNo;
	
	@Column(name = "upload_caf")
    private String uploadCaf;
	
	@Column(name = "customer_name")
    private String customerName;
	
	@Column(name = "account_number")
    private Long accountNumber;

	@Column(name = "type_of_link")
    private String typeOfLink;

	@Column(name = "link_installation_date")
    private LocalDateTime linkInstallationDate;

	@Column(name = "link_acceptance_date")
    private LocalDateTime linkAcceptanceDate;

	@Column(name = "purchase_order_date")
    private LocalDateTime purchaseOrderDate;
	
	@Column(name = "partner_id")
    private Long partnerId;

	@Column(name = "expiry_date")
    private LocalDateTime expiryDate;

	@Column(name = "distance")
    private Long distance;

	@Column(name = "distance_unit")
    private String distancUnit;

	@Column(name = "bandwidth")
    private Long bandwidth;

	@Column(name = "uploadQOS")
    private String uploadQOS;
	
	@Column(name = "downloadQOS")
    private String downloadQOS;

	@Column(name = "link_router_location")
    private String linkRouterLocation;

	@Column(name = "link_port_type")
    private String linkPortType;

	@Column(name = "vlan_id")
    private Long planId;

	@Column(name = "bandwidth_type")
    private String newbandwidthType;

	@Column(name = "link_router_name")
    private String linkRouterName;

	@Column(name = "circuit_billing_id")
    private Long circuitBillingId;

	@Column(name = "pop")
    private String pop;

	@Column(name = "termination_address")
    private String terminationAddress;
	
	@Column(name = "note")
    private String note;

	@Column(name = "contact_person")
    private String contactPerson;
	
	@Column(name = "mobile_number")
    private String mobileNumber;

	@Column(name = "landline_number")
    private String landlineNumber;

	@Column(name = "email_id")
    private String emailId;

	@Column(name = "billing_cycle")
    private String billing_Cycle;

	@Column(name = "billing_type")
    private String billingType;

	@Column(name = "billable")
    private String billable;

	@Column(name = "billing_group")
    private String billingGroup;

	@Column(name = "payable")
    private String payable;

	@Column(name = "enable_processing")
    private String enableProcessing;

	@Column(name = "deposite")
    private String deposite;

	@Column(name = "po_number")
    private String poNumber;

	@Column(name = "full_name")
    private String fullName;

	@Column(name = "organisation")
    private String organisation;

	@Column(name = "address1")
    private String address1;

	@Column(name = "address2")
    private String address2;

	@Column(name = "city")
    private String city;

	@Column(name = "zipcode")
    private String zipcode;

	@Column(name = "state")
    private String state;

	@Column(name = "country")
    private String country;

	@Column(name = "status")
    private String status;

	@Column(name = "s_discount_type")
    private String sDiscountType;

	@Column(name = "service_area_type")
    private String serviceAreaType;

	@Column(name = "s_discount")
    private Double sDiscount;
	
	@Column(name = "billable_cust_id")
    private Long billable_cust_id;

	@Column(name = "is_delete")
    private Integer isDelete;

	@Column(name = "MVNOID")
    private Long mvnoid;

	@Column(name = "buid")
    private Long buid;

	@Column(name = "discount_expiry_date")
    private LocalDateTime discountExpiryDate;

	@Column(name = "new_discount_expiry_date")
    private LocalDateTime newDiscountExpiryDate;

	@Column(name = "new_discount")
    private Double newDiscount;

	@Column(name = "new_discount_type")
    private String newDiscountType;

	@Column(name = "remarks")
    private String remarks;

	@Column(name = "next_team_hir_mapping")
    private Long nextTeamHirMapping;

	@Column(name = "next_staff")
    private Long nextStaff;

	@Column(name = "branch")
    private String branch;

	@Column(name = "connection_type")
    private String connectionType;

	@Column(name = "custservicemappingid")
    private Long custservicemappingid;

	@Override
	public Long getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDeleteFlag() {
		// TODO Auto-generated method stub
		return false;
	}
}
