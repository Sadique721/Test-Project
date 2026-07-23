package com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubAreaDTO extends Auditable implements IBaseDto {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted;
    private Integer countryId;
    private Integer cityId;
    private Integer stateId;
    private Integer mvnoId;
    private Long buId;
    private Long areaId;
    private String filename;
    private String uniquename;
    private Long pincodeId;
    private String pincode;
    private String countryName;
    private String stateName;
    private String cityName;
    private String areaName;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    public SubAreaDTO(Long id, String name, String status, Integer mvnoId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.mvnoId = mvnoId;
    }

    public SubAreaDTO(Long id, String name, String status, Integer mvnoId,
                      Long areaId, String areaName, Integer cityId, Integer stateId, Integer countryId, Boolean isDeleted,
                      Long pincodeId, String pincode, String countryName, String stateName, String cityName) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.mvnoId = mvnoId;
        this.areaId = areaId;
        this.areaName = areaName;
        this.cityId = cityId;
        this.stateId = stateId;
        this.countryId = countryId;
        this.isDeleted = isDeleted;
        this.pincodeId = pincodeId;
        this.pincode = pincode;
        this.countryName = countryName;
        this.stateName = stateName;
        this.cityName = cityName;
    }
}
