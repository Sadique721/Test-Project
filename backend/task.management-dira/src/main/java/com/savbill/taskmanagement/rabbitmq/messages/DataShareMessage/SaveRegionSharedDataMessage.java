package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;


import com.savbill.taskmanagement.core.modules.Branch.domain.Branch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class SaveRegionSharedDataMessage {
    private Long id;
    private String rname;
    @JsonIgnore
    private List<Branch> branchidList;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
//    @JsonIgnore
    private String serviceAreaNameList;
    private String lastModifiedByName;
}
