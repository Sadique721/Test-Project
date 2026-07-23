package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import com.savbill.cpm.model.postpaid.Country;
import lombok.Data;

@Data
public class UpdateStateSharedDataMessage {
    private Integer id;

    private String name;

    private String status;

    //@JsonSerialize(using = CountrySerializer.class)
    //@JsonDeserialize(using = CountryDeserializer.class)
    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

}



