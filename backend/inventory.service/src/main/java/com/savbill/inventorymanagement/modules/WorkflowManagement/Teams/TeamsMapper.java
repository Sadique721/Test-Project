package com.savbill.inventorymanagement.modules.WorkflowManagement.Teams;

import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.PartnerManagement.Partner;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.stream.Collectors;


@Mapper
public abstract class TeamsMapper implements IBaseMapper<TeamsDTO, Teams> {

    @Autowired
    private StaffUserRepository staffRepository;

    @Autowired
    private PartnerRepository partnerRepository;
    
    @Autowired
    private TeamsService teamsService;

    @Mappings({
        @Mapping(source = "teams.staffUser", target = "staffUserIds"),
        @Mapping(source = "teams.partner", target = "partnerid"),
        @Mapping(source = "teams.parentTeams", target = "parentteamid"),
        @Mapping(target = "displayId", source = "id"),
        @Mapping(target = "displayName", source = "name")
    })
    @Override
    public abstract TeamsDTO domainToDTO(Teams teams, @Context CycleAvoidingMappingContext context);
    @Mappings({
        @Mapping(source = "dtoData.staffUserIds", target = "staffUser"),
        @Mapping(source = "dtoData.partnerid", target = "partner"),
        @Mapping(source = "dtoData.parentteamid", target = "parentTeams")
    })
    @Override
    public abstract Teams dtoToDomain(TeamsDTO dtoData, @Context CycleAvoidingMappingContext context);
    
    Long fromParentTeamsToId(Teams parentTeams) {
        return parentTeams == null ? null : parentTeams.getId();
    }
    
    Teams fromIdToParentTeams(Long entityId) {
        if (entityId == null) {
            return null;
        }
        Teams entity;
        try {
            entity = teamsService.getById(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer fromStaffToId(StaffUser entity) {
        return entity == null ? null : entity.getId();
    }

    StaffUser fromIdToStaff(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        StaffUser entity;
        try {
//            entity = staffUserService.get(entityId);
            entity = staffRepository.findById(Integer.valueOf(entityId)).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer frommPartnerToId(Partner entity) {
        return entity == null ? null : entity.getId();
    }

    Partner fromIdToPartner(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Partner entity;
        try {
//            entity = partnerService.get(entityId);
            entity = partnerRepository.findById(entityId).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    void afterMapping(@MappingTarget TeamsDTO teamsDTO, Teams teams) {
        try {
            if (null != teams.getStaffUser() && 0 < teams.getStaffUser().size()) {
                teamsDTO.setStaffNameList(teams.getStaffUser().stream().map(StaffUser::getFullName).collect(Collectors.toList()));
            } else {
                teamsDTO.setStaffNameList(Collections.singletonList("-"));
            }
            if (null != teams.getPartner()) {
                teamsDTO.setPartnername(teams.getPartner().getName());
            } else {
                teamsDTO.setPartnername("-");
            }
            if(teams.getParentTeams() != null) {
            	teamsDTO.setParentteamid(teams.getParentTeams().getId());
            	teamsDTO.setParentTeamName(teams.getParentTeams().getName());
            }else {
            	teamsDTO.setParentTeamName("-");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Teams Mapper" + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
