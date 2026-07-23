package com.savbill.inventorymanagement.modules.CustPlanMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Customers.CustomersPojo;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustPlanMapppingPojo extends Auditable {

    private Integer id;

    @NotNull
    private Integer planId;

    private PostpaidPlanPojo postpaidPlanPojo;

    @NotNull
    private Integer custid;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expiryDate;

    private String startDateString;
    private String endDateString;
    private String expiryDateString;

    @NotNull
    private String status;

    private Long qospolicyId;

    private String uploadqos;

    private String downloadqos;

    private String uploadts;

    private String downloadts;

//    @JsonManagedReference
//    private List<CustQuotaDtlsPojo> quotaList = new ArrayList<>();

//    @Autowired
//    private MessageSender messageSender;

//    public void setQuotaList(List<CustQuotaDtlsPojo> quotaList) {
//        this.quotaList = quotaList;
//        QuotaDetailsMessage message = new QuotaDetailsMessage(quotaList);
//        messageSender.send(message, RabbitMqConstants.QUEUE_CUSTOMER_PACKAGE_REL);
//    }

    private String service;

    private Boolean isDelete = false;

    private Double offerPrice;
    private Double taxAmount;
    private Double walletBalUsed = 0.0;
    private String purchaseType;
    private Long onlinePurchaseId;
    private String purchaseFrom;
    private Long debitdocid;

    private Double validity;

    private String planName;

    private Double discount;
    private Integer plangroupid;

    private Integer planValidityDays;

    private Boolean isInvoiceToOrg = false;

    private String billTo = "CUSTOMER";

    private Double newAmount = 0d;

    private Integer renewalId;

    private Integer custRefId;

    private String custRefName;

    private String expiry;

    private String custPlanStatus = "Active";

    private Boolean isinvoicestop = false;

    private Boolean istrialplan = false;

    private Boolean isInvoiceCreated = false;

    private Integer graceDays;

    private Integer custServiceMappingId;

    private String plangroup;

    private Integer serviceId;

    private String ezyBillServiceId;

    private Double oldDiscount;
    private String remarks;
    private String invoiceType;
    private Long traildebitdocid;
    private Double isTrialValidityDays;
    private Integer trialPlanValidityCount;

    private String ezBillPackageId;
    private String casId;

    private String invoiceformat;

    private Integer billableCustomerId=null;

    private String unitsOfValidity;

    private String extendValidityremarks;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime extendDate;

    private String discountType="One-time";

    private LocalDate discountExpiryDate;

    private LocalDateTime startServiceDate;

    private Integer cprIdForPromiseToPay;

    private Boolean isHold = Boolean.FALSE;

    private Boolean isVoid = Boolean.FALSE;

    private Boolean isContainsCustomerInvoice;

    private Integer customerCpr;

    public CustPlanMapppingPojo() {
    }

    public CustPlanMapppingPojo(@NotNull Integer planId, @NotNull Integer custid, CustomersPojo customer, LocalDateTime startDate, LocalDateTime expiryDate, Long qospolicyId, String uploadqos, String downloadqos, String uploadts, String downloadts, String service, Double offerPrice, Double taxAmount, LocalDateTime endDate, Double validity, Integer planValidityDays, Integer planGroupId, Integer renewalId, String custRefName) {
        this.planId = planId;
        this.custid = custid;
        this.customer = customer;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.qospolicyId = qospolicyId;
        this.uploadqos = uploadqos;
        this.downloadqos = downloadqos;
        this.uploadts = uploadts;
        this.downloadts = downloadts;
        this.service = service;
        this.offerPrice = offerPrice;
        this.taxAmount = taxAmount;
        this.endDate = endDate;
        this.validity = validity;
        this.planValidityDays = planValidityDays;
        this.plangroupid = planGroupId;
        this.renewalId = renewalId;
        this.custRefName = custRefName;
    }

    public CustPlanMapppingPojo(LocalDateTime startDate, LocalDateTime expiryDate) {
        this.startDate = startDate;
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "CustPlanMapppingPojo [id=" + id + ", planId=" + planId + ", custid=" + custid + ", startDate=" + startDate + ", endDate=" + endDate + ", expiryDate=" + expiryDate + ", status=" + status + ", validity=" + validity + ", planValidityDays=" + planValidityDays + "]";
    }

    public CustPlanMapppingPojo(CustPlanMappping custPlanMapppping) {
        if(custPlanMapppping.getPlanId()!= null)
            this.planId = custPlanMapppping.getPlanId();
        if(custPlanMapppping.getCustId()!= null)
            this.custid = custPlanMapppping.getCustId();
        if(custPlanMapppping.getStartDate()!= null)
            this.startDate = custPlanMapppping.getStartDate();
        if(custPlanMapppping.getExpiryDate()!= null)
            this.expiryDate = custPlanMapppping.getExpiryDate();
//        if(custPlanMapppping.getQospolicy()!= null  && custPlanMapppping.getQospolicy().getId()!= null)
//            this.qospolicyId = custPlanMapppping.getQospolicy().getId();
        if(custPlanMapppping.getEndDate()!= null)
            this.endDate = custPlanMapppping.getEndDate();
    }
}
