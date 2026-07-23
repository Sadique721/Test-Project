package com.savbill.revenuemanagement.core.entity.customers;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblcustomers")
@EntityListeners(AuditableListener.class)
public class Customers extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custid", nullable = false, length = 40)
    private Integer id;

    //@Column(nullable = false, length = 40)
    private String title;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(length = 40)
    private String password;

    @Column(nullable = false, length = 40)
    private String firstname;

    @Column(nullable = false, length = 40)
    private String lastname;

    @Column(name = "custname", nullable = false, length = 40)
    private String custname;

//    @ManyToOne(targetEntity = PlanGroup.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
//    private PlanGroup plangroup;

    @Column(nullable = false, length = 40)
    private String contactperson;

    @Column(length = 40)
    private String cafno;

    @Column(length = 25)
    private String pan;

    @Column(length = 25)
    private String gst;

    @Column(length = 25)
    private String aadhar;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "Boolean default false")
    private Boolean mactelflag = false;

    @Column(nullable = false, length = 10)
    private String mobile;


    private String countryCode;

    @Column(length = 10)
    private String altmobile;

    @Column(length = 50)
    private String altphone;

    @Column(length = 100)
    private String altemail;

    @Column(length = 100)
    private String fax;

    private Integer resellerid;

//    @ManyToOne
//    @JoinColumn(name = "salesrepid")
//    private StaffUser salesrep;

    @Column(nullable = false, length = 75)
    private String voicesrvtype;

    @Column
    private Boolean voiceprovision = false;

    @Column(nullable = false, length = 75)
    private String didno;

    @Column(nullable = false, length = 75)
    private String childdidno;

    private String intercomno;

    @Column(nullable = false, length = 75)
    private String intercomgrp;

    @Column(columnDefinition = "Boolean default false")
    private Boolean onlinerenewalflag = false;

    private Boolean voipenableflag = false;
    private Boolean isorgcust = false;


    @Column(columnDefinition = "Boolean default false", name = "isinvoicestop")
    private Boolean isinvoicestop = false;

    @Column(columnDefinition = "Boolean default false", name = "istrialplan")
    private Boolean istrialplan = false;

    @Column(nullable = false, length = 75)
    private String custcategory;

    private Double walletbalance = 0.0;

    @Column(length = 50)
    private String networktype;

    private Long defaultpoolid;

    @Column(name = "servicearea_id")
    private Long serviceAreaId;

//    @ManyToOne
//    @JoinColumn(name = "servicearea_id")
//    private ServiceArea servicearea;

//    @ManyToOne
//    @JoinColumn(name = "network_device_id")
//    private NetworkDevices networkdevices;

    private Long oltslotid;

    private Long oltportid;

    @Column(length = 75)
    private String strconntype;

    @Column(length = 75)
    private String stroltname;

    @Column(length = 75)
    private String strslotname;

    @Column(length = 75)
    private String strportname;

    @Column(name = "cstatus", nullable = false, length = 100)
    private String status;

    @Column(name = "invoiceoption", nullable = false, length = 100)
    private String invoiceOption;

    @Column(name = "failcount", nullable = false, length = 100)
    private Integer failcount;

    @UpdateTimestamp
    @Column(name = "last_password_change", nullable = false, updatable = true)
    private LocalDateTime last_password_change;

    @Column(name = "accountnumber", length = 100)
    private String acctno;

    @Column(name = "customertype", nullable = false, length = 100)
    private String custtype; //Postpaid,Prepaid

    @Column(name = "phone", nullable = false, length = 100)
    private String phone;

    @UpdateTimestamp
    @Column(name = "laststatuschangedate", nullable = false, length = 100)
    private LocalDateTime lastStatusChangeDate;

    @Column(name = "BILLDAY")
    private Integer billday;


    @Column(name = "partnerid")
    private Integer partner;
