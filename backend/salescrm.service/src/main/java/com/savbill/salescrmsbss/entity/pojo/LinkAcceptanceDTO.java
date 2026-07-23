package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.LeadServiceMapping;
import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDate;

@Data
public class LinkAcceptanceDTO{

    @Id
    private Long id;
    private Long leadId;
    private String circuitName;
    private String circuitStatus;
    private Long cafNo;
    private String uploadCAF;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String acctNumber;
    private String typeOfLink;  //dropdowm value not clear.
    private Long planService;
    private String serviceName;
    private Long planId;
    private LocalDate linkInstallationDate;
    private LocalDate linkAcceptanceDate;
    private LocalDate purchaseOrderDate;
    private String circleName;
    private LocalDate orderLoginDate;
    private Long partner;  //(Select Partner Dropdown)
    private LocalDate expiryDate;
    private Long distance;
    private String distanceUnit;  //(KM)
    private Long bandwidth;  //(Kbps)
    private String uploadQOS;
    private String downloadQOS;
    private String linkRouterLocation;
    private String linkPortType;
    private String linkRouterIP;
    private String linkPortOnRouter;
    private String vLANId;
    private String bandwidthType;
    private String linkRouterName;
    private String circuitBillingId;
    private String pop;      //(Select POP dropdown);
    private String associatedLevel;
    private String locationLevel1;
    private String locationLevel2;
    private String locationLevel3;
    private String locationLevel4;
    private String baseStationId1;
    private String baseStationId2;
    private String organisationCircle;
    private String terminationCircle;
    private String organisationAddress;
    private String terminationAddress;
    private String organisationAddress2;
    private String terminationAddress2;
    private String note;
    private String contactPerson;
    private String contactPerson1;
    private String contactPerson2;
    private String mobileNo;
    private String mobileNumber1;
    private String mobileNumber2;
    private String landLineNumber;
    private String landLineNumber1;
    private String landLineNumber2;
    private String email;
    private String emailId1;
    private String emailId2;
    private String remarks;
    private Long traiRate;
    private String otcChargesFile;
    private String serviceChargerFile;
    //Items :

    //Add Items
    private String staticOrPooledIP;
    private String chargeTypeFile;
    private String billingCycle;
    private String billingType;
    private String billable; //Circuit Or Account;
    private String billingGroup;  //dropdown
    private String payable; //circuit or account;
    private String enableProcessing; //- Yes or No
    private String deposite;
    private String poNumber;
    private String billRemark;
    private String fullName;
    private String organisation;
    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String state;
    private String country;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private String status;
    private Long buId;
    private Integer custId;
    private String serviceAreaType;  // HO/BO/CO
    private String branch;
    private String connectionType;
    private String connectionNo;
    private String location;
    private String countryCode;
    private String valleyType;
    private String insideValley;

