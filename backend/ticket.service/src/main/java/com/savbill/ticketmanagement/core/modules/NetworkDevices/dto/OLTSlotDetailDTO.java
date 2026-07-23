package com.savbill.ticketmanagement.core.modules.NetworkDevices.dto;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OLTSlotDetailDTO extends Auditable implements IBaseDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String status;
    private Boolean isDeleted = false;

//    @JsonManagedReference
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private List<OLTPortDTO> oltPortDetailsList = new ArrayList<>();

    private Long networkId;

    private Integer mvnoId;
    
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
