package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.savbill.salescrmsbss.entity.pojo.*;
import com.savbill.salescrmsbss.entity.pojo.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Entity
@Table(name = "tblmleadmaster")
@Data
public class LeadMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_master_id", nullable = false)
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "email")
	private String email;

	@Column(name = "title")
	private String title;

	@Column(name = "custname")
	private String custname;

	@Column(name = "contactperson")
	private String contactperson;

	@Column(name = "pan")
	private String pan;

	@Column(name = "gst")
	private String gst;

	@Column(name = "aadhar")
	private String aadhar;

	@Column(name = "status")
	private String status;

	@Column(name = "cstatus")
	private String cstatus;

	@Column(name = "failcount")
	private Integer failcount;

	@Column(name = "acctno")
	private String acctno;

	@Column(name = "custtype")
	private String custtype;

	@Column(name = "phone")
	private String phone;

	@Column(name = "billday")
	private Integer billday;

	@Column(name = "partnerid")
	private Integer partnerid;

	@Column(name = "onuid")
	private String onuid;

	@Column(name = "nextBillDate")
	private LocalDate nextBillDate;

	@Column(name = "lastBillDate")
	private LocalDate lastBillDate;

	@Column(name = "addresstype")
	private String addresstype;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "city")
	private Integer city;

	@Column(name = "state")
	private Integer state;

	@Column(name = "country")
	private Integer country;

	@Column(name = "pincode")
	private Integer pincode;

	@Column(name = "area")
	private Integer area;

	@Column(name = "outstanding")
	private Double outstanding;

	@Column(name = "oldpassword1")
	private String oldpassword1;

	@Column(name = "newpassword")
	private String newpassword;

	@Column(name = "oldpassword2")
	private String oldpassword2;

	@Column(name = "oldpassword3")
	private String oldpassword3;

	@Column(name = "selfcarepwd")
	private String selfcarepwd;

	@CreationTimestamp
	@Column(name = "last_password_change")
	private LocalDateTime last_password_change;

	@Column(name = "lastpasswordchangestring")
	private String lastpasswordchangestring;

	@DiffIgnore
	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CustPlanMappping> planMappingList = new ArrayList<>();
	@DiffIgnore
	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CustomerAddress> addressList = new ArrayList<>();

	@Column(name = "radiusprofileIds")
	private String radiusprofileIds;

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<DebitDocument> debitDocList = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CreditDocument> creditDocuments = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CustChargeDetails> overChargeList = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CustomerDocDetails> custDocList = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CustChargeDetails> indiChargeList = new ArrayList<>();

	@JsonBackReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_ledger_id")
	private CustomerLedger custLeger;

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CustMacMappping> custMacMapppingList = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<CustLedgerDtls> ledgerDtls = new ArrayList<>();

	@JsonBackReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_record_id")
	private RecordPayment paymentDetails;

	@Column(name = "flashMsg")
	private String flashMsg;

	@Column(name = "mactelflag")
	private Boolean mactelflag;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "countryCode")
	private String countryCode;

	@Column(name = "cafno")
	private String cafno;

	@Column(name = "altmobile")
	private String altmobile;

	@Column(name = "altphone")
	private String altphone;

	@Column(name = "altemail")
	private String altemail;

	@Column(name = "fax")
	private String fax;

	@Column(name = "resellerid")
	private Integer resellerid;

	@Column(name = "salesrepid")
	private Integer salesrepid;

	@Column(name = "voicesrvtype")
	private String voicesrvtype;

	@Column(name = "voiceprovision")
	private Boolean voiceprovision;

	@Column(name = "didno")
	private String didno;

	@Column(name = "childdidno")
	private String childdidno;

	@Column(name = "intercomno")
	private String intercomno;

	@Column(name = "intercomgrp")
	private String intercomgrp;

	@Column(name = "onlinerenewalflag")
	private Boolean onlinerenewalflag;

	@Column(name = "voipenableflag")
	private Boolean voipenableflag;

	@Column(name = "custcategory")
	private String custcategory;

	@Column(name = "walletbalance")
	private Double walletbalance;

	@Column(name = "networktype")
	private String networktype;

	@Column(name = "defaultpoolid")
	private Long defaultpoolid;

	@Column(name = "serviceareaid")
	private Long serviceareaid;

	@Column(name = "networkdevicesid")
	private Long networkdevicesid;

	@Column(name = "oltslotid")
	private Long oltslotid;

	@Column(name = "oltportid")
	private Long oltportid;

	@Column(name = "strconntype")
	private String strconntype;

	@Column(name = "stroltname")
	private String stroltname;

	@Column(name = "strslotname")
	private String strslotname;

	@Column(name = "strportname")
	private String strportname;

	@Column(name = "OldBNGRouterinterface")
	private String OldBNGRouterinterface;

	@Column(name = "OldVSIName")
	private String OldVSIName;

	@Column(name = "ASNNumber")
	private String ASNNumber;

	@Column(name = "BNGRouterinterface")
	private String BNGRouterinterface;

	@Column(name = "BNGRoutername")
	private String BNGRoutername;

	@Column(name = "IPPrefixes")
	private String IPPrefixes;

	@Column(name = "IPV6Prefixes")
	private String IPV6Prefixes;

	@Column(name = "LANIP")
	private String LANIP;

	@Column(name = "LANIPV6")
	private String LANIPV6;

	@Column(name = "LLAccountid")
	private String LLAccountid;

	@Column(name = "LLConnectiontype")
	private String LLConnectiontype;

	@Column(name = "LLExpirydate")
	private String LLExpirydate;

	@Column(name = "LLMedium")
	private String LLMedium;

	@Column(name = "LLServiceid")
	private String LLServiceid;

	@Column(name = "MACADDRESS")
	private String MACADDRESS;

	@Column(name = "Peerip")
	private String Peerip;

	@Column(name = "POOLIP")
	private String POOLIP;

	@Column(name = "QOS")
	private String QOS;

	@Column(name = "RDExport")
	private String RDExport;

	@Column(name = "RDValue")
	private String RDValue;

	@Column(name = "VLANID")
	private String VLANID;

	@Column(name = "VRFName")
	private String VRFName;

	@Column(name = "VSIID")
	private String VSIID;

	@Column(name = "VSIName")
	private String VSIName;

	@Column(name = "WANIP")
	private String WANIP;

	@Column(name = "WANIPV6")
	private String WANIPV6;

	@Column(name = "billentityname")
	private String billentityname;

	@Column(name = "addparam1")
	private String addparam1;

	@Column(name = "addparam2")
	private String addparam2;

	@Column(name = "addparam3")
	private String addparam3;

	@Column(name = "addparam4")
	private String addparam4;

	@Column(name = "purchaseorder")
	private String purchaseorder;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "allowedIPAddress")
	private String allowedIPAddress;

	@Column(name = "OldWANIP")
	private String OldWANIP;

	@Column(name = "OldLLAccountid")
	private String OldLLAccountid;

	@Column(name = "firstActivationDate")
	private LocalDateTime firstActivationDate;

	@Column(name = "isDeleted")
	private boolean isDeleted;

	@Column(name = "createDateString")
	private String createDateString;

	@Column(name = "updateDateString")
	private String updateDateString;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "url")
	private String url;

	@Column(name = "gis_code")
	private String gisCode;

	@Column(name = "salesremark")
	private String salesremark;

	@Column(name = "servicetype")
	private String servicetype;

	@Column(name = "isCustCaf")
	private String isCustCaf;

	@Column(name = "previousCafApprover")
	private Integer previousCafApprover;

	@Column(name = "nextCafApprover")
	private Integer nextCafApprover;

	@Column(name = "serviceareaName")
	private String serviceareaName;

	@Column(name = "cafApproveStatus")
	private String cafApproveStatus;

	@Column(name = "mvnoId")
	private Long mvnoId;

	@Column(name = "tinNo")
	private String tinNo;

	@Column(name = "passportNo")
	private String passportNo;

	@Column(name = "dunningCategory")
	private String dunningCategory;

	@Column(name = "plangroupid")
	private Integer plangroupid;

	@Column(name = "parentCustomerId")
	private Integer parentCustomerId;

	@Column(name = "parentCustomerName")
	private String parentCustomerName;

	@Column(name = "invoiceType")
	private String invoiceType;

	@Column(name = "calendarType")
	private String calendarType;

	@Column(name = "discount")
	private Double discount;

	@Column(name = "buId")
	private Long buId;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_source_id")
	private LeadSource leadSource;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_sub_source_id")
	private LeadSubSource leadSubSource;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reject_reason_id")
	private RejectReason rejectReason;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reject_sub_reason_id")
	private RejectSubReason rejectSubReason;

	@Column(name = "reason_to_change_service_provider")
	private String reasonToChangeServiceProvider;

	@Column(name = "previous_vendor")
	private String previousVendor;

	@Column(name = "servicer_type")
	private String servicerType;

	@Column(name = "lead_status")
	private String leadStatus;

	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;

	@UpdateTimestamp
	@Column(name = "last_modified_on")
	private LocalDateTime lastModifiedOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_by_name")
	private String createdByName;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@Column(name = "rejected_on")
	private LocalDateTime rejectedOn;

	@Column(name = "rejected_by")
	private String rejectedBy;

	@Column(name = "approved_on")
	private LocalDateTime approvedOn;

	@Column(name = "approved_by")
	private String approvedBy;

	@Column(name = "re_open_on")
	private LocalDateTime reOpenOn;

	@Column(name = "re_open_by")
	private String reOpenBy;

	@Column(name = "altmobile1")
	private String altmobile1;

	@Column(name = "altmobile2")
	private String altmobile2;

	@Column(name = "altmobile3")
	private String altmobile3;

	@Column(name = "altmobile4")
	private String altmobile4;

	public Integer getNextApproveStaffId() {
		return nextApproveStaffId;
	}

	public void setNextApproveStaffId(Integer nextApproveStaffId) {
		this.nextApproveStaffId = nextApproveStaffId;
	}

	public Integer getNextTeamMappingId() {
		return nextTeamMappingId;
	}

	public void setNextTeamMappingId(Integer nextTeamMappingId) {
		this.nextTeamMappingId = nextTeamMappingId;
	}

	@Column(name = "next_approve_staff_id")
	private Integer nextApproveStaffId;

	@Column(name = "next_team_mapping_id")
	private Integer nextTeamMappingId;

	@Column(name = "lead_category")
	private String leadCategory;

	@Column(name = "heard_about_subisu_from")
	private String heardAboutSubisuFrom;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_partner_id")
	private Partner partner;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_customer_id")
	private Customers customers;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_staff_id")
	private StaffUser staffUser;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_branch_id")
	private Branch leadBranch;

	@Column(name = "lead_agent_id")
	private Long leadAgentId;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_service_area_id")
	private ServiceArea serviceArea;

	@Column(name = "feasibility")
	private String feasibility;

	@Column(name = "feasibility_remark")
	private String feasibilityRemark;

	@Column(name = "feasibility_required")
	private String feasibilityRequired;

	@Column(name = "reject_lead_time")
	private LocalDateTime rejectLeadTime;

	@Column(name = "lead_type")
	private String leadType;

	@Column(name = "existing_customer_id")
	private Long existingCustomerId;

	@Column(name = "no_lead_followup_send_notification")
	private boolean noLeadFollowupSendNotification;

	@Column(name = "final_approved")
	private boolean finalApproved;

	@Column(name = "plan_type")
	private String planType;

	@Column(name = "lead_no")
	private String leadNo;

	@Column(name = "present_check_for_payment")
	private boolean presentCheckForPayment;

	@Column(name = "present_check_for_permanent")
	private boolean presentCheckForPermanent;

	@Column(name = "lead_customer_category")
	private String leadCustomerCategory;

	@Column(name = "lead_customer_type")
	private String leadCustomerType;

	@Column(name = "lead_customer_sub_type")
	private String leadCustomerSubType;

	@Column(name = "lead_customer_sector")
	private String leadCustomerSector;

	@Column(name = "lead_customer_sub_sector")
	private String leadCustomerSubSector;

	@Column(name = "valley_type")
	private String valleyType;

	@Column(name = "inside_valley")
	private String insideValley;

	@Column(name = "outside_valley")
	private String outsideValley;

	@Column(name = "competitor_duration")
	private String competitorDuration;

	@Column(name = "expiry")
	private LocalDate expiry;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "feedback")
	private String feedback;

	@Column(name = "gender")
	private String gender;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "branch_id")
	private Branch branch;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pop_id")
	private PopManagement popManagement;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(name = "secondary_contact_details")
	private String secondaryContactDetails;

	@Column(name = "secondary_phone")
	private String secondaryPhone;

	@Column(name = "secondary_email")
	private String secondaryEmail;

	@Column(name = "previous_amount")
	private Double previousAmount;

	@Column(name = "previous_month")
	private String previousMonth;

	@Column(name = "lead_origin_type")
	private String leadOriginType;

	@Column(name = "require_service_type")
	private String requireServiceType;

	@Column(name = "landline_number")
	private String landlineNumber;

	@Column(name = "p_contact_phno")
	private String pcontactphno;

	@Column(name = "s_contact_name")
	private String scontactname;

	@Column(name = "business_verticals")
	private String businessverticals;

	@Column(name = "sub_business_verticals")
	private String subbusinessverticals;

	@Column(name = "connection_type")
	private String connectiontype;

	@Column(name = "link_type")
	private String linktype;

	@Column(name = "circuit_area")
	private String circuitarea;

	@Column(name = "closure_date")
	private LocalDate closuredate;

	@Column(name = "circuit_id")
	private Long circuitid;

	@Column(name = "circuit_name")
	private String circuitname;

	@Column(name = "lead_variety")
	private String leadvariety;

	@Column(name = "billable_cust_id")
	private String billableCustomerId = null;

	@Column(name = "s_discount_type")
	private String discountType;

	@Column(name = "discount_expiry_date")
	private LocalDate discountExpiryDate;

	@Column(name = "caf_converted_date")
	private LocalDate cafConvertedDate;

	@Column(name = "caf_converted_staff_id")
	private Integer cafConvertedStaffId;

	@Column(name = "parent_experience")
	private String parentExperience;

	@Column(name = "locationlevel1")
	private String locationlevel1;

	@Column(name = "locationlevel2")
	private String locationlevel2;

	@Column(name = "locationlevel3")
	private String locationlevel3;

	@Column(name = "locationlevel4")
	private String locationlevel4;

	@Column(name = "skypeid_imid")
	private String skypeid_imid;

	@Column(name = "associatedLevel")
	private String associatedLevel;

	@Column(name = "organisation")
	private String organisation;

	@Column(name = "nation")
	private String nation;

	@Column(name = "is_lead_quickinv")
	private Integer isLeadQuickInv;

	@Column(name = "lead_identity")
	private String leadIdentity;

	@Column(name = "lead_department")
	private String leadDepartment;

	@Column(name = "designation")
	private String designation;
	@Column(name = "nextfollowupdate")
	private LocalDate nextfollowupdate;
	@Column(name = "nextfollowuptime")
	private LocalTime nextfollowuptime;
	@Transient
	private Boolean isLeadFromCWSC;
	@Column(name="blockNo")
	private String blockNo;

	@Column(name ="currency")
	private String currency;

	public LeadMaster(Long id) {
		this.id = id;
	}

	public LeadMaster() {
	}

	public LeadMaster(LeadMaster leadMaster){
	    this.id = leadMaster.id;
		this.username = leadMaster.username;
		this.password = leadMaster.password;
		this.firstname = leadMaster.firstname;
		this.lastname = leadMaster.lastname;
		this.email = leadMaster.email;
		this.title = leadMaster.title;
		this.custname = leadMaster.custname;
		this.contactperson =leadMaster. contactperson;
		this.pan = leadMaster.pan;
		this.gst = leadMaster.gst;
		this.aadhar = leadMaster.aadhar;
		this.status = leadMaster.status;
		this.cstatus = leadMaster.cstatus;
		this.failcount = leadMaster.failcount;
		this.acctno = leadMaster.acctno;
		this.custtype = leadMaster.custtype;
		this.phone = leadMaster.phone;
		this.billday = leadMaster.billday;
		this.partnerid =leadMaster.partnerid;
		this.onuid = leadMaster.onuid;
		this.nextBillDate = leadMaster.nextBillDate;
		this.lastBillDate =leadMaster.lastBillDate;
		this.addresstype = leadMaster.addresstype;
		this.address1 = leadMaster.address1;
		this.address2 = leadMaster.address2;
		this.city = leadMaster.city;
		this.state = leadMaster.state;
		this.country = leadMaster.country;
		this.pincode = leadMaster.pincode;
		this.area = leadMaster.area;
		this.outstanding = leadMaster.outstanding;
		this.oldpassword1 = leadMaster.oldpassword1;
		this.newpassword = leadMaster.newpassword;
		this.oldpassword2 = leadMaster.oldpassword2;
		this.oldpassword3 = leadMaster.oldpassword3;
		this.selfcarepwd = leadMaster.selfcarepwd;
		this.last_password_change = leadMaster.last_password_change;
		this.lastpasswordchangestring = leadMaster.lastpasswordchangestring;
		this.planMappingList = leadMaster.planMappingList;
		this.addressList = leadMaster.addressList;
		this.radiusprofileIds = leadMaster.radiusprofileIds;
		this.debitDocList = leadMaster.debitDocList;
		this.creditDocuments = leadMaster.creditDocuments;
		this.overChargeList = leadMaster.overChargeList;
		this.custDocList = leadMaster.custDocList;
		this.indiChargeList = leadMaster.indiChargeList;
		this.custLeger = leadMaster.custLeger;
		this.custMacMapppingList = leadMaster.custMacMapppingList;
		this.ledgerDtls = leadMaster.ledgerDtls;
		this.paymentDetails = leadMaster.paymentDetails;
		this.flashMsg = leadMaster.flashMsg;
		this.mactelflag = leadMaster.mactelflag;
		this.mobile = leadMaster.mobile;
		this.countryCode = leadMaster.countryCode;
		this.cafno = leadMaster.cafno;
		this.altmobile = leadMaster.altmobile;
		this.altphone = leadMaster.altphone;
		this.altemail = leadMaster.altemail;
		this.fax = leadMaster.fax;
		this.resellerid = leadMaster.resellerid;
		this.salesrepid = leadMaster.salesrepid;
		this.voicesrvtype = leadMaster.voicesrvtype;
		this.voiceprovision = leadMaster.voiceprovision;
		this.didno = leadMaster.didno;
		this.childdidno = leadMaster.childdidno;
		this.intercomno = leadMaster.intercomno;
		this.intercomgrp = leadMaster.intercomgrp;
		this.onlinerenewalflag = leadMaster.onlinerenewalflag;
		this.voipenableflag =leadMaster.voipenableflag;
		this.custcategory = leadMaster.custcategory;
		this.walletbalance = leadMaster.walletbalance;
		this.networktype = leadMaster.networktype;
		this.defaultpoolid = leadMaster.defaultpoolid;
		this.serviceareaid =leadMaster.serviceareaid;
		this.networkdevicesid = leadMaster.networkdevicesid;
		this.oltslotid = leadMaster.oltslotid;
		this.oltportid = leadMaster.oltportid;
		this.strconntype = leadMaster.strconntype;
		this.stroltname = leadMaster.stroltname;
		this.strslotname = leadMaster.strslotname;
		this.strportname = leadMaster.strportname;
		this.OldBNGRouterinterface = leadMaster.OldBNGRouterinterface;
		this.OldVSIName = leadMaster.OldVSIName;
		this.ASNNumber = leadMaster.ASNNumber;
		this.BNGRouterinterface = leadMaster.BNGRouterinterface;
		this.BNGRoutername = leadMaster.BNGRoutername;
		this.IPPrefixes = leadMaster.IPPrefixes;
		this.IPV6Prefixes = leadMaster.IPV6Prefixes;
		this.LANIP = leadMaster.LANIP;
		this.LANIPV6 = leadMaster.LANIPV6;
		this.LLAccountid = leadMaster.LLAccountid;
		this.LLConnectiontype = leadMaster.LLConnectiontype;
		this.LLExpirydate = leadMaster.LLExpirydate;
		this.LLMedium = leadMaster.LLMedium;
		this.LLServiceid = leadMaster.LLServiceid;
		this.MACADDRESS = leadMaster.MACADDRESS;
		this.Peerip = leadMaster.Peerip;
		this.POOLIP = leadMaster.POOLIP;
		this.QOS = leadMaster.QOS;
		this.RDExport = leadMaster.RDExport;
		this.RDValue = leadMaster.RDValue;
		this.VLANID = leadMaster.VLANID;
		this.VRFName = leadMaster.VRFName;
		this.VSIID = leadMaster.VSIID;
		this.VSIName = leadMaster.VSIName;
		this.WANIP = leadMaster.WANIP;
		this.WANIPV6 = leadMaster.WANIPV6;
		this.billentityname = leadMaster.billentityname;
		this.addparam1 = leadMaster.addparam1;
		this.addparam2 = leadMaster.addparam2;
		this.addparam3 = leadMaster.addparam3;
		this.addparam4 = leadMaster.addparam4;
		this.purchaseorder = leadMaster.purchaseorder;
		this.remarks = leadMaster.remarks;
		this.allowedIPAddress = leadMaster.allowedIPAddress;
		this.OldWANIP = leadMaster.OldWANIP;
		this.OldLLAccountid = leadMaster.OldLLAccountid;
		this.firstActivationDate = leadMaster.firstActivationDate;
		this.isDeleted = leadMaster.isDeleted;
		this.createDateString = leadMaster.createDateString;
		this.updateDateString = leadMaster.updateDateString;
		this.latitude = leadMaster.latitude;
		this.longitude = leadMaster.longitude;
		this.url = leadMaster.url;
		this.gisCode = leadMaster.gisCode;
		this.salesremark = leadMaster.salesremark;
		this.servicetype = leadMaster.servicetype;
		this.isCustCaf = leadMaster.isCustCaf;
		this.previousCafApprover = leadMaster.previousCafApprover;
		this.nextCafApprover = leadMaster.nextCafApprover;
		this.serviceareaName = leadMaster.serviceareaName;
		this.cafApproveStatus = leadMaster.cafApproveStatus;
		this.mvnoId = leadMaster.mvnoId;
		this.tinNo = leadMaster.tinNo;
		this.passportNo = leadMaster.passportNo;
		this.dunningCategory = leadMaster.dunningCategory;
		this.plangroupid = leadMaster.plangroupid;
		this.parentCustomerId = leadMaster.parentCustomerId;
		this.parentCustomerName = leadMaster.parentCustomerName;
		this.invoiceType = leadMaster.invoiceType;
		this.calendarType = leadMaster.calendarType;
		this.discount = leadMaster.discount;
		this.buId = leadMaster.buId;
		this.leadSource = leadMaster.leadSource;
		this.leadSubSource = leadMaster.leadSubSource;
		this.rejectReason = leadMaster.rejectReason;
		this.rejectSubReason = leadMaster.rejectSubReason;
		this.reasonToChangeServiceProvider = leadMaster.reasonToChangeServiceProvider;
		this.previousVendor = leadMaster.previousVendor;
		this.servicerType = leadMaster.servicerType;
		this.leadStatus = leadMaster.leadStatus;
		this.createdOn = leadMaster.createdOn;
		this.lastModifiedOn = leadMaster.lastModifiedOn;
		this.createdBy = leadMaster.createdBy;
		this.createdByName = leadMaster.createdByName;
		this.lastModifiedBy = leadMaster.lastModifiedBy;
		this.rejectedOn = leadMaster.rejectedOn;
		this.rejectedBy = leadMaster.rejectedBy;
		this.approvedOn = leadMaster.approvedOn;
		this.approvedBy = leadMaster.approvedBy;
		this.reOpenOn = leadMaster.reOpenOn;
		this.reOpenBy = leadMaster.reOpenBy;
		this.altmobile1 = leadMaster.altmobile1;
		this.altmobile2 = leadMaster.altmobile2;
		this.altmobile3 = leadMaster.altmobile3;
		this.altmobile4 = leadMaster.altmobile4;
		this.nextApproveStaffId = leadMaster.nextApproveStaffId;
		this.nextTeamMappingId = leadMaster.nextTeamMappingId;
		this.leadCategory = leadMaster.leadCategory;
		this.heardAboutSubisuFrom = leadMaster.heardAboutSubisuFrom;
		this.partner = leadMaster.partner;
		this.customers = leadMaster.customers;
		this.staffUser = leadMaster.staffUser;
		this.leadBranch = leadMaster.leadBranch;
		this.leadAgentId = leadMaster.leadAgentId;
		this.serviceArea = leadMaster.serviceArea;
		this.feasibility = leadMaster.feasibility;
		this.feasibilityRemark = leadMaster.feasibilityRemark;
		this.feasibilityRequired = leadMaster.feasibilityRequired;
		this.rejectLeadTime = leadMaster.rejectLeadTime;
		this.leadType = leadMaster.leadType;
		this.existingCustomerId = leadMaster.existingCustomerId;
		this.noLeadFollowupSendNotification = leadMaster.noLeadFollowupSendNotification;
		this.finalApproved = leadMaster.finalApproved;
		this.planType = leadMaster.planType;
		this.leadNo = leadMaster.leadNo;
		this.presentCheckForPayment = leadMaster.presentCheckForPayment;
		this.presentCheckForPermanent = leadMaster.presentCheckForPermanent;
		this.leadCustomerCategory = leadMaster.leadCustomerCategory;
		this.leadCustomerType = leadMaster.leadCustomerType;
		this.leadCustomerSubType = leadMaster.leadCustomerSubType;
		this.leadCustomerSector = leadMaster.leadCustomerSector;
		this.leadCustomerSubSector = leadMaster.leadCustomerSubSector;
		this.valleyType = leadMaster.valleyType;
		this.insideValley = leadMaster.insideValley;
		this.outsideValley = leadMaster.outsideValley;
		this.competitorDuration = leadMaster.competitorDuration;
		this.expiry = leadMaster.expiry;
		this.amount = leadMaster.amount;
		this.feedback = leadMaster.feedback;
		this.gender = leadMaster.gender;
		this.branch = leadMaster.branch;
		this.popManagement = leadMaster.popManagement;
		this.dateOfBirth = leadMaster.dateOfBirth;
		this.secondaryContactDetails = leadMaster.secondaryContactDetails;
		this.secondaryPhone = leadMaster.secondaryPhone;
		this.secondaryEmail = leadMaster.secondaryEmail;
		this.previousAmount = leadMaster.previousAmount;
		this.previousMonth = leadMaster.previousMonth;
		this.leadOriginType = leadMaster.leadOriginType;
		this.requireServiceType = leadMaster.requireServiceType;
		this.landlineNumber = leadMaster.landlineNumber;
		this.pcontactphno = leadMaster.pcontactphno;
		this.scontactname = leadMaster.scontactname;
		this.businessverticals = leadMaster.businessverticals;
		this.subbusinessverticals = leadMaster.subbusinessverticals;
		this.connectiontype = leadMaster.connectiontype;
		this.linktype = leadMaster.linktype;
		this.circuitarea = leadMaster.circuitarea;
		this.closuredate = leadMaster.closuredate;
		this.circuitid = leadMaster.circuitid;
		this.circuitname = leadMaster.circuitname;
		this.leadvariety = leadMaster.leadvariety;
		this.billableCustomerId = leadMaster.billableCustomerId;
		this.discountType = leadMaster.discountType;
		this.discountExpiryDate = leadMaster.discountExpiryDate;
		this.cafConvertedDate = leadMaster.cafConvertedDate;
		this.cafConvertedStaffId = leadMaster.cafConvertedStaffId;
		this.parentExperience = leadMaster.parentExperience;
		this.locationlevel1 = leadMaster.locationlevel1;
		this.locationlevel2 = leadMaster.locationlevel2;
		this.locationlevel3 = leadMaster.locationlevel3;
		this.locationlevel4 = leadMaster.locationlevel4;
		this.skypeid_imid = leadMaster.skypeid_imid;
		this.associatedLevel = leadMaster.associatedLevel;
		this.organisation = leadMaster.organisation;
		this.nation =leadMaster. nation;
		this.isLeadQuickInv = leadMaster.isLeadQuickInv;
		this.leadIdentity = leadMaster.leadIdentity;
		this.leadDepartment = leadMaster.leadDepartment;
		this.designation = leadMaster.designation;
	}

	public LeadMaster(LeadMasterPojo leadMasterPojo, Long mvnoId, Long buId, Long staffId) {
		this.id = leadMasterPojo.getId();
		this.username = leadMasterPojo.getUsername();
		this.password = leadMasterPojo.getPassword();
		this.firstname = leadMasterPojo.getFirstname();
		this.lastname = leadMasterPojo.getLastname();
		this.email = leadMasterPojo.getEmail();
		this.title = leadMasterPojo.getTitle();
		this.custname = leadMasterPojo.getCustname();
		this.contactperson = leadMasterPojo.getContactperson();
		this.pan = leadMasterPojo.getPan();
		this.gst = leadMasterPojo.getGst();
		this.aadhar = leadMasterPojo.getAadhar();
		this.status = leadMasterPojo.getStatus();
		this.failcount = leadMasterPojo.getFailcount();
		this.acctno = leadMasterPojo.getAcctno();
		this.custtype = leadMasterPojo.getCusttype();
		this.phone = leadMasterPojo.getPhone();
		this.billday = leadMasterPojo.getBillday();
		this.partnerid = leadMasterPojo.getPartnerid();
		this.onuid = leadMasterPojo.getOnuid();
		this.nextBillDate = leadMasterPojo.getNextBillDate();
		this.lastBillDate = leadMasterPojo.getLastBillDate();
		this.addresstype = leadMasterPojo.getAddresstype();
		this.address1 = leadMasterPojo.getAddress1();
		this.address2 = leadMasterPojo.getAddress2();
		this.city = leadMasterPojo.getCity();
		this.state = leadMasterPojo.getState();
		this.currency = leadMasterPojo.getCurrency();
		/*if(leadMasterPojo.getAddressList().get(0).getStateId()!=null){
			this.state = leadMasterPojo.getAddressList().get(0).getStateId();
		}*/
		this.country = leadMasterPojo.getCountry();
		this.pincode = leadMasterPojo.getPincode();
		this.area = leadMasterPojo.getArea();
		this.outstanding = leadMasterPojo.getOutstanding();
		this.oldpassword1 = leadMasterPojo.getOldpassword1();
		this.oldpassword2 = leadMasterPojo.getOldpassword2();
		this.oldpassword3 = leadMasterPojo.getOldpassword3();
		this.selfcarepwd = leadMasterPojo.getSelfcarepwd();
		this.last_password_change = leadMasterPojo.getLast_password_change();
		this.lastpasswordchangestring = leadMasterPojo.getLastpasswordchangestring();
		this.altmobile1 = leadMasterPojo.getAltmobile1();
		this.altmobile2 = leadMasterPojo.getAltmobile2();
		this.altmobile3 = leadMasterPojo.getAltmobile3();
		this.altmobile4 = leadMasterPojo.getAltmobile4();
		if (leadMasterPojo.getPlanMappingList() != null && leadMasterPojo.getPlanMappingList().size() > 0) {
			List<CustPlanMappping> custPlanMapppingList = new ArrayList<CustPlanMappping>();
			for (CustPlanMapppingPojo custPlanMapppingPojo : leadMasterPojo.getPlanMappingList()) {
				custPlanMapppingList.add(new CustPlanMappping(custPlanMapppingPojo));
			}
			this.planMappingList = custPlanMapppingList;
		}
		if (leadMasterPojo.getAddressList() != null && leadMasterPojo.getAddressList().size() > 0) {
			List<CustomerAddress> customerAddressList = new ArrayList<CustomerAddress>();
			for (CustomerAddressPojo customerAddressPojo : leadMasterPojo.getAddressList()) {
				customerAddressList.add(new CustomerAddress(customerAddressPojo));
			}
			this.addressList = customerAddressList;
		}
		if (leadMasterPojo.getRadiusprofileIds() != null && leadMasterPojo.getRadiusprofileIds().size() > 0) {
			List<String> Ids = leadMasterPojo.getRadiusprofileIds().stream().map(e -> String.valueOf(e))
					.collect(Collectors.toList());
			this.radiusprofileIds = String.join(",", Ids);
		}
		if (leadMasterPojo.getDebitDocList() != null && leadMasterPojo.getDebitDocList().size() > 0) {
			List<DebitDocument> debitDocumentList = new ArrayList<DebitDocument>();
			for (DebitDocumentPojo debitDocumentPojo : leadMasterPojo.getDebitDocList()) {
				debitDocumentList.add(new DebitDocument(debitDocumentPojo));
			}
			this.debitDocList = debitDocumentList;
		}
		if (leadMasterPojo.getCreditDocuments() != null && leadMasterPojo.getCreditDocuments().size() > 0) {
			List<CreditDocument> creditDocumentList = new ArrayList<CreditDocument>();
			for (CreditDocumentPojo creditDocumentPojo : leadMasterPojo.getCreditDocuments()) {
				creditDocumentList.add(new CreditDocument(creditDocumentPojo));
			}
			this.creditDocuments = creditDocumentList;
		}
		if (leadMasterPojo.getOverChargeList() != null && leadMasterPojo.getOverChargeList().size() > 0) {
			List<CustChargeDetails> custChargeDetailsList = new ArrayList<CustChargeDetails>();
			for (CustChargeDetailsPojo custChargeDetailsPojo : leadMasterPojo.getOverChargeList()) {
				custChargeDetailsList.add(new CustChargeDetails(custChargeDetailsPojo));
			}
			this.overChargeList = custChargeDetailsList;
		}
		if (leadMasterPojo.getCustDocList() != null && leadMasterPojo.getCustDocList().size() > 0) {
			List<CustomerDocDetails> customerDocDetailsList = new ArrayList<CustomerDocDetails>();
			for (CustomerDocDetailsPojo customerDocDetailsPojo : leadMasterPojo.getCustDocList()) {
				customerDocDetailsList.add(new CustomerDocDetails(customerDocDetailsPojo));
			}
			this.custDocList = customerDocDetailsList;
		}
		if (leadMasterPojo.getIndiChargeList() != null && leadMasterPojo.getIndiChargeList().size() > 0) {
			List<CustChargeDetails> custChargeDetailsList = new ArrayList<CustChargeDetails>();
			for (CustChargeDetailsPojo custChargeDetailsPojo : leadMasterPojo.getIndiChargeList()) {
				custChargeDetailsList.add(new CustChargeDetails(custChargeDetailsPojo));
			}
			this.indiChargeList = custChargeDetailsList;
		}
		if (leadMasterPojo.getCustMacMapppingList() != null && leadMasterPojo.getCustMacMapppingList().size() > 0) {
			List<CustMacMappping> custMacMapppingList = new ArrayList<CustMacMappping>();
			for (CustMacMapppingPojo custMacMapppingPojo : leadMasterPojo.getCustMacMapppingList()) {
				custMacMapppingList.add(new CustMacMappping(custMacMapppingPojo));
			}
			this.custMacMapppingList = custMacMapppingList;
		}
		if (leadMasterPojo.getLedgerDtls() != null && leadMasterPojo.getLedgerDtls().size() > 0) {
			List<CustLedgerDtls> custLedgerDtlsList = new ArrayList<CustLedgerDtls>();
			for (CustLedgerDtlsPojo custLedgerDtlsPojo : leadMasterPojo.getLedgerDtls()) {
				custLedgerDtlsList.add(new CustLedgerDtls(custLedgerDtlsPojo));
			}
			this.ledgerDtls = custLedgerDtlsList;
		}
		if (leadMasterPojo.getCustLeger() != null) {
			this.custLeger = new CustomerLedger(leadMasterPojo.getCustLeger());
		}
		if (leadMasterPojo.getPaymentDetails() != null) {
			this.paymentDetails = new RecordPayment(leadMasterPojo.getPaymentDetails());
		}
		this.flashMsg = leadMasterPojo.getFlashMsg();
		this.mactelflag = leadMasterPojo.getMactelflag();
		this.mobile = leadMasterPojo.getMobile();
		this.countryCode = leadMasterPojo.getCountryCode();
		this.cafno = leadMasterPojo.getCafno();
		this.altmobile = leadMasterPojo.getAltmobile();
		this.altphone = leadMasterPojo.getAltphone();
		this.altemail = leadMasterPojo.getAltemail();
		this.fax = leadMasterPojo.getFax();
		this.resellerid = leadMasterPojo.getResellerid();
		this.salesrepid = leadMasterPojo.getSalesrepid();
		this.voicesrvtype = leadMasterPojo.getVoicesrvtype();
		this.voiceprovision = leadMasterPojo.getVoiceprovision();
		this.childdidno = leadMasterPojo.getChilddidno();
		this.didno = leadMasterPojo.getDidno();
		this.intercomno = leadMasterPojo.getIntercomno();
		this.intercomgrp = leadMasterPojo.getIntercomgrp();
		this.onlinerenewalflag = leadMasterPojo.getOnlinerenewalflag();
		this.voipenableflag = leadMasterPojo.getVoipenableflag();
		this.custcategory = leadMasterPojo.getCustcategory();
		this.walletbalance = leadMasterPojo.getWalletbalance();
		this.networktype = leadMasterPojo.getNetworktype();
		this.defaultpoolid = leadMasterPojo.getDefaultpoolid();
		this.serviceareaid = leadMasterPojo.getServiceareaid();
		this.networkdevicesid = leadMasterPojo.getNetworkdevicesid();
		this.oltslotid = leadMasterPojo.getOltslotid();
		this.oltportid = leadMasterPojo.getOltportid();
		this.strconntype = leadMasterPojo.getStrconntype();
		this.stroltname = leadMasterPojo.getStroltname();
		this.strslotname = leadMasterPojo.getStrslotname();
		this.strportname = leadMasterPojo.getStrportname();
		this.OldBNGRouterinterface = leadMasterPojo.getOldBNGRouterinterface();
		this.OldVSIName = leadMasterPojo.getOldVSIName();
		this.ASNNumber = leadMasterPojo.getASNNumber();
		this.BNGRouterinterface = leadMasterPojo.getBNGRouterinterface();
		this.BNGRoutername = leadMasterPojo.getBNGRoutername();
		this.IPPrefixes = leadMasterPojo.getIPPrefixes();
		this.IPV6Prefixes = leadMasterPojo.getIPV6Prefixes();
		this.LANIP = leadMasterPojo.getLANIP();
		this.LANIPV6 = leadMasterPojo.getLANIPV6();
		this.LLAccountid = leadMasterPojo.getLLAccountid();
		this.LLConnectiontype = leadMasterPojo.getLLConnectiontype();
		this.LLExpirydate = leadMasterPojo.getLLExpirydate();
		this.LLMedium = leadMasterPojo.getLLMedium();
		this.LLServiceid = leadMasterPojo.getLLServiceid();
		this.MACADDRESS = leadMasterPojo.getMACADDRESS();
		this.Peerip = leadMasterPojo.getPeerip();
		this.POOLIP = leadMasterPojo.getPOOLIP();
		this.QOS = leadMasterPojo.getQOS();
		this.RDExport = leadMasterPojo.getRDExport();
		this.RDValue = leadMasterPojo.getRDValue();
		this.VLANID = leadMasterPojo.getVLANID();
		this.VRFName = leadMasterPojo.getVRFName();
		this.VSIID = leadMasterPojo.getVSIID();
		this.VSIName = leadMasterPojo.getVSIName();
		this.WANIP = leadMasterPojo.getWANIP();
		this.WANIPV6 = leadMasterPojo.getWANIPV6();
		this.billentityname = leadMasterPojo.getBillentityname();
		this.addparam1 = leadMasterPojo.getAddparam1();
		this.addparam2 = leadMasterPojo.getAddparam2();
		this.addparam3 = leadMasterPojo.getAddparam3();
		this.addparam4 = leadMasterPojo.getAddparam4();
		this.purchaseorder = leadMasterPojo.getPurchaseorder();
		this.remarks = leadMasterPojo.getRemarks();
		this.allowedIPAddress = leadMasterPojo.getAllowedIPAddress();
		this.OldWANIP = leadMasterPojo.getOldWANIP();
		this.OldLLAccountid = leadMasterPojo.getOldLLAccountid();
		this.firstActivationDate = leadMasterPojo.getFirstActivationDate();
		this.isDeleted = leadMasterPojo.isDeleted();
		this.createDateString = leadMasterPojo.getCreateDateString();
		this.updateDateString = leadMasterPojo.getUpdateDateString();
		this.latitude = leadMasterPojo.getLatitude();
		this.longitude = leadMasterPojo.getLongitude();
		this.url = leadMasterPojo.getUrl();
		this.gisCode = leadMasterPojo.getGisCode();
		this.salesremark = leadMasterPojo.getSalesremark();
		this.servicetype = leadMasterPojo.getServicetype();
		this.isCustCaf = leadMasterPojo.getIsCustCaf();
		this.previousCafApprover = leadMasterPojo.getPreviousCafApprover();
		this.nextCafApprover = leadMasterPojo.getNextCafApprover();
		this.serviceareaName = leadMasterPojo.getServiceareaName();
		this.cafApproveStatus = leadMasterPojo.getCafApproveStatus();
		this.tinNo = leadMasterPojo.getTinNo();
		this.passportNo = leadMasterPojo.getPassportNo();
		this.dunningCategory = leadMasterPojo.getDunningCategory();
		this.plangroupid = leadMasterPojo.getPlangroupid();
		this.parentCustomerId = leadMasterPojo.getParentCustomerId();
		this.parentCustomerName = leadMasterPojo.getParentCustomerName();
		this.invoiceType = leadMasterPojo.getInvoiceType();
		this.calendarType = leadMasterPojo.getCalendarType();
		this.discount = leadMasterPojo.getDiscount();
		if (leadMasterPojo.getLeadSourceId() != null) {
			this.leadSource = new LeadSource(leadMasterPojo.getLeadSourceId());
		}
		if (leadMasterPojo.getLeadSubSourceId() != null) {
			this.leadSubSource = new LeadSubSource(leadMasterPojo.getLeadSubSourceId());
		}
		if (leadMasterPojo.getLeadSourceId() != null) {
			this.leadSource = new LeadSource(leadMasterPojo.getLeadSourceId());
		}
		if (leadMasterPojo.getRejectReasonId() != null) {
			this.rejectReason = new RejectReason(leadMasterPojo.getRejectReasonId());
		}
		if (leadMasterPojo.getRejectSubReasonId() != null) {
			this.rejectSubReason = new RejectSubReason(leadMasterPojo.getRejectSubReasonId());
		}
		this.reasonToChangeServiceProvider = leadMasterPojo.getReasonToChangeServiceProvider();
		this.previousVendor = leadMasterPojo.getPreviousVendor();
		this.servicerType = leadMasterPojo.getServicerType();
		this.leadStatus = leadMasterPojo.getLeadStatus();
		if(leadMasterPojo.getMvnoId()!=null){
			this.mvnoId=leadMasterPojo.getMvnoId();
		}else{
			this.mvnoId = mvnoId;
		}
		this.buId = buId;
		this.createdBy = String.valueOf(staffId);
		this.createdByName = leadMasterPojo.getCreatedByName();
		this.leadCategory = leadMasterPojo.getLeadCategory();
		this.heardAboutSubisuFrom = leadMasterPojo.getHeardAboutSubisuFrom();
		if (leadMasterPojo.getLeadPartnerId() != null) {
			this.partner = new Partner(leadMasterPojo.getLeadPartnerId());
		}
		if (leadMasterPojo.getLeadCustomerId() != null) {
			this.customers = new Customers(leadMasterPojo.getLeadCustomerId());
		}
		if (leadMasterPojo.getLeadStaffId() != null) {
			this.staffUser = new StaffUser(leadMasterPojo.getLeadStaffId());
		}
		if (leadMasterPojo.getLeadBranchId() != null) {
			this.leadBranch = new Branch(leadMasterPojo.getLeadBranchId());
		}
		if (leadMasterPojo.getLeadServiceAreaId() != null) {
			this.serviceArea = new ServiceArea(leadMasterPojo.getLeadServiceAreaId());
		}
		this.leadAgentId = leadMasterPojo.getLeadAgentId();
		this.feasibility = leadMasterPojo.getFeasibility();
		this.feasibilityRemark = leadMasterPojo.getFeasibilityRemark();
		this.feasibilityRequired = leadMasterPojo.getFeasibility();
		this.rejectLeadTime = leadMasterPojo.getRejectLeadTime();
		this.leadType = leadMasterPojo.getLeadType();
		this.existingCustomerId = leadMasterPojo.getExistingCustomerId();
		this.finalApproved = leadMasterPojo.isFinalApproved();
		this.planType = leadMasterPojo.getPlanType();
		this.nextApproveStaffId = leadMasterPojo.getNextApproveStaffId();
		this.nextTeamMappingId = leadMasterPojo.getNextTeamMappingId();
		this.leadNo = leadMasterPojo.getLeadNo();
		this.presentCheckForPayment = leadMasterPojo.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMasterPojo.isPresentCheckForPermanent();
		this.leadCustomerCategory = leadMasterPojo.getLeadCustomerCategory();
		this.leadCustomerType = leadMasterPojo.getLeadCustomerType();
		this.leadCustomerSubType = leadMasterPojo.getLeadCustomerSubType();
		this.leadCustomerSector = leadMasterPojo.getLeadCustomerSector();
		this.leadCustomerSubSector = leadMasterPojo.getLeadCustomerSubSector();
		this.valleyType = leadMasterPojo.getValleyType();
		this.insideValley = leadMasterPojo.getInsideValley();
		this.outsideValley = leadMasterPojo.getOutsideValley();
		this.competitorDuration = leadMasterPojo.getCompetitorDuration();
		this.expiry = leadMasterPojo.getExpiry();
		this.amount = leadMasterPojo.getAmount();
		this.feedback = leadMasterPojo.getFeedback();
		this.gender = leadMasterPojo.getGender();
		if (leadMasterPojo.getBranchId() != null) {
			this.branch = new Branch(leadMasterPojo.getBranchId());
		}
		if (leadMasterPojo.getPopManagementId() != null) {
			this.popManagement = new PopManagement(leadMasterPojo.getPopManagementId());
		}
		this.dateOfBirth = leadMasterPojo.getDateOfBirth();
		this.secondaryContactDetails = leadMasterPojo.getSecondaryContactDetails();
		this.secondaryPhone = leadMasterPojo.getSecondaryPhone();
		this.secondaryEmail = leadMasterPojo.getSecondaryEmail();
		this.previousAmount = leadMasterPojo.getPreviousAmount();
		this.previousMonth = leadMasterPojo.getPreviousMonth();
		this.leadOriginType = leadMasterPojo.getLeadOriginType();
		this.requireServiceType = leadMasterPojo.getRequireServiceType();
		this.landlineNumber = leadMasterPojo.getLandlineNumber();
		this.pcontactphno = leadMasterPojo.getPcontactphno();
		this.scontactname = leadMasterPojo.getScontactname();
		this.businessverticals = leadMasterPojo.getBusinessverticals();
		this.subbusinessverticals = leadMasterPojo.getSubbusinessverticals();
		this.connectiontype = leadMasterPojo.getConnectiontype();
		this.linktype = leadMasterPojo.getLinktype();
		this.circuitarea = leadMasterPojo.getCircuitarea();
		this.closuredate = leadMasterPojo.getClosuredate();
		this.circuitid = leadMasterPojo.getCircuitid();
		this.circuitname = leadMasterPojo.getCircuitname();
		this.leadvariety = leadMasterPojo.getLeadvariety();
		this.billableCustomerId = leadMasterPojo.getBillableCustomerId();
		this.discountType = leadMasterPojo.getDiscountType();
		this.discountExpiryDate = leadMasterPojo.getDiscountExpiryDate();
		this.cafConvertedDate = leadMasterPojo.getCafConvertedDate();
		this.cafConvertedStaffId = leadMasterPojo.getCafConvertedStaffId();
		this.locationlevel1 = leadMasterPojo.getLocationlevel1();
		this.locationlevel2 = leadMasterPojo.getLocationlevel2();
		this.locationlevel3 = leadMasterPojo.getLocationlevel3();
		this.locationlevel4 = leadMasterPojo.getLocationlevel4();
		this.organisation = leadMasterPojo.getOrganisation();
		this.nation = leadMasterPojo.getNation();
		this.skypeid_imid = leadMasterPojo.getSkypeid_imid();
		this.associatedLevel = leadMasterPojo.getAssociatedLevel();
		if(leadMasterPojo.getIsLeadQuickInv()!= null)
			this.isLeadQuickInv = leadMasterPojo.getIsLeadQuickInv()==true?1:0;

		this.leadDepartment = leadMasterPojo.getLeadDepartment();
		this.designation = leadMasterPojo.getDesignation();
		this.blockNo = leadMasterPojo.getBlockNo();
	}

	@Override
	public String toString() {
		return "LeadMaster toString Override :" + username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public String getContactperson() {
		return contactperson;
	}

	public void setContactperson(String contactperson) {
		this.contactperson = contactperson;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getGst() {
		return gst;
	}

	public void setGst(String gst) {
		this.gst = gst;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getFailcount() {
		return failcount;
	}

	public void setFailcount(Integer failcount) {
		this.failcount = failcount;
	}

	public String getAcctno() {
		return acctno;
	}

	public void setAcctno(String acctno) {
		this.acctno = acctno;
	}

	public String getCusttype() {
		return custtype;
	}

	public void setCusttype(String custtype) {
		this.custtype = custtype;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getBillday() {
		return billday;
	}

	public void setBillday(Integer billday) {
		this.billday = billday;
	}

	public Integer getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(Integer partnerid) {
		this.partnerid = partnerid;
	}

	public String getOnuid() {
		return onuid;
	}

	public void setOnuid(String onuid) {
		this.onuid = onuid;
	}

	public LocalDate getNextBillDate() {
		return nextBillDate;
	}

	public void setNextBillDate(LocalDate nextBillDate) {
		this.nextBillDate = nextBillDate;
	}

	public LocalDate getLastBillDate() {
		return lastBillDate;
	}

	public void setLastBillDate(LocalDate lastBillDate) {
		this.lastBillDate = lastBillDate;
	}

	public String getAddresstype() {
		return addresstype;
	}

	public void setAddresstype(String addresstype) {
		this.addresstype = addresstype;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Integer getCity() {
		return city;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		this.country = country;
	}

	public Integer getPincode() {
		return pincode;
	}

	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}

	public Double getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(Double outstanding) {
		this.outstanding = outstanding;
	}

	public String getOldpassword1() {
		return oldpassword1;
	}

	public void setOldpassword1(String oldpassword1) {
		this.oldpassword1 = oldpassword1;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getOldpassword2() {
		return oldpassword2;
	}

	public void setOldpassword2(String oldpassword2) {
		this.oldpassword2 = oldpassword2;
	}

	public String getOldpassword3() {
		return oldpassword3;
	}

	public void setOldpassword3(String oldpassword3) {
		this.oldpassword3 = oldpassword3;
	}

	public String getSelfcarepwd() {
		return selfcarepwd;
	}

	public void setSelfcarepwd(String selfcarepwd) {
		this.selfcarepwd = selfcarepwd;
	}

	public LocalDateTime getLast_password_change() {
		return last_password_change;
	}

	public void setLast_password_change(LocalDateTime last_password_change) {
		this.last_password_change = last_password_change;
	}

	public String getLastpasswordchangestring() {
		return lastpasswordchangestring;
	}

	public void setLastpasswordchangestring(String lastpasswordchangestring) {
		this.lastpasswordchangestring = lastpasswordchangestring;
	}

	public List<CustPlanMappping> getPlanMappingList() {
		return planMappingList;
	}

	public void setPlanMappingList(List<CustPlanMappping> planMappingList) {
		this.planMappingList = planMappingList;
	}

	public List<CustomerAddress> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<CustomerAddress> addressList) {
		this.addressList = addressList;
	}

	public String getRadiusprofileIds() {
		return radiusprofileIds;
	}

	public void setRadiusprofileIds(String radiusprofileIds) {
		this.radiusprofileIds = radiusprofileIds;
	}

	public List<DebitDocument> getDebitDocList() {
		return debitDocList;
	}

	public void setDebitDocList(List<DebitDocument> debitDocList) {
		this.debitDocList = debitDocList;
	}

	public List<CreditDocument> getCreditDocuments() {
		return creditDocuments;
	}

	public void setCreditDocuments(List<CreditDocument> creditDocuments) {
		this.creditDocuments = creditDocuments;
	}

	public List<CustChargeDetails> getOverChargeList() {
		return overChargeList;
	}

	public void setOverChargeList(List<CustChargeDetails> overChargeList) {
		this.overChargeList = overChargeList;
	}

	public List<CustomerDocDetails> getCustDocList() {
		return custDocList;
	}

	public void setCustDocList(List<CustomerDocDetails> custDocList) {
		this.custDocList = custDocList;
	}

	public List<CustChargeDetails> getIndiChargeList() {
		return indiChargeList;
	}

	public void setIndiChargeList(List<CustChargeDetails> indiChargeList) {
		this.indiChargeList = indiChargeList;
	}

	public CustomerLedger getCustLeger() {
		return custLeger;
	}

	public void setCustLeger(CustomerLedger custLeger) {
		this.custLeger = custLeger;
	}

	public List<CustMacMappping> getCustMacMapppingList() {
		return custMacMapppingList;
	}

	public void setCustMacMapppingList(List<CustMacMappping> custMacMapppingList) {
		this.custMacMapppingList = custMacMapppingList;
	}

	public List<CustLedgerDtls> getLedgerDtls() {
		return ledgerDtls;
	}

	public void setLedgerDtls(List<CustLedgerDtls> ledgerDtls) {
		this.ledgerDtls = ledgerDtls;
	}

	public RecordPayment getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(RecordPayment paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public String getFlashMsg() {
		return flashMsg;
	}

	public void setFlashMsg(String flashMsg) {
		this.flashMsg = flashMsg;
	}

	public Boolean getMactelflag() {
		return mactelflag;
	}

	public void setMactelflag(Boolean mactelflag) {
		this.mactelflag = mactelflag;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCafno() {
		return cafno;
	}

	public void setCafno(String cafno) {
		this.cafno = cafno;
	}

	public String getAltmobile() {
		return altmobile;
	}

	public void setAltmobile(String altmobile) {
		this.altmobile = altmobile;
	}

	public String getAltphone() {
		return altphone;
	}

	public void setAltphone(String altphone) {
		this.altphone = altphone;
	}

	public String getAltemail() {
		return altemail;
	}

	public void setAltemail(String altemail) {
		this.altemail = altemail;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Integer getResellerid() {
		return resellerid;
	}

	public void setResellerid(Integer resellerid) {
		this.resellerid = resellerid;
	}

	public Integer getSalesrepid() {
		return salesrepid;
	}

	public void setSalesrepid(Integer salesrepid) {
		this.salesrepid = salesrepid;
	}

	public String getVoicesrvtype() {
		return voicesrvtype;
	}

	public void setVoicesrvtype(String voicesrvtype) {
		this.voicesrvtype = voicesrvtype;
	}

	public Boolean getVoiceprovision() {
		return voiceprovision;
	}

	public void setVoiceprovision(Boolean voiceprovision) {
		this.voiceprovision = voiceprovision;
	}

	public String getDidno() {
		return didno;
	}

	public void setDidno(String didno) {
		this.didno = didno;
	}

	public String getChilddidno() {
		return childdidno;
	}

	public void setChilddidno(String childdidno) {
		this.childdidno = childdidno;
	}

	public String getIntercomno() {
		return intercomno;
	}

	public void setIntercomno(String intercomno) {
		this.intercomno = intercomno;
	}

	public String getIntercomgrp() {
		return intercomgrp;
	}

	public void setIntercomgrp(String intercomgrp) {
		this.intercomgrp = intercomgrp;
	}

	public Boolean getOnlinerenewalflag() {
		return onlinerenewalflag;
	}

	public void setOnlinerenewalflag(Boolean onlinerenewalflag) {
		this.onlinerenewalflag = onlinerenewalflag;
	}

	public Boolean getVoipenableflag() {
		return voipenableflag;
	}

	public void setVoipenableflag(Boolean voipenableflag) {
		this.voipenableflag = voipenableflag;
	}

	public String getCustcategory() {
		return custcategory;
	}

	public void setCustcategory(String custcategory) {
		this.custcategory = custcategory;
	}

	public Double getWalletbalance() {
		return walletbalance;
	}

	public void setWalletbalance(Double walletbalance) {
		this.walletbalance = walletbalance;
	}

	public String getNetworktype() {
		return networktype;
	}

	public void setNetworktype(String networktype) {
		this.networktype = networktype;
	}

	public Long getDefaultpoolid() {
		return defaultpoolid;
	}

	public void setDefaultpoolid(Long defaultpoolid) {
		this.defaultpoolid = defaultpoolid;
	}

	public Long getServiceareaid() {
		return serviceareaid;
	}

	public void setServiceareaid(Long serviceareaid) {
		this.serviceareaid = serviceareaid;
	}

	public Long getNetworkdevicesid() {
		return networkdevicesid;
	}

	public void setNetworkdevicesid(Long networkdevicesid) {
		this.networkdevicesid = networkdevicesid;
	}

	public Long getOltslotid() {
		return oltslotid;
	}

	public void setOltslotid(Long oltslotid) {
		this.oltslotid = oltslotid;
	}

	public Long getOltportid() {
		return oltportid;
	}

	public void setOltportid(Long oltportid) {
		this.oltportid = oltportid;
	}

	public String getStrconntype() {
		return strconntype;
	}

	public void setStrconntype(String strconntype) {
		this.strconntype = strconntype;
	}

	public String getStroltname() {
		return stroltname;
	}

	public void setStroltname(String stroltname) {
		this.stroltname = stroltname;
	}

	public String getStrslotname() {
		return strslotname;
	}

	public void setStrslotname(String strslotname) {
		this.strslotname = strslotname;
	}

	public String getStrportname() {
		return strportname;
	}

	public void setStrportname(String strportname) {
		this.strportname = strportname;
	}

	public String getOldBNGRouterinterface() {
		return OldBNGRouterinterface;
	}

	public void setOldBNGRouterinterface(String oldBNGRouterinterface) {
		OldBNGRouterinterface = oldBNGRouterinterface;
	}

	public String getOldVSIName() {
		return OldVSIName;
	}

	public void setOldVSIName(String oldVSIName) {
		OldVSIName = oldVSIName;
	}

	public String getASNNumber() {
		return ASNNumber;
	}

	public void setASNNumber(String aSNNumber) {
		ASNNumber = aSNNumber;
	}

	public String getBNGRouterinterface() {
		return BNGRouterinterface;
	}

	public void setBNGRouterinterface(String bNGRouterinterface) {
		BNGRouterinterface = bNGRouterinterface;
	}

	public String getBNGRoutername() {
		return BNGRoutername;
	}

	public void setBNGRoutername(String bNGRoutername) {
		BNGRoutername = bNGRoutername;
	}

	public String getIPPrefixes() {
		return IPPrefixes;
	}

	public void setIPPrefixes(String iPPrefixes) {
		IPPrefixes = iPPrefixes;
	}

	public String getIPV6Prefixes() {
		return IPV6Prefixes;
	}

	public void setIPV6Prefixes(String iPV6Prefixes) {
		IPV6Prefixes = iPV6Prefixes;
	}

	public String getLANIP() {
		return LANIP;
	}

	public void setLANIP(String lANIP) {
		LANIP = lANIP;
	}

	public String getLANIPV6() {
		return LANIPV6;
	}

	public void setLANIPV6(String lANIPV6) {
		LANIPV6 = lANIPV6;
	}

	public String getLLAccountid() {
		return LLAccountid;
	}

	public void setLLAccountid(String lLAccountid) {
		LLAccountid = lLAccountid;
	}

	public String getLLConnectiontype() {
		return LLConnectiontype;
	}

	public void setLLConnectiontype(String lLConnectiontype) {
		LLConnectiontype = lLConnectiontype;
	}

	public String getLLExpirydate() {
		return LLExpirydate;
	}

	public void setLLExpirydate(String lLExpirydate) {
		LLExpirydate = lLExpirydate;
	}

	public String getLLMedium() {
		return LLMedium;
	}

	public void setLLMedium(String lLMedium) {
		LLMedium = lLMedium;
	}

	public String getLLServiceid() {
		return LLServiceid;
	}

	public void setLLServiceid(String lLServiceid) {
		LLServiceid = lLServiceid;
	}

	public String getMACADDRESS() {
		return MACADDRESS;
	}

	public void setMACADDRESS(String mACADDRESS) {
		MACADDRESS = mACADDRESS;
	}

	public String getPeerip() {
		return Peerip;
	}

	public void setPeerip(String peerip) {
		Peerip = peerip;
	}

	public String getPOOLIP() {
		return POOLIP;
	}

	public void setPOOLIP(String pOOLIP) {
		POOLIP = pOOLIP;
	}

	public String getQOS() {
		return QOS;
	}

	public void setQOS(String qOS) {
		QOS = qOS;
	}

	public String getRDExport() {
		return RDExport;
	}

	public void setRDExport(String rDExport) {
		RDExport = rDExport;
	}

	public String getRDValue() {
		return RDValue;
	}

	public void setRDValue(String rDValue) {
		RDValue = rDValue;
	}

	public String getVLANID() {
		return VLANID;
	}

	public void setVLANID(String vLANID) {
		VLANID = vLANID;
	}

	public String getVRFName() {
		return VRFName;
	}

	public void setVRFName(String vRFName) {
		VRFName = vRFName;
	}

	public String getVSIID() {
		return VSIID;
	}

	public void setVSIID(String vSIID) {
		VSIID = vSIID;
	}

	public String getVSIName() {
		return VSIName;
	}

	public void setVSIName(String vSIName) {
		VSIName = vSIName;
	}

	public String getWANIP() {
		return WANIP;
	}

	public void setWANIP(String wANIP) {
		WANIP = wANIP;
	}

	public String getWANIPV6() {
		return WANIPV6;
	}

	public void setWANIPV6(String wANIPV6) {
		WANIPV6 = wANIPV6;
	}

	public String getBillentityname() {
		return billentityname;
	}

	public void setBillentityname(String billentityname) {
		this.billentityname = billentityname;
	}

	public String getAddparam1() {
		return addparam1;
	}

	public void setAddparam1(String addparam1) {
		this.addparam1 = addparam1;
	}

	public String getAddparam2() {
		return addparam2;
	}

	public void setAddparam2(String addparam2) {
		this.addparam2 = addparam2;
	}

	public String getAddparam3() {
		return addparam3;
	}

	public void setAddparam3(String addparam3) {
		this.addparam3 = addparam3;
	}

	public String getAddparam4() {
		return addparam4;
	}

	public void setAddparam4(String addparam4) {
		this.addparam4 = addparam4;
	}

	public String getPurchaseorder() {
		return purchaseorder;
	}

	public void setPurchaseorder(String purchaseorder) {
		this.purchaseorder = purchaseorder;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAllowedIPAddress() {
		return allowedIPAddress;
	}

	public void setAllowedIPAddress(String allowedIPAddress) {
		this.allowedIPAddress = allowedIPAddress;
	}

	public String getOldWANIP() {
		return OldWANIP;
	}

	public void setOldWANIP(String oldWANIP) {
		OldWANIP = oldWANIP;
	}

	public String getOldLLAccountid() {
		return OldLLAccountid;
	}

	public void setOldLLAccountid(String oldLLAccountid) {
		OldLLAccountid = oldLLAccountid;
	}

	public LocalDateTime getFirstActivationDate() {
		return firstActivationDate;
	}

	public void setFirstActivationDate(LocalDateTime firstActivationDate) {
		this.firstActivationDate = firstActivationDate;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

	public String getUpdateDateString() {
		return updateDateString;
	}

	public void setUpdateDateString(String updateDateString) {
		this.updateDateString = updateDateString;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getGisCode() {
		return gisCode;
	}

	public void setGisCode(String gisCode) {
		this.gisCode = gisCode;
	}

	public String getSalesremark() {
		return salesremark;
	}

	public void setSalesremark(String salesremark) {
		this.salesremark = salesremark;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}

	public String getIsCustCaf() {
		return isCustCaf;
	}

	public void setIsCustCaf(String isCustCaf) {
		this.isCustCaf = isCustCaf;
	}

	public Integer getPreviousCafApprover() {
		return previousCafApprover;
	}

	public void setPreviousCafApprover(Integer previousCafApprover) {
		this.previousCafApprover = previousCafApprover;
	}

	public Integer getNextCafApprover() {
		return nextCafApprover;
	}

	public void setNextCafApprover(Integer nextCafApprover) {
		this.nextCafApprover = nextCafApprover;
	}

	public String getServiceareaName() {
		return serviceareaName;
	}

	public void setServiceareaName(String serviceareaName) {
		this.serviceareaName = serviceareaName;
	}

	public String getCafApproveStatus() {
		return cafApproveStatus;
	}

	public void setCafApproveStatus(String cafApproveStatus) {
		this.cafApproveStatus = cafApproveStatus;
	}

	public Long getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Long mvnoId) {
		this.mvnoId = mvnoId;
	}

	public String getTinNo() {
		return tinNo;
	}

	public void setTinNo(String tinNo) {
		this.tinNo = tinNo;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getDunningCategory() {
		return dunningCategory;
	}

	public void setDunningCategory(String dunningCategory) {
		this.dunningCategory = dunningCategory;
	}

	public Integer getPlangroupid() {
		return plangroupid;
	}

	public void setPlangroupid(Integer plangroupid) {
		this.plangroupid = plangroupid;
	}

	public Integer getParentCustomerId() {
		return parentCustomerId;
	}

	public void setParentCustomerId(Integer parentCustomerId) {
		this.parentCustomerId = parentCustomerId;
	}

	public String getParentCustomerName() {
		return parentCustomerName;
	}

	public void setParentCustomerName(String parentCustomerName) {
		this.parentCustomerName = parentCustomerName;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getCalendarType() {
		return calendarType;
	}

	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Long getBuId() {
		return buId;
	}

	public void setBuId(Long buId) {
		this.buId = buId;
	}

	public LeadSource getLeadSource() {
		return leadSource;
	}

	public void setLeadSource(LeadSource leadSource) {
		this.leadSource = leadSource;
	}

	public LeadSubSource getLeadSubSource() {
		return leadSubSource;
	}

	public void setLeadSubSource(LeadSubSource leadSubSource) {
		this.leadSubSource = leadSubSource;
	}

	public String getReasonToChangeServiceProvider() {
		return reasonToChangeServiceProvider;
	}

	public void setReasonToChangeServiceProvider(String reasonToChangeServiceProvider) {
		this.reasonToChangeServiceProvider = reasonToChangeServiceProvider;
	}

	public String getPreviousVendor() {
		return previousVendor;
	}

	public void setPreviousVendor(String previousVendor) {
		this.previousVendor = previousVendor;
	}

	public String getServicerType() {
		return servicerType;
	}

	public void setServicerType(String servicerType) {
		this.servicerType = servicerType;
	}

	public String getLeadStatus() {
		return leadStatus;
	}

	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(LocalDateTime lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public LocalDateTime getRejectedOn() {
		return rejectedOn;
	}

	public void setRejectedOn(LocalDateTime rejectedOn) {
		this.rejectedOn = rejectedOn;
	}

	public String getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(String rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public LocalDateTime getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(LocalDateTime approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public LocalDateTime getReOpenOn() {
		return reOpenOn;
	}

	public void setReOpenOn(LocalDateTime reOpenOn) {
		this.reOpenOn = reOpenOn;
	}

	public String getReOpenBy() {
		return reOpenBy;
	}

	public void setReOpenBy(String reOpenBy) {
		this.reOpenBy = reOpenBy;
	}

	public RejectReason getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(RejectReason rejectReason) {
		this.rejectReason = rejectReason;
	}

	public RejectSubReason getRejectSubReason() {
		return rejectSubReason;
	}

	public void setRejectSubReason(RejectSubReason rejectSubReason) {
		this.rejectSubReason = rejectSubReason;
	}

	public String getLeadCategory() {
		return leadCategory;
	}

	public void setLeadCategory(String leadCategory) {
		this.leadCategory = leadCategory;
	}

	public String getHeardAboutSubisuFrom() {
		return heardAboutSubisuFrom;
	}

	public void setHeardAboutSubisuFrom(String heardAboutSubisuFrom) {
		this.heardAboutSubisuFrom = heardAboutSubisuFrom;
	}

	public Long getLeadAgentId() {
		return leadAgentId;
	}

	public void setLeadAgentId(Long leadAgentId) {
		this.leadAgentId = leadAgentId;
	}

	public String getFeasibility() {
		return feasibility;
	}

	public void setFeasibility(String feasibility) {
		this.feasibility = feasibility;
	}

	public String getFeasibilityRemark() {
		return feasibilityRemark;
	}

	public void setFeasibilityRemark(String feasibilityRemark) {
		this.feasibilityRemark = feasibilityRemark;
	}

	public String getFeasibilityRequired() {
		return feasibilityRequired;
	}

	public void setFeasibilityRequired(String feasibilityRequired) {
		this.feasibilityRequired = feasibilityRequired;
	}

	public LocalDateTime getRejectLeadTime() {
		return rejectLeadTime;
	}

	public void setRejectLeadTime(LocalDateTime rejectLeadTime) {
		this.rejectLeadTime = rejectLeadTime;
	}

	public String getLeadType() {
		return leadType;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public Long getExistingCustomerId() {
		return existingCustomerId;
	}

	public void setExistingCustomerId(Long existingCustomerId) {
		this.existingCustomerId = existingCustomerId;
	}

	public boolean isNoLeadFollowupSendNotification() {
		return noLeadFollowupSendNotification;
	}

	public void setNoLeadFollowupSendNotification(boolean noLeadFollowupSendNotification) {
		this.noLeadFollowupSendNotification = noLeadFollowupSendNotification;
	}

	public boolean isFinalApproved() {
		return finalApproved;
	}

	public void setFinalApproved(boolean finalApproved) {
		this.finalApproved = finalApproved;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public Customers getCustomers() {
		return customers;
	}

	public void setCustomers(Customers customers) {
		this.customers = customers;
	}

	public StaffUser getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(StaffUser staffUser) {
		this.staffUser = staffUser;
	}

	public Branch getLeadBranch() {
		return leadBranch;
	}

	public void setLeadBranch(Branch leadBranch) {
		this.leadBranch = leadBranch;
	}

	public ServiceArea getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(ServiceArea serviceArea) {
		this.serviceArea = serviceArea;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getLeadNo() {
		return leadNo;
	}

	public void setLeadNo(String leadNo) {
		this.leadNo = leadNo;
	}

	public boolean isPresentCheckForPayment() {
		return presentCheckForPayment;
	}

	public void setPresentCheckForPayment(boolean presentCheckForPayment) {
		this.presentCheckForPayment = presentCheckForPayment;
	}

	public boolean isPresentCheckForPermanent() {
		return presentCheckForPermanent;
	}

	public void setPresentCheckForPermanent(boolean presentCheckForPermanent) {
		this.presentCheckForPermanent = presentCheckForPermanent;
	}

	public String getLeadCustomerCategory() {
		return leadCustomerCategory;
	}

	public void setLeadCustomerCategory(String leadCustomerCategory) {
		this.leadCustomerCategory = leadCustomerCategory;
	}

	public String getLeadCustomerType() {
		return leadCustomerType;
	}

	public void setLeadCustomerType(String leadCustomerType) {
		this.leadCustomerType = leadCustomerType;
	}

	public String getLeadCustomerSubType() {
		return leadCustomerSubType;
	}

	public void setLeadCustomerSubType(String leadCustomerSubType) {
		this.leadCustomerSubType = leadCustomerSubType;
	}

	public String getLeadCustomerSector() {
		return leadCustomerSector;
	}

	public void setLeadCustomerSector(String leadCustomerSector) {
		this.leadCustomerSector = leadCustomerSector;
	}

	public String getLeadCustomerSubSector() {
		return leadCustomerSubSector;
	}

	public void setLeadCustomerSubSector(String leadCustomerSubSector) {
		this.leadCustomerSubSector = leadCustomerSubSector;
	}

	public String getValleyType() {
		return valleyType;
	}

	public void setValleyType(String valleyType) {
		this.valleyType = valleyType;
	}

	public String getInsideValley() {
		return insideValley;
	}

	public void setInsideValley(String insideValley) {
		this.insideValley = insideValley;
	}

	public String getOutsideValley() {
		return outsideValley;
	}

	public void setOutsideValley(String outsideValley) {
		this.outsideValley = outsideValley;
	}

	public String getCompetitorDuration() {
		return competitorDuration;
	}

	public void setCompetitorDuration(String competitorDuration) {
		this.competitorDuration = competitorDuration;
	}

	public LocalDate getExpiry() {
		return expiry;
	}

	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public PopManagement getPopManagement() {
		return popManagement;
	}

	public void setPopManagement(PopManagement popManagement) {
		this.popManagement = popManagement;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getSecondaryContactDetails() {
		return secondaryContactDetails;
	}

	public void setSecondaryContactDetails(String secondaryContactDetails) {
		this.secondaryContactDetails = secondaryContactDetails;
	}

	public String getSecondaryPhone() {
		return secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public Double getPreviousAmount() {
		return previousAmount;
	}

	public void setPreviousAmount(Double previousAmount) {
		this.previousAmount = previousAmount;
	}

	public String getPreviousMonth() {
		return previousMonth;
	}

	public void setPreviousMonth(String previousMonth) {
		this.previousMonth = previousMonth;
	}

	public String getLeadOriginType() {
		return leadOriginType;
	}

	public void setLeadOriginType(String leadOriginType) {
		this.leadOriginType = leadOriginType;
	}

	public String getRequireServiceType() {
		return requireServiceType;
	}

	public void setRequireServiceType(String requireServiceType) {
		this.requireServiceType = requireServiceType;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
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

	public String getCstatus() {
		return cstatus;
	}

	public void setCstatus(String cstatus) {
		this.cstatus = cstatus;
	}

	public Integer getIsLeadQuickInv() {
		return isLeadQuickInv;
	}

	public void setIsLeadQuickInv(Integer isLeadQuickInv) {
		this.isLeadQuickInv = isLeadQuickInv;
	}

	public String getLeadIdentity() {
		return leadIdentity;
	}

	public void setLeadIdentity(String leadIdentity) {
		this.leadIdentity = leadIdentity;
	}

	public String getLeadDepartment() {
		return leadDepartment;
	}

	public void setLeadDepartment(String leadDepartment) {
		this.leadDepartment = leadDepartment;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

//	public String getCurrency() {
//		return currency;
//	}

//	public void setCurrency(String currency) {
//		this.currency = currency;
//	}

	@Override
	public int hashCode() {
		return (id != null) ? id.hashCode() : 0;
	}

}
