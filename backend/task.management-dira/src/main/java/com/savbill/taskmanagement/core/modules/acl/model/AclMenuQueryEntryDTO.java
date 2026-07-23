package com.savbill.taskmanagement.core.modules.acl.model;

import lombok.Data;

import java.util.List;

@Data
public class AclMenuQueryEntryDTO {
    private Long menuid;
    private String name;
    private String dispName;
    private Long classid;
    private Long parentid;
    private Long level;
    private Long aclid;
    private List<Integer> permits;
}