//
//    @OneToMany(targetEntity = CustomerPayment.class, cascade = CascadeType.ALL)
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @JoinColumn(name = "custid", referencedColumnName = "custid")
//    private List<CustomerPayment> customerPayments;

    @Column(name = "NEXTBILLDATE", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;

    @Column(name = "LASTBILLDATE", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastBillDate;

//    @ManyToMany
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @JoinTable(name = "tblradiusprocustrel", joinColumns = {@JoinColumn(name = "custid")}
//            , inverseJoinColumns = {@JoinColumn(name = "radiusprofileid")})
//    private List<RadiusProfile> radiusProfiles = new ArrayList<>();

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer")
    @EqualsAndHashCode.Exclude
    private List<CustPlanMappping> planMappingList = new ArrayList<>();

//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.TRUE)
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true)
//    @OrderBy("id")
//    private List<CustomerAddress> addressList = new ArrayList<>();

    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @OrderBy("id desc")
    @Where(clause = "planid is not null")
    private List<DebitDocument> debitDocList = new ArrayList<>();

//    @DiffIgnore
//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
//    @OrderBy("id desc")
//    private List<CreditDocument> creditDocuments = new ArrayList<>();

//    @DiffIgnore
//    @JsonManagedReference
//    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
//    private CustomerLedger custLeger;

//    @DiffIgnore
//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
//    @OrderBy("id desc")
//    private List<CustomerLedgerDtls> ledgerDtls = new ArrayList<>();

    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.MERGE)
    @Where(clause = "chargetype != 'CUSTOMER_DIRECT'") //Chargetype=1 Plan Overrider Charge, 1=Manual Charge
    @OrderBy("id desc")
    private List<CustChargeDetails> overChargeList = new ArrayList<>();

    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "chargetype = 'CUSTOMER_DIRECT'") //Chargetype=1 Plan Overrider Charge, 1=Manual Charge
    @OrderBy("id desc")
    private List<CustChargeDetails> indiChargeList = new ArrayList<>();

//    @DiffIgnore
//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
//    @OrderBy("id desc")
//    private List<CustMacMappping> custMacMapppingList = new ArrayList<>();
//
//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
//    @OrderBy("docId desc")
//    private List<CustomerDocDetails> custDocList = new ArrayList<>();

    @JsonIgnore
    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "parentcustid")
    private Customers parentCustomers;

    @Column(name = "invoice_type")
    private String invoiceType;

    @Column(name = "onuid")
    private String onuid;

    @Transient
    private String addresstype;

    @Transient
    private String address1;

    @Transient
    private String address2;

    @Transient
    private Integer city;

    @Transient
    private Integer state;

    @Transient
    private Integer country;

    @Transient
    private Integer pincode;

    @Transient
    private Integer area;

