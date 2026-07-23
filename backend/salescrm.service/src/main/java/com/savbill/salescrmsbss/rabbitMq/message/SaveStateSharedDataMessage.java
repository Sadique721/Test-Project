package com.savbill.salescrmsbss.rabbitMq.message;


import com.savbill.salescrmsbss.entity.Country;
import lombok.Data;

@Data

public class SaveStateSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;

    private Integer createdById;
    private Integer lastModifiedById;

    private String createdByName;

    private String lastModifiedByName;

}
