package com.savbill.revenuemanagement.core.entity.partner;


import com.savbill.revenuemanagement.core.dto.common.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PartnerPaymentDTO implements IBaseDto {

    private Long id;
    private String transcategory;
    private String paymentmode;
    private String refno;
    private Integer partnerId;
    private Double amount = 0.0;
    private String chequenumber;
    private LocalDate chequedate;
    private String remarks;
    private LocalDate paymentdate;
    private String bank_name;
    private String branch_name;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private String status;
    private Integer credit;

    private String onlinesource;
    private Long sourceBank;
    private Long destinationBank;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

    @Override
    public Long getBuId() {
        return null;
    }
}