//    @Transient
//    private String pincode;

    @Transient
    private String command;

    @Column(name = "outstandingbalance", nullable = false, updatable = false)
    private Double outstanding;

    @Transient
    private String newpassword;

    @Transient
    private String OldBNGRouterinterface;

    @Transient
    private String OldVSIName;

    @Column(name = "ASNNumber", columnDefinition = "text")
    private String ASNNumber;

    @Column(name = "BNGRouterinterface", columnDefinition = "text")
    private String BNGRouterinterface;

    @Column(name = "BNGRoutername", columnDefinition = "text")
    private String BNGRoutername;

    @Column(name = "IPPrefixes", columnDefinition = "text")
    private String IPPrefixes;

    @Column(name = "IPV6Prefixes", columnDefinition = "text")
    private String IPV6Prefixes;

    @Column(name = "LANIP", columnDefinition = "text")
    private String LANIP;

    @Column(name = "LANIPV6", columnDefinition = "text")
    private String LANIPV6;

    @Column(name = "LLAccountid", columnDefinition = "text")
    private String LLAccountid;

    @Column(name = "LLConnectiontype", columnDefinition = "text")
    private String LLConnectiontype;

    @Column(name = "LLExpirydate", columnDefinition = "text")
    private String LLExpirydate;

    @Column(name = "LLMedium", columnDefinition = "text")
    private String LLMedium;

    @Column(name = "LLServiceid", columnDefinition = "text")
    private String LLServiceid;

    @Column(name = "MACADDRESS", columnDefinition = "text")
    private String MACADDRESS;

    @Column(name = "Peerip", columnDefinition = "text")
    private String Peerip;

    @Column(name = "POOLIP", columnDefinition = "text")
    private String POOLIP;

    @Column(name = "QOS", columnDefinition = "text")
    private String QOS;

    @Column(name = "RDExport", columnDefinition = "text")
    private String RDExport;

    @Column(name = "RDValue", columnDefinition = "text")
    private String RDValue;

    @Column(name = "VLANID", columnDefinition = "text")
    private String VLANID;

    @Column(name = "VRFName", columnDefinition = "text")
    private String VRFName;

    @Column(name = "VSIID", columnDefinition = "text")
    private String VSIID;

    @Column(name = "VSIName", columnDefinition = "text")
    private String VSIName;

    @Column(name = "WANIP", columnDefinition = "text")
    private String WANIP;

    @Column(name = "WANIPV6", columnDefinition = "text")
    private String WANIPV6;

    @Column(name = "billentityname ", length = 200)
    private String billentityname;

    @Column(name = "addparam1", columnDefinition = "text")
    private String addparam1;

    @Column(name = "addparam2", columnDefinition = "text")
    private String addparam2;

    @Column(name = "addparam3", columnDefinition = "text")
    private String addparam3;

    @Column(name = "addparam4", columnDefinition = "text")
    private String addparam4;


    @Column(name = "purchaseorder", length = 200)
    private String purchaseorder;

    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;

    @Column(nullable = false, length = 40)
    private String oldpassword1;

    @Column(nullable = false, length = 40)
    private String oldpassword2;

    @Column(nullable = false, length = 40)
    private String oldpassword3;

    private String selfcarepwd;

    @Column(name = "allowedipaddrs", length = 100)
    private String allowedIPAddress;

    @Transient
    private Integer parentCustomersId;

    @Transient
    private String OldWANIP;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Transient
    private String OldLLAccountid;

    @Column(name = "firstactivationdate")
    private LocalDateTime firstActivationDate;

    private String otp;

    private LocalDateTime otpvalidate;

    private String latitude;
    private String longitude;
    private String url;
    private String gis_code;

    private String salesremark;
    private String servicetype;

    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMapping;

    @Column(name = "caf_approve_status")
    private String cafApproveStatus;

    @Transient
    private Integer billRunCustPackageRelId;

    @Transient
    private String ConnectionMode;

    @Column(name = "passport_no", length = 25)
    private String passportNo;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "dunning_category", nullable = false, length = 40)
    private String dunningCategory;

    @Transient
    private String fullName;

    private transient String partnerName;

    private transient String serviceAreName;

    @Column(name = "tin_no")
    private String tinNo;

    @Column(name = "calendartype", nullable = false, length = 100, columnDefinition = "varchar(100) default 'English'")
    private String calendarType;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "plan_purchase_type")
    private String planPurchaseType;

    @Column(name = "lead_source")
    private String leadSource;
    @Column(name = "feasibility_required")
    private String feasibilityRequired;

    @Column(name = "branchid")
    private Long branch;

    @Column(name = "VALLEY_TYPE")
    private String valleyType;

    @Column(name = "CUSTOMER_AREA")
    private String customerArea;

    @Column(name = "CUSTOMER_TYPE")
    private String customerType;

    @Column(name = "CUSTOMER_SUB_TYPE")
    private String customerSubType;

    @Column(name = "CUSTOMER_SECTOR")
    private String customerSector;

    @Column(name = "CUSTOMER_SUB_SECTOR")
    private String customerSubSector;

    @Column(name = "lco_id")
    private Integer lcoId;

    @Column(name = "is_from_pwc")
    private Boolean is_from_pwc;

    @Column(name = "popid")
    private long popid;

    @Column(name = "oltid")
    private Long oltid;

    @Column(name = "masterdbid")
    private Long masterdbid;

    @Column(name = "splitterid")
    private Long splitterid;

    @Column(name = "lead_id")
    private Long leadId;

    @Column(name = "lead_no")
    private String leadNo;
    @Column(name = "nas_port")
    private String nasPort;
    @Column(name = "framed_ip")
    private String framedIp;

    @Column(name = "dunning_sub_sector", length = 40)
    private String dunningSubSector;

    @Column(name = "dunning_sub_type", length = 40)
    private String dunningSubType;

    @Column(name = "dunning_type", length = 40)
    private String dunningType;

    @Column(name = "dunning_sector", length = 40)
    private String dunningSector;

    @Column(name = "ezybill_customers_id")
    private String ezyBillCustomersId;

    @Column(name = "ezybill_account_number", length = 100)
    private String ezyBillAccountNumber;

    @Column(name = "feasibility")
    private String feasibility;

    @Column(name = "feasibility_remark")
    private String feasibilityRemark;

    @Column(name = "customerlabel")
    private String custlabel;

    @Column(name = "staffid")
    private Long staffId;

    @Column(name = "framed_ip_bind")
    private String framedIpBind;

    @Column(name = "ip_pool_name_bind")
    private String ipPoolNameBind;

    @Transient
    private String registrationDate;

    @Transient
    private String planName;


