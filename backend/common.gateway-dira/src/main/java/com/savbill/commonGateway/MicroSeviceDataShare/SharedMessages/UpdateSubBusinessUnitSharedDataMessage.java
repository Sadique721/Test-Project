package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class UpdateSubBusinessUnitSharedDataMessage {

    private Long id;


    private String subbuname;


    private String subbucode;


    private Long businessunitid;

    private Boolean isDeleted = false;


    private Integer mvnoId;


    private String status;
    private String createdByName;

    private String lastModifiedByName;

    private Integer createdById;
    private Integer lastModifiedById;
}
