package com.savbill.taskmanagement.core.modules.NetworkDevices.dto;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NetworkDeviceDTO extends Auditable implements IBaseDto {
    private Long id;
    @NotNull
    private String name;

    Long productId;
    Long inwardId;

    @NotNull
    private String devicetype;
    @NotNull
    private String status;
    private String latitude;
    private String longitude;
    private Boolean isDeleted = false;
//    @NotNull
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private ServiceAreaDTO servicearea;

//    List<Long> serviceAreaIdsList;
//    List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();

    private Integer mvnoId;

//    private Long parentNetworkDeviceId;
    private Integer availableInPorts;
    private Integer totalInPorts;
    private Integer availableOutPorts;
    private Integer totalOutPorts;
    private Integer totalPorts;


    private Integer availablePorts;

    private Long itemId;

    private Long custInventoryId;

    private Long inventorymappingId;

    private String productName;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
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
