package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LightServiceAreaDTO {

    private Long id;

    private String name;

    private Integer createdById;

    private List<Long> pincodes;

    private String serviceAreaStatus;

    public LightServiceAreaDTO(Long id , String name , Integer createdById, String serviceAreaStatus){
        this.id = id;
        this.name = name;
        this.createdById = createdById;
        this.serviceAreaStatus= serviceAreaStatus;
    }


}
