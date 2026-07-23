package com.savbill.commonGateway.moules.TeamsManagement.Teams;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.PartnerManagement.Partner;
import com.savbill.commonGateway.moules.PartnerManagement.PartnerRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.stream.Collectors;

@Mapper
public abstract class TeamsMapper implements IBaseMapper<TeamsDTO, Teams> {
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @Override
    @Mappings({
            @Mapping(source = "teams.staffUser", target = "staffUserIds"),
            @Mapping(source = "teams.partner", target = "partnerid"),
            @Mapping(source = "teams.parentTeams", target = "parentteamid"),
            @Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "name")
    })
    public abstract TeamsDTO domainToDTO(Teams teams, @Context CycleAvoidingMappingContext context);

    @Override
    @Mappings({
            @Mapping(source = "dtoData.staffUserIds", target = "staffUser"),
            @Mapping(source = "dtoData.partnerid", target = "partner"),
            @Mapping(source = "dtoData.parentteamid", target = "parentTeams")
    })
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
            entity = teamsRepository.findById(entityId).orElse(null);
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
            entity = staffUserRepository.findById(entityId).orElse(null);
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
            entity = partnerRepository.findById(entityId).orElse(null);
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
//            if (null != teams.getPartner()) {
//                teamsDTO.setPartnername(teams.getPartner().getName());
//            } else {
//                teamsDTO.setPartnername("-");
//            }
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
