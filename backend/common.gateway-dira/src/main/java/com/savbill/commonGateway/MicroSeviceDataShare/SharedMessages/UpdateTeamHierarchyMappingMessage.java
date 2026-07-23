package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import com.savbill.commonGateway.moules.TeamsManagement.TeamHierarchyMapping.TeamHierarchyMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTeamHierarchyMappingMessage {
    List<TeamHierarchyMapping> teamHierarchyMappingList;
    private Long hierarchyId;
    private Integer operationId;
}
