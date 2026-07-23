package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    @NotNull
    private ServiceAreaDTO servicearea;

    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OLTSlotDetailDTO> oltslotsList=new ArrayList<>();
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

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
