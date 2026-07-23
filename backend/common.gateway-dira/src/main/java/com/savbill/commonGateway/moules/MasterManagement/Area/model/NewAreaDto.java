package com.savbill.commonGateway.moules.MasterManagement.Area.model;

import lombok.Data;

@Data
public class NewAreaDto {
    private Long id;
    private String name;
    private Integer countryId;
    private Integer cityId;
    private Integer stateId;
    private Long pincodeId;


    public NewAreaDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public NewAreaDto(Long id, String name, Integer countryId, Integer cityId, Integer stateId, Long pincodeId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
        this.cityId = cityId;
        this.stateId = stateId;
        this.pincodeId = pincodeId;
    }
}
