package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Entity
@Data
@ToString
@Table(name = "TBLCUSTPACKAGEREL")
public class CustPlanMappping {

    @Id
    @Column(name = "custpackageid", nullable = false, length = 40)
    private Long id;

    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;

    @Column(nullable = false, length = 40)
    private String service;

    @Column(name = "startdate", nullable = false, length = 40)
    private LocalDateTime startDate;

    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @Column(name = "expirydate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expiryDate;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "custid")
    private Integer custid;


    @Column(name = "qospolicyid")
    private String qospolicyid;


    private String uploadqos;

    private String downloadqos;

    private String uploadts;

    private String downloadts;
    private Double offerPrice;
    private Double taxAmount;
    private Double walletBalUsed = 0.0;
    private String purchaseType;
    private Long onlinePurchaseId;
    private String purchaseFrom;

    @DiffIgnore
    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "custPlanMappping", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private List<CustQuotaDetails> quotaList = new ArrayList<>();

    private Long debitdocid;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Transient
    private Double validity;

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

    @Column(name = "grace_days")
    private Integer graceDays;

    @Column(name = "cust_plan_status", columnDefinition = "Active")
    private String custPlanStatus;

    @Column(name = "notificationlevel", columnDefinition = "INT DEFAULT 0")
    private Integer notificationLevel = 0;

    @Column(name = "istriggercoadm", columnDefinition = "Boolean default true")
    private boolean isTriggerCoaDm;

    @Transient
    private String planName;

    @Transient
    private String operation;

    @Column(name = "onquotaexhausteventname")
    private String onQuotaExhaustEventName;

    public CustPlanMappping() {
    }

    public CustPlanMappping(CustPlanMappping custPlanMappping) {
        this.expiryDate = custPlanMappping.getExpiryDate();
    }

    private Long creditdocid;

    public CustPlanMappping(Map message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (message.get("id") != null)
            this.id = Long.parseLong(message.get("id").toString());
        if (message.get("custid") != null)
            this.custid = Integer.parseInt(message.get("custid").toString());
        if (message.get("planId") != null)
            this.planId = Integer.parseInt(message.get("planId").toString());
        if (message.get("startDate") != null) {
            LocalDateTime startDate = convertStringToActualDateFormat("startDate", message, formatter) != null ? convertStringToActualDateFormat("startDate", message, formatter) : convertToActualFormat("startDate", message, formatter);
            if (startDate != null)
                this.startDate = startDate;
            else
                this.startDate = convertStringToActualDateFormat("startDateString", message, formatter) != null ? convertStringToActualDateFormat("startDateString", message, formatter) : convertToActualFormat("startDateString", message, formatter);
        }
        if (message.get("graceDays") != null)
            this.graceDays = Integer.parseInt(message.get("graceDays").toString());
        if (message.get("endDate") != null) {
            LocalDateTime endDate = convertStringToActualDateFormat("endDate", message, formatter) != null ? convertStringToActualDateFormat("endDate", message, formatter) : convertToActualFormat("endDate", message, formatter);
            if (endDate != null)
                this.endDate = endDate;
            else
                this.endDate = convertStringToActualDateFormat("endDateString", message, formatter) != null ? convertStringToActualDateFormat("endDateString", message, formatter) : convertToActualFormat("endDateString", message, formatter);
        }
        if (message.get("expiryDate") != null) {
            LocalDateTime expiryDate = convertStringToActualDateFormat("expiryDate", message, formatter) != null ? convertStringToActualDateFormat("expiryDate", message, formatter) : convertToActualFormat("expiryDate", message, formatter);
            if (endDate != null)
                this.expiryDate = expiryDate;
            else
                this.expiryDate = convertStringToActualDateFormat("expiryDateString", message, formatter) != null ? convertStringToActualDateFormat("expiryDateString", message, formatter) : convertToActualFormat("expiryDateString", message, formatter);
        }
        if (message.get("status") != null)
            this.status = message.get("status").toString();
        if (message.get("qospolicyId") != null)
            this.qospolicyid = message.get("qospolicyId").toString();
        if (message.get("uploadqos") != null)
            this.uploadqos = message.get("uploadqos").toString();
        if (message.get("downloadqos") != null)
            this.downloadqos = message.get("downloadqos").toString();
        if (message.get("uploadts") != null)
            this.uploadqos = message.get("uploadts").toString();
        if (message.get("downloadts") != null)
            this.downloadts = message.get("downloadts").toString();
        if (message.get("service") != null)
            this.service = message.get("service").toString();
        if (message.get("isDelete") != null)
            this.isDelete = Boolean.parseBoolean(message.get("isDelete").toString());
        if (message.get("offerPrice") != null)
            this.offerPrice = Double.parseDouble(message.get("offerPrice").toString());
        if (message.get("taxAmount") != null)
            this.taxAmount = Double.parseDouble(message.get("taxAmount").toString());
        if (message.get("creditdocid") != null)
            this.creditdocid = Long.parseLong(message.get("creditdocid").toString());
        if (message.get("walletBalUsed") != null)
            this.walletBalUsed = Double.parseDouble(message.get("walletBalUsed").toString());
        if (message.get("purchaseType") != null)
            this.purchaseType = message.get("purchaseType").toString();
        if (message.get("onlinePurchaseId") != null)
            this.onlinePurchaseId = Long.parseLong(message.get("onlinePurchaseId").toString());
        if (message.get("purchaseFrom") != null)
            this.purchaseFrom = message.get("purchaseFrom").toString();
        if (message.get("debitdocid") != null)
            this.debitdocid = Long.parseLong(message.get("debitdocid").toString());
        if (message.get("validity") != null)
            this.validity = Double.parseDouble(message.get("validity").toString());
        if (message.get("custPlanStatus") != null)
            this.custPlanStatus = message.get("custPlanStatus").toString();
        if (message.get("operation") != null)
            this.operation = message.get("operation").toString();

        if (message.get("quotaDtls") != null) {
            List quotaDetails = (List) message.get("quotaDtls");
            for (int i = 0; i < quotaDetails.size(); i++) {
                Map custQuotaDetails = (Map) quotaDetails.get(i);
                custQuotaDetails.put("customer", this.custid);
                CustQuotaDetails custQuota = new CustQuotaDetails(custQuotaDetails);
                custQuota.setCustPlanMappping(this);
                if (message.get("purchaseType") != null)
                    this.quotaList.add(custQuota);
            }
        }


    }

    public CustPlanMappping(Long id, LocalDateTime endDate, LocalDateTime expiryDate, Integer custid, String custPlanStatus) {
        this.id = id;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.custid = custid;
        this.custPlanStatus = custPlanStatus;
    }

    public LocalDateTime convertToActualFormat(String key, Map message, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(message.get(key).toString(), formatter);
        } catch (Exception ex) {
            //skip and try with another entry
        }
        return null;
    }

    public LocalDateTime convertStringToActualDateFormat(String key, Map message, DateTimeFormatter formatter) {
        try {
            int[] dateTimeArray = Arrays.stream(
                            message.get(key).toString().replaceAll("[\\[\\]]", "") // Remove brackets
                                    .split(",\\s*"))             // Split by comma and optional spaces
                    .mapToInt(Integer::parseInt)         // Convert to int
                    .toArray();

            LocalDateTime localDateTime = LocalDateTime.of(
                    dateTimeArray[0], // Year
                    dateTimeArray[1], // Month
                    dateTimeArray[2], // Day
                    dateTimeArray[3], // Hour
                    dateTimeArray[4]
            );

            return LocalDateTime.parse(localDateTime.format(formatter), formatter);
        } catch (Exception ex) {
            //skip and try with another entry
        }
        return null;
    }
}
