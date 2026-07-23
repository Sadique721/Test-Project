package com.savbill.integrationsystem.ReverseBusinessPromotion.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "tblmreversebusinesspromotionrawdata")
public class ReverseBusinessPromotionRawData implements IBaseData<Long> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_no", length = 40)
    private Long serialNumber;
    @Column(name = "added_date")
    private LocalDate addedDate;
    @Column(name = "billing_start_date")
    private LocalDateTime billingStartDate;
    @Column(name = "billing_end_date")
    private LocalDateTime billingEndDate;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "doc_number")
    private String docNumber;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_user_name")
    private String customerUserName;
    @Column(name = "bill_gen_id")
    private Long billGenId;
    @Column(name = "customer_account_number")
    private String customerAccountNumber;
    @Column(name = "customer_account_type")
    private String customerAccountType;
    @Column(name = "transaction_name")
    private String transactionName;
    @Column(name = "branch_code")
    String branchCode;
    @Column(name = "business_code")
    String businessCode;
    @Column(name = "ic_code")
    String ICCode;
    @Column(name = "nav_ledger_id")
    String NAVLedgerId;
    @Column(name = "amount")
    Double amount;
    @Column(name = "debit_doc_id")
    Long debitDocId;
    @Column(name = "service_area_id")
    Integer serviceAreaId;
    @Column(name = "is_pushed")
    Boolean isPushed;
    @Column(name = "serial_number_business_promotion_final")
    Long serialNumberBusinessPromotionFinal;

    @Override
    public Long getPrimaryKey() {
        return this.serialNumber;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public ReverseBusinessPromotionRawData(LocalDate addedDate, LocalDateTime billingStartDate, LocalDateTime billingEndDate,
                                           String transactionType, String docNumber, String customerName, String customerUserName, Long billGenId,
                                           String customerAccountNumber, String customerAccountType, String transactionName, String branchCode,
                                           String businessCode, String ICCode, String NAVLedgerId, Double amount, Long debitDocId, Integer serviceAreaId,
                                           Boolean isPushed) {
        this.addedDate = addedDate;
        this.billingStartDate = billingStartDate;
        this.billingEndDate = billingEndDate;
        this.transactionType = transactionType;
        this.docNumber = docNumber;
        this.customerName = customerName;
        this.customerUserName = customerUserName;
        this.billGenId = billGenId;
        this.customerAccountNumber = customerAccountNumber;
        this.customerAccountType = customerAccountType;
        this.transactionName = transactionName;
        this.branchCode = branchCode;
        this.businessCode = businessCode;
        this.ICCode = ICCode;
        this.NAVLedgerId = NAVLedgerId;
        this.amount = amount;
        this.debitDocId = debitDocId;
        this.serviceAreaId = serviceAreaId;
        this.isPushed = isPushed;
    }


}
