package com.savbill.commonGateway.moules.acl.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AclRoleDTO {

    private Long roleid;

    private List<AclRoleOperationsDTO> operations = new ArrayList<AclRoleOperationsDTO>();
}
