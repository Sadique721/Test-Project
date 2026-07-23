package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UpdatePincodeSharedDataMessage {

    private Long id;

    private String pincode;

    private String status;

    private Boolean isDeleted = false;


    private Integer countryId;


    private Integer cityId;


    private Integer stateId;


    //private List<Area> areaList = new ArrayList<>();


    private Integer mvnoId;
    private Integer createdById;
    @JsonIgnore
    private Integer lastModifiedById;
    @JsonIgnore
    private String createdByName;
    @JsonIgnore
    private String lastModifiedByName;
}
