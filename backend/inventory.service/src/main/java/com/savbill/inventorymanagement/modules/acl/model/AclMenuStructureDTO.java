package com.savbill.inventorymanagement.modules.acl.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AclMenuStructureDTO {
    private AclMenuDtoNew data;
    private Boolean expanded = false;
    private List<AclMenuStructureDTO> children;
}
