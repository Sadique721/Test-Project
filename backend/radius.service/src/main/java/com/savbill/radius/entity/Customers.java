package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.savbill.radius.aaa.data.CustomerCreateData;
import com.savbill.radius.kafka.CustomMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "tblcustomers")
public class Customers {

    @Id
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

    @Column(nullable = false, length = 40)
    private String contactperson;

    @Column(nullable = false, length = 40)
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

    @Column(columnDefinition = "Boolean default false")
    private Boolean voipenableflag = false;

    @Column(nullable = false, length = 75)
    private String custcategory;

    private Double walletbalance = 0.0;

    @Column(length = 50)
    private String networktype;

    private Long defaultpoolid;

    //    @ManyToOne
//    @JoinColumn(name = "servicearea_id")
//    private ServiceArea servicearea;
    @Column(name = "servicearea_id")
    private Long servicearea;

    //    @ManyToOne
//    @JoinColumn(name = "network_device_id")
//    private NetworkDevices networkdevices;
    @Column(name = "network_device_id")
    private String networkdevices;

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


    //    @ManyToOne
//    @JoinColumn(name = "partnerid")
//    private Partner partner;
    @Column(name = "partnerid")
    private String partner;

//    @OneToMany(targetEntity = CustomerPayment.class, cascade = CascadeType.ALL)
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
//
//    @DiffIgnore
//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cust", orphanRemoval = true, cascade = CascadeType.ALL)
//    @OrderBy("id desc")
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @Transient
    private List<CustPlanMappping> planMappingList = new ArrayList<>();


//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true)
//    @OrderBy("id")
//    private List<CustomerAddress> addressList = new ArrayList<>();
    /*
    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @OrderBy("id desc")
    @Where(clause = "planid is not null")
    private List<DebitDocument> debitDocList = new ArrayList<>();

    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private List<CreditDocument> creditDocuments = new ArrayList<>();

    @DiffIgnore
    @JsonManagedReference
    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private CustomerLedger custLeger;

    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private List<CustomerLedgerDtls> ledgerDtls = new ArrayList<>();

    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
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

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @OrderBy("docId desc")
    private List<CustomerDocDetails> custDocList = new ArrayList<>();
*/

//    @DiffIgnore
//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
//    @OrderBy("id desc")
//    private List<CustMacMappping> custMacMapppingList = new ArrayList<>();

