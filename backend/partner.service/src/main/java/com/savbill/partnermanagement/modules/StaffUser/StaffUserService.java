package com.savbill.partnermanagement.modules.StaffUser;

import com.savbill.partnermanagement.common.AbstractService;
import com.savbill.partnermanagement.constants.CommonConstants;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.modules.TeamUserMapping.TeamUserMapping;
import com.savbill.partnermanagement.modules.Teams.Teams;
import com.savbill.partnermanagement.rabbitmq.setting.SaveStaffUserSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.setting.UpdateStaffUserSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class StaffUserService extends AbstractService<StaffUser, StaffUserPojo, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(StaffUserService.class);

    @Autowired
    private StaffUserRepository entityRepository;



    public void deleteStaffUser(Integer id) {
        entityRepository.deleteById(id);
        //Optional<StaffUser> staffUser = entityRepository.findById(id);
        //staffUser.get().setIsDelete(true);
        //StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser.get(), new CycleAvoidingMappingContext());
        //List<ServiceAreaDTO> serviceAreaDTOS = staffUser.get().getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        //QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
        //BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(id));
        //List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));

        //StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS);
        //messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS, RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
        //messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE);
        //createDataSharedService.deleteEntityDataForAllMicroService(staffUser);
    }

    public List<StaffUser> getActiveStaffUserFromUsername(String username) {
        return entityRepository.findByUsernameAndStatusAndIsDeleteIsFalse(username, CommonConstants.ACTIVE_STATUS);
    }


    @Transactional
    public void saveStaffUserEntity(SaveStaffUserSharedDataMessage message) throws Exception {
        try {
            StaffUser staffUser = new StaffUser();
            staffUser.setId(message.getId());
            staffUser.setUsername(message.getUsername());
            staffUser.setPassword(message.getPassword());
            staffUser.setFirstname(message.getFirstname());
            staffUser.setLastname(message.getLastname());
            staffUser.setStatus(message.getStatus());
            staffUser.setEmail(message.getEmail());
            staffUser.setPhone(message.getPhone());
            staffUser.setPartnerid(message.getPartnerid());
            staffUser.setRoles(message.getRoles());
            staffUser.setIsDelete(message.getIsDelete());
            staffUser.setCreatedById(message.getCreatedById());
            staffUser.setLastModifiedById(message.getLastModifiedById());
            if(message.getTeamsList().size()>0){
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    //teamUserMappingsRepocitory.save(teamUserMapping);
                }
            }
            if(!message.getLast_login_time().equalsIgnoreCase("null")) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setMvnoId(message.getMvnoId());
            //staffUser.setBranchId(message.getBranchId());
            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            entityRepository.save(staffUser);
            logger.info("Staff User created successfully with name " + message.getUsername());
        } catch (CustomValidationException e) {
            logger.error("Unable to create staff user with name " + message.getUsername(), e.getMessage());
        }
    }

    @Transactional
    public void updatetaffUserEntity(UpdateStaffUserSharedDataMessage message) throws Exception {
        try {
            StaffUser staffUser = entityRepository.findById(message.getId()).orElse(null);
            if (staffUser != null) {
                staffUser.setId(message.getId());
                staffUser.setUsername(message.getUsername());
                staffUser.setPassword(message.getPassword());
                staffUser.setFirstname(message.getFirstname());
                staffUser.setLastname(message.getLastname());
                staffUser.setStatus(message.getStatus());
                //staffUser.setEmail(message.getEmail());
                //staffUser.setPhone(message.getPhone());
//                staffUser.setTeam(message.getTeam());
                //List<TeamUserMapping> teamUserMappingList =  teamUserMappingsRepocitory.findAllByStaffId(Long.valueOf(message.getId()));
                //if (teamUserMappingList.size() != 0) {
                    //for (TeamUserMapping teamUserMapping : teamUserMappingList) {
                        //teamUserMappingsRepocitory.deleteById(teamUserMapping.getId());
                    //}
                //}
                if(message.getTeamsList().size()>0){
                    for (Teams item : message.getTeamsList()) {
                        TeamUserMapping teamUserMapping = new TeamUserMapping();
                        teamUserMapping.setTeamId(item.getId());
                        teamUserMapping.setStaffId(message.getId().longValue());
                        //teamUserMappingsRepocitory.save(teamUserMapping);
                    }
                }
                staffUser.setCreatedById(message.getCreatedById());
                staffUser.setLastModifiedById(message.getLastModifiedById());
                if (!message.getLast_login_time().equalsIgnoreCase("null")) {
                    staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
                } else {
                    staffUser.setLast_login_time(null);
                }
                staffUser.setPartnerid(message.getPartnerid());
                staffUser.setRoles(message.getRoles());
                staffUser.setIsDelete(message.getIsDelete());
                staffUser.setMvnoId(message.getMvnoId());
                //staffUser.setBranchId(message.getBranchId());
                staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
                staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
                entityRepository.save(staffUser);
                logger.info("Staff User updated successfully with name " + message.getUsername());
            } else {
                StaffUser staffUser2 = new StaffUser();
                staffUser2.setId(message.getId());
                staffUser2.setUsername(message.getUsername());
                staffUser2.setPassword(message.getPassword());
                staffUser2.setFirstname(message.getFirstname());
                staffUser2.setLastname(message.getLastname());
                staffUser2.setStatus(message.getStatus());
                //staffUser2.setEmail(message.getEmail());
                //staffUser2.setPhone(message.getPhone());
                if(message.getTeamsList().size()>0){
                    for (Teams item : message.getTeamsList()) {
                        TeamUserMapping teamUserMapping = new TeamUserMapping();
                        teamUserMapping.setTeamId(item.getId());
                        teamUserMapping.setStaffId(message.getId().longValue());
                        //teamUserMappingsRepocitory.save(teamUserMapping);
                    }
                }
                if (message.getLast_login_time() != null) {
                    staffUser2.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
                } else {
                    staffUser2.setLast_login_time(null);
                }
                staffUser2.setPartnerid(message.getPartnerid());
                staffUser2.setRoles(message.getRoles());
                staffUser2.setIsDelete(message.getIsDelete());
                staffUser2.setMvnoId(message.getMvnoId());
                //staffUser2.setBranchId(message.getBranchId());
                staffUser2.setServiceAreaNameList(message.getServiceAreaNameList());
                staffUser2.setBusinessUnitNameList(message.getBusinessUnitNameList());
                entityRepository.save(staffUser2);
                logger.info("Staff User updated successfully with name " + message.getUsername());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update staff user with name " + message.getUsername(), e.getMessage());
        }
    }


    @Override
    public JpaRepository<StaffUser, Integer> getRepository() {
        return entityRepository;
    }
}
