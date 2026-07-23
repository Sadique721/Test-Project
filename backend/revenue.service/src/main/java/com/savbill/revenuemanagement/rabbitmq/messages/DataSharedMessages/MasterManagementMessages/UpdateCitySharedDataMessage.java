package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages;


import com.savbill.revenuemanagement.mastermanagement.State.domian.State;

import lombok.Data;

@Data
public class UpdateCitySharedDataMessage {

    private Integer id;


    private String name;


    private String status;


    private Integer countryId;


    private State state;


    private Boolean isDelete;


    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
