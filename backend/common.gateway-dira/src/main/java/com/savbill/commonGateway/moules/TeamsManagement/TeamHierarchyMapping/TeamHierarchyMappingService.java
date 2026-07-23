package com.savbill.commonGateway.moules.TeamsManagement.TeamHierarchyMapping;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.SaveTeamHierarchyMappingMessage;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.UpdateTeamHierarchyMappingMessage;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamHierarchyMappingService {

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;
    private static final Logger logger = LoggerFactory.getLogger(TeamHierarchyMappingService.class);
    public void saveTeamHierarchyMapping (SaveTeamHierarchyMappingMessage message) {
        try {
            for (TeamHierarchyMapping item : message.getTeamHierarchyMappingList()) {
                TeamHierarchyMapping teamHierarchyMapping = new TeamHierarchyMapping();
                teamHierarchyMapping.setId(item.getId());
                teamHierarchyMapping.setTeamId(item.getTeamId());
                teamHierarchyMapping.setHierarchyId(message.getHierarchyId().intValue());
                teamHierarchyMapping.setIsDeleted(item.getIsDeleted());
                teamHierarchyMappingRepo.save(teamHierarchyMapping);
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to create team hierarchy mapping with hierarchy id " + message.getHierarchyId(), e.getMessage());
        }
    }

    public void updateTeamHierarchyMapping (UpdateTeamHierarchyMappingMessage message) {
        try {
            if (message.getOperationId().equals(CommonConstants.OPERATION_UPDATE)) {
                List<TeamHierarchyMapping> teamHierarchyMappingList = teamHierarchyMappingRepo.findAllByHierarchyId(message.getHierarchyId().intValue());
                teamHierarchyMappingRepo.deleteAll(teamHierarchyMappingList);
                for (TeamHierarchyMapping item : message.getTeamHierarchyMappingList()) {
                    TeamHierarchyMapping teamHierarchyMapping = new TeamHierarchyMapping();
                    teamHierarchyMapping.setId(item.getId());
                    teamHierarchyMapping.setTeamId(item.getTeamId());
                    teamHierarchyMapping.setHierarchyId(message.getHierarchyId().intValue());
                    teamHierarchyMapping.setIsDeleted(item.getIsDeleted());
                    teamHierarchyMappingRepo.save(teamHierarchyMapping);
                }
            } else if (message.getOperationId().equals(CommonConstants.OPERATION_DELETE)) {
                List<TeamHierarchyMapping> teamHierarchyMappingList = teamHierarchyMappingRepo.findAllByHierarchyId(message.getHierarchyId().intValue());
                teamHierarchyMappingRepo.deleteAll(teamHierarchyMappingList);
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update team hierarchy mapping with hierarchy id " + message.getHierarchyId(), e.getMessage());
        }
    }
}
