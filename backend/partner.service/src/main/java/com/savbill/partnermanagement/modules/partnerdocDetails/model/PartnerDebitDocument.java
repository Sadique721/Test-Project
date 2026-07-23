package com.savbill.partnermanagement.modules.partnerdocDetails.model;

import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data


@ToString
@Table(name = "tblpartnerdebitdocument")
public class PartnerDebitDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debitdocumentid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "debitdocumentnumber", nullable = false, length = 40)
    private String docnumber;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partnerid")
    private Partner partner;

    @Column(name = "billdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime billdate;

    @Column(name = "createdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdate;

    @Column(name = "startdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @Column(name = "enddate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endate;

    @Column(name = "duedate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime duedate;

    @Column(name = "latepaymentdate", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime latepaymentdate;

    @Column(name = "subtotal", nullable = false, length = 40)
    private double subtotal;

    @Column(name = "tax", nullable = false, length = 40)
    private double tax;

    @Column(name = "discount", nullable = false, length = 40)
    private double discount;

    @Column(name = "totalamount", nullable = false, length = 40)
    private double totalamount;

    @Column(name = "previousbalance", nullable = false, length = 40)
    private double previousbalance;

    @Column(name = "latepaymentfee", nullable = false, length = 40)
    private double latepaymentfee;

    @Column(name = "currentpayment", nullable = false, length = 40)
    private double currentpayment;

    @Column(name = "currentdebit", nullable = false, length = 40)
    private double currentdebit;

    @Column(name = "currentcredit", nullable = false, length = 40)
    private double currentcredit;

    @Column(name = "totaldue", nullable = false, length = 40)
    private double totaldue;

    @Column(name = "totalamountinwords", nullable = false, length = 40)
    private String amountinwords;

    @Column(name = "totaldueinwords", nullable = false, length = 40)
    private String dueinwords;

    @Column(name = "partnerbillrunid", nullable = false, length = 40)
    private Integer billrunid;

    @Column(name = "billrunstatus", nullable = false, length = 40)
    private String billrunstatus;

    @Column(name = "xmldocument", nullable = false)
    private String document;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedamount;

    @Column(name = "remark", nullable = false)
    private String remark;
}
