package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;

import com.savbill.ticketmanagement.core.modules.State.domian.State;
import lombok.Data;

@Data
public class SaveCitySharedDataMessage {
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
