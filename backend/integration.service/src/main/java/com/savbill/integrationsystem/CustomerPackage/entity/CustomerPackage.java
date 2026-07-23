package com.savbill.integrationsystem.CustomerPackage.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblcustpackagerel")
public class CustomerPackage {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid")
    private Long custPackageId;
    @Column(name = "custid")
    private Integer custid;
    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;
    @Column(name = "startdate")
    private LocalDateTime startDate;
    @Column(name = "enddate")
    private LocalDateTime endDate;
    @Column(name = "expirydate")
    private LocalDateTime expiryDate;
    private String status;
    @Column(name = "qospolicyid")
    private String qospolicyid;
    private String uploadqos;
    private String downloadqos;
    private String uploadts;
    private String downloadts;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "discount")
    private Double discount;


    @Column(columnDefinition = "Boolean default false",name = "isinvoicestop")
    private Boolean isinvoicestop = false;

    @Column(columnDefinition = "Boolean default false",name = "istrialplan")
    private Boolean istrialplan = false;

    @Column(name = "dbr")
    private Double dbr=0.0;

    @Column(name = "is_invoice_to_org")
    private boolean isInvoiceToOrg;

    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "next_approver")
    private Integer nextApprover;

    @Column(name = "debitdocid")
    private Integer debitdocid;

    @Column(name = "next_staff")
    private Integer nextStaff;

//    @Column(name = "staff_approver_status")
//    private String staffapproverstatus;

    private String service;

    private Integer traildebitdocid;

    @Column(name = "is_trial_validity", length = 4)
    private Double isTrialValidityDays;

    @Column(name = "trial_plan_validity_count")
    private Integer trialPlanValidityCount = 0;

    @Column(name = "custservicemappingid", nullable = false)
    private Integer custServiceMappingId;

    @Column(name = "invoice_type")
    private String invoiceType;

    public CustomerPackage(Map message){
//        Map<String, Object> message = custPackageRelMessage.getData();
        if (message.get("id") != null)
            this.custPackageId = Long.parseLong(message.get("id").toString());
//        if(message.get("customer") != null)
//            this.customer = new Customers((Map)message.get("customer"));
        if (message.get("custid") != null)
            this.custid = Integer.parseInt(message.get("custid").toString());
        if (message.get("planId") != null)
            this.planId = Integer.parseInt(message.get("planId").toString());
        if (message.get("startDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.startDate = LocalDateTime.parse(message.get("startDate").toString(), formatter);
        }
        if (message.get("endDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.endDate = LocalDateTime.parse(message.get("endDate").toString(), formatter);
        }
        if (message.get("expiryDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.expiryDate = LocalDateTime.parse(message.get("expiryDate").toString(), formatter);
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
            this.uploadts = message.get("uploadts").toString();
        if (message.get("downloadts") != null)
            this.downloadts = message.get("downloadts").toString();
//        if (message.get("quotaList") != null)
//            this.id = Integer.parseInt(message.get("quotaList").toString());
        if (message.get("service") != null)
            this.service = message.get("service").toString();
        if (message.get("isDelete") != null)
            this.isDelete = Boolean.parseBoolean(message.get("isDelete").toString());
        if (message.get("discount") != null)
            this.discount = Double.parseDouble(message.get("discount").toString());
        if (message.get("isinvoicestop") != null)
            this.isinvoicestop = Boolean.parseBoolean(message.get("isinvoicestop").toString());
        if (message.get("istrialplan") != null)
            this.istrialplan = Boolean.parseBoolean(message.get("istrialplan").toString());
        if (message.get("isInvoiceToOrg") != null)
            this.isInvoiceToOrg = Boolean.parseBoolean(message.get("isInvoiceToOrg").toString());
        if (message.get("isTrialValidityDays") != null)
            this.isTrialValidityDays = Double.parseDouble(message.get("isTrialValidityDays").toString());
        if (message.get("dbr") != null)
            this.dbr = Double.parseDouble(message.get("dbr").toString());
        if (message.get("isTrialValidityDays") != null)
            this.isTrialValidityDays = Double.parseDouble(message.get("isTrialValidityDays").toString());
        if (message.get("nextApprover") != null)
            this.nextApprover = Integer.parseInt(message.get("nextApprover").toString());
        if (message.get("debitdocid") != null)
            this.debitdocid = Integer.parseInt(message.get("debitdocid").toString());
        if (message.get("traildebitdocid") != null)
            this.traildebitdocid = Integer.parseInt(message.get("traildebitdocid").toString());
        if (message.get("nextStaff") != null)
            this.nextStaff = Integer.parseInt(message.get("nextStaff").toString());
        if (message.get("traildebitdocid") != null)
            this.traildebitdocid = Integer.parseInt(message.get("traildebitdocid").toString());
        if (message.get("trialPlanValidityCount") != null)
           this.trialPlanValidityCount = Integer.parseInt(message.get("trialPlanValidityCount").toString());
        if (message.get("billTo") != null)
            this.billTo = message.get("billTo").toString();
        if (message.get("custServiceMappingId") != null)
            this.custServiceMappingId = Integer.parseInt(message.get("custServiceMappingId").toString());
        if (message.get("invoiceType") != null)
            this.invoiceType = message.get("invoiceType").toString();

//        if (message.get("mvnoId") != null)
//            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());




    }



}
