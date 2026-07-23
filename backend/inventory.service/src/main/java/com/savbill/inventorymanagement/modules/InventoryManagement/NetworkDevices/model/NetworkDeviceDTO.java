package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDeviceDTO extends Auditable implements IBaseDto {
    private Long id;
    @NotNull
    private String name;
    private String displayname;

    Long productId;
    Long inwardId;

    @NotNull
    private String devicetype;
    @NotNull
    private String status;
    private String latitude;
    private String longitude;
    private Boolean isDeleted = false;
//    private ServiceAreaDTO servicearea;

    List<Long> serviceAreaIdsList;
    @NotNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();

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

//    @Override
//    public Long getBuId() {
//        return null;
//    }


    public NetworkDeviceDTO(Long id, String name, String displayname, Long productId, String devicetype, String status, String latitude, String longitude, Boolean isDeleted, Integer mvnoId, Long itemId, Long custInventoryId, Long inventorymappingId) {
        this.id = id;
        this.name = name;
        this.displayname = displayname;
        this.productId = productId;
        this.devicetype = devicetype;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDeleted = isDeleted;
        this.mvnoId = mvnoId;
        this.itemId = itemId;
        this.custInventoryId = custInventoryId;
        this.inventorymappingId = inventorymappingId;
    }
}
