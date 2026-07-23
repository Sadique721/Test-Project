package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.dto;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.dto.common.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PaymentGatewayDTO extends Auditable implements IBaseDto {

    private Long id;
    private String returnurl;
    private String pgurl;
    private String name;
    private String prefix;
    private Boolean partnerenableflag = false;
    private Boolean userenableflag = false;
    private Boolean isDeleted = false;
    private String status;
    private Integer mvnoId;
    
    

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }



	@Override
	public Integer getMvnoId() {
		return null;
	}

    @Override
    public Long getBuId() {
        return null;
    }
}
