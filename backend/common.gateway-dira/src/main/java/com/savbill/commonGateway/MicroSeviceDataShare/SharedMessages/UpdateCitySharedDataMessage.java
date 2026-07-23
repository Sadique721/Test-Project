package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;


import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import lombok.Data;

@Data
public class UpdateCitySharedDataMessage {

    private Integer id;


    private String name;


    private String status;


    private Integer countryId;


    private State state;


    private Boolean isDelete = false;


    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
