package com.savbill.commonGateway.moules.acl.model;

import lombok.Data;

@Data
public class AclOperationsDTO {

    private Long id;

    private String opName;

    private Long classid;
    
    private Long parentOperationId;
    
    private boolean isVisible;
    
    private boolean accessible = false;

}
