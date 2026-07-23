package com.savbill.salescrmsbss.rabbitMq.message;


import com.savbill.salescrmsbss.entity.State;
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