    @JsonIgnore
    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "parentcustid")
    private Customers parentCustomers;

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
    private String parentCustomerName;

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

    @Column(name = "previous_caf_approver")
    private Integer previousCafApprover;

    @Column(name = "next_caf_approver")
    private Integer nextCafApprover;

    @Column(name = "caf_approve_status")
    private String cafApproveStatus;

    @Transient
    private Integer billRunCustPackageRelId;

    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

    @Column(name = "calendartype", nullable = false, length = 100, columnDefinition = "varchar(100) default 'English'")
    private String calendarType;

    @Column(name = "invoice_type", nullable = false, length = 100, columnDefinition = "varchar(100) default 'English'")
    private String invoiceType;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = MacAddressMapping.class)
    @JoinColumn(name = "custid")
    @OrderBy("macAddressId desc")
    private List<MacAddressMapping> macAddressMappingList = new ArrayList<>();

    @Column(name = "nas_port")
    private String nasPort;
    @Column(name = "framed_ip")
    private String framedIp;

    @Column(name = "framed_ip_bind")
    private String framedIpBind;

    @Column(name = "ip_pool_name_bind")
    private String ipPoolNameBind;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "maxconcurrentsession")
    private Integer maxconcurrentsession;

    @Column(name = "expirydate", columnDefinition = "text")
    private String expirydate;

    @Column(name = "gatewayip", columnDefinition = "text")
    private String gatewayip;

    @Column(name = "skipnetconf", columnDefinition = "text")
    private String skipnetconf;

    @Column(name = "rdimport", columnDefinition = "text")
    private String rdimport;

    @Transient
    private String parentQuotaType;

    @Column(name = "ipv4")
    private String ipv4;

    @Column(name = "ipv6")
    private String ipv6;

    @Column(name = "vlan")
    private String vlan;

    @Column(name = "nas_port_id")
    private String nasPortId;

    @Column(name = "nas_ip_address")
    private String nasIpAddress;

    @Column(name = "framed_ipv6_address")
    private String framedIpv6Address;

    @Column(name = "framedroute")
    private String framedroute;

    @Column(name = "delegatedprefix")
    private String delegatedprefix;

    @Column(name = "mac_provision")
    private Boolean mac_provision;

    @Column(name = "mac_auth_enable")
    private Boolean mac_auth_enable;

    @Column(name = "framed_ip_netmask")
    private String framedIPNetmask;

    @Column(name = "framed_ipv6_prefix")
    private String framedIPv6Prefix;

    @Column(name = "primary_dns")
    private String primaryDNS;
    @Column(name = "primary_ipv6_dns")
    private String primaryIPv6DNS;
    @Column(name = "secondary_ipv6_dns")
    private String secondaryIPv6DNS;
    @Column(name = "secondary_dns")
    private String secondaryDNS;
    @Column(name = "macretentionperiod")
    private Integer macRetentionPeriod;
    @Column(name = "macretentionunit")
    private String macRetentionUnit;
    @Column(name = "nextquotaresetdate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextQuotaResetDate;
    @Column(name="blockno")
    private String blockNo;
    @Transient
    private LocalDate nearestMacRetentionDate;
    @Transient
    private LocalDate quotaResetDate;

    @Column (name="service_activation_date")
    private Timestamp serviceActivationDate;

    public Customers() {
    }

    public Customers(Customers customers) {
        this.voicesrvtype = customers.getVoicesrvtype();
        this.didno = customers.getDidno();
        this.childdidno = customers.getChilddidno();
        this.intercomgrp = customers.getIntercomgrp();
        this.intercomno = customers.getIntercomno();
        this.status = customers.getStatus();
        this.mobile = customers.getMobile();
        this.altmobile = customers.getAltmobile();
        this.email = customers.getEmail();
        this.altemail = customers.getAltemail();
        this.phone = customers.getPhone();
        this.altphone = customers.getAltphone();
        this.fax = customers.getFax();
        this.title = customers.getTitle();
        this.firstname = customers.getFirstname();
        this.aadhar = customers.getAadhar();
        this.contactperson = customers.getContactperson();
        this.gst = customers.getGst();
        this.pan = customers.getPan();
        this.networktype = customers.getNetworktype();
        this.defaultpoolid = customers.getDefaultpoolid();
        this.oltportid = customers.getOltportid();
        this.oltslotid = customers.getOltslotid();
        this.onuid = customers.getOnuid();
        this.servicearea = customers.getServicearea();
        this.LLConnectiontype = customers.getLLConnectiontype();
        this.calendarType = customers.getCalendarType();
        this.maxconcurrentsession = customers.getMaxconcurrentsession();
        this.mac_auth_enable = customers.getMac_auth_enable();
        this.macRetentionPeriod = customers.getMacRetentionPeriod();
        this.macRetentionUnit = customers.getMacRetentionUnit();
        //this.billday = customers.getBillday();
    }

//    @Override
//    public String toString() {
//        return "Customer toString Override :" + username;
//    }

    @Transient
    private String fullName;

    @PostLoad
    protected void defaultInitialize() {
        try {
            fullName = "";
            if (null != getTitle() && !getTitle().isEmpty() && getTitle().trim().length() > 0) {
                fullName = getTitle();
            }
            if (null != getFirstname() && !getFirstname().isEmpty() && getFirstname().trim().length() > 0) {
                fullName += " " + getFirstname();
            }
            if (null != getLastname() && !getLastname().isEmpty() && getLastname().trim().length() > 0) {
                fullName += " " + getLastname();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Customers(String firstname, String username, String mobile, String email, String acctno) {

        this.mobile = mobile;
        this.email = email;
        this.firstname = firstname;
        this.username = username;
        this.acctno = acctno;
        //this.billday = customers.getBillday();
    }

    public Customers(Integer id) {
        this.id = id;
    }


    public Customers(CustomMessage customMessage) {
        Map<String, Object> message = customMessage.getCustomerData();
        if (message.get("id") != null) {
            this.id = Integer.parseInt(message.get("id").toString());
        }
        if (message.get("title") != null) {
            this.title = message.get("title").toString();
        }
        if (message.get("username") != null) {
            this.username = message.get("username").toString();
        }
        if (message.get("password") != null) {
            this.password = message.get("password").toString();
        }
        if (message.get("firstname") != null) {
            this.firstname = message.get("firstname").toString();
        }
        if (message.get("lastname") != null) {
            this.lastname = message.get("lastname").toString();
        }
        if (message.get("custname") != null) {
            this.custname = message.get("custname").toString();
        }


        if (message.get("cafno") != null) {
            this.cafno = message.get("cafno").toString();
        }
        if (message.get("email") != null) {
            this.email = message.get("email").toString();
        }
        if (message.get("mactelflag") != null) {
            this.mactelflag = Boolean.parseBoolean(message.get("mactelflag").toString());
        }
        if (message.get("mobile") != null) {
            this.mobile = message.get("mobile").toString();
        }
        if (message.get("voicesrvtype") != null) {
            this.voicesrvtype = message.get("voicesrvtype").toString();
        }
        if (message.get("voiceprovision") != null) {
            this.voiceprovision = Boolean.parseBoolean(message.get("voiceprovision").toString());
        }
        if (message.get("intercomno") != null) {
            this.intercomno = message.get("intercomno").toString();
        }
        if (message.get("intercomgrp") != null) {
            this.intercomgrp = message.get("intercomgrp").toString();
        }
        if (message.get("onlinerenewalflag") != null) {
            this.onlinerenewalflag = Boolean.parseBoolean(message.get("onlinerenewalflag").toString());
        }
        if (message.get("voipenableflag") != null) {
            this.voipenableflag = Boolean.parseBoolean(message.get("voipenableflag").toString());
        }
        if (message.get("custcategory") != null) {
            this.custcategory = message.get("custcategory").toString();
        }
        if (message.get("networktype") != null) {
            this.networktype = message.get("networktype").toString();
        }
        if (message.get("defaultpoolid") != null) {
            this.defaultpoolid = Long.parseLong(message.get("defaultpoolid").toString());
        }
        if (message.get("servicearea") != null) {
            this.servicearea = Long.parseLong(message.get("servicearea").toString());
        }
        if (message.get("status") != null) {
            this.status = message.get("status").toString();
        }
        if (message.get("invoiceOption") != null) {
            this.invoiceOption = message.get("invoiceOption").toString();
        }
        if (message.get("failcount") != null) {
            this.failcount = Integer.valueOf(message.get("failcount").toString());
        }
        if (message.get("last_password_change") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String val = LocalDateTime.parse(message.get("last_password_change").toString()).format(formatter);
            this.last_password_change = LocalDateTime.parse(message.get("last_password_change").toString(), formatter);
//            this.last_password_change = (LocalDateTime) message.get("last_password_change");
        }
        if (message.get("acctno") != null) {
            this.acctno = message.get("acctno").toString();
        }
        if (message.get("custtype") != null) {
            this.custtype = message.get("custtype").toString();
        }
        if (message.get("phone") != null) {
            this.phone = message.get("phone").toString();
        }
        if (message.get("lastStatusChangeDate") != null) {
            this.lastStatusChangeDate = LocalDateTime.parse(message.get("lastStatusChangeDate").toString());
        }
        if (message.get("billday") != null) {
            this.billday = Integer.parseInt(message.get("billday").toString());
        }
//        if (message.get("partner") != null) {
//            this.partner = message.get("partner").toString();
//        }
//        if (message.get("customerPayments") != null) {
//            this.customerPayments = message.get("customerPayments").toString();
//        }
        if (message.get("nextBillDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.nextBillDate = LocalDate.parse(message.get("nextBillDate").toString(), formatter);
        }
        if (message.get("lastBillDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.lastBillDate = LocalDate.parse(message.get("lastBillDate").toString(), formatter);
        }
        if (message.get("nextQuotaResetDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.nextQuotaResetDate = LocalDate.parse(message.get("nextQuotaResetDate").toString(), formatter);
        }
//        if (message.get("radiusProfiles") != null) {
//            this.radiusProfiles = message.get("radiusProfiles").toString();
//        }
//        if (message.get("planMappingList") != null) {
//            this.planMappingList = message.get("planMappingList").toString();
//        }
        if (message.get("custMacMapppingList") != null) {
//            this.custMacMapppingList = message.get("custMacMapppingList").toString();
            List macAddresses = (List) message.get("custMacMapppingList");
            for (int i = 0; i < macAddresses.size(); i++) {
                MacAddressMapping macAddressMapping = new MacAddressMapping((Map) macAddresses.get(i));
                this.macAddressMappingList.add(macAddressMapping);
            }
        }
        if (message.get("newpassword") != null) {
            this.newpassword = message.get("newpassword").toString();
        }
        if (message.get("OldBNGRouterinterface") != null) {
            this.OldBNGRouterinterface = message.get("OldBNGRouterinterface").toString();
        }
        if (message.get("OldVSIName") != null) {
            this.OldVSIName = message.get("OldVSIName").toString();
        }
        if (message.get("contactperson") != null) {
            this.contactperson = message.get("contactperson").toString();
        }
        if (message.get("partner") != null) {
            this.partner = message.get("partner").toString();
        }
        if (message.get("BNGRouterinterface") != null) {
            this.BNGRouterinterface = message.get("BNGRouterinterface").toString();
        }
        if (message.get("BNGRoutername") != null) {
            this.BNGRoutername = message.get("BNGRoutername").toString();
        }
        if (message.get("IPPrefixes") != null) {
            this.IPPrefixes = message.get("IPPrefixes").toString();
        }
        if (message.get("IPV6Prefixes") != null) {
            this.IPV6Prefixes = message.get("IPV6Prefixes").toString();
        }
        if (message.get("LANIP") != null) {
            this.LANIP = message.get("LANIP").toString();
        }
        if (message.get("LANIPV6") != null) {
            this.LANIPV6 = message.get("LANIPV6").toString();
        }
        if (message.get("LLAccountid") != null) {
            this.LLAccountid = message.get("LLAccountid").toString();
        }
        if (message.get("LLConnectiontype") != null) {
            this.LLConnectiontype = message.get("LLConnectiontype").toString();
        }
        if (message.get("LLExpirydate") != null) {
            this.LLExpirydate = message.get("LLExpirydate").toString();
        }
        if (message.get("LLMedium") != null) {
            this.LLMedium = message.get("LLMedium").toString();
        }
        if (message.get("LLServiceid") != null) {
            this.LLServiceid = message.get("LLServiceid").toString();
        }
        if (message.get("MACADDRESS") != null) {
            this.MACADDRESS = message.get("MACADDRESS").toString();
        }
        if (message.get("Peerip") != null) {
            this.Peerip = message.get("Peerip").toString();
        }
        if (message.get("POOLIP") != null) {
            this.POOLIP = message.get("POOLIP").toString();
        }
        if (message.get("QOS") != null) {
            this.QOS = message.get("QOS").toString();
        }
        if (message.get("RDExport") != null) {
            this.RDExport = message.get("RDExport").toString();
        }
        if (message.get("RDValue") != null) {
            this.RDValue = message.get("RDValue").toString();
        }
        if (message.get("VLANID") != null) {
            this.VLANID = message.get("VLANID").toString();
        }
        if (message.get("VRFName") != null) {
            this.VRFName = message.get("VRFName").toString();
        }
        if (message.get("VSIID") != null) {
            this.VSIID = message.get("VSIID").toString();
        }
        if (message.get("VSIName") != null) {
            this.VSIName = message.get("VSIName").toString();
        }
        if (message.get("WANIP") != null) {
            this.WANIP = message.get("WANIP").toString();
        }
        if (message.get("WANIPV6") != null) {
            this.WANIPV6 = message.get("WANIPV6").toString();
        }
        if (message.get("billentityname") != null)
            this.billentityname = message.get("billentityname").toString();
        if (message.get("addparam1") != null)
            this.addparam1 = message.get("addparam1").toString();
        if (message.get("addparam2") != null)
            this.addparam2 = message.get("addparam2").toString();
        if (message.get("addparam3") != null)
            this.addparam3 = message.get("addparam3").toString();
        if (message.get("addparam4") != null)
            this.addparam4 = message.get("addparam4").toString();
        if (message.get("purchaseorder") != null)
            this.purchaseorder = message.get("purchaseorder").toString();
        if (message.get("remarks") != null)
            this.remarks = message.get("remarks").toString();
        if (message.get("oldpassword1") != null)
            this.oldpassword1 = message.get("oldpassword1").toString();
        if (message.get("oldpassword2") != null)
            this.oldpassword2 = message.get("oldpassword2").toString();
        if (message.get("oldpassword3") != null)
            this.oldpassword3 = message.get("oldpassword3").toString();
        if (message.get("selfcarepwd") != null)
            this.selfcarepwd = message.get("selfcarepwd").toString();
        if (message.get("allowedIPAddress") != null)
            this.allowedIPAddress = message.get("allowedIPAddress").toString();
        if (message.get("parentCustomersId") != null)
            this.parentCustomersId = Integer.parseInt(message.get("parentCustomersId").toString());
        if (message.get("OldWANIP") != null)
            this.OldWANIP = message.get("OldWANIP").toString();
        if (message.get("isDeleted") != null)
            this.isDeleted = Boolean.parseBoolean(message.get("isDeleted").toString());
        if (message.get("OldLLAccountid") != null)
            this.OldLLAccountid = message.get("OldLLAccountid").toString();
        if (message.get("firstActivationDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.firstActivationDate = LocalDateTime.parse(message.get("firstActivationDate").toString(), formatter);
        }
        if (message.get("otpvalidate") != null)
            this.otpvalidate = LocalDateTime.parse(message.get("otpvalidate").toString());
        if (message.get("otp") != null)
            this.otp = message.get("otp").toString();
        if (message.get("latitude") != null)
            this.latitude = message.get("latitude").toString();
        if (message.get("longitude") != null)
            this.longitude = message.get("longitude").toString();
        if (message.get("url") != null)
            this.url = message.get("url").toString();
        if (message.get("gis_code") != null)
            this.gis_code = message.get("gis_code").toString();
        if (message.get("salesremark") != null)
            this.salesremark = message.get("salesremark").toString();
        if (message.get("servicetype") != null)
            this.servicetype = message.get("servicetype").toString();
        if (message.get("previousCafApprover") != null)
            this.previousCafApprover = Integer.parseInt(message.get("previousCafApprover").toString());
        if (message.get("nextCafApprover") != null)
            this.nextCafApprover = Integer.parseInt(message.get("nextCafApprover").toString());
        if (message.get("cafApproveStatus") != null)
            this.cafApproveStatus = message.get("cafApproveStatus").toString();
        if (message.get("billRunCustPackageRelId") != null)
            this.billRunCustPackageRelId = Integer.parseInt(message.get("billRunCustPackageRelId").toString());
        if (message.get("mvnoId") != null)
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
        if (message.get("fullName") != null)
            this.fullName = message.get("fullName").toString();
        if (message.get("calendarType") != null) {
            this.calendarType = message.get("calendarType").toString();
        }
        if (message.get("invoice_type") != null)
            this.invoiceType = message.get("invoice_type").toString();
        if (message.get("nasPort") != null)
            this.nasPort = message.get("nasPort").toString();
        if (message.get("framedIp") != null)
            this.framedIp = message.get("framedIp").toString();
        if (message.get("framedIpBind") != null)
            this.framedIpBind = message.get("framedIpBind").toString();
        if (message.get("ipPoolNameBind") != null)
            this.ipPoolNameBind = message.get("ipPoolNameBind").toString();
        if (message.get("buId") != null)
            this.buId = Long.parseLong(message.get("buId").toString());
        if (message.get("maxconcurrentsession") != null)
            this.maxconcurrentsession = Integer.valueOf(message.get("maxconcurrentsession").toString());
        if (message.get("ipv4") != null) {
            this.ipv4 = message.get("ipv4").toString();
        }
        if (message.get("ipv6") != null) {
            this.ipv6 = message.get("ipv6").toString();
        }
        if (message.get("vlan") != null) {
            this.vlan = message.get("vlan").toString();
        }
        if (message.get("NASIPADDRESS") != null) {
            this.nasIpAddress = message.get("NASIPADDRESS").toString();
        }
        if (message.get("NASPORTID") != null) {
            this.nasPortId = message.get("NASPORTID").toString();
        }
        if (message.get("FRAMEDIPV6ADDRESS") != null) {
            this.framedIpv6Address = message.get("FRAMEDIPV6ADDRESS").toString();
        }
        if (message.get("delegatedprefix") != null) {
            this.delegatedprefix = message.get("delegatedprefix").toString();
        }
        if (message.get("framedroute") != null) {
            this.framedroute = message.get("framedroute").toString();
        }
        if (message.get("mac_provision") != null) {
            this.mac_provision = Boolean.valueOf(message.get("mac_provision").toString());
        }
        if (message.get("framedIPNetmask") != null) {
            this.framedIPNetmask = String.valueOf(message.get("framedIPNetmask").toString());
        }
        if (message.get("framedIPv6Prefix") != null) {
            this.framedIPv6Prefix = String.valueOf(message.get("framedIPv6Prefix").toString());
        }
        if (message.get("gatewayIP") != null) {
            this.gatewayip = String.valueOf(message.get("gatewayIP").toString());
        }
        if (message.get("primaryDNS") != null) {
            this.primaryDNS = String.valueOf(message.get("primaryDNS").toString());
        }
        if (message.get("primaryIPv6DNS") != null) {
            this.primaryIPv6DNS = String.valueOf(message.get("primaryIPv6DNS").toString());
        }
        if (message.get("secondaryDNS") != null) {
            this.secondaryDNS = String.valueOf(message.get("secondaryDNS").toString());
        }
        if (message.get("secondaryIPv6DNS") != null) {
            this.secondaryIPv6DNS = String.valueOf(message.get("secondaryIPv6DNS").toString());
        }
        if (message.get("mac_auth_enable") != null) {
            this.mac_auth_enable = Boolean.valueOf(message.get("mac_auth_enable").toString());
        } else {
            this.mac_auth_enable = true;
        }
        if (message.get("macRetentionUnit") != null) {
            this.macRetentionUnit = message.get("macRetentionUnit").toString();
        }
        if (message.get("macRetentionPeriod") != null) {
            this.macRetentionPeriod = Integer.valueOf(message.get("macRetentionPeriod").toString());
        }
        if(message.get("blockNo")!=null){
            this.blockNo = message.get("blockNo").toString();
        }
    }

    public Customers(Map message) {
        if (message.get("id") != null) {
            this.id = Integer.parseInt(message.get("id").toString());
        }
        if (message.get("title") != null) {
            this.title = message.get("title").toString();
        }
        if (message.get("username") != null) {
            this.username = message.get("username").toString();
        }
        if (message.get("password") != null) {
            this.password = message.get("password").toString();
        }
        if (message.get("firstname") != null) {
            this.firstname = message.get("firstname").toString();
        }
        if (message.get("lastname") != null) {
            this.lastname = message.get("lastname").toString();
        }
        if (message.get("custname") != null) {
            this.custname = message.get("custname").toString();
        }


        if (message.get("cafno") != null) {
            this.cafno = message.get("cafno").toString();
        }
        if (message.get("email") != null) {
            this.email = message.get("email").toString();
        }
        if (message.get("mactelflag") != null) {
            this.mactelflag = Boolean.parseBoolean(message.get("mactelflag").toString());
        }
        if (message.get("mobile") != null) {
            this.mobile = message.get("mobile").toString();
        }
        if (message.get("voicesrvtype") != null) {
            this.voicesrvtype = message.get("voicesrvtype").toString();
        }
        if (message.get("voiceprovision") != null) {
            this.voiceprovision = Boolean.parseBoolean(message.get("voiceprovision").toString());
        }
        if (message.get("intercomno") != null) {
            this.intercomno = message.get("intercomno").toString();
        }
        if (message.get("intercomgrp") != null) {
            this.intercomgrp = message.get("intercomgrp").toString();
        }
        if (message.get("onlinerenewalflag") != null) {
            this.onlinerenewalflag = Boolean.parseBoolean(message.get("onlinerenewalflag").toString());
        }
        if (message.get("voipenableflag") != null) {
            this.voipenableflag = Boolean.parseBoolean(message.get("voipenableflag").toString());
        }
        if (message.get("custcategory") != null) {
            this.custcategory = message.get("custcategory").toString();
        }
        if (message.get("networktype") != null) {
            this.networktype = message.get("networktype").toString();
        }
        if (message.get("defaultpoolid") != null) {
            this.defaultpoolid = Long.parseLong(message.get("defaultpoolid").toString());
        }
        if (message.get("servicearea") != null) {
            this.servicearea = Long.parseLong(message.get("servicearea").toString());
        }
        if (message.get("status") != null) {
            this.status = message.get("status").toString();
        }
        if (message.get("invoiceOption") != null) {
            this.invoiceOption = message.get("invoiceOption").toString();
        }
        if (message.get("failcount") != null) {
            this.failcount = Integer.valueOf(message.get("failcount").toString());
        }
        /*if (message.get("last_password_change") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String val = LocalDateTime.parse(message.get("last_password_change").toString()).format(formatter);
            this.last_password_change = LocalDateTime.parse(message.get("last_password_change").toString(), formatter);
//            this.last_password_change = (LocalDateTime) message.get("last_password_change");
        }
        */
        if (message.get("acctno") != null) {
            this.acctno = message.get("acctno").toString();
        }
        if (message.get("custtype") != null) {
            this.custtype = message.get("custtype").toString();
        }
        if (message.get("phone") != null) {
            this.phone = message.get("phone").toString();
        }
        /*if (message.get("lastStatusChangeDate") != null) {
            this.lastStatusChangeDate = LocalDateTime.parse(message.get("lastStatusChangeDate").toString());
        }*/
        if (message.get("billday") != null) {
            this.billday = Integer.parseInt(message.get("billday").toString());
        }
//        if (message.get("partner") != null) {
//            this.partner = message.get("partner").toString();
//        }
//        if (message.get("customerPayments") != null) {
//            this.customerPayments = message.get("customerPayments").toString();
//        }
        /*if (message.get("nextBillDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.nextBillDate = LocalDate.parse(message.get("nextBillDate").toString(), formatter);
        }
        if (message.get("lastBillDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.lastBillDate = LocalDate.parse(message.get("lastBillDate").toString(), formatter);
        }*/
//        if (message.get("radiusProfiles") != null) {
//            this.radiusProfiles = message.get("radiusProfiles").toString();
//        }
//        if (message.get("planMappingList") != null) {
//            this.planMappingList = message.get("planMappingList").toString();
//        }
//        if (message.get("custMacMapppingList") != null) {
////            this.custMacMapppingList = message.get("custMacMapppingList").toString();
//            List macAddresses = (List) message.get("custMacMapppingList");
//            for(int i=0; i<macAddresses.size(); i++){
//                CustMacMappping macAddressMapping = new CustMacMappping((Map)macAddresses.get(i));
//                this.custMacMapppingList.add(macAddressMapping);
//            }
//        }

        if (message.get("custMacMapppingList") != null) {
//            this.custMacMapppingList = message.get("custMacMapppingList").toString();
            List macAddresses = (List) message.get("custMacMapppingList");
            for (int i = 0; i < macAddresses.size(); i++) {
                MacAddressMapping macAddressMapping = new MacAddressMapping((Map) macAddresses.get(i));
                this.macAddressMappingList.add(macAddressMapping);
            }
        }
//        if (message.get("planMappingList") != null) {
//            List plans = (List) message.get("planMappingList");
//            for(int i=0; i<plans.size(); i++){
//                CustPlanMappping custPlanMappping = new CustPlanMappping((Map)plans.get(i));
//                this.planMappingList.add(custPlanMappping);
//            }
//        }
        if (message.get("newpassword") != null) {
            this.newpassword = message.get("newpassword").toString();
        }
        if (message.get("OldBNGRouterinterface") != null) {
            this.OldBNGRouterinterface = message.get("OldBNGRouterinterface").toString();
        }
        if (message.get("OldVSIName") != null) {
            this.OldVSIName = message.get("OldVSIName").toString();
        }
        if (message.get("contactperson") != null) {
            this.contactperson = message.get("contactperson").toString();
        }
        if (message.get("partner") != null) {
            this.partner = message.get("partner").toString();
        }
        if (message.get("BNGRouterinterface") != null) {
            this.BNGRouterinterface = message.get("BNGRouterinterface").toString();
        }
        if (message.get("BNGRoutername") != null) {
            this.BNGRoutername = message.get("BNGRoutername").toString();
        }
        if (message.get("IPPrefixes") != null) {
            this.IPPrefixes = message.get("IPPrefixes").toString();
        }
        if (message.get("IPV6Prefixes") != null) {
            this.IPV6Prefixes = message.get("IPV6Prefixes").toString();
        }
        if (message.get("LANIP") != null) {
            this.LANIP = message.get("LANIP").toString();
        }
        if (message.get("LANIPV6") != null) {
            this.LANIPV6 = message.get("LANIPV6").toString();
        }
        if (message.get("LLAccountid") != null) {
            this.LLAccountid = message.get("LLAccountid").toString();
        }
        if (message.get("LLConnectiontype") != null) {
            this.LLConnectiontype = message.get("LLConnectiontype").toString();
        }
        if (message.get("LLExpirydate") != null) {
            this.LLExpirydate = message.get("LLExpirydate").toString();
        }
        if (message.get("LLMedium") != null) {
            this.LLMedium = message.get("LLMedium").toString();
        }
        if (message.get("LLServiceid") != null) {
            this.LLServiceid = message.get("LLServiceid").toString();
        }
        if (message.get("MACADDRESS") != null) {
            this.MACADDRESS = message.get("MACADDRESS").toString();
        }
        if (message.get("Peerip") != null) {
            this.Peerip = message.get("Peerip").toString();
        }
        if (message.get("POOLIP") != null) {
            this.POOLIP = message.get("POOLIP").toString();
        }
        if (message.get("QOS") != null) {
            this.QOS = message.get("QOS").toString();
        }
        if (message.get("RDExport") != null) {
            this.RDExport = message.get("RDExport").toString();
        }
        if (message.get("RDValue") != null) {
            this.RDValue = message.get("RDValue").toString();
        }
        if (message.get("VLANID") != null) {
            this.VLANID = message.get("VLANID").toString();
        }
        if (message.get("VRFName") != null) {
            this.VRFName = message.get("VRFName").toString();
        }
        if (message.get("VSIID") != null) {
            this.VSIID = message.get("VSIID").toString();
        }
        if (message.get("VSIName") != null) {
            this.VSIName = message.get("VSIName").toString();
        }
        if (message.get("WANIP") != null) {
            this.WANIP = message.get("WANIP").toString();
        }
        if (message.get("WANIPV6") != null) {
            this.WANIPV6 = message.get("WANIPV6").toString();
        }
        if (message.get("billentityname") != null)
            this.billentityname = message.get("billentityname").toString();
        if (message.get("addparam1") != null)
            this.addparam1 = message.get("addparam1").toString();
        if (message.get("addparam2") != null)
            this.addparam2 = message.get("addparam2").toString();
        if (message.get("addparam3") != null)
            this.addparam3 = message.get("addparam3").toString();
        if (message.get("addparam4") != null)
            this.addparam4 = message.get("addparam4").toString();
        if (message.get("purchaseorder") != null)
            this.purchaseorder = message.get("purchaseorder").toString();
        if (message.get("remarks") != null)
            this.remarks = message.get("remarks").toString();
        if (message.get("oldpassword1") != null)
            this.oldpassword1 = message.get("oldpassword1").toString();
        if (message.get("oldpassword2") != null)
            this.oldpassword2 = message.get("oldpassword2").toString();
        if (message.get("oldpassword3") != null)
            this.oldpassword3 = message.get("oldpassword3").toString();
        if (message.get("selfcarepwd") != null)
            this.selfcarepwd = message.get("selfcarepwd").toString();
        if (message.get("allowedIPAddress") != null)
            this.allowedIPAddress = message.get("allowedIPAddress").toString();
        if (message.get("parentCustomersId") != null)
            this.parentCustomersId = Integer.parseInt(message.get("parentCustomersId").toString());
        if (message.get("OldWANIP") != null)
            this.OldWANIP = message.get("OldWANIP").toString();
        if (message.get("isDeleted") != null)
            this.isDeleted = Boolean.parseBoolean(message.get("isDeleted").toString());
        if (message.get("OldLLAccountid") != null)
            this.OldLLAccountid = message.get("OldLLAccountid").toString();
//        if (message.get("firstActivationDate") != null) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            this.firstActivationDate = LocalDateTime.parse(message.get("firstActivationDate").toString(), formatter);
//        }
        if (message.get("otpvalidate") != null)
            this.otpvalidate = LocalDateTime.parse(message.get("otpvalidate").toString());
        if (message.get("otp") != null)
            this.otp = message.get("otp").toString();
        if (message.get("latitude") != null)
            this.latitude = message.get("latitude").toString();
        if (message.get("longitude") != null)
            this.longitude = message.get("longitude").toString();
        if (message.get("url") != null)
            this.url = message.get("url").toString();
        if (message.get("gis_code") != null)
            this.gis_code = message.get("gis_code").toString();
        if (message.get("salesremark") != null)
            this.salesremark = message.get("salesremark").toString();
        if (message.get("servicetype") != null)
            this.servicetype = message.get("servicetype").toString();
        if (message.get("previousCafApprover") != null)
            this.previousCafApprover = Integer.parseInt(message.get("previousCafApprover").toString());
        if (message.get("nextCafApprover") != null)
            this.nextCafApprover = Integer.parseInt(message.get("nextCafApprover").toString());
        if (message.get("cafApproveStatus") != null)
            this.cafApproveStatus = message.get("cafApproveStatus").toString();
        if (message.get("billRunCustPackageRelId") != null)
            this.billRunCustPackageRelId = Integer.parseInt(message.get("billRunCustPackageRelId").toString());
        if (message.get("mvnoId") != null)
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
        if (message.get("fullName") != null)
            this.fullName = message.get("fullName").toString();
        if (message.get("calendarType") != null)
            this.calendarType = message.get("calendarType").toString();
        if (message.get("buId") != null)
            this.buId = Long.parseLong(message.get("buId").toString());
        if (message.get("maxconcurrentsession") != null)
            this.maxconcurrentsession = Integer.valueOf(message.get("maxconcurrentsession").toString());
        if (message.get("ipv4") != null) {
            this.ipv4 = message.get("ipv4").toString();
        }
        if (message.get("ipv6") != null) {
            this.ipv6 = message.get("ipv6").toString();
        }
        if (message.get("vlan") != null) {
            this.vlan = message.get("vlan").toString();
        }

        if (message.get("NASIPADDRESS") != null) {
            this.nasIpAddress = message.get("NASIPADDRESS").toString();
        }
        if (message.get("NASPORTID") != null) {
            this.nasPortId = message.get("NASPORTID").toString();
        }
        if (message.get("FRAMEDIPV6ADDRESS") != null) {
            this.framedIpv6Address = message.get("FRAMEDIPV6ADDRESS").toString();
        }
        if (message.get("framedIPNetmask") != null) {
            this.framedIPNetmask = String.valueOf(message.get("framedIPNetmask").toString());
        }
        if (message.get("framedIPv6Prefix") != null) {
            this.framedIPv6Prefix = String.valueOf(message.get("framedIPv6Prefix").toString());
        }
        if (message.get("gatewayIP") != null) {
            this.gatewayip = String.valueOf(message.get("gatewayIP").toString());
        }
        if (message.get("primaryDNS") != null) {
            this.primaryDNS = String.valueOf(message.get("primaryDNS").toString());
        }
        if (message.get("primaryIPv6DNS") != null) {
            this.primaryIPv6DNS = String.valueOf(message.get("primaryIPv6DNS").toString());
        }
        if (message.get("secondaryDNS") != null) {
            this.secondaryDNS = String.valueOf(message.get("secondaryDNS").toString());
        }
        if (message.get("secondaryIPv6DNS") != null) {
            this.secondaryIPv6DNS = String.valueOf(message.get("secondaryIPv6DNS").toString());
        }
        if (message.get("mac_auth_enable") != null) {
            this.mac_auth_enable = Boolean.valueOf(message.get("mac_auth_enable").toString());
        } else {
            this.mac_auth_enable = true;
        }
        if (message.get("macRetentionPeriod") != null) {
            this.macRetentionPeriod = Integer.valueOf(message.get("macRetentionPeriod").toString());
        }
        if (message.get("macRetentionUnit") != null) {
            this.macRetentionUnit = message.get("macRetentionUnit").toString();
        }
        if (message.get("delegatedprefix") != null) {
            this.delegatedprefix = message.get("macRetentionUnit").toString();
        }
        if (message.get("framedroute ") != null) {
            this.framedroute = message.get("framedroute").toString();
        }
    }

    public Customers(CustomerCreateData customerData) {
        this.username = customerData.getUsername();
        this.password = customerData.getPassword();
        this.custname = customerData.getUsername();
//        this.parentCustomersId = customerData.getParentCustomerId();
//        this.maxconcurrentsession = customerData.getMaxconcurrentsession();
        this.status = customerData.getStatus();
        this.firstname = customerData.getFirstname();
        this.lastname = customerData.getLastname();
        this.title = customerData.getTitle();
        this.email = customerData.getEmail();
        this.failcount = customerData.getFailcount();
        this.custtype = "Prepaid";
        this.isDeleted = false;
        if (customerData.getPartnerid() != null)
//            this.partner = String.valueOf(customerData.getPartnerid());
            this.partner = customerData.getPartnerid();
        else
            this.partner = "1";
        this.contactperson = customerData.getUsername();
        this.acctno = customerData.getAcct_no();
        this.BNGRouterinterface = customerData.getBngrouterinterface();
        this.QOS = customerData.getQos();
        this.VLANID = customerData.getVlanid();
        this.WANIP = customerData.getWanip();
        this.remarks = customerData.getRemarks();
        this.LANIP = customerData.getLanip();
        this.ASNNumber = customerData.getAsnnumber();
        this.LLAccountid = customerData.getLlaccountid();
        this.IPPrefixes = customerData.getIpprefixes();
        this.RDExport = customerData.getRdexport();
        this.RDValue = customerData.getRdvalue();
        this.VRFName = customerData.getVrfname();
        this.Peerip = customerData.getPeerip();
        this.VSIID = customerData.getVsiid();
        this.VSIName = customerData.getVsiname();
        this.addparam1 = customerData.getThparam1();
        this.addparam2 = customerData.getThparam2();
        this.addparam3 = customerData.getThparam3();
        this.addparam4 = customerData.getThparam4();
        this.mobile = customerData.getMobile();
        if (customerData.getEdate() != null && !customerData.getEdate().isEmpty())
            this.expirydate = customerData.getEdate();
        else
            this.expirydate = null;
        this.gatewayip = customerData.getGatewayIP();
        this.skipnetconf = customerData.getSkipnetconf();
        this.rdimport = customerData.getRdimport();
        this.ipv4 = customerData.getIpv4();
        this.ipv6 = customerData.getIpv6();
        this.vlan = customerData.getVlan();
        this.mac_auth_enable = customerData.getMac_auth_enable();
        this.mac_provision = customerData.getMac_provision();
        this.phone = customerData.phone;
        this.framedIPNetmask = customerData.getFramedIPNetmask();
        this.framedIPv6Prefix = customerData.getFramedIPv6Prefix();
        this.primaryDNS = customerData.getPrimaryDNS();
        this.primaryIPv6DNS = customerData.getPrimaryIPv6DNS();
        this.secondaryIPv6DNS = customerData.getSecondaryIPv6DNS();
        this.secondaryDNS = customerData.getSecondaryDNS();
        this.macRetentionUnit = customerData.getMacRetentionUnit();
        this.macRetentionPeriod = customerData.getMacRetentionPeriod();
    }
}
