package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.Role;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.RoleDTO;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.RoleService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Mapper(componentModel = "spring", uses = TeamsMapper.class)
@Mapper
public abstract class StaffUserMapper implements IBaseMapper<StaffUserPojo, StaffUser> {

    private String MODULE = " [StaffUserMapper] ";
    @Autowired
    private RoleService roleService;
    @Autowired
    private StaffUserRepository staffUserRepository;


    @Override
    @Mappings({
            @Mapping(source = "staffUser.roles", target = "roleIds"),
            @Mapping(source = "staffUser.createdate", target = "regDate", dateFormat = "dd/MM/yyyy HH:mm a"),
            @Mapping(source = "staffUser.updatedate", target = "updatedatestring", dateFormat = "dd/MM/yyyy HH:mm a"),
            @Mapping(source = "staffUser.staffUserparent.id", target = "parentStaffId"),
            //@Mapping(source = "staffUser.team", target = "teamIds"),
            @Mapping(target = "displayId", source = "staffUser.id"),
            @Mapping(target = "displayName", source = "staffUser.username")
    })
    public abstract StaffUserPojo domainToDTO(StaffUser staffUser, CycleAvoidingMappingContext context);

    @Override
    @Mappings({
            @Mapping(source = "dtoData.roleIds", target = "roles"),
            @Mapping(source = "dtoData.parentStaffId", target = "staffUserparent")
    })
    //@Mapping(source = "dtoData.teamIds", target = "team")
    public abstract StaffUser dtoToDomain(StaffUserPojo dtoData, CycleAvoidingMappingContext context);

    public abstract java.util.Set<Role> mapRoleIdsToRole(List<Integer> value);

    public abstract java.util.List<Integer> mapRolesToRoleIds(Set<Role> value);

    Integer fromRoleToId(Role entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Role fromIdToRole(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Role entity;
        try {
            RoleDTO entityDTO = roleService.getEntityById(entityId.longValue());
            entity = roleService.convertRolePojoToRoleModel(entityDTO);
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    StaffUser fromParentStaffIdToStaffUserparent(Integer entityId){
        if (entityId == null) {
            return null;
        }
        StaffUser entity = new StaffUser();
        try{
            entity = staffUserRepository.findById(entityId).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

  /*  Long fromTeamToId(Teams entity) {
        return entity == null ? null : entity.getId();
    }

    Teams fromIdToTeam(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Teams entity;
        try {
            TeamsDTO entityDTO = teamsService.getEntityById(entityId.longValue());
            entity = teamsMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }*/

    @AfterMapping
    void afterMapping(@MappingTarget StaffUserPojo staffUserPojo, StaffUser staffUser) {
        try {

//            if (null != staffUser.getPartnerid()) {
//                Partner partner = partnerService.get(staffUser.getPartnerid());
//                staffUserPojo.setPartnerName(null != partner && null != partner.getName() ? partner.getName() : "-");
//            } else {
//                staffUserPojo.setPartnerName("-");
//            }

            if (null != staffUser.getRoles() && 0 < staffUser.getRoles().size()) {
                staffUserPojo.setRoleName(staffUser.getRoles().stream().map(Role::getRolename).collect(Collectors.toList()));
            } else {
                staffUserPojo.setRoleName(new ArrayList<>());
            }

         /*   if (null != staffUser.getTeam() && 0 < staffUser.getTeam().size()) {
                staffUserPojo.setTeamNameList(staffUser.getTeam().stream().map(Teams::getName).collect(Collectors.toList()));
            } else
                staffUserPojo.setTeamNameList(Arrays.asList("-"));*/

        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}

