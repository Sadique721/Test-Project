package com.savbill.notification.rabbitmq.message;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class SaveBusinessUnitSharedDataMessage {


    private Long id;

    private String buname;

    private String bucode;

    private String status;


    private String planBindingType;


    private Boolean isDeleted ;


    private Integer mvnoId;


    private Integer createdById;
    private Integer lastModifiedById;
}
