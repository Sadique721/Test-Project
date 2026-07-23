package com.savbill.inventorymanagement.modules.StaffUser;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.TeamsMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = TeamsMapper.class)
public abstract class StaffUserMapper implements IBaseMapper<StaffUserPojo, StaffUser> {
//
//    private String MODULE = " [StaffUserMapper] ";
//
//    @Autowired
//    private PartnerRepository partnerRepository;
////    @Autowired
////    private RoleService roleService;
//    @Autowired
//    private StaffUserRepository staffRepository;
//
//    @Mappings({
//        @Mapping(source = "staffUser.roles", target = "roleIds"),
//        @Mapping(source = "staffUser.createdate", target = "regDate", dateFormat = "dd/MM/yyyy HH:mm a"),
//        @Mapping(source = "staffUser.updatedate", target = "updatedatestring", dateFormat = "dd/MM/yyyy HH:mm a"),
////        @Mapping(source = "staffUser.staffUserparent", target = "parentStaffId"),
//        //@Mapping(source = "staffUser.team", target = "teamIds"),
//        @Mapping(target = "displayId", source = "staffUser.id"),
//        @Mapping(target = "displayName", source = "staffUser.username")
//    })
//    @Override
//    public abstract StaffUserPojo domainToDTO(StaffUser staffUser, CycleAvoidingMappingContext context);
//    @Mappings({
////        @Mapping(source = "dtoData.roleIds", target = "roles"),
////        @Mapping(source = "dtoData.parentStaffId", target = "staffUserparent")
//    })
//    @Override
//    //@Mapping(source = "dtoData.teamIds", target = "team")
//    public abstract StaffUser dtoToDomain(StaffUserPojo dtoData, CycleAvoidingMappingContext context);
//
////    public abstract Set<Role> mapRoleIdsToRole(List<Integer> value);
//
//    public abstract List<Integer> mapRolesToRoleIds(Set<Role> value);
//
//    Integer fromRoleToId(Role entity) {
//        return entity == null ? null : entity.getId().intValue();
//    }
//
////    Role fromIdToRole(Integer entityId) {
////        if (entityId == null) {
////            return null;
////        }
////        Role entity;
////        try {
////            RoleDTO entityDTO = roleService.getEntityById(entityId.longValue());
////            entity = roleService.convertRolePojoToRoleModel(entityDTO);
////            entity.setId(entityId.longValue());
////        } catch (Exception e) {
////            e.printStackTrace();
////            entity = null;
////        }
////        return entity;
////    }
//
//    StaffUser fromParentStaffIdToStaffUserparent(Integer entityId){
//        if (entityId == null) {
//            return null;
//        }
//        StaffUser entity = new StaffUser();
//        try{
////            entity = staffUserService.get(entityId);
//            entity = staffRepository.findById(Long.valueOf(entityId)).get();
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
//
//  /*  Long fromTeamToId(Teams entity) {
//        return entity == null ? null : entity.getId();
//    }
//
//    Teams fromIdToTeam(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        Teams entity;
//        try {
//            TeamsDTO entityDTO = teamsService.getEntityById(entityId.longValue());
//            entity = teamsMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//            entity.setId(entityId.longValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }*/
//
//    @AfterMapping
//    void afterMapping(@MappingTarget StaffUserPojo staffUserPojo, StaffUser staffUser) {
//        try {
//
//            if (null != staffUser.getPartnerid()) {
////                Partner partner = partnerService.get(staffUser.getPartnerid());
//                Partner partner = partnerRepository.findById(staffUser.getPartnerid()).get();
//                staffUserPojo.setPartnerName(null != partner && null != partner.getName() ? partner.getName() : "-");
//            } else {
//                staffUserPojo.setPartnerName("-");
//            }
//
//            if (null != staffUser.getRoles() && 0 < staffUser.getRoles().size()) {
//                staffUserPojo.setRoleName(staffUser.getRoles().stream().map(Role::getRolename).collect(Collectors.toList()));
//            } else {
//                staffUserPojo.setRoleName(new ArrayList<>());
//            }
//
//         /*   if (null != staffUser.getTeam() && 0 < staffUser.getTeam().size()) {
//                staffUserPojo.setTeamNameList(staffUser.getTeam().stream().map(Teams::getName).collect(Collectors.toList()));
//            } else
//                staffUserPojo.setTeamNameList(Arrays.asList("-"));*/
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }
}
