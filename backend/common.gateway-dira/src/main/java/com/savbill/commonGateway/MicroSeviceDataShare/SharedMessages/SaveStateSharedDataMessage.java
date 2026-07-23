package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;


import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
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
