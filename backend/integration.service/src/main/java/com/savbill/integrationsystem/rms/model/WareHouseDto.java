package com.savbill.integrationsystem.rms.model;

import com.savbill.integrationsystem.billgen.model.ServiceAreaDTO;
import com.savbill.integrationsystem.billgen.model.TeamsDTO;
import com.savbill.integrationsystem.core.dto.IBaseDto;
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
    Long mvnoId;
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
    @Override
    public Long getIdentityKey() {
        return id;
    }
    @Override
    public Long getMvnoId() {
        return mvnoId;
    }
}
