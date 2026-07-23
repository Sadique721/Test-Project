package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Table(name = "tblmcreditnotegenrawdata")
@AllArgsConstructor
public class CreditNoteGenRawData implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_no", length = 40)
    private Long serialNumber;
    @Column(name = "added_date")
    private LocalDate addedDate;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "doc_number")
    private String docNumber;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_user_name")
    private String customerUserName;
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
    @Column(name = "credit_doc_id")
    Long creditDocId;
    @Column(name = "service_area_id")
    Integer serviceAreaId;
    @Column(name = "is_pushed")
    Boolean isPushed;
    @Column(name = "serial_number_credit_note_final")
    Long serialNumberCreditNotFinal;

    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;

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

    public CreditNoteGenRawData(LocalDate addedDate, String transactionType, String docNumber, String customerName, String customerUserName,
                                String customerAccountNumber, String customerAccountType, String transactionName, String branchCode,
                                String businessCode, String ICCode, String NAVLedgerId, Double amount, Long creditDocId, Integer serviceAreaId,
                                Boolean isPushed, String pushableLedgerId) {
        this.addedDate = addedDate;
        this.transactionType = transactionType;
        this.docNumber = docNumber;
        this.customerName = customerName;
        this.customerUserName = customerUserName;
        this.customerAccountNumber = customerAccountNumber;
        this.customerAccountType = customerAccountType;
        this.transactionName = transactionName;
        this.branchCode = branchCode;
        this.businessCode = businessCode;
        this.ICCode = ICCode;
        this.NAVLedgerId = NAVLedgerId;
        this.amount = amount;
        this.creditDocId = creditDocId;
        this.serviceAreaId = serviceAreaId;
        this.isPushed = isPushed;
        this.pushableLedgerId = pushableLedgerId;
    }
}
