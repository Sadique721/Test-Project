package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Table(name = "tblmbillgenrawdata")
@AllArgsConstructor
public class BillGenRawData implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_no", length = 40)
    private Long serialNumber;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "added_date")
    private LocalDate addedDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "billing_start_date")
    private LocalDateTime billingStartDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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
    @Column(name = "serial_number_bill_gen_final")
    Long serialNumberBillGenFinal;
    @Column(name = "isdelete")
    private Boolean isdelete;
    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;

    @Column(name = "olt")
    private String olt;
    @Column(name = "pop")
    private String pop;


    @Override
    public Long getPrimaryKey() {
        return this.serialNumber;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isdelete = deleteFlag;

    }

    @Override
    public boolean getDeleteFlag() {
        return isdelete;
    }

//    public BillGenRawData(BillGenMessageData message) {
//        this.addedDate = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
////        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
//        this.billingStartDate = LocalDateTime.parse(message.getBillingStartDate(), formatter);
//        this.billingEndDate = LocalDateTime.parse(message.getBillingEndDate(), formatter);
//        this.transactionType = message.getTransactionType();
//        this.docNumber = message.getDocumentNumber();
//        this.customerName = message.getCustomerName();
//        this.customerUserName = message.getCustomerUserName();
//        this.billGenId = Long.valueOf(message.getBillGenId());
//        this.customerAccountNumber = message.getCustomerAccountNumber();
//        this.customerAccountType = "Retail";
//        this.transactionName = message.getTransactionName();
//        this.branchCode = message.getBranchCode();
//        this.businessCode = message.getBusinessCode();
//        this.ICCode = message.getICCode();
//        this.NAVLedgerId = message.getNAVLedgerId();
//        this.amount = message.getAmount();
//        this.debitDocId = message.getDebitDocId();
//        this.serviceAreaId = message.getServiceAreaId();
//        this.isPushed = false;
////        this.transactionType = message.getTransactionType();
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BillGenRawData that = (BillGenRawData) o;
        return getSerialNumber() != null && Objects.equals(getSerialNumber(), that.getSerialNumber());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public BillGenRawData(LocalDate addedDate, LocalDateTime billingStartDate, LocalDateTime billingEndDate,
                          String transactionType, String docNumber, String customerName, String customerUserName, Long billGenId,
                          String customerAccountNumber, String customerAccountType, String transactionName, String branchCode,
                          String businessCode, String ICCode, String NAVLedgerId, Double amount, Long debitDocId, Integer serviceAreaId,
                          Boolean isPushed, String pushableLedgerId, String olt, String pop) {
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
        this.pushableLedgerId = pushableLedgerId;
        this.olt = olt;
        this.pop = pop;
    }
}
