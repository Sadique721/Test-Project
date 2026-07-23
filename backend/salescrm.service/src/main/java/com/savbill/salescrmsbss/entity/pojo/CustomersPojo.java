package com.savbill.salescrmsbss.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomersPojo {

	private Integer id;

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

    private Integer failcount = 0;

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

//    private String pincode;

    private Double outstanding;

    private String oldpassword1;

    private String newpassword;

    private String oldpassword2;

    private String oldpassword3;

    private String selfcarepwd;

    private String last_password_change;

    private String lastpasswordchangestring;

    @JsonManagedReference
    private List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();

    @JsonManagedReference
    private List<CustomerAddressPojo> addressList = new ArrayList<>();

    private List<Integer> radiusprofileIds = new ArrayList<>();

    @JsonManagedReference
    private List<DebitDocumentPojo> debitDocList = new ArrayList<>();

    @JsonManagedReference
    private List<CreditDocumentPojo> creditDocuments = new ArrayList<>();

    @JsonManagedReference
    private List<CustChargeDetailsPojo> overChargeList = new ArrayList<>();

    @JsonManagedReference
    private List<CustomerDocDetailsDTO> custDocList = new ArrayList<>();

    @JsonManagedReference
    private List<CustChargeDetailsPojo> indiChargeList = new ArrayList<>();

    @JsonManagedReference
    private CustomerLedgerPojo custLeger;

    @JsonManagedReference
    private List<CustMacMapppingPojo> custMacMapppingList = new ArrayList<>();

    @JsonManagedReference
    private List<CustLedgerDtlsPojo> ledgerDtls = new ArrayList<>();

    private RecordPaymentPojo paymentDetails;

    private String flashMsg;

    private Boolean mactelflag = false;

    @Size(min = 5, max = 15, message = "Mobile number must have 10 digits!")
    private String mobile;

    // added country code
    private String countryCode;

    private String cafno;

    private String altmobile;

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

    @JsonIgnore
    private CustomersPojo parentCustomers;

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

    private Boolean isDeleted = false;

    private String createDateString;
    private String updateDateString;

    private String latitude;
    private String longitude;
    private String url;
    private String gis_code;
    private String salesremark;
    private String servicetype;

    private String isCustCaf;

    private Integer previousCafApprover;
    private Integer nextCafApprover;
    private String serviceareaName;
    private String cafApproveStatus;

    private Integer mvnoId;

    private String tinNo;

    private String passportNo;

    private String dunningCategory;

    private Integer plangroupid;

    private Integer parentCustomerId;

    private String parentCustomerName;

    private String invoiceType;

    private String calendarType;

    private double discount;

    private Long buId;

    private Integer custPackageId;

    private Long partnerLedgerMappingId;

    private String planPurchaseType;

    private String leadSource;
    private String feasibilityRequired;
}