    public LinkAcceptanceDTO(LeadServiceMapping leadServiceMapping) {

        if(leadServiceMapping != null) {
            if(leadServiceMapping.getId()!=null){
                setId(leadServiceMapping.getId());
            }
            setLeadId(leadServiceMapping.getLeadId());
            setCircuitName(leadServiceMapping.getLeaseCircuitName());
            setConnectionNo(leadServiceMapping.getConnectionNo());
            setCircuitStatus(leadServiceMapping.getCircuitStatus());
            setPlanId(leadServiceMapping.getPlanId());
            setCafNo(leadServiceMapping.getCafNo());
            setRemarks(leadServiceMapping.getRemarks());
            setUploadCAF(leadServiceMapping.getUploadCAF());
            setCustomerName(leadServiceMapping.getCustomerName());
            setAcctNumber(leadServiceMapping.getAccountNumber());
            setTypeOfLink(leadServiceMapping.getTypeOfLink());
            setLinkInstallationDate(leadServiceMapping.getLinkInstallationDate());
            setLinkAcceptanceDate(leadServiceMapping.getLinkAcceptanceDate());
            setPurchaseOrderDate(leadServiceMapping.getPurchaseOrderDate());
            setCircleName(leadServiceMapping.getCircleName());
            setOrderLoginDate(leadServiceMapping.getOrderLoginDate());
            setPartner(leadServiceMapping.getPartner());
            setExpiryDate(leadServiceMapping.getExpiryDate());
            setDistance(leadServiceMapping.getDistance());
            setDistanceUnit(leadServiceMapping.getDistanceUnit());
            setBandwidth(leadServiceMapping.getBandwidth());
            setUploadQOS(leadServiceMapping.getUploadQOS());
            setDownloadQOS(leadServiceMapping.getDownloadQOS());
            setLinkRouterLocation(leadServiceMapping.getLinkRouterLocation());
            setLinkPortType(leadServiceMapping.getLinkPortType());
            setLinkRouterIP(leadServiceMapping.getLinkRouterIp());
            setLinkPortOnRouter(leadServiceMapping.getLinkPortOnRouter());
            setVLANId(leadServiceMapping.getVLANId());
            setBandwidthType(leadServiceMapping.getBandwidthType());
            setLinkRouterName(leadServiceMapping.getLinkRouterName());
            setCircuitBillingId(leadServiceMapping.getCircuitBillingId());
            setPop(leadServiceMapping.getPop());
            setAssociatedLevel(leadServiceMapping.getAssociatedLevel());
            setLocationLevel1(leadServiceMapping.getLocationLevel1());
            setLocationLevel2(leadServiceMapping.getLocationLevel2());
            setLocationLevel3(leadServiceMapping.getLocationLevel3());
            setLocationLevel4(leadServiceMapping.getLocationLevel4());
            setBaseStationId1(leadServiceMapping.getBaseStationId1());
            setBaseStationId2(leadServiceMapping.getBaseStationId2());
            setOrganisationCircle(leadServiceMapping.getOrganisationCircle());
            setTerminationCircle(leadServiceMapping.getTerminationCircle());
            setTerminationAddress(leadServiceMapping.getTerminationAddress());
            setOrganisationAddress2(leadServiceMapping.getOrganisationAddress2());
            setTerminationAddress2(leadServiceMapping.getTerminationAddress2());
            setNote(leadServiceMapping.getNote());
            setContactPerson(leadServiceMapping.getContactPerson());
            setContactPerson1(leadServiceMapping.getContactPerson1());
            setContactPerson2(leadServiceMapping.getContactPerson2());
            setMobileNo(leadServiceMapping.getMobileNumber());
            setMobileNumber1(leadServiceMapping.getMobileNumber1());
            setMobileNumber2(leadServiceMapping.getMobileNumber2());
            setLandLineNumber(leadServiceMapping.getLandlineNumber());
            setLandLineNumber1(leadServiceMapping.getLandlineNumber1());
            setLandLineNumber2(leadServiceMapping.getLandlineNumber2());
            setEmail(leadServiceMapping.getEmailId());
            setEmailId1(leadServiceMapping.getEmailId1());
            setEmailId2(leadServiceMapping.getEmailId2());
            setTraiRate(leadServiceMapping.getTraiRate());
            setOtcChargesFile(leadServiceMapping.getOtcChargesFile());
            setServiceChargerFile(leadServiceMapping.getServiceChargerFile());
            setStaticOrPooledIP(leadServiceMapping.getStaticOrPooledIP());
            setChargeTypeFile(leadServiceMapping.getChargeTypeFile());
            setBillingCycle(leadServiceMapping.getBillingCycle());
            setBillingType(leadServiceMapping.getBillingType());
            setBillable(leadServiceMapping.getBillable());
            setBillingGroup(leadServiceMapping.getBillingGroup());
            setPayable(leadServiceMapping.getPayable());
            setEnableProcessing(leadServiceMapping.getEnableProcessing());
            setDeposite(leadServiceMapping.getDeposite());
            setPoNumber(leadServiceMapping.getPoNumber());
            setBillRemark(leadServiceMapping.getBillRemark());
            setFullName(leadServiceMapping.getFullName());
            setOrganisation(leadServiceMapping.getOrganisation());
            setAddress1(leadServiceMapping.getAddress1());
            setAddress2(leadServiceMapping.getAddress2());
            setCity(leadServiceMapping.getCity());
            setZipCode(leadServiceMapping.getZipcode());
            setState(leadServiceMapping.getState());
            setCountry(leadServiceMapping.getCountry());
            setStatus(leadServiceMapping.getStatus());
            setMvnoId(leadServiceMapping.getMvnoId());
            setBuId(leadServiceMapping.getBuId());
            setServiceAreaType(leadServiceMapping.getServiceAreaType());
            setBranch(leadServiceMapping.getBranch());
            setPlanService(leadServiceMapping.getServiceId());
            setServiceName(leadServiceMapping.getServiceName());
            setConnectionType(leadServiceMapping.getConnectionType());
            setLocation(leadServiceMapping.getLocation());
            setCountryCode(leadServiceMapping.getCountryCode());
            setValleyType(leadServiceMapping.getValleyType());
            setInsideValley(leadServiceMapping.getInsideValley());
        }
    }
    public LinkAcceptanceDTO() {
    }

}
