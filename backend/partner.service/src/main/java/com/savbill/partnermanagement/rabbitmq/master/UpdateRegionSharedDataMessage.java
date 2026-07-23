package com.savbill.partnermanagement.rabbitmq.master;
import com.savbill.partnermanagement.modules.MasterManagement.Branch.Branch;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRegionSharedDataMessage {
    private Long id;
    private String rname;
    private List<Branch> branchidList;
    private String status;
    private Boolean isDeleted;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
