package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;



import com.savbill.commonGateway.moules.TeamsManagement.TeamHierarchyMapping.TeamHierarchyMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateHierarchyShareDataMessage {
    private Long id;

    private Integer mvnoId;

    private Boolean isDeleted;

    private Long buId;

    private String hierarchyName;

    private String eventName;

    private List<TeamHierarchyMapping> teamHierarchyMappingList;

    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
