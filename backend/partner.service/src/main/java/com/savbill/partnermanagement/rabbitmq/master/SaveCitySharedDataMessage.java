package com.savbill.partnermanagement.rabbitmq.master;

import com.savbill.partnermanagement.modules.MasterManagement.State.State;
import lombok.Data;

@Data
public class SaveCitySharedDataMessage {

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
