package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import com.savbill.cpm.modules.BuildingMgmt.DTO.BuildingMappingDTO;
import lombok.Data;

import java.util.List;

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
