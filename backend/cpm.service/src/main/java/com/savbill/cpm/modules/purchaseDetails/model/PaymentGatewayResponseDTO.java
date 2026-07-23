package com.savbill.cpm.modules.purchaseDetails.model;

import lombok.Data;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;

import java.time.LocalDateTime;

@Data
public class PaymentGatewayResponseDTO extends Auditable implements IBaseDto {
    private Long id;
    private Long pgId;
    private Long purchaseId;
    private String response;
    private LocalDateTime responseDate;
    private Boolean isDeleted = false;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}
}
