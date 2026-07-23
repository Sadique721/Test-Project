package com.savbill.integrationsystem.billgen.entity;
import lombok.Data;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class SaveBranchSharedDataMessage {

    private Long id;
    private String name;
    private String status;
    private String branch_code;
    private Boolean isDeleted = false;
    private Integer mvnoId;
}
