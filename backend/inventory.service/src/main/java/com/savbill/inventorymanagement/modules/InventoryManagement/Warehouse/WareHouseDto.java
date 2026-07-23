package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.model.ProductWarehouseMappingDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaDTO;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.TeamsDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WareHouseDto implements IBaseDto {
    Long id;
    String name;
    String description;
    String status;
    String address1;
    String address2;
    String pincode;
    String city;
    String state;
    String country;
    String longitude;
    String latitude;
    Integer mvnoId;
    List<Long> serviceAreaIdsList;
    List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();
    List<Long> parentServiceAreaIdsList;
    List<ServiceAreaDTO> parenetServiceAreaNameList = new ArrayList<>();
    String warehouseType;
    private String rmsWarehouseId;
    private String navWarehouseId;
    private Long branchId;
    private List<TeamsDTO> teamsList =  new ArrayList<>();
    private List<TeamsDTO> teamsDTOList = new ArrayList<>();
    private List<Long> teamsIdsList = new ArrayList<>();
    private String warehouseCode;
    private List<ProductWarehouseMappingDTO> productWarehouseMappingDTOS = new ArrayList<>();
    @Override
    public Long getIdentityKey() {
        return id;
    }
    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    public WareHouseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public WareHouseDto() {
        this.serviceAreaNameList = new ArrayList<>();
        this.parenetServiceAreaNameList = new ArrayList<>();
        this.teamsList = new ArrayList<>();
        this.teamsDTOList = new ArrayList<>();
        this.teamsIdsList = new ArrayList<>();
    }
//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
