package com.savbill.salescrmsbss.entity.pojo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.savbill.salescrmsbss.entity.CreditDocument;
import com.savbill.salescrmsbss.entity.CustChargeDetails;
import com.savbill.salescrmsbss.entity.CustLedgerDtls;
import com.savbill.salescrmsbss.entity.CustMacMappping;
import com.savbill.salescrmsbss.entity.CustPlanMappping;
import com.savbill.salescrmsbss.entity.CustomerAddress;
import com.savbill.salescrmsbss.entity.CustomerDocDetails;
import com.savbill.salescrmsbss.entity.DebitDocument;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadMasterPojo {

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
	private String cstatus;

	private Integer failcount = 0;

	private String acctno;

	private String custtype;

	private String phone;

	private Integer billday;

	private Integer partnerid;

	private String onuid;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate nextBillDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate lastBillDate;

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

	@CreationTimestamp
	private LocalDateTime last_password_change;

	private String lastpasswordchangestring;

	private List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();

	private List<CustomerAddressPojo> addressList = new ArrayList<>();

	private List<Integer> radiusprofileIds = new ArrayList<>();

	private List<DebitDocumentPojo> debitDocList = new ArrayList<>();

	private List<CreditDocumentPojo> creditDocuments = new ArrayList<>();

	private List<CustChargeDetailsPojo> overChargeList = new ArrayList<>();

	private List<CustomerDocDetailsPojo> custDocList = new ArrayList<>();

	private List<CustChargeDetailsPojo> indiChargeList = new ArrayList<>();

	private CustomerLedgerPojo custLeger;

	private List<CustMacMapppingPojo> custMacMapppingList = new ArrayList<>();

	private List<CustLedgerDtlsPojo> ledgerDtls = new ArrayList<>();

	private RecordPaymentPojo paymentDetails;

	private String flashMsg;

	private Boolean mactelflag = false;

	@Size(min = 5, max = 15, message = "Mobile number must have 10 digits!")
	private String mobile;

	private String countryCode;

	private String cafno;

	private String altmobile;
	private String altmobile1;
	private String altmobile2;
	private String altmobile3;
	private String altmobile4;

	private String altphone;

	private String altemail;

	private String fax;

	private Integer resellerid;

	private Integer salesrepid;

	private String voicesrvtype;

	private Boolean voiceprovision = false;

	private String didno;

	private String childdidno;

	private String intercomno;

	private String intercomgrp;

	private Boolean onlinerenewalflag = false;

	private Boolean voipenableflag = false;

	private String custcategory;

	private Double walletbalance = 0.0;

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

	private LocalDateTime firstActivationDate;

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

	private String tinNo;

	private String passportNo;

	private String dunningCategory;

	private Integer plangroupid;

	private Integer parentCustomerId;

	private String parentCustomerName;

	private String invoiceType;

	private String calendarType;

	private Double discount;

	private Long leadSourceId;

	private Long leadSubSourceId;

	private Long rejectReasonId;

	private Long rejectSubReasonId;

	private String rejectReasonName;

	private String rejectSubReasonName;

	private String leadSourceName;

	private String leadSubSourceName;

	private String reasonToChangeServiceProvider;

	private String previousVendor;

	private String servicerType;

	private String leadStatus = "Inquiry";

	private String createdBy;
	private String createdByName;

	private Integer nextApproveStaffId;

	private Integer nextTeamMappingId;

	private String leadCategory;

	private String heardAboutSubisuFrom;

	private Integer leadPartnerId;

	private String leadPartnerName;

	private Integer leadCustomerId;

	private String leadCustomerName;

	private Integer leadStaffId;

	private String leadStaffName;

	private Long leadBranchId;

	private String leadBranchName;

	private Long leadAgentId;

	private Long leadServiceAreaId;

	private String leadServiceAreaName;

	private String feasibility;

	private String feasibilityRemark;

	private String feasibilityRequired = "Yes";

	private LocalDateTime rejectLeadTime;

	private boolean leadReopenAllow = false;

	private String leadType;

	private Long existingCustomerId;

	private Long approveBuId;

	private Integer approveCurrentLoggedInStaffId;

	private String approveFirstname;

	private String flag;

	private Long mvnoId;

	private Long buId;

	private Long approveMvnoId;

	private Integer approverNextLeadApprover;

	private String approveRemark;

	private Long approveServiceareaid;

	private Integer approveStaffId;

	private String approveStatus;

	private String approveUsername;

	private boolean finalApproved = false;

	private String planType;

	private String assigneeName;

	private Boolean isCustomerCafeIsUpdated;

	private Integer customerId;

	private String leadNo;

	private boolean presentCheckForPayment;

	private boolean presentCheckForPermanent;

	private LeadSourcePojo leadSourcePojo;

	private String lastModifiedBy;

	private LocalDateTime rejectedOn;

	private LocalDateTime approvedOn;

	private LocalDateTime reOpenOn;

	private String approvedBy;

	private String rejectedBy;

	private String reOpenBy;

	private List<LeadDocDetailsDTO> leadDocDetailsList = new ArrayList<>();

	private String leadCustomerCategory;

	private String leadCustomerType;

	private String leadCustomerSubType;

	private String leadCustomerSector;

	private String leadCustomerSubSector;

	private String valleyType;

	private String insideValley;

	private String outsideValley;

	private String competitorDuration;

	private String parentExperience;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate expiry;

	private Double amount;

	private String feedback;

	private String gender;

	private Long branchId;

	private String branchName;

	private Long popManagementId;

	private String popManagementName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;

	private String secondaryContactDetails;

	private String secondaryPhone;

	private String secondaryEmail;

	private Double previousAmount;

	private String previousMonth;

	private String leadOriginType;

	private String requireServiceType;

	private String landlineNumber;

	private Integer leadFollowUpCount;

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

	private String billableCustomerId = null;

	private String discountType = "One-time";

	private LocalDate discountExpiryDate;

	private LocalDate cafConvertedDate;

	private Integer cafConvertedStaffId;

	private String cafCovertedStaffName;

	private String locationlevel1;

	private String locationlevel2;

	private String locationlevel3;

	private String locationlevel4;

	private String skypeid_imid;

	private String organisation;

	private String associatedLevel;

	private String nation;

	private Boolean isLeadQuickInv;

	private String leadIdentity;

	private String leadDepartment;

	private String designation;
	private LocalDate nextfollowupdate;
	private LocalTime nextfollowuptime;

	private String mvnoName;

	private Boolean isLeadFromCWSC;

	private String blockNo;

	private String currency;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPcontactphno() {
		return pcontactphno;
	}

	public void setPcontactphno(String pcontactphno) {
		this.pcontactphno = pcontactphno;
	}

	public String getScontactname() {
		return scontactname;
	}

	public void setScontactname(String scontactname) {
		this.scontactname = scontactname;
	}

	public String getBusinessverticals() {
		return businessverticals;
	}

	public void setBusinessverticals(String businessverticals) {
		this.businessverticals = businessverticals;
	}

	public String getSubbusinessverticals() {
		return subbusinessverticals;
	}

	public void setSubbusinessverticals(String subbusinessverticals) {
		this.subbusinessverticals = subbusinessverticals;
	}

	public String getConnectiontype() {
		return connectiontype;
	}

	public void setConnectiontype(String connectiontype) {
		this.connectiontype = connectiontype;
	}

	public String getLinktype() {
		return linktype;
	}

	public void setLinktype(String linktype) {
		this.linktype = linktype;
	}

	public String getCircuitarea() {
		return circuitarea;
	}

	public void setCircuitarea(String circuitarea) {
		this.circuitarea = circuitarea;
	}

	public LocalDate getClosuredate() {
		return closuredate;
	}

	public void setClosuredate(LocalDate closuredate) {
		this.closuredate = closuredate;
	}

	public Long getCircuitid() {
		return circuitid;
	}

	public void setCircuitid(Long circuitid) {
		this.circuitid = circuitid;
	}

	public String getCircuitname() {
		return circuitname;
	}

	public void setCircuitname(String circuitname) {
		this.circuitname = circuitname;
	}

	public String getLeadvariety() {
		return leadvariety;
	}

	public void setLeadvariety(String leadvariety) {
		this.leadvariety = leadvariety;
	}

	public LocalDate getCafConvertedDate() {
		return cafConvertedDate;
	}

	public void setCafConvertedDate(LocalDate cafConvertedDate) {
		this.cafConvertedDate = cafConvertedDate;
	}

	public Integer getCafConvertedStaffId() {
		return cafConvertedStaffId;
	}

	public void setCafConvertedStaffId(Integer cafConvertedStaffId) {
		this.cafConvertedStaffId = cafConvertedStaffId;
	}

	public LeadMasterPojo(LeadMaster leadMaster) {
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
		this.gst = leadMaster.getGst();
		this.aadhar = leadMaster.getAadhar();
		this.status = leadMaster.getStatus();
		this.cstatus = leadMaster.getCstatus();
		this.failcount = leadMaster.getFailcount();
		this.acctno = leadMaster.getAcctno();
		this.custtype = leadMaster.getCusttype();
		this.phone = leadMaster.getPhone();
		this.billday = leadMaster.getBillday();
		this.partnerid = leadMaster.getPartnerid();
		this.onuid = leadMaster.getOnuid();
		this.nextBillDate = leadMaster.getNextBillDate();
		this.lastBillDate = leadMaster.getLastBillDate();
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
		this.last_password_change = leadMaster.getLast_password_change();
		this.lastpasswordchangestring = leadMaster.getLastpasswordchangestring();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
			List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<CustPlanMapppingPojo>();
			for (CustPlanMappping custPlanMappping : leadMaster.getPlanMappingList()) {
				custPlanMapppingPojoList.add(new CustPlanMapppingPojo(custPlanMappping));
			}
			this.planMappingList = custPlanMapppingPojoList;
		}
		if (leadMaster.getAddressList() != null && leadMaster.getAddressList().size() > 0) {
			List<CustomerAddressPojo> customerAddressPojoList = new ArrayList<CustomerAddressPojo>();
			for (CustomerAddress customerAddress : leadMaster.getAddressList()) {
				customerAddressPojoList.add(new CustomerAddressPojo(customerAddress));
			}
			this.addressList = customerAddressPojoList;
		}
		if (leadMaster.getRadiusprofileIds() != null && !leadMaster.getRadiusprofileIds().equalsIgnoreCase("")) {
			List<String> Ids = Lists.newArrayList(Splitter.on(" , ").split(leadMaster.getRadiusprofileIds()));
			List<Integer> radiusProfileIds = Ids.stream().map(n -> Integer.parseInt(n)).collect(Collectors.toList());
			this.radiusprofileIds = radiusProfileIds;
		}
		if (leadMaster.getDebitDocList() != null && leadMaster.getDebitDocList().size() > 0) {
			List<DebitDocumentPojo> debitDocumentPojoList = new ArrayList<DebitDocumentPojo>();
			for (DebitDocument debitDocument : leadMaster.getDebitDocList()) {
				debitDocumentPojoList.add(new DebitDocumentPojo(debitDocument));
			}
			this.debitDocList = debitDocumentPojoList;
		}
		if (leadMaster.getCreditDocuments() != null && leadMaster.getCreditDocuments().size() > 0) {
			List<CreditDocumentPojo> creditDocumentPojoList = new ArrayList<CreditDocumentPojo>();
			for (CreditDocument creditDocument : leadMaster.getCreditDocuments()) {
				creditDocumentPojoList.add(new CreditDocumentPojo(creditDocument));
			}
			this.creditDocuments = creditDocumentPojoList;
		}
		if (leadMaster.getOverChargeList() != null && leadMaster.getOverChargeList().size() > 0) {
			List<CustChargeDetailsPojo> custChargeDetailsPojoList = new ArrayList<CustChargeDetailsPojo>();
			for (CustChargeDetails custChargeDetails : leadMaster.getOverChargeList()) {
				custChargeDetailsPojoList.add(new CustChargeDetailsPojo(custChargeDetails));
			}
			this.overChargeList = custChargeDetailsPojoList;
		}
		if (leadMaster.getCustDocList() != null && leadMaster.getCustDocList().size() > 0) {
			List<CustomerDocDetailsPojo> customerDocDetailsPojoList = new ArrayList<CustomerDocDetailsPojo>();
			for (CustomerDocDetails customerDocDetails : leadMaster.getCustDocList()) {
				customerDocDetailsPojoList.add(new CustomerDocDetailsPojo(customerDocDetails));
			}
			this.custDocList = customerDocDetailsPojoList;
		}
		if (leadMaster.getIndiChargeList() != null && leadMaster.getIndiChargeList().size() > 0) {
			List<CustChargeDetailsPojo> custChargeDetailsPojoList = new ArrayList<CustChargeDetailsPojo>();
			for (CustChargeDetails custChargeDetails : leadMaster.getIndiChargeList()) {
				custChargeDetailsPojoList.add(new CustChargeDetailsPojo(custChargeDetails));
			}
			this.indiChargeList = custChargeDetailsPojoList;
		}
		if (leadMaster.getCustMacMapppingList() != null && leadMaster.getCustMacMapppingList().size() > 0) {
			List<CustMacMapppingPojo> custMacMapppingPojoList = new ArrayList<CustMacMapppingPojo>();
			for (CustMacMappping custMacMappping : leadMaster.getCustMacMapppingList()) {
				custMacMapppingPojoList.add(new CustMacMapppingPojo(custMacMappping));
			}
			this.custMacMapppingList = custMacMapppingPojoList;
		}
		if (leadMaster.getLedgerDtls() != null && leadMaster.getLedgerDtls().size() > 0) {
			List<CustLedgerDtlsPojo> custLedgerDtlsPojoList = new ArrayList<CustLedgerDtlsPojo>();
			for (CustLedgerDtls custLedgerDtls : leadMaster.getLedgerDtls()) {
				custLedgerDtlsPojoList.add(new CustLedgerDtlsPojo(custLedgerDtls));
			}
			this.ledgerDtls = custLedgerDtlsPojoList;
		}
		if (leadMaster.getCustLeger() != null) {
			this.custLeger = new CustomerLedgerPojo(leadMaster.getCustLeger());
		}
		if (leadMaster.getPaymentDetails() != null) {
			this.paymentDetails = new RecordPaymentPojo(leadMaster.getPaymentDetails());
		}
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
		this.firstActivationDate = leadMaster.getFirstActivationDate();
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
		if (leadMaster.getLeadSource() != null) {
			this.leadSourceId = leadMaster.getLeadSource().getId();
			this.leadSourceName = leadMaster.getLeadSource().getLeadSourceName();
		}
		if (leadMaster.getLeadSubSource() != null) {
			this.leadSubSourceId = leadMaster.getLeadSubSource().getId();
			this.leadSubSourceName = leadMaster.getLeadSubSource().getLeadSubSourceName();
		}
		if (leadMaster.getRejectReason() != null) {
			this.rejectReasonId = leadMaster.getRejectReason().getId();
			this.rejectReasonName = leadMaster.getRejectReason().getName();
		}
		if (leadMaster.getRejectSubReason() != null) {
			this.rejectSubReasonId = leadMaster.getRejectSubReason().getId();
			this.rejectSubReasonName = leadMaster.getRejectSubReason().getName();
		}
		this.reasonToChangeServiceProvider = leadMaster.getReasonToChangeServiceProvider();
		this.previousVendor = leadMaster.getPreviousVendor();
		this.servicerType = leadMaster.getServicerType();
		this.leadStatus = leadMaster.getLeadStatus();
		this.leadCategory = leadMaster.getLeadCategory();
		this.heardAboutSubisuFrom = leadMaster.getHeardAboutSubisuFrom();
		if (leadMaster.getPartner() != null) {
			this.leadPartnerId = leadMaster.getPartner().getId();
			this.leadPartnerName = leadMaster.getPartner().getName();
		}
		if (leadMaster.getCustomers() != null) {
			this.leadCustomerId = leadMaster.getCustomers().getId();
			this.leadCustomerName = leadMaster.getCustomers().getFirstname() + " "
					+ leadMaster.getCustomers().getLastname();
		}
		if (leadMaster.getStaffUser() != null) {
			this.leadStaffId = leadMaster.getStaffUser().getId();
			this.leadStaffName = leadMaster.getStaffUser().getFirstname() + " "
					+ leadMaster.getStaffUser().getLastname();
		}
		if (leadMaster.getLeadBranch() != null) {
			this.leadBranchId = leadMaster.getLeadBranch().getId();
			this.leadBranchName = leadMaster.getLeadBranch().getName();
		}
		this.leadAgentId = leadMaster.getLeadAgentId();
		if (leadMaster.getServiceArea() != null) {
			this.leadServiceAreaId = leadMaster.getServiceArea().getId();
			this.leadServiceAreaName = leadMaster.getServiceArea().getName();
		}
		this.feasibility = leadMaster.getFeasibility();
		this.feasibilityRemark = leadMaster.getFeasibilityRemark();
		this.feasibilityRequired = leadMaster.getFeasibilityRequired();
		this.rejectLeadTime = leadMaster.getRejectLeadTime();
		this.leadType = leadMaster.getLeadType();
		this.existingCustomerId = leadMaster.getExistingCustomerId();
		if (leadMaster.getLeadStatus() != null && leadMaster.getLeadStatus().equalsIgnoreCase("Rejected")) {
			if (leadMaster.getRejectLeadTime() != null) {
				Duration duration = Duration.between(leadMaster.getRejectLeadTime(), LocalDateTime.now());
				if (duration.toDays() <= 30)
					this.leadReopenAllow = true;
			}
		}
		this.finalApproved = leadMaster.isFinalApproved();
		this.planType = leadMaster.getPlanType();
		this.buId = leadMaster.getBuId();
		this.mvnoId = leadMaster.getMvnoId();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.leadNo = leadMaster.getLeadNo();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		this.presentCheckForPayment = leadMaster.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMaster.isPresentCheckForPermanent();
		this.leadCustomerCategory = leadMaster.getLeadCustomerCategory();
		this.leadCustomerType = leadMaster.getLeadCustomerType();
		this.leadCustomerSubType = leadMaster.getLeadCustomerSubType();
		this.leadCustomerSector = leadMaster.getLeadCustomerSector();
		this.leadCustomerSubSector = leadMaster.getLeadCustomerSubSector();
		this.valleyType = leadMaster.getValleyType();
		this.insideValley = leadMaster.getInsideValley();
		this.outsideValley = leadMaster.getOutsideValley();
		this.competitorDuration = leadMaster.getCompetitorDuration();
		this.expiry = leadMaster.getExpiry();
		this.amount = leadMaster.getAmount();
		this.feedback = leadMaster.getFeedback();
		this.gender = leadMaster.getGender();
		this.currency = leadMaster.getCurrency();
		if (leadMaster.getBranch() != null) {
			this.branchId = leadMaster.getBranch().getId();
			this.branchName = leadMaster.getBranch().getName();
		}
		if (leadMaster.getPopManagement() != null) {
			this.popManagementId = leadMaster.getPopManagement().getId();
			this.popManagementName = leadMaster.getPopManagement().getPopName();
		}
		this.dateOfBirth = leadMaster.getDateOfBirth();
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
		this.altmobile1 = leadMaster.getAltmobile1();
		this.altmobile2 = leadMaster.getAltmobile2();
		this.altmobile3 = leadMaster.getAltmobile3();
		this.altmobile4 = leadMaster.getAltmobile4();

		this.billableCustomerId = leadMaster.getBillableCustomerId();
		this.discountType = leadMaster.getDiscountType();
		this.discountExpiryDate = leadMaster.getDiscountExpiryDate();
		this.cafConvertedDate = leadMaster.getCafConvertedDate();
		this.cafConvertedStaffId = leadMaster.getCafConvertedStaffId();
		if (leadMaster.getParentExperience() != null) {
			this.parentExperience = leadMaster.getParentExperience();
		}
		this.locationlevel1 = leadMaster.getLocationlevel1();
		this.locationlevel2 = leadMaster.getLocationlevel2();
		this.locationlevel3 = leadMaster.getLocationlevel3();
		this.locationlevel4 = leadMaster.getLocationlevel4();
		this.organisation = leadMaster.getOrganisation();
		this.nation = leadMaster.getNation();
		this.skypeid_imid = leadMaster.getSkypeid_imid();
		this.associatedLevel = leadMaster.getAssociatedLevel();
		this.blockNo = leadMaster.getBlockNo();
		if(leadMaster.getIsLeadQuickInv()!= null)
			this.isLeadQuickInv = leadMaster.getIsLeadQuickInv()==1?true:false;
		if(leadMaster.getLeadIdentity()!= null)
			this.leadIdentity = leadMaster.getLeadIdentity();
		if(leadMaster.getLeadDepartment()!=null) {
			this.leadDepartment = leadMaster.getLeadDepartment();
		}
		if(leadMaster.getNextfollowupdate() !=null){
			this.nextfollowupdate=leadMaster.getNextfollowupdate();
		}
		if(leadMaster.getNextfollowuptime() !=null){
			this.nextfollowuptime=leadMaster.getNextfollowuptime();
		}
	}

	public LeadMasterPojo(LeadMaster leadMaster, Long reOpenDay) {
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
		this.gst = leadMaster.getGst();
		this.aadhar = leadMaster.getAadhar();
		this.status = leadMaster.getStatus();
		this.cstatus = leadMaster.getCstatus();
		this.failcount = leadMaster.getFailcount();
		this.acctno = leadMaster.getAcctno();
		this.custtype = leadMaster.getCusttype();
		this.phone = leadMaster.getPhone();
		this.billday = leadMaster.getBillday();
		this.partnerid = leadMaster.getPartnerid();
		this.onuid = leadMaster.getOnuid();
		this.nextBillDate = leadMaster.getNextBillDate();
		this.lastBillDate = leadMaster.getLastBillDate();
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
		this.createdByName = leadMaster.getCreatedByName();
		this.last_password_change = leadMaster.getLast_password_change();
		this.lastpasswordchangestring = leadMaster.getLastpasswordchangestring();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		this.mvnoId = leadMaster.getMvnoId();
		if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
			List<CustPlanMapppingPojo> custPlanMapppingPojoList = new ArrayList<CustPlanMapppingPojo>();
			for (CustPlanMappping custPlanMappping : leadMaster.getPlanMappingList()) {
				custPlanMapppingPojoList.add(new CustPlanMapppingPojo(custPlanMappping));
			}
			this.planMappingList = custPlanMapppingPojoList;
		}
		if (leadMaster.getAddressList() != null && leadMaster.getAddressList().size() > 0) {
			List<CustomerAddressPojo> customerAddressPojoList = new ArrayList<CustomerAddressPojo>();
			for (CustomerAddress customerAddress : leadMaster.getAddressList()) {
				customerAddressPojoList.add(new CustomerAddressPojo(customerAddress));
			}
			this.addressList = customerAddressPojoList;
		}
		if (leadMaster.getRadiusprofileIds() != null && !leadMaster.getRadiusprofileIds().equalsIgnoreCase("")) {
			List<String> Ids = Lists.newArrayList(Splitter.on(" , ").split(leadMaster.getRadiusprofileIds()));
			List<Integer> radiusProfileIds = Ids.stream().map(n -> Integer.parseInt(n)).collect(Collectors.toList());
			this.radiusprofileIds = radiusProfileIds;
		}
		if (leadMaster.getDebitDocList() != null && leadMaster.getDebitDocList().size() > 0) {
			List<DebitDocumentPojo> debitDocumentPojoList = new ArrayList<DebitDocumentPojo>();
			for (DebitDocument debitDocument : leadMaster.getDebitDocList()) {
				debitDocumentPojoList.add(new DebitDocumentPojo(debitDocument));
			}
			this.debitDocList = debitDocumentPojoList;
		}
		if (leadMaster.getCreditDocuments() != null && leadMaster.getCreditDocuments().size() > 0) {
			List<CreditDocumentPojo> creditDocumentPojoList = new ArrayList<CreditDocumentPojo>();
			for (CreditDocument creditDocument : leadMaster.getCreditDocuments()) {
				creditDocumentPojoList.add(new CreditDocumentPojo(creditDocument));
			}
			this.creditDocuments = creditDocumentPojoList;
		}
		if (leadMaster.getOverChargeList() != null && leadMaster.getOverChargeList().size() > 0) {
			List<CustChargeDetailsPojo> custChargeDetailsPojoList = new ArrayList<CustChargeDetailsPojo>();
			for (CustChargeDetails custChargeDetails : leadMaster.getOverChargeList()) {
				custChargeDetailsPojoList.add(new CustChargeDetailsPojo(custChargeDetails));
			}
			this.overChargeList = custChargeDetailsPojoList;
		}
		if (leadMaster.getCustDocList() != null && leadMaster.getCustDocList().size() > 0) {
			List<CustomerDocDetailsPojo> customerDocDetailsPojoList = new ArrayList<CustomerDocDetailsPojo>();
			for (CustomerDocDetails customerDocDetails : leadMaster.getCustDocList()) {
				customerDocDetailsPojoList.add(new CustomerDocDetailsPojo(customerDocDetails));
			}
			this.custDocList = customerDocDetailsPojoList;
		}
		if (leadMaster.getIndiChargeList() != null && leadMaster.getIndiChargeList().size() > 0) {
			List<CustChargeDetailsPojo> custChargeDetailsPojoList = new ArrayList<CustChargeDetailsPojo>();
			for (CustChargeDetails custChargeDetails : leadMaster.getIndiChargeList()) {
				custChargeDetailsPojoList.add(new CustChargeDetailsPojo(custChargeDetails));
			}
			this.indiChargeList = custChargeDetailsPojoList;
		}
		if (leadMaster.getCustMacMapppingList() != null && leadMaster.getCustMacMapppingList().size() > 0) {
			List<CustMacMapppingPojo> custMacMapppingPojoList = new ArrayList<CustMacMapppingPojo>();
			for (CustMacMappping custMacMappping : leadMaster.getCustMacMapppingList()) {
				custMacMapppingPojoList.add(new CustMacMapppingPojo(custMacMappping));
			}
			this.custMacMapppingList = custMacMapppingPojoList;
		}
		if (leadMaster.getLedgerDtls() != null && leadMaster.getLedgerDtls().size() > 0) {
			List<CustLedgerDtlsPojo> custLedgerDtlsPojoList = new ArrayList<CustLedgerDtlsPojo>();
			for (CustLedgerDtls custLedgerDtls : leadMaster.getLedgerDtls()) {
				custLedgerDtlsPojoList.add(new CustLedgerDtlsPojo(custLedgerDtls));
			}
			this.ledgerDtls = custLedgerDtlsPojoList;
		}
		if (leadMaster.getCustLeger() != null) {
			this.custLeger = new CustomerLedgerPojo(leadMaster.getCustLeger());
		}
		if (leadMaster.getPaymentDetails() != null) {
			this.paymentDetails = new RecordPaymentPojo(leadMaster.getPaymentDetails());
		}
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
		this.firstActivationDate = leadMaster.getFirstActivationDate();
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
		if (leadMaster.getLeadSource() != null) {
			this.leadSourceId = leadMaster.getLeadSource().getId();
			this.leadSourceName = leadMaster.getLeadSource().getLeadSourceName();
		}
		if (leadMaster.getLeadSubSource() != null) {
			this.leadSubSourceId = leadMaster.getLeadSubSource().getId();
			this.leadSubSourceName = leadMaster.getLeadSubSource().getLeadSubSourceName();
		}
		if (leadMaster.getRejectReason() != null) {
			this.rejectReasonId = leadMaster.getRejectReason().getId();
			this.rejectReasonName = leadMaster.getRejectReason().getName();
		}
		if (leadMaster.getRejectSubReason() != null) {
			this.rejectSubReasonId = leadMaster.getRejectSubReason().getId();
			this.rejectSubReasonName = leadMaster.getRejectSubReason().getName();
		}
		this.reasonToChangeServiceProvider = leadMaster.getReasonToChangeServiceProvider();
		this.previousVendor = leadMaster.getPreviousVendor();
		this.servicerType = leadMaster.getServicerType();
		this.leadStatus = leadMaster.getLeadStatus();
		this.leadCategory = leadMaster.getLeadCategory();
		this.heardAboutSubisuFrom = leadMaster.getHeardAboutSubisuFrom();
		if (leadMaster.getPartner() != null) {
			this.leadPartnerId = leadMaster.getPartner().getId();
			this.leadPartnerName = leadMaster.getPartner().getName();
		}
		if (leadMaster.getCustomers() != null) {
			this.leadCustomerId = leadMaster.getCustomers().getId();
			this.leadCustomerName = leadMaster.getCustomers().getFirstname() + " "
					+ leadMaster.getCustomers().getLastname();
		}
		if (leadMaster.getStaffUser() != null) {
			this.leadStaffId = leadMaster.getStaffUser().getId();
			this.leadStaffName = leadMaster.getStaffUser().getFirstname() + " "
					+ leadMaster.getStaffUser().getLastname();
		}
		if (leadMaster.getLeadBranch() != null) {
			this.leadBranchId = leadMaster.getLeadBranch().getId();
			this.leadBranchName = leadMaster.getLeadBranch().getName();
		}
		this.leadAgentId = leadMaster.getLeadAgentId();
		if (leadMaster.getServiceArea() != null) {
			this.leadServiceAreaId = leadMaster.getServiceArea().getId();
			this.leadServiceAreaName = leadMaster.getServiceArea().getName();
		}
		this.feasibility = leadMaster.getFeasibility();
		this.feasibilityRemark = leadMaster.getFeasibilityRemark();
		this.feasibilityRequired = leadMaster.getFeasibilityRequired();
		this.rejectLeadTime = leadMaster.getRejectLeadTime();
		this.leadType = leadMaster.getLeadType();
		this.existingCustomerId = leadMaster.getExistingCustomerId();
		if (leadMaster.getLeadStatus() != null && leadMaster.getLeadStatus().equalsIgnoreCase("Rejected")) {
			if (leadMaster.getRejectLeadTime() != null) {
				Duration duration = Duration.between(leadMaster.getRejectLeadTime(), LocalDateTime.now());
				if (duration.toDays() < reOpenDay)
					this.leadReopenAllow = true;
			}
		}
		this.finalApproved = leadMaster.isFinalApproved();
		this.planType = leadMaster.getPlanType();
		this.buId = leadMaster.getBuId();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.leadNo = leadMaster.getLeadNo();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		this.presentCheckForPayment = leadMaster.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMaster.isPresentCheckForPermanent();
		this.leadCustomerCategory = leadMaster.getLeadCustomerCategory();
		this.leadCustomerType = leadMaster.getLeadCustomerType();
		this.leadCustomerSubType = leadMaster.getLeadCustomerSubType();
		this.leadCustomerSector = leadMaster.getLeadCustomerSector();
		this.leadCustomerSubSector = leadMaster.getLeadCustomerSubSector();
		this.valleyType = leadMaster.getValleyType();
		this.insideValley = leadMaster.getInsideValley();
		this.outsideValley = leadMaster.getOutsideValley();
		this.competitorDuration = leadMaster.getCompetitorDuration();
		this.expiry = leadMaster.getExpiry();
		this.amount = leadMaster.getAmount();
		this.feedback = leadMaster.getFeedback();
		this.gender = leadMaster.getGender();
		if (leadMaster.getBranch() != null) {
			this.branchId = leadMaster.getBranch().getId();
			this.branchName = leadMaster.getBranch().getName();
		}
		if (leadMaster.getPopManagement() != null) {
			this.popManagementId = leadMaster.getPopManagement().getId();
			this.popManagementName = leadMaster.getPopManagement().getPopName();
		}
		this.dateOfBirth = leadMaster.getDateOfBirth();
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
		this.altmobile1 = leadMaster.getAltmobile1();
		this.altmobile2 = leadMaster.getAltmobile2();
		this.altmobile3 = leadMaster.getAltmobile3();
		this.altmobile4 = leadMaster.getAltmobile4();
		this.cafConvertedDate = leadMaster.getCafConvertedDate();
		this.cafConvertedStaffId = leadMaster.getCafConvertedStaffId();
		this.locationlevel1 = leadMaster.getLocationlevel1();
		this.locationlevel2 = leadMaster.getLocationlevel2();
		this.locationlevel3 = leadMaster.getLocationlevel3();
		this.locationlevel4 = leadMaster.getLocationlevel4();
		this.organisation = leadMaster.getOrganisation();
		this.nation = leadMaster.getNation();
		this.skypeid_imid = leadMaster.getSkypeid_imid();
		this.associatedLevel = leadMaster.getAssociatedLevel();
		this.blockNo = leadMaster.getBlockNo();
		if(leadMaster.getIsLeadQuickInv()!= null)
			this.isLeadQuickInv = leadMaster.getIsLeadQuickInv()==1?true:false;
		if(leadMaster.getLeadIdentity()!= null)
			this.leadIdentity = leadMaster.getLeadIdentity();

		if(leadMaster.getLeadDepartment()!=null){
			this.leadDepartment = leadMaster.getLeadDepartment();
		}

		if(leadMaster.getDesignation()!=null){
			this.designation = leadMaster.getDesignation();
		}
		if(leadMaster.getNextfollowupdate() !=null){
			this.nextfollowupdate=leadMaster.getNextfollowupdate();
		}
		if(leadMaster.getNextfollowuptime() !=null){
			this.nextfollowuptime=leadMaster.getNextfollowuptime();
		}
	}


	public LeadMasterPojo(
			String title,
			String firstname,
			String lastname,
			String leadNo,
			String mobile,
			String leadSourceName,
			String leadSubSourceName,
			Long leadSubSourceId,
			String leadBranchName,
			String leadCustomerName,
			String leadPartnerName,
			String leadServiceAreaName,
			String leadStaffName,
			Long leadSourceId,
			Long leadAgentId,
			Long leadBranchId,
			Integer leadCustomerId,
			Integer leadPartnerId,
			Long leadServiceAreaId,
			Integer leadStaffId,
			String leadStatus,
			String cstatus,
			String assigneeName,
			String mvnoName,
			String createdBy,
			String createdByName,
			LocalDate cafConvertedDate,
			Integer nextApproveStaffId,
			Long id,
			Boolean finalApproved,
			Long buId,
			Integer nextTeamMappingId,
			String username,
			Long serviceareaid,
			String currentStaff,
			String status,
			LocalDate nextfollowupdate,
			LocalTime nextfollowuptime,
			Long mvnoId
	)
 {
		this.title = title;
		this.firstname = firstname;
		this.lastname = lastname;
		this.leadNo = leadNo;
		this.mobile = mobile;
		this.leadSourceName = leadSourceName;
		this.leadSubSourceName = leadSubSourceName;
//		this.leadBranchName = leadBranchName;
		this.leadCustomerName = leadCustomerName;
		this.leadPartnerName = leadPartnerName;
		this.leadServiceAreaName = leadServiceAreaName;
		this.leadStaffName = leadStaffName;
		this.leadSubSourceId = leadSubSourceId;
		this.leadSourceId=leadSourceId;
		this.leadAgentId = leadAgentId;
//		this.leadBranchId = leadBranchId;
	 	this.branchId = leadBranchId;
	 	this.branchName = leadBranchName;
		this.leadCustomerId = leadCustomerId;
		this.leadPartnerId = leadPartnerId;
		this.leadServiceAreaId = leadServiceAreaId;
		this.leadStaffId = leadStaffId;
		this.leadStatus = leadStatus;
		this.cstatus = cstatus;
		this.assigneeName = currentStaff;
		this.mvnoName = mvnoName;
		this.createdBy = createdBy;
		this.createdByName = createdByName;
		this.cafConvertedDate = cafConvertedDate;
		this.nextApproveStaffId = nextApproveStaffId;
		this.id = id;
		this.finalApproved = finalApproved;
		this.buId = buId;
		this.nextTeamMappingId = nextTeamMappingId;
		this.username = username;
		this.serviceareaid = serviceareaid;
		this.assigneeName = assigneeName;
		this.status = status;
		this.nextfollowupdate = nextfollowupdate;
		this.nextfollowuptime = nextfollowuptime;
		this.mvnoId=mvnoId;
	}

}
