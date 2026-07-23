package com.savbill.revenuemanagement.core.entity.customers;

import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustChargeDetailsRevenue;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@ToString
@Table(name = "tblcustchargedtls")
@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustChargeDetails extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cstchargeid", nullable = false, length = 40)
    private Integer id;


    public CustChargeDetails() {
    }

    @Column(name = "validity", length = 4)
    private Double validity;

    @Column(name = "planid", length = 40)
    private Integer planid;

    @Column(name = "chargeid", length = 40)
    private Integer chargeid;

    @Column(name = "chargetype", nullable = false, length = 40)
    private String chargetype;

    @Column(name = "price", nullable = false, length = 40)
    private Double price;

    @Column(name = "actual_price", nullable = false, length = 40)
    private Double actualprice;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "charge_date")
    private LocalDateTime charge_date;

    @Column(name = "startdate")
    private LocalDateTime startdate;

    @Column(name = "enddate")
    private LocalDateTime enddate;

    @Column(name = "taxamount")
    private Double taxamount;

    @Column(name = "is_reversed")
    private Boolean is_reversed;

    @Column(name = " rev_date")
    private LocalDateTime rev_date;

    @Column(name = " rev_amt")
    private Double rev_amt;

    @Column(name = " rev_remarks")
    private String rev_remarks;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custid")
    private Customers customer;

    private Boolean isUsed;

    @Column(name = "purchase_entity_id")
    private Long purchaseEntityId;

    @Column(name = "debitdocid")
    private Long debitdocid;

    private Long ippooldtlsid;

    @Column(name = "billable_cust_id")
    private Integer billableCustomerId;

    @Column(name = "type", nullable = false, length = 40)
    private String type;
    
    @Column(name = "planvalidity", length = 4)
    private Integer planValidity;
    
    @Column(name = "unitsofvalidity", length = 40)
    private String unitsOfValidity;
    
    @Column(name = "taxid",length = 40)
    private Integer taxId;
    
    @Column(name = "custpackageid", nullable = false, length = 40)
    private Integer custPlanMapppingId;
    
    @Column(name = "lastbilldate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastBillDate;
    
    @Column(name = "nextbilldate", length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;
    
    @Column(name = "billingcycle")
    private Integer billingCycle;
    
    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Column(name = "dbr", length = 40)
    private Double dbr = 0.0;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;

    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "new_amount")
    private Double newAmount;

    @Column(name = "static_ip_address")
    private String staticIPAdrress ;

    @Column(name="connection_no")
    private String connection_no;

    @Column(name = "installment_frequency", length = 20)
    private String installmentFrequency; // MONTHLY, QUARTERLY, ANNUALLY

    @Column(name = "installment_no")
    private Integer installmentNo;

    @Column(name = "total_installments")
    private Integer totalInstallments;

    @Column(name = "amount_per_installment", precision = 20, scale = 4)
    private BigDecimal amountPerInstallment;

    @Column(name = "installment_start_date")
    private LocalDate installmentStartDate;

    @Column(name = "next_installment_date")
    private LocalDate nextInstallmentDate;

    @Column(name = "last_installment_date")
    private LocalDate lastInstallmentDate;

    @Column(name = "installment_enabled")
    private Boolean installmentEnabled;

    public CustChargeDetails(CustChargeDetailsRevenue custChargeDetails,Customers customers) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        this.id =custChargeDetails.getId();
        this.validity =custChargeDetails.getValidity();
        this.planid =custChargeDetails.getPlanid();
        this.chargeid =custChargeDetails.getChargeid();
        this.chargetype =custChargeDetails.getChargetype();
        this.price =custChargeDetails.getPrice();
        this.actualprice =custChargeDetails.getActualprice();
        if (custChargeDetails.getCharge_date()!=null) {
            this.charge_date = DateTimeUtil.getLocaldateTimefromString(custChargeDetails.getCharge_date());//LocalDateTime.parse(custChargeDetails.getCharge_date(), formatter);
        }
        if (custChargeDetails.getStartdate()!=null) {
            this.startdate = DateTimeUtil.getLocaldateTimefromString(custChargeDetails.getStartdate());//LocalDateTime.parse(custChargeDetails.getStartdate(), formatter);
        }
        if (custChargeDetails.getEnddate()!=null) {
            this.enddate = DateTimeUtil.getLocaldateTimefromString(custChargeDetails.getEnddate());//LocalDateTime.parse(custChargeDetails.getEnddate(), formatter2);
        }
        this.taxamount =custChargeDetails.getTaxamount();
        this.is_reversed =custChargeDetails.getIs_reversed();
        if (custChargeDetails.getRev_date()!=null) {
            this.rev_date = DateTimeUtil.getLocaldateTimefromString(custChargeDetails.getRev_date());//LocalDateTime.parse(custChargeDetails.getRev_date(), formatter);
        }
        this.rev_amt =custChargeDetails.getRev_amt();
        this.customer =customers;
        this.isUsed =custChargeDetails.getIsUsed();
        this.purchaseEntityId =custChargeDetails.getPurchaseEntityId();
        this.debitdocid =custChargeDetails.getDebitdocid();
        this.billableCustomerId =custChargeDetails.getBillableCustomerId();
        this.type =custChargeDetails.getType();
        this.planValidity =custChargeDetails.getPlanValidity();
        this.unitsOfValidity =custChargeDetails.getUnitsOfValidity();
        this.taxId =custChargeDetails.getTaxId();
        this.custPlanMapppingId =custChargeDetails.getCustPlanMapppingId();
        if (custChargeDetails.getLastBillDate()!=null) {
            this.lastBillDate = LocalDate.parse(custChargeDetails.getLastBillDate());
        }
        if (custChargeDetails.getNextBillDate()!=null) {
            this.nextBillDate = LocalDate.parse(custChargeDetails.getNextBillDate());
        }
        this.billingCycle =custChargeDetails.getBillingCycle();
        this.isDeleted =custChargeDetails.getIsDeleted();
        this.dbr =custChargeDetails.getDbr();
        if(custChargeDetails.getDiscount()!=null) {
            this.discount = custChargeDetails.getDiscount();
        }else{
            this.discount=0.0;
        }
        this.isInvoiceToOrg =custChargeDetails.getIsInvoiceToOrg();
        this.billTo =custChargeDetails.getBillTo();
        this.newAmount =custChargeDetails.getNewAmount();
        this.staticIPAdrress =custChargeDetails.getStaticIPAdrress();
        this.connection_no =custChargeDetails.getConnection_no();
        this.installmentFrequency = custChargeDetails.getInstallmentFrequency();
        this.installmentNo=custChargeDetails.getInstallmentNo();
        this.totalInstallments=custChargeDetails.getTotalInstallments();
        this.amountPerInstallment= custChargeDetails.getAmountPerInstallment();
        if (custChargeDetails.getInstallmentStartDate()!=null) {
            this.installmentStartDate = LocalDate.parse(custChargeDetails.getInstallmentStartDate());
        }
        if (custChargeDetails.getNextInstallmentDate()!=null) {
            this.nextInstallmentDate = LocalDate.parse(custChargeDetails.getNextInstallmentDate());
        }
        if (custChargeDetails.getLastInstallmentDate()!=null) {
            this.lastInstallmentDate = LocalDate.parse(custChargeDetails.getLastInstallmentDate());
        }
        if (custChargeDetails.getInstallmentEnabled()!=null) {
            this.installmentEnabled = custChargeDetails.getInstallmentEnabled();
        }
    }


}
