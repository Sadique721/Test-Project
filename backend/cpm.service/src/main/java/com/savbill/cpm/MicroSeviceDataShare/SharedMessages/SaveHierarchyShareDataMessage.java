package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;


import com.savbill.cpm.modules.Teams.domain.TeamHierarchyMapping;
import lombok.Data;

import java.util.List;

@Data
public class SaveHierarchyShareDataMessage {

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
