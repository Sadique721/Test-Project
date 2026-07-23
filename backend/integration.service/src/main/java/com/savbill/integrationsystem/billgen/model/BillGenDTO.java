package com.savbill.integrationsystem.billgen.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class BillGenDTO extends Auditable<Long> implements IBaseDto {

    private Long serialNumber;
    private LocalDate addedDate;
    private LocalDateTime billingStartDate;
    private LocalDateTime billingEndDate;
    private String transactionType;
    private String docNumber;
    private String customerName;
    private String customerUserName;
    private Long billGenId;
    private String customerAccountNumber;
    private String customerAccountType;
    private String transactionName;
    String branchCode;
    String businessCode;
    String ICCode;
    String NAVLedgerId;
    Double amount;
    Long debitDocId;
    Integer serviceAreaId;
    Boolean isPushed;
    Integer serialNumberBillGenFinal;
    private Boolean isdelete;
    String serviceAreaName;


    @Override
    public Long getIdentityKey() {
        return serialNumber;
    }

    @Override
    public Long getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Long mvnoId) {

    }
}