//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(targetEntity = LinkAcceptance.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JoinColumn(referencedColumnName = "custid", name = "custid")
//    private List<LinkAcceptance> linkAcceptanceList = new ArrayList<>();
//
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = CustomerServiceMapping.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "custid", name = "custid")
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
//
    @Column(name = "current_assignee_id")
    private Integer currentAssigneeId;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "reject_reason_id")
//    private RejectReason rejectReason;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "reject_sub_reason_id")
//   private RejectSubReason rejectSubReason;

    @Column(name = "reject_caf_time")
    private LocalDateTime rejectCafTime;


    @Column(name = "business_type")
    private String businessType;

    @Column(name = "additionalemail")
    private String additionalemail;

    @Column(name = "salesrepresentative")
    private String salesrepresentative;

    @Column(name = "skypeid_imid")
    private String skypeid_imid;

    @Column(name = "organisation")
    private String organisation;

    @Column(name = "rating")
    private String rating;

    @Column(name = "automaticnotification")
    private String automaticnotification;

    @Column(name = "locationlevel1")
    private String locationlevel1;

    @Column(name = "locationlevel2")
    private String locationlevel2;

    @Column(name = "locationlevel3")
    private String locationlevel3;

    @Column(name = "locationlevel4")
    private String locationlevel4;

    @Column(name = "ponumber")
    private String ponumber;

    @Column(name = "customerbillingid")
    private String customerbillingid;

    @Column(name = "businessunit")
    private String businessunit;

    @Column(name = "subbusinessunit")
    private String subbusinessunit;

    @Column(name = "is_dunning_activate")
    private Boolean isDunningActivate;

    @Column(name = "dunning_activate_for")
    private String dunningActivateFor;

    @Column(name = "last_dunning_date")
    private LocalDateTime lastDunningDate;

    @Column(name = "billable_customer_id")
    private Integer billableCustomerId;

    @Column(name = "is_dunning_enable")
    private Boolean isDunningEnable;

    @Column(name = "dunning_action")
    private String dunningAction;

    @Column(name = "is_notification_enable")
    public Boolean isNotificationEnable;

    @Column(name = "parent_experience")
    private String parentExperience;

    @Column(name = "department")
    private String department;
    @Column(name = "is_using_by_thread")
    private Boolean isUsingByThread;

    @Column(name = "earlybilldays"  )
    private Integer earlyBillDays;

    @Column(name = "earlybillday" )
    private Integer earlyBillDay;

    @Column(name = "earlybilldate")
    private LocalDate earlyBilldate;

    @Column(name="blockno")
    private String blockNo;

    @Column(name="driving_licence")
    private String drivingLicence;

    @Column(name="customer_nid")
    private String customerNid;

    @Column(name="customer_vrn")
    private String customerVrn;

    @Column(name = "renew_plan_limit")
    private Integer renewPlanLimit;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name ="currency")
    private String currency;

    @Column(name ="bill_day_updated")
    private boolean billDayUpdated;

    @Column(name = "previous_billday")
    private Integer previousBillday;

    public String getFullName(Customers customers) {
        return customers.getTitle().concat(" ").concat(customers.getFirstname()).concat(" ").concat(customers.getLastname());
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = CustomerChargeHistory.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "custid", name = "cust_id")
    private List<CustomerChargeHistory> customerChargeHistories = new ArrayList<>();
    @Column(name = "grace_day")
    private Integer graceDay= 0;

    public Customers(Integer id,String username,String custname,String status){
        this.id = id;
        this.username = username;
        this.custname = custname;
        this.status = status;
    }

    public Customers(Integer id,String username,String custname,String status,Integer mvnoId,Long buid){
        this.id = id;
        this.username = username;
        this.custname = custname;
        this.status = status;
        this.mvnoId=mvnoId;
        this.buId=buid;
    }

    public Customers(Integer id,String username,String custname,String status,Integer mvnoId,Long buid,Integer partnerid){
        this.id = id;
        this.username = username;
        this.custname = custname;
        this.status = status;
        this.mvnoId=mvnoId;
        this.buId=buid;
        this.partner = partnerid;
    }

}
