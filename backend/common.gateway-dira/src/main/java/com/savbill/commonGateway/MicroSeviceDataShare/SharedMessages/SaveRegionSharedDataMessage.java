package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import lombok.Data;
import java.util.List;
@Data
public class SaveRegionSharedDataMessage {
    private Long id;
    private String rname;
    private List<Branch> branchidList;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;

    private String lastModifiedByName;

}
