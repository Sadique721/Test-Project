package com.savbill.revenuemanagement.core.entity.staff;

import com.savbill.revenuemanagement.core.entity.TeamUserMapping.TeamUserMapping;
import com.savbill.revenuemanagement.core.entity.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.revenuemanagement.core.entity.role.repository.RoleRepository;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.mastermanagement.Branch.repository.BranchRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SaveStaffUserSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.UpdateStaffUserSharedDataMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class StaffUserService extends AbstractService<StaffUser, StaffUserPojo, Integer> {

    public StaffUserService(BranchRepository branchRepository,
                            RoleRepository roleRepository) {
        sortColMap.put("id", "staffid");
        sortColMap.put("name", "firstname");
        sortColMap.put("userName", "username");
        sortColMap.put("roleName", "srn.concatname");
        this.branchRepository = branchRepository;
        this.roleRepository = roleRepository;
    }

    private static String MODULE = " [StaffUserService] ";
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private StaffUserRepository entityRepository;

    @Autowired
    private TeamUserMappingsRepocitory teamUserMappingsRepocitory;



    public void saveStaffuser (SaveStaffUserSharedDataMessage message){

        try {
            StaffUser staffUser = new StaffUser();
            staffUser.setId(message.getId());
            staffUser.setUsername(message.getUsername());
            staffUser.setPassword(message.getPassword());
            staffUser.setFirstname(message.getFirstname());
            staffUser.setLastname(message.getLastname());
            staffUser.setStatus(message.getStatus());
            staffUser.setPartnerid(message.getPartnerid());
            //staffUser.setRoles(message.getRoles());
            staffUser.setIsDelete(message.getIsDelete());
            staffUser.setEmail(message.getEmail());
            staffUser.setPhone(message.getPhone());
   //         staffUser.setParentStaffId(message.getParentStaffId());
            staffUser.setLcoId(message.getLcoId());
            if(!message.getLast_login_time().equalsIgnoreCase("null") ) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setBranchId(message.getBranchId());
//            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
//            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            if(message.getTeamsList().size()>0){
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    teamUserMappingsRepocitory.save(teamUserMapping);
                }
            }
            entityRepository.save(staffUser);
            ApplicationLogger.logger.info("Staff User created successfully with name " + message.getUsername());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create staff user with name " + message.getUsername(), e.getMessage());
        }
    }



    public void updateStaffUser(UpdateStaffUserSharedDataMessage message) throws Exception {
        try {
            StaffUser staffUser = entityRepository.findById(message.getId()).orElse(null);
            if(staffUser!=null)
                staffUser.setId(message.getId());
            else
                staffUser = new StaffUser();
            staffUser.setUsername(message.getUsername());
            staffUser.setPassword(message.getPassword());
            staffUser.setFirstname(message.getFirstname());
            staffUser.setLastname(message.getLastname());
            staffUser.setStatus(message.getStatus());
            staffUser.setLcoId(message.getLcoId());
           // staffUser.setParentStaffId(message.getParentStaffId());
            staffUser.setPhone(message.getPhone());
            staffUser.setEmail(message.getEmail());
            if(!message.getLast_login_time().equalsIgnoreCase("null")) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setPartnerid(message.getPartnerid());
            //staffUser.setRoles(message.getRoles());
            staffUser.setIsDelete(message.getIsDelete());
//            if(message.getTeamsList().size()>0){
//                List<TeamUserMapping> oldteamMapping = new ArrayList<>();
//                oldteamMapping = teamUserMappingsRepocitory.findAllByStaffIdIn(staffUser.getId().longValue());
//                teamUserMappingsRepocitory.deleteAll(oldteamMapping);
//                for (Teams item : message.getTeamsList()) {
//                    TeamUserMapping teamUserMapping = new TeamUserMapping();
//                    teamUserMapping.setTeamId(item.getId());
//                    teamUserMapping.setStaffId(message.getId().longValue());
//                    teamUserMappingsRepocitory.save(teamUserMapping);
//                }
//            }
            List<TeamUserMapping> teamUserMappingList = teamUserMappingsRepocitory.findAllByStaffId(Long.valueOf(message.getId()));
            if (teamUserMappingList.size() != 0) {
                for (TeamUserMapping teamUserMapping : teamUserMappingList) {
                    teamUserMappingsRepocitory.deleteById(teamUserMapping.getId());
                }
            }
            if(message.getTeamsList().size()>0){
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    teamUserMappingsRepocitory.save(teamUserMapping);
                }
            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setBranchId(message.getBranchId());
//            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
//            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            entityRepository.save(staffUser);
            ApplicationLogger.logger.info("Staff User created successfully with name " + message.getUsername());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create staff user with name " + message.getUsername(), e.getMessage());
        }
    }

    public StaffUserPojo convertStaffUserModelToStaffUserPojo(StaffUser staffUser) throws Exception {
        String SUBMODULE = MODULE + " [convertStaffUserModelToStaffUserPojo()] ";
        StaffUserPojo pojo = null;
        try {
            if (staffUser != null) {
                pojo = new StaffUserPojo();
                pojo.setId(staffUser.getId());
                pojo.setUsername(staffUser.getUsername());
                if (null == pojo.getId()) pojo.setPassword(staffUser.getPassword());
                pojo.setEmail(staffUser.getEmail());
                pojo.setLcoId(staffUser.getLcoId());
                pojo.setFirstname(staffUser.getFirstname());
                pojo.setLastname(staffUser.getLastname());
                pojo.setStatus(staffUser.getStatus());
                pojo.setPhone(staffUser.getPhone());
                pojo.setCountryCode(staffUser.getCountryCode());
                pojo.setFailcount(staffUser.getFailcount());
                pojo.setCreatedate(staffUser.getCreatedate());
                pojo.setUpdatedate(staffUser.getUpdatedate());
                pojo.setLast_login_time(pojo.getLast_login_time());
                pojo.setPartnerid(staffUser.getPartnerid());
                pojo.setSysstaff(staffUser.getSysstaff());
//                pojo.setFullName(staffUser.getFullName());
//                pojo.setServicearea(staffUser.getServicearea());
//                pojo.setBusinessUnit(staffUser.getBusinessUnit());
//                pojo.setServiceAreaIdsList(staffUser.getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
//                pojo.setBusinessUnitIdsList(staffUser.getBusinessUnitNameList().stream().map(BusinessUnit::getId).collect(Collectors.toList()));
//                pojo.setStaffUserServiceMappingList(staffUser.getStaffUserServiceMappings());
                //                if (staffUser.getMvnoId() != null) {
                pojo.setMvnoId(staffUser.getMvnoId());
//                }
//                if (staffUser.getServicearea() != null) pojo.setServiceAreaId(staffUser.getServicearea().getId());
//                if (staffUser.getBusinessUnit() != null) pojo.setBusinessunitid(staffUser.getBusinessUnit().getId());
//
//                if (staffUser.getServiceAreaNameList() != null && staffUser.getServiceAreaNameList().size() > 0) {
//                    List<Integer> serviceAreaIds = new ArrayList<>();
//                    List<String> serviceArealist = new ArrayList<>();
//                    for (ServiceArea serviceArea : staffUser.getServiceAreaNameList()) {
//                        serviceAreaIds.add(serviceArea.getId().intValue());
//                        serviceArealist.add(serviceArea.getName());
//                    }
//                    pojo.setServiceAreasId(serviceAreaIds);
//                    pojo.setServiceAreasNameList(serviceArealist);
//
//                }

//                if (staffUser.getBusinessUnitNameList() != null && staffUser.getBusinessUnitNameList().size() > 0) {
//                    List<Integer> bussinessUnitIds = new ArrayList<>();
//                    List<String> bussinessUnitNameList = new ArrayList<>();
//                    for (BusinessUnit businessUnit : staffUser.getBusinessUnitNameList()) {
//                        bussinessUnitIds.add(businessUnit.getId().intValue());
//                        bussinessUnitNameList.add(businessUnit.getBuname());
//                    }
//                    pojo.setBusinessunitids(bussinessUnitIds);
//                    pojo.setBusinessUnitNamesList(bussinessUnitNameList);
//
//                }

//                if (staffUser.getParentStaffId() != null)
//                    pojo.setParentStaffId(staffUser.getParentStaffId());

//                if (staffUser.getRoles() != null && staffUser.getRoles().size() > 0) {
//                    List<Integer> roleIds = new ArrayList<>();
//                    List<String> roleNameList = new ArrayList<>();
//                    for (Role role : staffUser.getRoles()) {
//                        roleIds.add(role.getId().intValue());
//                        roleNameList.add(role.getRolename());
//                    }
//                    pojo.setRoleIds(roleIds);
//                    pojo.setRoleName(roleNameList);
//                }
                if (null != staffUser.getCreatedate()) {
                    pojo.setRegDate(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a").format(staffUser.getCreatedate()));
                }
//                if (null != staffUser.getPartnerid()) {
//                    Partner partner = partnerService.get(staffUser.getPartnerid());
//                    if (null != partner) {
//                        pojo.setPartnerName(null != partner.getName() ? partner.getName() : "-");
//                    } else {
//                        pojo.setPartnerName("-");
//                    }
//                }
//
//                if (null != staffUser.getParentStaffId()) {
//                    Optional<StaffUser> parent = entityRepository.findById(staffUser.getParentStaffId());
//                    if (parent.isPresent()) {
//                        pojo.setParentstaffname(parent.get().getUsername());
//                    } else {
//                        pojo.setParentstaffname("-");
//                    }
//                }
//                if (null != staffUser.getTeam() && 0 < staffUser.getTeam().size()) {
//                    Set<Long> teamIds = new HashSet<>();
//                    List<String> teamNameList = new ArrayList<>();
//                    for (Teams role : staffUser.getTeam()) {
//                        teamIds.add(role.getId());
//                        teamNameList.add(role.getName());
//                    }
//                    pojo.setTeamIds(teamIds);
//                    pojo.setTeamNameList(teamNameList);
//                }
//                if (staffUser.getBranchId() != null) {
//                    Branch branch = branchRepository.findById(Long.valueOf(staffUser.getBranchId())).orElse(null);
//                    if (branch != null) {
//                        pojo.setBranchName(branch.getName());
//                    } else {
//                        pojo.setBranchName("-");
//                    }
//                } else {
//                    pojo.setBranchName("-");
//                }
                pojo.setHrmsId(staffUser.getHrmsId());
                pojo.setProfileImage(staffUser.getProfileImage());
                pojo.setDisplayId(staffUser.getId());
                pojo.setDisplayName(staffUser.getFirstname());

                if(staffUser.getDepartment() !=null){
                    pojo.setDepartment(staffUser.getDepartment());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }
//
    public List<StaffUserPojo> convertResponseModelIntoPojo(List<StaffUser> staffUserList) throws Exception {
        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
        List<StaffUserPojo> pojoListRes = new ArrayList<>();
        try {
            if (staffUserList != null && staffUserList.size() > 0) {
                for (StaffUser staffUser : staffUserList) {
                    pojoListRes.add(convertStaffUserModelToStaffUserPojo(staffUser));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    @Override
    public StaffUser get(Integer id) {
        StaffUser staffUser = super.get(id);
        if (getMvnoIdFromCurrentStaff() == null) return staffUser;
        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (staffUser.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || staffUser.getMvnoId().intValue() == 1))
            return staffUser;
        return null;
    }

    @Override
    protected JpaRepository<StaffUser, Integer> getRepository() {
        return null;
    }

}
