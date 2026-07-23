package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO;

import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingMappingDTO implements IBaseDto {
    private Long id;
    private String buildingNumber;
    private Long buildingMgmtId;
    private Boolean isDeleted=false;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
    }


}
