package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages;

import lombok.Data;

@Data
public class SavePincodeSharedDataMessage {

    private Long id;

    private String pincode;

    private String status;

    private Boolean isDeleted ;


    private Integer countryId;


    private Integer cityId;


    private Integer stateId;


    //private List<Area> areaList = new ArrayList<>();


    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

}
