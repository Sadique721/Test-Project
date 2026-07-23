package com.savbill.partnermanagement.rabbitmq.master;

import lombok.Data;

@Data
public class SaveCountrySharedDataMessage {

    private Integer id;


    private String name;


    private String status;


//    private List<State> stateList = new ArrayList<>();


    private Boolean isDelete = false;

    private Integer mvnoId;

    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;



}
