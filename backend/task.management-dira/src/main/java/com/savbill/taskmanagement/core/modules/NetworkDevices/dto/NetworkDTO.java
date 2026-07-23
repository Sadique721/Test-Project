package com.savbill.taskmanagement.core.modules.NetworkDevices.dto;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NetworkDTO extends Auditable implements IBaseDto
{
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String devicetype;
    @NotNull
    private String status;
    private Boolean isDeleted = false;
//    @NotNull
//    private ServiceAreaDTO servicearea;
//
//    @JsonManagedReference
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private List<OLTSlotDetailDTO> oltslotsList=new ArrayList<>();
    private Integer mvnoId;
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
