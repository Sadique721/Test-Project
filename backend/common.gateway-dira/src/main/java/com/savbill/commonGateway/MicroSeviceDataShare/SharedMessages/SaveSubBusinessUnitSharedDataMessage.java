package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class SaveSubBusinessUnitSharedDataMessage {


    private Long id;


    private String subbuname;


    private String subbucode;


    private Long businessunitid;

    private Boolean isDeleted;


    private Integer mvnoId;


    private String status;

    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
