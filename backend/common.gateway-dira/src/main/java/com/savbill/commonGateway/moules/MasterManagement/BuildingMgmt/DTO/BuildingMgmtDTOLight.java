package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class BuildingMgmtDTOLight {
    private Long buildingMgmtId;
    private String buildingName;

    public BuildingMgmtDTOLight(Long buildingMgmtId, String buildingName) {
        this.buildingMgmtId = buildingMgmtId;
        this.buildingName = buildingName;
    }

    // Getters and Setters
}
