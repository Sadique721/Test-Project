package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.LinkAcceptanceDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Entity
@Data
@Table(name = "tbltleadservicemapping")
public class LeadServiceMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, length = 40)
	private Long id;

	@Column(name = "leadid")
	private Long leadId;

	@Column(name = "serviceid")
	private Long serviceId;

	@Column(name = "planid")
	private Long planId;

	@Column(name = "plan_name")
	private String planName;

	@Column(name = "service_name")
	private String serviceName;

	@Column(name = "connection_no")
	private String connectionNo;

	@Column(name = "nickname")
	private String nickName;

	@Transient
	private LocalDate stopServiceDate;

	@Column(name = "is_deleted", columnDefinition = "Boolean default false")
	private Boolean isDeleted = false;

	@Column(name = "purchaseorder")
	private String purchaseorder;

	@Column(name = "invoice_format")
	private String invoiceformat;

	@Column(name = "invoice_type")
	private String invoiceType;

	@Column(name = "lease_circuit_name")
	private String leaseCircuitName;
	@Column(name = "circuit_status")
	private String circuitStatus;
	@Column(name = "caf_no")
	private Long cafNo;
	@Column(name = "upload_caf")
	private String uploadCAF;
	@Column(name = "customer_name")
	private String customerName;
	@Column(name = "account_number")
	private String accountNumber;
	@Column(name = "type_of_link")
	private String typeOfLink;
	@Column(name = "link_installation_date")
	private LocalDate linkInstallationDate;
	@Column(name = "link_acceptance_date")
	private LocalDate linkAcceptanceDate;
	@Column(name = "purchase_order_date")
	private LocalDate purchaseOrderDate;
	@Column(name = "circle_name")
	private String circleName;
	@Column(name = "order_login_date")
	private LocalDate orderLoginDate;
	@Column(name = "partner_id")
	private Long partner;
	@Column(name = "expiry_date")
	private LocalDate expiryDate;
	@Column(name = "distance")
	private Long distance;
	@Column(name = "distance_unit")
	private String distanceUnit;
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
	@Column(name = "link_router_ip")
	private String linkRouterIp;
	@Column(name = "link_port_on_router")
	private String linkPortOnRouter;
	@Column(name = "vlan_id")
	private String vLANId;
	@Column(name = "bandwidth_type")
	private String bandwidthType;
	@Column(name = "link_router_name")
	private String linkRouterName;
	@Column(name = "circuit_billing_id")
	private String circuitBillingId;
	@Column(name = "pop")
	private String pop;
	@Column(name = "associated_level")
	private String associatedLevel;
	@Column(name = "location_level1")
	private String locationLevel1;
	@Column(name = "location_level2")
	private String locationLevel2;
	@Column(name = "location_level3")
	private String locationLevel3;
	@Column(name = "location_level4")
	private String locationLevel4;
	@Column(name = "base_station_id1")
	private String baseStationId1;
	@Column(name = "base_station_id2")
	private String baseStationId2;
	@Column(name = "organisation_circle")
	private String organisationCircle;
	@Column(name = "termination_circle")
	private String terminationCircle;
	@Column(name = "organisation_address")
	private String organisationAddress;
	@Column(name = "termination_address")
	private String terminationAddress;
	@Column(name = "organisation_address2")
	private String organisationAddress2;
	@Column(name = "termination_address2")
	private String terminationAddress2;
	@Column(name = "note")
	private String note;
	@Column(name = "contact_person")
	private String contactPerson;
	@Column(name = "contact_person1")
	private String contactPerson1;
	@Column(name = "contact_person2")
	private String contactPerson2;
	@Column(name = "mobile_number")
	private String mobileNumber;
	@Column(name = "mobile_number1")
	private String mobileNumber1;
	@Column(name = "mobile_number2")
	private String mobileNumber2;
	@Column(name = "landline_number")
	private String landlineNumber;
	@Column(name = "landline_number1")
	private String landlineNumber1;
	@Column(name = "landline_number2")
	private String landlineNumber2;
	@Column(name = "email_id")
	private String emailId;
	@Column(name = "email_id1")
	private String emailId1;
	@Column(name = "email_id2")
	private String emailId2;
