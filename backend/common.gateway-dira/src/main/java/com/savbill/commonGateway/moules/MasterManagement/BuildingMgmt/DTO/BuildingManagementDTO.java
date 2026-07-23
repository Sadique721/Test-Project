package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO;

import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BuildingManagementDTO  implements IBaseDto {
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

    public BuildingManagementDTO(Long buildingMgmtId, String buildingName, Integer pincodeId, Integer areaId,
                                 Integer subAreaId, Integer mvnoId, Integer buid, Boolean isDeleted,
                                 Long buildingMappingId, String buildingNumber, Long buildingManagementId, Boolean buildingMappingIsDeleted, String buildingType) {  // FIXED: Correct argument order
        this.buildingMgmtId = buildingMgmtId;
        this.buildingName = buildingName;
        this.pincodeId = pincodeId;
        this.areaId = areaId;
        this.subAreaId = subAreaId;
        this.mvnoId = mvnoId;
        this.buid = buid;
        this.isDeleted = isDeleted;
        this.buildingMappings = new ArrayList<>();
        this.buildingType = buildingType;

        if (buildingMappingId != null) {
            this.buildingMappings.add(new BuildingMappingDTO(buildingMappingId, buildingNumber, buildingManagementId, buildingMappingIsDeleted)); // FIXED: Correct parameter passing
        }
    }



    @Override
    public Long getIdentityKey() {
        return this.buildingMgmtId;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId=mvnoId;
    }
}
