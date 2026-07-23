package com.savbill.inventorymanagement.modules.StaffUser;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.MasterManagement.Branch.Branch;
import com.savbill.inventorymanagement.modules.MasterManagement.Branch.BranchRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnit;
import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnitRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.QStaffUserServiceAreaMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.inventorymanagement.modules.PartnerManagement.Partner;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerRepository;
import com.savbill.inventorymanagement.modules.Role.Role;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveStaffUserSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateStaffUserSharedDataMessage;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffUserService extends ExBaseAbstractService<StaffUserPojo, StaffUser, Integer> {
    public StaffUserService(StaffUserRepository repository, StaffUserMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[StaffUserService]";
    }

    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    BusinessUnitRepository businessUnitRepository;

    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;

    @PersistenceContext
    EntityManager entityManager;
    private static final Logger logger = Logger.getLogger(StaffUserService.class);

    @Transient
    public void saveStaffUserEntity(SaveStaffUserSharedDataMessage message) throws Exception {
        try {
            StaffUser staffUser = new StaffUser();
            staffUser.setId(message.getId());
            staffUser.setUsername(message.getUsername());
            staffUser.setPassword(message.getPassword());
            staffUser.setFirstname(message.getFirstname());
            staffUser.setLastname(message.getLastname());
            staffUser.setStatus(message.getStatus());
            staffUser.setPartnerid(message.getPartnerid());
            staffUser.setRoles(message.getRoles());
            staffUser.setIsDelete(message.getIsDelete());
            staffUser.setCreatedById(message.getCreatedById());
            staffUser.setLastModifiedById(message.getLastModifiedById());
            staffUser.setEmail(message.getEmail());
            staffUser.setPhone(message.getPhone());
            if(message.getTeamsList().size()>0){
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    teamUserMappingsRepocitory.save(teamUserMapping);
                }
            }
            if(!message.getLast_login_time().equalsIgnoreCase("null")) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setBranchId(message.getBranchId());
            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            staffUserRepository.save(staffUser);
            logger.info("Staff User created successfully with name " + message.getUsername());
        } catch (CustomValidationException e) {
            logger.error("Unable to create staff user with name " + message.getUsername() + " , Error: " + e.getMessage());
        }
    }

    @Transient
    public void updatetaffUserEntity(UpdateStaffUserSharedDataMessage message) throws Exception {
        try {
            StaffUser staffUser = staffUserRepository.findById(message.getId()).orElse(null);
            if (staffUser != null) {
                staffUser.setId(message.getId());
                staffUser.setUsername(message.getUsername());
                staffUser.setPassword(message.getPassword());
                staffUser.setFirstname(message.getFirstname());
                staffUser.setLastname(message.getLastname());
                staffUser.setStatus(message.getStatus());
//                staffUser.setTeam(message.getTeam());
                List<TeamUserMapping> teamUserMappingList =  teamUserMappingsRepocitory.findAllByStaffId(Long.valueOf(message.getId()));
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
                staffUser.setBranchId(message.getBranchId());
                staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
                staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
                staffUser.setEmail(message.getEmail());
                staffUser.setPhone(message.getPhone());
                staffUserRepository.save(staffUser);
                logger.info("Staff User updated successfully with name " + message.getUsername());
            } else {
                StaffUser staffUser2 = new StaffUser();
                staffUser2.setId(message.getId());
                staffUser2.setUsername(message.getUsername());
                staffUser2.setPassword(message.getPassword());
                staffUser2.setFirstname(message.getFirstname());
                staffUser2.setLastname(message.getLastname());
                staffUser2.setStatus(message.getStatus());
                if(message.getTeamsList().size()>0){
                    for (Teams item : message.getTeamsList()) {
                        TeamUserMapping teamUserMapping = new TeamUserMapping();
                        teamUserMapping.setTeamId(item.getId());
                        teamUserMapping.setStaffId(message.getId().longValue());
                        teamUserMappingsRepocitory.save(teamUserMapping);
                    }
                }
                if (!message.getLast_login_time().equalsIgnoreCase("null")) {
                    staffUser2.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
                } else {
                    staffUser2.setLast_login_time(null);
                }
                staffUser2.setPartnerid(message.getPartnerid());
                staffUser2.setRoles(message.getRoles());
                staffUser2.setIsDelete(message.getIsDelete());
                staffUser2.setMvnoId(message.getMvnoId());
                staffUser2.setBranchId(message.getBranchId());
                staffUser2.setCreatedById(message.getCreatedById());
                staffUser2.setLastModifiedById(message.getLastModifiedById());
                staffUser2.setServiceAreaNameList(message.getServiceAreaNameList());
                staffUser2.setBusinessUnitNameList(message.getBusinessUnitNameList());
                staffUserRepository.save(staffUser2);
                logger.info("Staff User updated successfully with name " + message.getUsername());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update staff user with name " + message.getUsername() + " , Error: " + e.getMessage());
        }
    }

    public List<StaffUser> getStaffUserByServiceArea() {
        try {
            QStaffUser qStaffUser = QStaffUser.staffUser;
            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
//                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff()))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
                aBoolean = aBoolean.and(qStaffUser.id.in(query.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.in(serviceAreaIds))).and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff())));
            }
            if (getMvnoIdFromCurrentStaff() != 1) {
                return IterableUtils.toList(staffUserRepository.findAll(aBoolean));
            } else {
                return staffUserRepository.findAll();
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<Integer> getStaffUserIdsByServiceArea() {
        try {
            QStaffUser qStaffUser = QStaffUser.staffUser;
            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression predicate = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIds.isEmpty()) {
                    //Below code is commented reason QueryDSL corrupted -->startLine
//                    predicate = predicate
//                            .and(qStaffUser.id.in(
//                                    query.select(qStaffUserServiceAreaMapping.staffId)
//                                            .from(qStaffUserServiceAreaMapping)
//                                            .where(qStaffUserServiceAreaMapping.serviceId.in(serviceAreaIds))
//                            ))
//                            .and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff()));
                    //<<closeLine

                    JPAQuery<Integer> subQuery = new JPAQuery<>(entityManager);
                    predicate = predicate.and(qStaffUser.id.in(
                            subQuery.select(qStaffUserServiceAreaMapping.staffId)
                                    .from(qStaffUserServiceAreaMapping)
                                    .where(qStaffUserServiceAreaMapping.serviceId.in(serviceAreaIds))
                    ))
                            .and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff()));

                }
            }
            if (getMvnoIdFromCurrentStaff() != 1) {
                return query.select(qStaffUser.id)
                        .from(qStaffUser)
                        .where(predicate)
                        .fetch();
            } else {
                return query.select(qStaffUser.id)
                        .from(qStaffUser)
                        .fetch();
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + " Error while fetching Staff User IDs", ex);
            throw ex;
        }
    }

    public List<StaffUserPojo> convertResponseModelIntoPojo(List<StaffUser> staffUserList) throws Exception{
        String SUBMODULE = getModuleNameForLog() + "[convertResponseModelIntoPojo()]";
        List<StaffUserPojo> pojoListRes = new ArrayList<>();
        try {
            if (staffUserList != null && staffUserList.size() > 0) {
                for (StaffUser staffUser : staffUserList) {
                    pojoListRes.add(convertStaffUserModelToStaffUserPojo(staffUser));
                }
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage() , null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public StaffUserPojo convertStaffUserModelToStaffUserPojo(StaffUser staffUser) throws Exception{
        String SUBMODULE = getModuleNameForLog() + " [convertStaffUserModelToStaffUserPojo()] ";
        StaffUserPojo pojo = null;
        try {
            if (staffUser != null) {
                pojo = new StaffUserPojo();
                pojo.setId(staffUser.getId());
                pojo.setUsername(staffUser.getUsername());
                if (null == pojo.getId()) pojo.setPassword(staffUser.getPassword());
                pojo.setFirstname(staffUser.getFirstname());
                pojo.setLastname(staffUser.getLastname());
                pojo.setStatus(staffUser.getStatus());
                pojo.setCreatedate(staffUser.getCreatedate());
                pojo.setUpdatedate(staffUser.getUpdatedate());
                pojo.setLast_login_time(pojo.getLast_login_time());
                pojo.setPartnerid(staffUser.getPartnerid());
                pojo.setFullName(staffUser.getFullName());
                pojo.setServiceAreaIdsList(staffUser.getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                if(pojo.getBusinessUnitIdsList() != null) {
                    pojo.setBusinessUnitIdsList(staffUser.getBusinessUnitNameList().stream().map(BusinessUnit::getId).collect(Collectors.toList()));
                }
                pojo.setMvnoId(staffUser.getMvnoId());

                if (staffUser.getServiceAreaNameList() != null && staffUser.getServiceAreaNameList().size() > 0) {
                    List<Integer> serviceAreaIds = new ArrayList<>();
                    List<String> serviceArealist = new ArrayList<>();
                    for (ServiceArea serviceArea : staffUser.getServiceAreaNameList()) {
                        serviceAreaIds.add(serviceArea.getId().intValue());
                        serviceArealist.add(serviceArea.getName());
                    }
                    pojo.setServiceAreasId(serviceAreaIds);
                    pojo.setServiceAreasNameList(serviceArealist);
                }

                if (staffUser.getBusinessUnitNameList() != null && staffUser.getBusinessUnitNameList().size() > 0) {
                    List<Integer> bussinessUnitIds = new ArrayList<>();
                    List<String> bussinessUnitNameList = new ArrayList<>();
                    for (BusinessUnit businessUnit : staffUser.getBusinessUnitNameList()) {
                        bussinessUnitIds.add(businessUnit.getId().intValue());
                        bussinessUnitNameList.add(businessUnit.getBuname());
                    }
                    pojo.setBusinessunitids(bussinessUnitIds);
                    pojo.setBusinessUnitNamesList(bussinessUnitNameList);

                }

                if (staffUser.getRoles() != null && staffUser.getRoles().size() > 0) {
                    List<Integer> roleIds = new ArrayList<>();
                    List<String> roleNameList = new ArrayList<>();
                    for (Role role : staffUser.getRoles()) {
                        roleIds.add(role.getId().intValue());
                        roleNameList.add(role.getRolename());
                    }
                    pojo.setRoleIds(roleIds);
                    pojo.setRoleName(roleNameList);
                }
                if (null != staffUser.getCreatedate()) {
                    pojo.setRegDate(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a").format(staffUser.getCreatedate()));
                }
                if (null != staffUser.getPartnerid()) {
                    Partner partner = partnerRepository.findById(staffUser.getPartnerid()).orElse(null);
                    if (partner != null) {
                        pojo.setPartnerName(partner.getName());
                    } else {
                        pojo.setPartnerName("-");
                    }
                }
                if (null != staffUser.getTeam() && 0 < staffUser.getTeam().size()) {
                    Set<Long> teamIds = new HashSet<>();
                    List<String> teamNameList = new ArrayList<>();
                    for (Teams role : staffUser.getTeam()) {
                        teamIds.add(role.getId());
                        teamNameList.add(role.getName());
                    }
                    pojo.setTeamIds(teamIds);
                    pojo.setTeamNameList(teamNameList);
                }
                if (staffUser.getBranchId() != null) {
                    Branch branch = branchRepository.findById(staffUser.getBranchId()).orElse(null);
                    if (branch != null) {
                        pojo.setBranchName(branch.getName());
                    } else {
                        pojo.setBranchName("-");
                    }
                } else {
                    pojo.setBranchName("-");
                }
                pojo.setDisplayId(staffUser.getId());
                pojo.setDisplayName(staffUser.getFirstname());
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<StaffUser> getAllActiveEntities() {
        List<StaffUser> staffUsers = new ArrayList<>();
        List<Integer> ids;
        if (getMvnoIdFromCurrentStaff() == 1) {
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                ids = staffUserRepository.findAllLightStaffUserByStatusAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS).stream().map(StaffUser::getId).collect(Collectors.toList());
//                ids = staffUserRepository.findAllIdsByStatus(CommonConstants.ACTIVE_STATUS);
            } else {
                ids = staffUserRepository.findAllLightStaffUserByStatusAndPartneridAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId()).stream().map(StaffUser::getId).collect(Collectors.toList());
//                ids = staffUserRepository.findAllIdsByStatusAndPartnerId(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId());
            }
        } else {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    ids = staffUserRepository.findAllLightStaffByStatusAndIsDeleteIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(StaffUser::getId).collect(Collectors.toList());
//                    ids = staffUserRepository.findAllIdsByStatusAndMvnoIds(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    ids = staffUserRepository.findAllLightStaffByStatusAndPartneridAndIsDeleteIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(StaffUser::getId).collect(Collectors.toList());
//                    ids = staffUserRepository.findAllIdsByStatusAndPartnerIdAndMvnoIds(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            } else {
                List<BusinessUnit> businessUnit = businessUnitRepository.findAllByIdIn(getBUIdsFromCurrentStaff());
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    ids = staffUserRepository.findAllByStatusAndBusinessUnitNameListInAndIsDeleteIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, businessUnit, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(StaffUser::getId).collect(Collectors.toList());
                } else {
                    ids = staffUserRepository.findAllByStatusAndPartneridAndBusinessUnitNameListInAndIsDeleteIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(),businessUnit, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(StaffUser::getId).collect(Collectors.toList());
                }
            }
        }
        List<StaffUser> staffUserList = getStaffUserByServiceArea();
//        List<Integer> staffUserList = getStaffUserIdsByServiceArea();
        List<Integer> finalStaffIdList = null;
        if (!staffUserList.isEmpty()) {
            List<Integer> staffIdList = staffUserList.stream().map(StaffUser::getId).collect(Collectors.toList());
            finalStaffIdList = ids.stream().distinct().filter(staffIdList::contains).collect(Collectors.toList());
//            finalStaffIdList = ids.stream().distinct().filter(staffUserList::contains).collect(Collectors.toList());
        } else {
            finalStaffIdList = ids;
        }
        return staffUserRepository.findAllLightStaffById(finalStaffIdList)
                .stream()
                .sorted(Comparator.comparing(StaffUser::getId).reversed())
                .collect(Collectors.toList());
    }

    public List<StaffUserPojo> getAllActiveStaffUserPojo() {
        long startTime = System.currentTimeMillis();
        System.out.println("Step-1: Started fetching StaffUser IDs");
        long step1Start = System.currentTimeMillis();
        List<Integer> ids;
        if (getMvnoIdFromCurrentStaff() == 1) {
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                ids = staffUserRepository.findAllStaffIdsByStatusAndIsDeleteFalse(CommonConstants.ACTIVE_STATUS);
            } else {
                ids = staffUserRepository.findAllStaffIdsByStatusAndPartneridAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId());
            }
        } else {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    ids = staffUserRepository.findAllStaffIdsByStatusAndIsDeleteIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    ids = staffUserRepository.findAllStaffIdsByStatusAndPartneridAndIsDeleteIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            } else {
                List<BusinessUnit> businessUnit = businessUnitRepository.findAllByIdIn(getBUIdsFromCurrentStaff());
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    ids = staffUserRepository.findIdsByStatusAndBusinessUnitNameListInAndIsDeleteFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, businessUnit, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    ids = staffUserRepository.findIdsByStatusAndPartnerIdAndBusinessUnitNameListInAndIsDeleteFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId(),businessUnit, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
        }
        long step1End = System.currentTimeMillis();
        System.out.println("Step-1 completed in {} ms | Total StaffUser IDs fetched = {}" +
                (step1End - step1Start) + "ms " + (ids != null ? ids.size() : 0));

        System.out.println("Step-2: Filtering StaffUser IDs by service area");
        long step2Start = System.currentTimeMillis();

        List<Integer> staffIdList  = getStaffUserIdsByServiceArea();
        List<Integer> finalStaffIdList = null;
        if (!staffIdList .isEmpty()) {
            finalStaffIdList = ids.stream().distinct().filter(staffIdList::contains).collect(Collectors.toList());
        } else {
            finalStaffIdList = ids;
        }

        List<StaffUserPojo> staffUserPojos = staffUserRepository.findAllLightStaffUserPojoById(finalStaffIdList)
                .stream()
                .sorted(Comparator.comparing(StaffUserPojo::getId).reversed())
                .collect(Collectors.toList());

        long step2End = System.currentTimeMillis();
        System.out.println("Step-2 completed in {} ms | Final StaffUser IDs count = {}" +
                (step2End - step2Start) + "ms " + (finalStaffIdList != null ? finalStaffIdList.size() : 0));

        long totalEnd = System.currentTimeMillis();
        System.out.println("Total execution time for getAllActiveStaffUserPojo(): {} ms" + (totalEnd - startTime));

        return staffUserPojos;
    }


    public List<StaffUser> getActiveStaffUserFromUsername(String username) {
        return staffUserRepository.findByUsernameAndStatusAndIsDeleteIsFalse(username, CommonConstants.ACTIVE_STATUS);
    }
}
