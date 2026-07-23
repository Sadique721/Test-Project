package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;



import com.savbill.ticketmanagement.core.modules.Branch.domain.Branch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRegionSharedDataMessage {
    private Long id;
    private String rname;
    @JsonIgnore
    private List<Branch> branchidList;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    @JsonIgnore
    private Integer createdById;
    @JsonIgnore
    private Integer lastModifiedById;
    @JsonIgnore
    private String createdByName;
    @JsonIgnore
    private String lastModifiedByName;
}
