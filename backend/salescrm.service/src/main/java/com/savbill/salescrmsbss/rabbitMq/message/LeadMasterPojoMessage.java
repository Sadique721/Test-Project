package com.savbill.salescrmsbss.rabbitMq.message;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.savbill.salescrmsbss.entity.pojo.CustChargeDetailsPojo;
import com.savbill.salescrmsbss.entity.pojo.CustMacMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.CustomerAddressPojo;
import com.savbill.salescrmsbss.entity.pojo.LeadDocDetailsDTO;
import com.savbill.salescrmsbss.entity.pojo.LeadMasterPojo;
import com.savbill.salescrmsbss.entity.pojo.LeadSourcePojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadMasterPojoMessage {

	private Long id;

	private String username;

	private String password;

	private String firstname;

	private String lastname;

	private String email;

	private String title;

	private String custname;

	private String contactperson;

	private String pan;

	private String gst;

	private String aadhar;

	private String status;

	private Integer failcount;

	private String acctno;

	private String custtype;

	private String phone;

	private Integer billday;

	private Integer partnerid;

	private String onuid;

	private String nextBillDate;

	private String lastBillDate;

	private String addresstype;

	private String address1;

	private String address2;

	private Integer city;

	private Integer state;

	private Integer country;

	private Integer pincode;

	private Integer area;

	private Double outstanding;

	private String oldpassword1;

	private String newpassword;

	private String oldpassword2;

	private String oldpassword3;

	private String selfcarepwd;

	private String lastpasswordchangestring;

	private List<LeadCustPlanMapppingPojoMessage> planMappingList = new ArrayList<>();

	private List<LeadCustomerAddressPojoMessage> addressList = new ArrayList<>();

	private List<LeadCustChargeDetailsPojoMessage> overChargeList = new ArrayList<>();

	private List<LeadCustChargeDetailsPojoMessage> indiChargeList = new ArrayList<>();

	private List<LeadCustMacMapppingPojoMessage> custMacMapppingList = new ArrayList<>();

	private List<LeadDocDetailsDTOMessage> leadDocDetailsList = new ArrayList<>();

	private List<Integer> radiusprofileIds = new ArrayList<Integer>();

	private String flashMsg;

	private Boolean mactelflag;

	private String mobile;

	private String countryCode;

	private String cafno;

	private String altmobile;

	private String altphone;

	private String altemail;

	private String fax;

	private Integer resellerid;

	private Integer salesrepid;

	private String voicesrvtype;

	private Boolean voiceprovision;

	private String didno;

	private String childdidno;

	private String intercomno;

	private String intercomgrp;

	private Boolean onlinerenewalflag;

	private Boolean voipenableflag;

	private String custcategory;

	private Double walletbalance;

	private String networktype;

	private Long defaultpoolid;

	private Long serviceareaid;

	private Long networkdevicesid;

	private Long oltslotid;

	private Long oltportid;

	private String strconntype;

	private String stroltname;

	private String strslotname;

	private String strportname;

	private String OldBNGRouterinterface;

	private String OldVSIName;

	private String ASNNumber;

	private String BNGRouterinterface;

	private String BNGRoutername;

	private String IPPrefixes;

	private String IPV6Prefixes;

	private String LANIP;

	private String LANIPV6;

	private String LLAccountid;

	private String LLConnectiontype;

	private String LLExpirydate;

	private String LLMedium;

	private String LLServiceid;

	private String MACADDRESS;

	private String Peerip;

	private String POOLIP;

	private String QOS;

	private String RDExport;

	private String RDValue;

	private String VLANID;

	private String VRFName;

	private String VSIID;

	private String VSIName;

	private String WANIP;

	private String WANIPV6;

	private String billentityname;

	private String addparam1;

	private String addparam2;

	private String addparam3;

	private String addparam4;

	private String purchaseorder;

	private String remarks;

	private String allowedIPAddress;

	private String OldWANIP;

	private String OldLLAccountid;

	private String firstActivationDate;

	private boolean isDeleted = false;

	private String createDateString;

	private String updateDateString;

	private String latitude;

	private String longitude;

	private String url;

	private String gisCode;

	private String salesremark;

	private String servicetype;

	private String isCustCaf;

	private Integer previousCafApprover;

	private Integer nextCafApprover;

	private String serviceareaName;

	private String cafApproveStatus;

	private Long mvnoId;

	private String tinNo;

	private String passportNo;

	private String dunningCategory;

	private Integer plangroupid;

	private Integer parentCustomerId;

	private String parentCustomerName;

	private String invoiceType;

	private String calendarType;

	private Double discount;

	private Long buId;

	private LeadSourcePojo leadSourcePojo;

	private Long leadSubSourceId;

	private Long rejectReasonId;

	private Long rejectSubReasonId;

	private String reasonToChangeServiceProvider;

	private String previousVendor;

	private String servicerType;

	private String leadStatus;

	private String createdBy;

	private String lastModifiedBy;

	private String rejectedBy;

	private String approvedBy;

	private String reOpenBy;

	private Integer nextApproveStaffId;

	private Integer nextTeamMappingId;

	private String leadCategory;

	private String heardAboutSubisuFrom;

	private Integer leadPartnerId;

	private Integer leadCustomersId;

	private Integer leadStaffUserId;

	private Long leadBranchId;

	private Long leadAgentId;

	private Long leadServiceAreaId;

	private String feasibility;

	private String feasibilityRemark;

	private String feasibilityRequired;

	private String rejectLeadTime;

	private String leadType;

	private Long existingCustomerId;

	private boolean finalApproved;

	private String planType;

	private String leadNo;

	private boolean presentCheckForPayment;

	private boolean presentCheckForPermanent;

	private String leadCustomerCategory;

	private String leadCustomerType;

	private String leadCustomerSubType;

	private String leadCustomerSector;

	private String leadCustomerSubSector;

	private String valleyType;

	private String insideValley;

	private String outsideValley;

	private String competitorDuration;
	
	private String expiry;
	
	private Double amount;
	
	private String feedback;
	
	private String gender;
	
	private Long branchId;
	
	private Long popManagementId;
	
	private String dateOfBirth;
	
	private String secondaryContactDetails;
	
	private String secondaryPhone;
	
	private String secondaryEmail;

	private Double previousAmount;
	
	private String previousMonth;
	
	private String leadOriginType;
	
	private String requireServiceType;
	
	private String landlineNumber;
	private String pcontactphno;
	private String scontactname;

	private String businessverticals;

	private String subbusinessverticals;

	private String connectiontype;

	private String linktype;

	private String circuitarea;

	private LocalDate closuredate;

	private Long circuitid;

	private String circuitname;

	private String leadvariety;

	private String altmobile1;
	private String altmobile2;
	private String altmobile3;
	private String altmobile4;

	private Integer currentLoggedInStaffId;

	private String parentExperience;

	private Boolean isLeadQuickInv;

	private String leadIdentity;

	private String leadDepartment;
	private String nextfollowupdate;
	private String nextfollowuptime;
	private Boolean isLeadFromCWSC;
	private String blockNo;
	public LeadMasterPojoMessage(LeadMasterPojo leadMaster) {
		this.id = leadMaster.getId();
		this.username = leadMaster.getUsername();
		this.password = leadMaster.getPassword();
		this.firstname = leadMaster.getFirstname();
		this.lastname = leadMaster.getLastname();
		this.email = leadMaster.getEmail();
		this.title = leadMaster.getTitle();
		this.custname = leadMaster.getCustname();
		this.contactperson = leadMaster.getContactperson();
		this.pan = leadMaster.getPan();
		this.status=leadMaster.getStatus();
		this.gst = leadMaster.getGst();
		this.aadhar = leadMaster.getAadhar();
		this.status = leadMaster.getStatus();
		this.failcount = leadMaster.getFailcount();
		this.acctno = leadMaster.getAcctno();
		this.custtype = leadMaster.getCusttype();
		this.phone = leadMaster.getPhone();
		this.billday = leadMaster.getBillday();
		this.partnerid = leadMaster.getPartnerid();
		this.onuid = leadMaster.getOnuid();
		if (leadMaster.getNextBillDate() != null) {
			this.nextBillDate = leadMaster.getNextBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		if (leadMaster.getLastBillDate() != null) {
			this.lastBillDate = leadMaster.getLastBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		this.addresstype = leadMaster.getAddresstype();
		this.address1 = leadMaster.getAddress1();
		this.address2 = leadMaster.getAddress2();
		this.city = leadMaster.getCity();
		this.state = leadMaster.getState();
		this.country = leadMaster.getCountry();
		this.pincode = leadMaster.getPincode();
		this.area = leadMaster.getArea();
		this.outstanding = leadMaster.getOutstanding();
		this.oldpassword1 = leadMaster.getOldpassword1();
		this.oldpassword2 = leadMaster.getOldpassword2();
		this.oldpassword3 = leadMaster.getOldpassword3();
		this.selfcarepwd = leadMaster.getSelfcarepwd();
		this.createdBy = leadMaster.getCreatedBy();
		this.lastpasswordchangestring = leadMaster.getLastpasswordchangestring();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		this.radiusprofileIds = leadMaster.getRadiusprofileIds();
		this.flashMsg = leadMaster.getFlashMsg();
		this.mactelflag = leadMaster.getMactelflag();
		this.mobile = leadMaster.getMobile();
		this.countryCode = leadMaster.getCountryCode();
		this.cafno = leadMaster.getCafno();
		this.altmobile = leadMaster.getAltmobile();
		this.altphone = leadMaster.getAltphone();
		this.altemail = leadMaster.getAltemail();
		this.fax = leadMaster.getFax();
		this.resellerid = leadMaster.getResellerid();
		this.salesrepid = leadMaster.getSalesrepid();
		this.voicesrvtype = leadMaster.getVoicesrvtype();
		this.voiceprovision = leadMaster.getVoiceprovision();
		this.childdidno = leadMaster.getChilddidno();
		this.didno = leadMaster.getDidno();
		this.intercomno = leadMaster.getIntercomno();
		this.intercomgrp = leadMaster.getIntercomgrp();
		this.onlinerenewalflag = leadMaster.getOnlinerenewalflag();
		this.voipenableflag = leadMaster.getVoipenableflag();
		this.custcategory = leadMaster.getCustcategory();
		this.walletbalance = leadMaster.getWalletbalance();
		this.networktype = leadMaster.getNetworktype();
		this.defaultpoolid = leadMaster.getDefaultpoolid();
		this.serviceareaid = leadMaster.getServiceareaid();
		this.networkdevicesid = leadMaster.getNetworkdevicesid();
		this.oltslotid = leadMaster.getOltslotid();
		this.oltportid = leadMaster.getOltportid();
		this.strconntype = leadMaster.getStrconntype();
		this.stroltname = leadMaster.getStroltname();
		this.strslotname = leadMaster.getStrslotname();
		this.strportname = leadMaster.getStrportname();
		this.OldBNGRouterinterface = leadMaster.getOldBNGRouterinterface();
		this.OldVSIName = leadMaster.getOldVSIName();
		this.ASNNumber = leadMaster.getASNNumber();
		this.BNGRouterinterface = leadMaster.getBNGRouterinterface();
		this.BNGRoutername = leadMaster.getBNGRoutername();
		this.IPPrefixes = leadMaster.getIPPrefixes();
		this.IPV6Prefixes = leadMaster.getIPV6Prefixes();
		this.LANIP = leadMaster.getLANIP();
		this.LANIPV6 = leadMaster.getLANIPV6();
		this.LLAccountid = leadMaster.getLLAccountid();
		this.LLConnectiontype = leadMaster.getLLConnectiontype();
		this.LLExpirydate = leadMaster.getLLExpirydate();
		this.LLMedium = leadMaster.getLLMedium();
		this.LLServiceid = leadMaster.getLLServiceid();
		this.MACADDRESS = leadMaster.getMACADDRESS();
		this.Peerip = leadMaster.getPeerip();
		this.POOLIP = leadMaster.getPOOLIP();
		this.QOS = leadMaster.getQOS();
		this.RDExport = leadMaster.getRDExport();
		this.RDValue = leadMaster.getRDValue();
		this.VLANID = leadMaster.getVLANID();
		this.VRFName = leadMaster.getVRFName();
		this.VSIID = leadMaster.getVSIID();
		this.VSIName = leadMaster.getVSIName();
		this.WANIP = leadMaster.getWANIP();
		this.WANIPV6 = leadMaster.getWANIPV6();
		this.billentityname = leadMaster.getBillentityname();
		this.addparam1 = leadMaster.getAddparam1();
		this.addparam2 = leadMaster.getAddparam2();
		this.addparam3 = leadMaster.getAddparam3();
		this.addparam4 = leadMaster.getAddparam4();
		this.purchaseorder = leadMaster.getPurchaseorder();
		this.remarks = leadMaster.getRemarks();
		this.allowedIPAddress = leadMaster.getAllowedIPAddress();
		this.OldWANIP = leadMaster.getOldWANIP();
		this.OldLLAccountid = leadMaster.getOldLLAccountid();
		if (leadMaster.getFirstActivationDate() != null) {
			this.firstActivationDate = leadMaster.getFirstActivationDate()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		this.isDeleted = leadMaster.isDeleted();
		this.createDateString = leadMaster.getCreateDateString();
		this.updateDateString = leadMaster.getUpdateDateString();
		this.latitude = leadMaster.getLatitude();
		this.longitude = leadMaster.getLongitude();
		this.url = leadMaster.getUrl();
		this.gisCode = leadMaster.getGisCode();
		this.salesremark = leadMaster.getSalesremark();
		this.servicetype = leadMaster.getServicetype();
		this.isCustCaf = leadMaster.getIsCustCaf();
		this.previousCafApprover = leadMaster.getPreviousCafApprover();
		this.nextCafApprover = leadMaster.getNextCafApprover();
		this.serviceareaName = leadMaster.getServiceareaName();
		this.cafApproveStatus = leadMaster.getCafApproveStatus();
		this.tinNo = leadMaster.getTinNo();
		this.passportNo = leadMaster.getPassportNo();
		this.dunningCategory = leadMaster.getDunningCategory();
		this.plangroupid = leadMaster.getPlangroupid();
		this.parentCustomerId = leadMaster.getParentCustomerId();
		this.parentCustomerName = leadMaster.getParentCustomerName();
		this.invoiceType = leadMaster.getInvoiceType();
		this.calendarType = leadMaster.getCalendarType();
		this.discount = leadMaster.getDiscount();
		this.leadSourcePojo = leadMaster.getLeadSourcePojo();
		this.leadSubSourceId = leadMaster.getLeadSubSourceId();
		this.rejectReasonId = leadMaster.getRejectReasonId();
		this.rejectSubReasonId = leadMaster.getRejectSubReasonId();
		this.reasonToChangeServiceProvider = leadMaster.getReasonToChangeServiceProvider();
		this.previousVendor = leadMaster.getPreviousVendor();
		this.servicerType = leadMaster.getServicerType();
		this.leadStatus = leadMaster.getLeadStatus();
		this.leadCategory = leadMaster.getLeadCategory();
		this.heardAboutSubisuFrom = leadMaster.getHeardAboutSubisuFrom();
		this.leadPartnerId = leadMaster.getLeadPartnerId();
		this.leadCustomersId = leadMaster.getLeadCustomerId();
		this.leadStaffUserId = leadMaster.getLeadStaffId();
		this.leadBranchId = leadMaster.getLeadBranchId();
		this.leadAgentId = leadMaster.getLeadAgentId();
		this.leadServiceAreaId = leadMaster.getLeadServiceAreaId();
		this.feasibility = leadMaster.getFeasibility();
		this.feasibilityRemark = leadMaster.getFeasibilityRemark();
		this.feasibilityRequired = leadMaster.getFeasibilityRequired();
		if (leadMaster.getRejectLeadTime() != null) {
			this.rejectLeadTime = leadMaster.getRejectLeadTime()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		this.leadType = leadMaster.getLeadType();
		this.existingCustomerId = leadMaster.getExistingCustomerId();
		this.finalApproved = leadMaster.isFinalApproved();
		this.planType = leadMaster.getPlanType();
		this.buId = leadMaster.getBuId();
		this.mvnoId = leadMaster.getMvnoId();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.leadNo = leadMaster.getLeadNo();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		this.presentCheckForPayment = leadMaster.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMaster.isPresentCheckForPermanent();
		if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
			List<LeadCustPlanMapppingPojoMessage> leadCustPlanMapppingPojoMessageList = new ArrayList<LeadCustPlanMapppingPojoMessage>();
			for (CustPlanMapppingPojo custPlanMapppingPojo : leadMaster.getPlanMappingList()) {
				leadCustPlanMapppingPojoMessageList.add(new LeadCustPlanMapppingPojoMessage(custPlanMapppingPojo));
			}
			this.planMappingList = leadCustPlanMapppingPojoMessageList;
		}
		if (leadMaster.getAddressList() != null && leadMaster.getAddressList().size() > 0) {
			List<LeadCustomerAddressPojoMessage> leadCustomerAddressPojoMessageList = new ArrayList<LeadCustomerAddressPojoMessage>();
			for (CustomerAddressPojo customerAddressPojo : leadMaster.getAddressList()) {
				leadCustomerAddressPojoMessageList.add(new LeadCustomerAddressPojoMessage(customerAddressPojo));
			}
			this.addressList = leadCustomerAddressPojoMessageList;
		}
		if (leadMaster.getOverChargeList() != null && leadMaster.getOverChargeList().size() > 0) {
			List<LeadCustChargeDetailsPojoMessage> leadCustChargeDetailsPojoMessageList = new ArrayList<LeadCustChargeDetailsPojoMessage>();
			for (CustChargeDetailsPojo custChargeDetailsPojo : leadMaster.getOverChargeList()) {
				leadCustChargeDetailsPojoMessageList.add(new LeadCustChargeDetailsPojoMessage(custChargeDetailsPojo));
			}
			this.overChargeList = leadCustChargeDetailsPojoMessageList;
		}
		if (leadMaster.getIndiChargeList() != null && leadMaster.getIndiChargeList().size() > 0) {
			List<LeadCustChargeDetailsPojoMessage> leadCustChargeDetailsPojoMessageList = new ArrayList<LeadCustChargeDetailsPojoMessage>();
			for (CustChargeDetailsPojo custChargeDetailsPojo : leadMaster.getIndiChargeList()) {
				leadCustChargeDetailsPojoMessageList.add(new LeadCustChargeDetailsPojoMessage(custChargeDetailsPojo));
			}
			this.indiChargeList = leadCustChargeDetailsPojoMessageList;
		}
		if (leadMaster.getCustMacMapppingList() != null && leadMaster.getCustMacMapppingList().size() > 0) {
			List<LeadCustMacMapppingPojoMessage> leadCustMacMapppingPojoMessageList = new ArrayList<LeadCustMacMapppingPojoMessage>();
			for (CustMacMapppingPojo custMacMapppingPojo : leadMaster.getCustMacMapppingList()) {
				leadCustMacMapppingPojoMessageList.add(new LeadCustMacMapppingPojoMessage(custMacMapppingPojo));
			}
			this.custMacMapppingList = leadCustMacMapppingPojoMessageList;
		}
		if (leadMaster.getLeadDocDetailsList() != null && leadMaster.getLeadDocDetailsList().size() > 0) {
			List<LeadDocDetailsDTOMessage> leadDocDetailsDTOMessageList = new ArrayList<LeadDocDetailsDTOMessage>();
			for (LeadDocDetailsDTO leadDocDetailsDTO : leadMaster.getLeadDocDetailsList()) {
				leadDocDetailsDTOMessageList.add(new LeadDocDetailsDTOMessage(leadDocDetailsDTO));
			}
			this.leadDocDetailsList = leadDocDetailsDTOMessageList;
		}
		
		this.leadCustomerCategory = leadMaster.getLeadCustomerCategory();
		this.leadCustomerType = leadMaster.getLeadCustomerType();
		this.leadCustomerSubType = leadMaster.getLeadCustomerSubType();
		this.leadCustomerSector = leadMaster.getLeadCustomerSector();
		this.leadCustomerSubSector = leadMaster.getLeadCustomerSubSector();
		this.valleyType = leadMaster.getValleyType();
		this.insideValley = leadMaster.getInsideValley();
		this.outsideValley = leadMaster.getOutsideValley();
		this.competitorDuration = leadMaster.getCompetitorDuration();
		if (leadMaster.getExpiry() != null) {
			this.expiry = leadMaster.getExpiry()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		this.amount = leadMaster.getAmount();
		this.feedback = leadMaster.getFeedback();
		this.gender = leadMaster.getGender();
		this.branchId = leadMaster.getBranchId();
		this.popManagementId = leadMaster.getPopManagementId();
		if (leadMaster.getDateOfBirth() != null) {
			this.dateOfBirth = leadMaster.getDateOfBirth()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		this.secondaryContactDetails = leadMaster.getSecondaryContactDetails();
		this.secondaryPhone = leadMaster.getSecondaryPhone();
		this.secondaryEmail = leadMaster.getSecondaryEmail();
		this.previousAmount = leadMaster.getPreviousAmount();
		this.previousMonth = leadMaster.getPreviousMonth();
		this.leadOriginType = leadMaster.getLeadOriginType();
		this.requireServiceType = leadMaster.getRequireServiceType();
		this.landlineNumber = leadMaster.getLandlineNumber();
		this.pcontactphno = leadMaster.getPcontactphno();
		this.scontactname = leadMaster.getScontactname();
		this.businessverticals = leadMaster.getBusinessverticals();
		this.subbusinessverticals = leadMaster.getSubbusinessverticals();
		this.connectiontype = leadMaster.getConnectiontype();
		this.linktype = leadMaster.getLinktype();
		this.circuitarea = leadMaster.getCircuitarea();
		this.closuredate = leadMaster.getClosuredate();
		this.circuitid = leadMaster.getCircuitid();
		this.circuitname = leadMaster.getCircuitname();
		this.leadvariety = leadMaster.getLeadvariety();
		this.altmobile1=leadMaster.getAltmobile1();
		this.altmobile2=leadMaster.getAltmobile2();
		this.altmobile3=leadMaster.getAltmobile3();
		this.altmobile4=leadMaster.getAltmobile4();
		this.currentLoggedInStaffId=leadMaster.getApproveCurrentLoggedInStaffId();
		this.blockNo = leadMaster.getBlockNo();
		if(leadMaster.getParentExperience() != null){
			this.parentExperience = leadMaster.getParentExperience();
		}
		if(leadMaster.getIsLeadQuickInv() != null)
			this.isLeadQuickInv = leadMaster.getIsLeadQuickInv();
		if(leadMaster.getLeadIdentity()!= null)
			this.leadIdentity = leadMaster.getLeadIdentity();

		if(leadMaster.getLeadDepartment()!=null){
			this.leadDepartment = leadMaster.getLeadDepartment();
		}
		if(leadMaster.getNextfollowupdate()!=null){
			this.nextfollowupdate=leadMaster.getNextfollowupdate().toString();
		}
		if(leadMaster.getNextfollowuptime()!=null){
			this.nextfollowuptime=leadMaster.getNextfollowuptime().toString();
		}
	}
}
