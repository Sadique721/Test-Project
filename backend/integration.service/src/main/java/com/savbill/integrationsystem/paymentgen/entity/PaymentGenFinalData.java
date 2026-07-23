package com.savbill.integrationsystem.paymentgen.entity;

import com.savbill.integrationsystem.core.data.IBaseData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "tblmpaymentgenfinaldata")
public class PaymentGenFinalData implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_no", length = 40)
    private Long serialNumber;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "paymentdate")
    private LocalDate paymentDate;

    @Column(name = "amount")
    Double amount;


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
    Long serviceAreaId;

    @Column(name = "document_number")
    String documentNumber;

    @Column(name = "service_area_name")
    String serviceAreaName;

    @Column(name = "payment_mode")
    String paymentMode;

    @Column(name = "other_details")
    String otherDetails;


    @Transient
    String primaryKey;
    @Transient
    String isSelected;

    @Column(name = "olt")
    private String olt;
    @Column(name = "pop")
    private String pop;

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

    public PaymentGenFinalData(LocalDate paymentdate, Double amount, String branchCode, String businessCode, String ICCode, String NAVLedgerId, Long totalRecords, Long serviceAreaId, String serviceAreaName, String paymentmode, String otherdetails
            , String olt, String pop) {
        this.paymentDate = paymentdate;
        this.amount = amount;
        this.branchCode = branchCode;
        this.businessCode = businessCode;
        this.ICCode = ICCode;
        this.NAVLedgerId = NAVLedgerId;
        this.totalRecords = totalRecords;
        this.isPushed = false;
        this.serviceAreaId = serviceAreaId;
        this.serviceAreaName = serviceAreaName;
        this.paymentMode = paymentmode;
        this.otherDetails = otherdetails;
        this.pop = pop;
        this.olt = olt;

    }
}
