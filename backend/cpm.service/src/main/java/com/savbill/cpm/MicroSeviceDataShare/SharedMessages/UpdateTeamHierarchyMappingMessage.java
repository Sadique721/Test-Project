package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import com.savbill.cpm.modules.Teams.domain.TeamHierarchyMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTeamHierarchyMappingMessage {
    List<TeamHierarchyMapping> teamHierarchyMappingList;
    private Long hierarchyId;
    private Integer operationId;
}
