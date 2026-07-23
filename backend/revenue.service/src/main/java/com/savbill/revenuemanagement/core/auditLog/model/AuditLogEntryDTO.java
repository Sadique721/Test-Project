package com.savbill.revenuemanagement.core.auditLog.model;

import com.savbill.revenuemanagement.core.dto.common.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuditLogEntryDTO implements IBaseDto {

    private Long auditId;
    private LocalDate auditDate;
    private String userName;
    private Integer userId;
    private String employeeName;
    private Integer employeeId;
    private String module;
    private String operation;
    private String ipAddress;
    private String remark;
    private Long entityRefId;
    private Integer partnerId;
    private Boolean isSender;
    private Double amount;
    private Long toCustomerId;
    
    private Integer mvnoId;
    private String invoiceNumber;
    private Integer invoiceID;
    private  Integer debitDocGraceDays;


    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return auditId;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}

    @Override
    public Long getBuId() {
        return null;
    }

}
