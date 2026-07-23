package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.model.ProductWarehouseMapViewDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaViewDTO;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.TeamsViewDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WarehouseViewDto implements IBaseDto {
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
    List<ServiceAreaViewDTO> serviceAreaNameList = new ArrayList<>();
    List<ServiceAreaViewDTO> parenetServiceAreaNameList = new ArrayList<>();
    String warehouseType;
    String rmsWarehouseId;
    String navWarehouseId;
    String barnchName;
    List<TeamsViewDTO> teamsList =  new ArrayList<>();
    String warehouseCode;
    List<ProductWarehouseMapViewDTO> productWarehouseMapViewDTOS = new ArrayList<>();

    @Override
    public Long getIdentityKey() {
        return id;
    }
}
