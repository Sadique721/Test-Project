package com.diameter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tblcustpackagerel")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustPlanMappping extends Auditable {

    @Id
    @Column(name = "custpackageid", nullable = false)
    @JsonAlias({"id", "custpackageid"})
    private Long custPackageId;

    @Column(name = "custid")
    private Long custId;

    @Column(name = "planid")
    private Long planId;

    @Column(name = "startdate")
    private LocalDateTime startDate;

    @Column(name = "enddate")
    private LocalDateTime endDate;

    @Column(name = "expirydate")
    private LocalDateTime expiryDate;

    @Column(name = "status", length = 150)
    private String status;

    @Column(name = "service", length = 100)
    private String service;

    @Column(name = "qospolicyid")
    private Long qosPolicyId;

    @Column(name = "uploadqos", length = 120)
    private String uploadQos;

    @Column(name = "downloadqos", length = 120)
    private String downloadQos;

    @Column(name = "uploadts", length = 120)
    private String uploadTs;

    @Column(name = "downloadts", length = 120)
    private String downloadTs;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "offer_price")
    private Double offerPrice;

    @Column(name = "tax_amount")
    private Double taxAmount;

    @Column(name = "creditdocid")
    private Long creditDocId;

    @Column(name = "debitdocid")
    private Long debitDocId;

    @Column(name = "wallet_bal_used")
    private Double walletBalUsed;

    @Column(name = "purchase_type", length = 100)
    private String purchaseType;

    @Column(name = "online_purchase_id")
    private Long onlinePurchaseId;

    @Column(name = "purchase_from", length = 100)
    private String purchaseFrom;

    @Column(name = "grace_days")
    private Long graceDays;

    @Column(name = "cust_plan_status", length = 50)
    private String custPlanStatus;

    @Column(name = "notificationlevel")
    private Long notificationLevel;

    @Column(name = "istriggercoadm")
    private Boolean isTriggerCoadm;

    @Column(name = "onquotaexhausteventname", length = 100)
    private String onQuotaExhaustEventName;

    @Column(name = "plangroupid")
    private Long plangroupid;

    @Column(name = "custservicemappingid")
    private Long custservicemappingid;

    @Column(name = "bill_to", length = 25)
    private String billTo;

    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg = false;

    @Column(name = "customer_cpr")
    private Integer customerCpr;

    @Column(name = "is_hold")
    private Boolean isHold = false;

    @Column(name = "service_id")
    private Integer serviceId;
}
