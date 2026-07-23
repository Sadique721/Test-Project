package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMappingDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BuildingMgmtMessage {

    private Long buildingMgmtId;
    private String buildingName;
    private Integer pincodeId;
    private Integer areaId;
    private Integer subAreaId;
    private Integer mvnoId;
    private Integer buid;
    private Boolean isDeleted;
    private List<BuildingMappingDTO> buildingMappings;
    private String buildingType;
}
