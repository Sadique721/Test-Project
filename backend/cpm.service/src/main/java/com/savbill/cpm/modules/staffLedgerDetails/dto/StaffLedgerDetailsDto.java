package com.savbill.cpm.modules.staffLedgerDetails.dto;

import com.savbill.cpm.core.dto.IBaseDto2;
import com.savbill.cpm.model.common.StaffUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffLedgerDetailsDto implements IBaseDto2 {

    private Integer id;

    StaffUser staffUser;

    private Long custId;

    private Long creditDocId;

    private String paymentMode;

    private String transactionType;

    private Double amount;

    private String action;

    private Long bankId;

    private String bankName;
    private String remarks;

    private LocalDate date;

    private List<Integer> ledgerIds = new ArrayList<>();

    private List<Double> amountList = new ArrayList<>();

    private String status;

    private LocalDate chequedate;

    private String chequeno; //ChequeNo

    private String currency;


    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }

    public String staffLedgerDetailsname() {
        return staffLedgerDetailsname();
    }

    public void setBuId(Long aLong) {
    }
}
