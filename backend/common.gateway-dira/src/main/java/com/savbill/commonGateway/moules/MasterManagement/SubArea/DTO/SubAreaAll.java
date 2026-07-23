package com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO;

import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubAreaAll implements IBaseDto {
    private Long id;
    private String name;
    private String status;
    private Integer mvnoId;
    private Boolean isDeleted;
    private Integer countryId;
    private String countryName;
    private Integer stateId;
    private String stateName;
    private Integer cityId;
    private String cityName;
    private Long pincodeId;
    private String pincode;
    private Long areaId;
    private String areaName;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }
}
