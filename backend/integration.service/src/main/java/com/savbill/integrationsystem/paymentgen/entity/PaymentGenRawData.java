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
@RequiredArgsConstructor
@ToString
@Table(name = "tblmpaymentgenrawdata")
@AllArgsConstructor
public class PaymentGenRawData implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_no", length = 40)
    private Long serialNumber;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "paymentdate")
    private LocalDate paymentdate;

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

    @Column(name = "is_pushed")
    Boolean isPushed;

    @Column(name = "service_area_id")
    Long serviceAreaId;

    @Column(name = "document_number")
    String documentNumber;

    @Column(name = "other_details")
    String otherDetails;

    @Column(name = "payment_mode")
    String paymentMode;

    @Column(name = "payment_source")
    String paymentSource;

    @Column(name = "serial_number_payment_gen_final")
    Long serialNumberPaymentGenFinal;

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


}