//    @Column(name = "lease_circuit_remarks")
//    private String leaseCircuitRemarks;
	@Column(name = "trai_rate")
	private Long traiRate;
	@Column(name = "otc_charges_file")
	private String otcChargesFile;
	@Column(name = "service_charger_file")
	private String serviceChargerFile;
	@Column(name = "static_or_pooled_ip")
	private String staticOrPooledIP;
	@Column(name = "charge_type_file")
	private String chargeTypeFile;
	@Column(name = "billing_cycle")
	private String billingCycle;
	@Column(name = "billing_type")
	private String billingType;
	@Column(name = "billable")
	private String billable; // CircuitOrAccount;
	@Column(name = "billing_group")
	private String billingGroup;
	@Column(name = "payable")
	private String payable; // circuit or account;
	@Column(name = "enable_processing")
	private String enableProcessing; // - Yes or No
	@Column(name = "deposite")
	private String deposite;
	@Column(name = "po_number")
	private String poNumber;
	@Column(name = "bill_remark")
	private String billRemark;
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
	@Column(name = "MVNOID")
	private Integer mvnoId;
	@Column(name = "buid")
	private Long buId;

	@Column(name = "s_discount_type")
	private String discountType = "One-time";

	@Column(name = "s_discount")
	private Double discount = 0.0;

	@Column(name = "discount_expiry_date")
	private LocalDate discountExpiryDate;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "service_area_type")
	private String serviceAreaType;

	@Column(name = "new_discount_type")
	private String newDiscountType;

	@Column(name = "new_discount")
	private Double newDiscount;

	@Column(name = "new_discount_expiry_date")
	private LocalDate newDiscountExpiryDate;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "next_team_hir_mapping")
	private Integer nextTeamHierarchyMappingId;

	@Column(name = "next_staff")
	private Integer nextStaff;

	@Column(name = "branch")
	private String branch;

	@Column(name = "connection_type")
	private String connectionType;

	@Column(name="country_code")
	private String countryCode;

	@CreationTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
	@Column(name = "CREATEDATE", updatable = false)
	private LocalDateTime createdate;

	@UpdateTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
	@Column(name = "LASTMODIFIEDDATE")
	private LocalDateTime updatedate;

	@Column(name = "createbyname")
	private String createdByName;

	@Column(name = "updatebyname")
	private String lastModifiedByName;

	@Column(name = "CREATEDBYSTAFFID")
	private Integer createdById;

	@Column(name = "LASTMODIFIEDBYSTAFFID")
	private Integer lastModifiedById;

	@Column(name = "location")
	private String location;

	@Column(name = "valley_type")
	private String valleyType;

	@Column(name = "inside_valley")
	private String insideValley;

	public LeadServiceMapping(CustPlanMapppingPojo planMapppingPojo) {

		LinkAcceptanceDTO linkAcceptanceDTO = planMapppingPojo.getLinkAcceptanceDTO();

		if (linkAcceptanceDTO != null) {
			setId(linkAcceptanceDTO.getId());
			setLeadId(linkAcceptanceDTO.getLeadId());
			setLeaseCircuitName(linkAcceptanceDTO.getCircuitName());
			setPlanId(planMapppingPojo.getPlanId().longValue());
			setPlanName(planMapppingPojo.getPlanName());
			setEndDate(linkAcceptanceDTO.getEndDate());
			setStartDate(linkAcceptanceDTO.getStartDate());
			setCircuitStatus(linkAcceptanceDTO.getCircuitStatus());
			setConnectionNo(linkAcceptanceDTO.getConnectionNo());
			setCafNo(linkAcceptanceDTO.getCafNo());
			setUploadCAF(linkAcceptanceDTO.getUploadCAF());
			setCustomerName(linkAcceptanceDTO.getCustomerName());
			setAccountNumber(linkAcceptanceDTO.getAcctNumber());
			setTypeOfLink(linkAcceptanceDTO.getTypeOfLink());
			setLinkInstallationDate(linkAcceptanceDTO.getLinkInstallationDate());
			setLinkAcceptanceDate(linkAcceptanceDTO.getLinkAcceptanceDate());
			setPurchaseOrderDate(linkAcceptanceDTO.getPurchaseOrderDate());
			setCircleName(linkAcceptanceDTO.getCircleName());
			setOrderLoginDate(linkAcceptanceDTO.getOrderLoginDate());
			setPartner(linkAcceptanceDTO.getPartner());
			setExpiryDate(linkAcceptanceDTO.getExpiryDate());
			setDistance(linkAcceptanceDTO.getDistance());
			setDistanceUnit(linkAcceptanceDTO.getDistanceUnit());
			setBandwidth(linkAcceptanceDTO.getBandwidth());
			setUploadQOS(linkAcceptanceDTO.getUploadQOS());
			setDownloadQOS(linkAcceptanceDTO.getDownloadQOS());
			setLinkRouterLocation(linkAcceptanceDTO.getLinkRouterLocation());
			setLinkPortType(linkAcceptanceDTO.getLinkPortType());
			setLinkRouterIp(linkAcceptanceDTO.getLinkRouterIP());
			setLinkPortOnRouter(linkAcceptanceDTO.getLinkPortOnRouter());
			setVLANId(linkAcceptanceDTO.getVLANId());
			setBandwidthType(linkAcceptanceDTO.getBandwidthType());
			setLinkRouterName(linkAcceptanceDTO.getLinkRouterName());
			setCircuitBillingId(linkAcceptanceDTO.getCircuitBillingId());
			setPop(linkAcceptanceDTO.getPop());
			setAssociatedLevel(linkAcceptanceDTO.getAssociatedLevel());
			setLocationLevel1(linkAcceptanceDTO.getLocationLevel1());
			setLocationLevel2(linkAcceptanceDTO.getLocationLevel2());
			setLocationLevel3(linkAcceptanceDTO.getLocationLevel3());
			setLocationLevel4(linkAcceptanceDTO.getLocationLevel4());
			setBaseStationId1(linkAcceptanceDTO.getBaseStationId1());
			setBaseStationId2(linkAcceptanceDTO.getBaseStationId2());
			setOrganisationCircle(linkAcceptanceDTO.getOrganisationCircle());
			setTerminationCircle(linkAcceptanceDTO.getTerminationCircle());
			setTerminationAddress(linkAcceptanceDTO.getTerminationAddress());
			setOrganisationAddress2(linkAcceptanceDTO.getOrganisationAddress2());
			setTerminationAddress2(linkAcceptanceDTO.getTerminationAddress2());
			setNote(linkAcceptanceDTO.getNote());
			setContactPerson(linkAcceptanceDTO.getContactPerson());
			setContactPerson1(linkAcceptanceDTO.getContactPerson1());
			setContactPerson2(linkAcceptanceDTO.getContactPerson2());
			setMobileNumber(linkAcceptanceDTO.getMobileNo());
			setMobileNumber1(linkAcceptanceDTO.getMobileNumber1());
			setMobileNumber2(linkAcceptanceDTO.getMobileNumber2());
			setLandlineNumber(linkAcceptanceDTO.getLandLineNumber());
			setLandlineNumber1(linkAcceptanceDTO.getLandLineNumber1());
			setLandlineNumber2(linkAcceptanceDTO.getLandLineNumber2());
			setEmailId(linkAcceptanceDTO.getEmail());
			setEmailId1(linkAcceptanceDTO.getEmailId1());
			setEmailId2(linkAcceptanceDTO.getEmailId2());
			setTraiRate(linkAcceptanceDTO.getTraiRate());
			setOtcChargesFile(linkAcceptanceDTO.getOtcChargesFile());
			setServiceChargerFile(linkAcceptanceDTO.getServiceChargerFile());
			setStaticOrPooledIP(linkAcceptanceDTO.getStaticOrPooledIP());
			setChargeTypeFile(linkAcceptanceDTO.getChargeTypeFile());
			setBillingCycle(linkAcceptanceDTO.getBillingCycle());
			setBillingType(linkAcceptanceDTO.getBillingType());
			setBillable(linkAcceptanceDTO.getBillable());
			setBillingGroup(linkAcceptanceDTO.getBillingGroup());
			setPayable(linkAcceptanceDTO.getPayable());
			setEnableProcessing(linkAcceptanceDTO.getEnableProcessing());
			setDeposite(linkAcceptanceDTO.getDeposite());
			setPoNumber(linkAcceptanceDTO.getPoNumber());
			setBillRemark(linkAcceptanceDTO.getBillRemark());
			setRemarks(linkAcceptanceDTO.getRemarks());
			setFullName(linkAcceptanceDTO.getFullName());
			setOrganisation(linkAcceptanceDTO.getOrganisation());
			setAddress1(linkAcceptanceDTO.getAddress1());
			setAddress2(linkAcceptanceDTO.getAddress2());
			setCity(linkAcceptanceDTO.getCity());
			setZipcode(linkAcceptanceDTO.getZipCode());
			setState(linkAcceptanceDTO.getState());
			setCountry(linkAcceptanceDTO.getCountry());
			setStatus(linkAcceptanceDTO.getStatus());
			setMvnoId(linkAcceptanceDTO.getMvnoId());
			setBuId(linkAcceptanceDTO.getBuId());
			setServiceAreaType(linkAcceptanceDTO.getServiceAreaType());
			setBranch(linkAcceptanceDTO.getBranch());
			setServiceId(linkAcceptanceDTO.getPlanService());
			setServiceName(linkAcceptanceDTO.getServiceName());
			setConnectionType(linkAcceptanceDTO.getConnectionType());
			setLocation(linkAcceptanceDTO.getLocation());
			setCountryCode(linkAcceptanceDTO.getCountryCode());
			setValleyType(linkAcceptanceDTO.getValleyType());
			setInsideValley(linkAcceptanceDTO.getInsideValley());
		}
	}

	public LeadServiceMapping() {
	}

	public LeadServiceMapping(Long id) {
		this.id = id;
	}
}
