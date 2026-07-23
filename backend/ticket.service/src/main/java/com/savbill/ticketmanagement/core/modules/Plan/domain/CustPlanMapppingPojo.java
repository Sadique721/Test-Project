package com.savbill.ticketmanagement.core.modules.Plan.domain;

import com.savbill.ticketmanagement.core.data.Auditable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustPlanMapppingPojo extends Auditable {

    private Integer id;

    @NotNull
    private Integer planId;



    @NotNull
    private Integer custid;

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @JsonBackReference
//    private CustomersPojo customer;

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

    private Long creditdocid;

    private String planName;

    private Integer planValidityDays;

    private Boolean isInvoiceToOrg = false;

    private String billTo = "CUSTOMER";

    private Integer renewalId;

    private Integer custRefId;
    private String serialNumber;

    private String custRefName;

    private String expiry;

    private String custPlanStatus = "Active";

    private Boolean isinvoicestop = false;

    private Boolean istrialplan = false;

    private String plangroup;

    private Integer serviceId;

    private String ezyBillServiceId;

    private Double oldDiscount;
    private Long traildebitdocid;
    private Double isTrialValidityDays;
    private Integer trialPlanValidityCount;

    private String ezBillPackageId;
    private String casId;

    private String invoiceformat;

    private Integer billableCustomerId=null;

    private String unitsOfValidity;

    private String extendValidityremarks;

//    private LinkAcceptanceDTO linkAcceptanceDTO;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime extendDate;

    private String discountType="One-time";

    private LocalDate discountExpiryDate;

    private LocalDateTime startServiceDate;

    private Integer cprIdForPromiseToPay;

    private Boolean isVoid = Boolean.FALSE;

    private  String serviceParamName;


    public CustPlanMapppingPojo() {
    }



    public CustPlanMapppingPojo(LocalDateTime startDate, LocalDateTime expiryDate) {
        this.startDate = startDate;
        this.expiryDate = expiryDate;
    }


    @Override
    public String toString() {
        return "CustPlanMapppingPojo{" +
                "id=" + id +
                ", planId=" + planId +
                ", custid=" + custid +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", expiryDate=" + expiryDate +
                ", startDateString='" + startDateString + '\'' +
                ", endDateString='" + endDateString + '\'' +
                ", expiryDateString='" + expiryDateString + '\'' +
                ", status='" + status + '\'' +
                ", qospolicyId=" + qospolicyId +
                ", service='" + service + '\'' +
                ", isDelete=" + isDelete +
                ", creditdocid=" + creditdocid +
                ", planName='" + planName + '\'' +
                ", planValidityDays=" + planValidityDays +
                ", isInvoiceToOrg=" + isInvoiceToOrg +
                ", billTo='" + billTo + '\'' +
                ", renewalId=" + renewalId +
                ", custRefId=" + custRefId +
                ", serialNumber='" + serialNumber + '\'' +
                ", custRefName='" + custRefName + '\'' +
                ", expiry='" + expiry + '\'' +
                ", custPlanStatus='" + custPlanStatus + '\'' +
                ", isinvoicestop=" + isinvoicestop +
                ", istrialplan=" + istrialplan +
                ", plangroup='" + plangroup + '\'' +
                ", serviceId=" + serviceId +
                ", ezyBillServiceId='" + ezyBillServiceId + '\'' +
                ", oldDiscount=" + oldDiscount +
                ", traildebitdocid=" + traildebitdocid +
                ", isTrialValidityDays=" + isTrialValidityDays +
                ", trialPlanValidityCount=" + trialPlanValidityCount +
                ", ezBillPackageId='" + ezBillPackageId + '\'' +
                ", casId='" + casId + '\'' +
                ", invoiceformat='" + invoiceformat + '\'' +
                ", billableCustomerId=" + billableCustomerId +
                ", unitsOfValidity='" + unitsOfValidity + '\'' +
                ", extendValidityremarks='" + extendValidityremarks + '\'' +
                ", extendDate=" + extendDate +
                ", discountType='" + discountType + '\'' +
                ", discountExpiryDate=" + discountExpiryDate +
                ", startServiceDate=" + startServiceDate +
                ", cprIdForPromiseToPay=" + cprIdForPromiseToPay +
                ", isVoid=" + isVoid +
                ", serviceParamName='" + serviceParamName + '\'' +
                '}';
    }
}
