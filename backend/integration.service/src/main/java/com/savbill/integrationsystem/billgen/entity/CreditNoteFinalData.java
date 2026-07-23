package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmcreditnotefinaldata")
public class CreditNoteFinalData implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_no", length = 40)
    private Long serialNumber;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "added_date")
    private LocalDate addedDate;

    @Column(name = "amount")
    Double amount;

    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "branch_code")
    String branchCode;
    @Column(name = "business_code")
    String businessCode;
    @Column(name = "ic_code")
    String ICCode;
    @Column(name = "nav_ledger_id")
    String NAVLedgerId;

    @Column(name = "total_records")
    Long totalRecords;
    @Column(name = "is_pushed")
    Boolean isPushed;

    @Column(name = "service_area_id")
    Integer serviceAreaId;
    @Column(name = "document_number")
    String documentNumber;

    @Column(name = "service_area_name")
    String serviceAreaName;


    @Transient
    String primaryKey;
    @Transient
    String isSelected;

    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;


    @Override
    public Long getPrimaryKey() {
        return serialNumber;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    public CreditNoteFinalData(LocalDate addedDate, String businessCode,
                               String icCode,
                               String branchCode,
                               String navLedgerId,
                               Integer serviceAreaId,
                               Long totalRecords,
                               Double totalAmount, String transactionType, String serviceAreaName, String pushableLedgerId
    ) {
        this.addedDate = addedDate;
        this.amount = totalAmount;
        this.branchCode = branchCode;
        this.ICCode = icCode;
        this.NAVLedgerId = navLedgerId;
        this.totalRecords = totalRecords;
        this.serviceAreaId = serviceAreaId;
        this.isPushed = false;
        this.businessCode = businessCode;
        this.transactionType = transactionType;
        this.serviceAreaName = serviceAreaName;
        this.pushableLedgerId = pushableLedgerId;
    }
}

