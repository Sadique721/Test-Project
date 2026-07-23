package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.CommonUtils;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.repository.CustomRepository;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.exceptions.AlreadyExistException;
import com.savbill.commonGateway.exceptions.CustomMessageException;
import com.savbill.commonGateway.kafka.GeneratePasswordDto;
import com.savbill.commonGateway.kafka.KafkaConstant;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.Communication.Constants.CommunicationConstant;
import com.savbill.commonGateway.moules.Communication.Helper.CommunicationHelper;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import com.savbill.commonGateway.moules.MasterManagement.Branch.repository.BranchRepository;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.QBusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.mapper.BusinessUnitMapper;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.model.BusinessUnitDTO;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.service.BusinessUnitService;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.mapper.ServiceAreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.ServiceAreaService;
import com.savbill.commonGateway.moules.PartnerManagement.Partner;
import com.savbill.commonGateway.moules.PartnerManagement.PartnerRepository;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordPolicy;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordRepository;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.*;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.*;
import com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory.PasswordHistory;
import com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory.PasswordHistoryRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffRoleMapping.StaffRoleRel;
import com.savbill.commonGateway.moules.SettingsManagement.StaffRoleMapping.StaffRoleRelRepo;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserAccessibleRole.StaffAccessibleRoleMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserAccessibleRole.StaffAccessibleRoleMappingRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserBusinessUnitMapping.QStaffUserBusinessUnitMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserBusinessUnitMapping.StaffUserBusinessUnitMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserBusinessUnitMapping.StaffUserBusinessUnitMappingRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping.StaffLocationMappingRepo;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping.StaffUserLocationMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping.StaffUserLocationMappingDto;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.*;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.*;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserPlanServiceMapping.StaffUserPlanServiceMappingRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.QStaffUserServiceAreaMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.commonGateway.moules.TeamsManagement.TeamUserMapping.QTeamUserMapping;
import com.savbill.commonGateway.moules.TeamsManagement.TeamUserMapping.TeamUserMapping;
import com.savbill.commonGateway.moules.TeamsManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.commonGateway.moules.TeamsManagement.Teams.Teams;
import com.savbill.commonGateway.moules.TeamsManagement.Teams.TeamsRepository;
import com.savbill.commonGateway.moules.TeamsManagement.Teams.TeamsService;
import com.savbill.commonGateway.moules.Template.repository.NotificationTemplateRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.RabbitMqConstants;
import com.savbill.commonGateway.rabbitmq.messages.*;
import com.savbill.commonGateway.rabbitmq.messages.MvnoStatusMessage;
import com.savbill.commonGateway.rabbitmq.messages.StaffStatusChangeMessage;
import com.savbill.commonGateway.rabbitmq.messages.StaffUserMessage;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.savbill.commonGateway.spring.security.CustomUserDetailsService;
import com.savbill.commonGateway.utils.PropertyReaderUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.security.Key;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StaffUserService extends ExBaseAbstractService<StaffUserPojo, StaffUser, Integer> {

//    public StaffUserService(BranchRepository branchRepository,
//                            RoleRepository roleRepository) {
//        sortColMap.put("id", "staffid");
//        sortColMap.put("name", "firstname");
//        sortColMap.put("userName", "username");
//        sortColMap.put("roleName", "srn.concatname");
//        this.branchRepository = branchRepository;
//        this.roleRepository = roleRepository;
//    }

    @Autowired
    private StaffUserPlanServiceMappingRepository staffUserServiceRepository;
    @Autowired
    private StaffUserRepository entityRepository;
    @Autowired
    private StaffRoleRelRepo staffRoleRelRepo;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private RoleService roleService;
    @Autowired
    private StaffUserMapper staffUserMapper;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private BusinessUnitService businessUnitService;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;
    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    @Autowired
    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    @Autowired
    private StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;
    @Autowired
    MessageSender messageSender;
    @Autowired
    NotificationTemplateRepository templateRepository;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Autowired
    private BusinessUnitMapper businessUnitMapper;
    @Autowired
    private TeamsService teamsService;
    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    CreateDataSharedService createDataSharedService;
    private static String MODULE = " [StaffUserService] ";

    @Autowired
    BranchRepository branchRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    StaffLocationMappingRepo staffLocationMappingRepo;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    KafkaProducer<String, Object> kafkaProducer;

    @Autowired
    PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    PasswordRepository passwordRepository;
    @Autowired
    StaffAccessibleRoleMappingRepository staffAccessibleRoleMappingRepository;

    @Value(value = "${app.name}")
    private String applicationName;
    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;


    public StaffUserService(StaffUserRepository repository, StaffUserMapper mapper) {
        super(repository, mapper);
    }

//    public StaffUserService(StaffUserRepository repository, StaffUserMapper mapper, e staffUserMapper) {
//        super(repository, mapper);
//        this.staffUserMapper = staffUserMapper;
//    }

    public String getModuleNameForLog() {
        return "[StaffUserService]";
    }

    //

    /// /    @Autowired
    /// /    CustomerCafAssignmentRepository customerCafAssignmentRepository;
//
//    private static String MODULE = " [StaffUserService] ";
//
//    @Override
//    public JpaRepository<StaffUser, Integer> getRepository() {
//        return entityRepository;
//    }
//
    public Page<StaffUser> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            return entityRepository.searchEntity(searchText, pageRequest);
        } else {
            return entityRepository.searchEntity(searchText, pageRequest, getLoggedInUserPartnerId());
        }

    }

    //
    public List<StaffUserPojo> getAllActiveEntities() {
        List<StaffUserPojo> staffUsers = new ArrayList<>();
//        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//            staffUsers = entityRepository.findAllLightStaffUserPojoByStatusAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS);
//        } else {
//            staffUsers = entityRepository.findAllLightStaffUserPojoByStatusAndPartneridAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId());
//        }
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
            //staffUsers.stream().filter(staff -> (staff.getMvnoId() == getMvnoIdFromCurrentStaff() && staff.getMvnoId() != 1));
            staffUsers = entityRepository.findAllLightStaffUserPojoByMvnoId(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            staffUsers.forEach(staffUserPojo -> {
//                List<Integer> serviceAreaIds = staffUserServiceAreaMappingRepository.findServiceAreaByStaffId(staffUserPojo.getId());
//                staffUserPojo.setServiceAreaIdsList(serviceAreaIds.stream().map(integer -> integer.longValue()).collect(Collectors.toList()));
//                List<Long> businessUnitIdList = staffUserBusinessUnitMappingRepository.findBuidByStaffId(staffUserPojo.getId());
//                staffUserPojo.setBusinessUnitIdsList(businessUnitIdList);
//            });
            return staffUsers;
        } else {
            staffUsers = entityRepository.findAllLightStaffUserByMvnoIdAndBuIds(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff());
//            staffUsers.forEach(staffUserPojo -> {
//                List<Integer> serviceAreaIds = staffUserServiceAreaMappingRepository.findServiceAreaByStaffId(staffUserPojo.getId());
//                staffUserPojo.setServiceAreaIdsList(serviceAreaIds.stream().map(integer -> integer.longValue()).collect(Collectors.toList()));
//                List<Long> businessUnitIdList = staffUserBusinessUnitMappingRepository.findBuidByStaffId(staffUserPojo.getId());
//                staffUserPojo.setBusinessUnitIdsList(businessUnitIdList);
//            });
            return staffUsers;
        }
    }

    public List<StaffUserPojo> getAllActiveEntitiesByServiceAreaIds(List<Long> seriveAreaIds) {
        List<Object[]> staffData = new ArrayList<>();
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
            staffData = entityRepository.findAllLightStaffUserPojoByMvnoIdAndServiceAreaIds(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), seriveAreaIds);
        } else {
            staffData = entityRepository.findAllLightStaffUserByMvnoIdAndBuIdsAndServiceAreaIds(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff(), seriveAreaIds);
        }

        List<StaffUserPojo> staffList = staffData.stream()
                .map(obj -> new StaffUserPojo(
                        Integer.valueOf(obj[0].toString()),  // staffid
                        (String) obj[1],  // username
                        (String) obj[2],
                        (String) obj[3])// lastname
                ).collect(Collectors.toList());
        return staffList;
    }

    public Page<StaffUser> getAllActiveEntities(Integer pageNumber, Integer size, String product) {
        Page<StaffUser> staffUsers;
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        QRole role = QRole.role;
        QStaffUser qStaffUser = QStaffUser.staffUser;
        QBusinessUnit qBusinessUnit = QBusinessUnit.businessUnit;
        BooleanExpression expression = qStaffUser.isDelete.eq(false);
        if (getLoggedInUser().getMvnoId() != 1) {
            expression = expression.and(qStaffUser.mvnoId.in(getLoggedInUser().getMvnoId())
                    .and(qStaffUser.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))
                    .and(qStaffUser.roles.any().product.equalsIgnoreCase(product)));
        } else {
            expression = expression.and(qStaffUser.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))
                    .and(qStaffUser.roles.any().product.equalsIgnoreCase(product));
        }
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            expression = expression.and(qStaffUser.partnerid.eq(CommonConstants.DEFAULT_PARTNER_ID));
//            staffUsers = entityRepository.findAll(expression,pageable);
        } else {
            expression = expression.and(qStaffUser.partnerid.eq(getLoggedInUser().getPartnerId()));
            // staffUsers = entityRepository.findAll(expression,pageable);
        }
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().isEmpty()) {
//            staffUsers = entityRepository.findAll(expression, pageable);
        } else {
            expression = expression.and(qStaffUser.businessUnitNameList.any().in(businessUnitRepository.findAllById(getBUIdsFromCurrentStaff())));
//            staffUsers = entityRepository.findAll(expression, pageable);
        }
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        JPQLQuery<StaffUser> query = queryFactory
                .select(Projections.constructor(StaffUser.class,
                        qStaffUser.id,
                        qStaffUser.firstname,
                        qStaffUser.lastname,
                        qStaffUser.phone,
                        qStaffUser.partnerid,
                        qStaffUser.username
                ))
                .from(qStaffUser)
                .where(expression)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<StaffUser> staffUser = query.fetch();

        long total = queryFactory
                .select(qStaffUser.count())
                .from(qStaffUser)
                .where(expression)
                .fetchOne();

        Page<StaffUser> staffUserPage = new PageImpl<>(staffUser, pageable, total);
        return staffUserPage;
    }

    public List<StaffUser> getStaffUserFromUsername(String username) {
        return entityRepository.findByUsername(username);
    }

    //
    public List<StaffUser> getActiveStaffUserFromUsername(String username) {
        return entityRepository.findByUsernameAndStatusAndIsDeleteIsFalse(username, CommonConstants.ACTIVE_STATUS);
    }

    public List<StaffUserPojo> searchUserCustom(String searchText) throws Exception {
        List<StaffUser> list = entityRepository.findAllUsername(searchText);
        return convertResponseModelIntoPojo(list);
    }

    public void increaseFailAttempts(String username) {
        List<StaffUser> userList = entityRepository.findByUsername(username);
        if (userList != null && userList.size() > 0) {
            StaffUser user = userList.get(0);
            user.setFailcount(user.getFailcount() + 1);
            entityRepository.save(user);
        }
    }

    //
    public void resetFailAttempts(String username) {
        String SUBMODULE = MODULE + "[resetFailAttempts()]";
        List<StaffUser> userList = entityRepository.findByUsername(username);
        try {
            if (userList != null && userList.size() > 0) {
                StaffUser user = userList.get(0);
                user.setLast_login_time(LocalDateTime.now());
                user.setFailcount(0);
                entityRepository.save(user);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    //
    @Transactional
    public void createPartnerUser(Partner partner, Integer userId) throws Exception {
        String SUBMODULE = MODULE + "[createPartnerUser()]";
        StaffUser user = new StaffUser();
        try {
            user.setUsername(partner.getEmail());
            user.setPassword(CommonUtils.generateBcryptPassword(partner.getEmail()));
            user.setPartnerid(partner.getId());
            user.setEmail(partner.getEmail());
            user.setFirstname(partner.getName());
            user.setIsDelete(partner.getIsDelete());
            user.setLastname(partner.getName());
            user.setStatus(CommonConstants.ACTIVE_STATUS);
            user.setMvnoId(partner.getMvnoId());
            user.setPhone(partner.getMobile());
            // user.getBusinessUnit().setId(partner.getBuId());
            HashSet<Role> roles = new HashSet<>();
            //Add default role
//            String roleId = getLoggedInUser().getRolesList();
            if(partner.getPartnerType().equalsIgnoreCase("LCO")){
                user.setLcoId(partner.getId());
            }
            Long loggedInUserId = Long.valueOf(userId);
            StaffRoleRel staffRoleRel = staffRoleRelRepo.findByStaffId(loggedInUserId);
            String roleId = String.valueOf(staffRoleRel.getRoleId());
            Role role = null;
            if (partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO)) {
                String roleName = CommonUtils.getPartnerRoleName();
                if (!roleName.isEmpty()) {
                    List<Role> roleList = roleRepository.findAllByRolename(roleName);
                    if (!CollectionUtils.isEmpty(roleList))
                        role = roleService.convertRolePojoToRoleModel(roleService.getEntityById(roleList.get(0).getId()));
                }
            } else {
                role = roleService.convertRolePojoToRoleModel(roleService.getEntityById(CommonUtils.getPartnerRoleId(partner.getMvnoId()).longValue()));
            }
            if (role == null) {
                role = roleService.convertRolePojoToRoleModel(roleService.getEntityById(Long.valueOf(roleId)));
            }
            roles.add(role);
            user.setRoles(roles);
            user.setCreatedById(userId);
            user.setLastModifiedById(userId);
            user.setCreatedByName(entityRepository.findById(userId).map(StaffUser::getCreatedByName).toString());
            user.setLastModifiedByName(entityRepository.findById(userId).map(StaffUser::getCreatedByName).toString());
            user = staffUserRepository.save(user);
            if (partner.getServiceAreaList().size() > 0) {
                StaffUser finalUser = user;
                partner.getServiceAreaList().forEach(serviceArea -> {
                    StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMapping.setStaffId(finalUser.getId());
                    staffUserServiceAreaMapping.setServiceId(Math.toIntExact(serviceArea.getId()));
                    staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                    staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                    staffUserServiceAreaMapping.setCreatedById(finalUser.getId());
                    staffUserServiceAreaMapping.setLastModifiedByName(finalUser.getUsername());
                    staffUserServiceAreaMapping.setLastModifiedById(finalUser.getId());
                    staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                    staffUserServiceAreaMapping.setCreatedByName(finalUser.getUsername());
                    staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMapping);
                    ApplicationLogger.logger.info("StaffUserServiceAreaMapping:-  " + staffUserServiceAreaMapping.toString());
                });
                if (partner.getId() != null && partner.getBuId() != null) {
                    StaffUser finalUser2 = user;
                    StaffUserBusinessUnitMapping staffUserBusinessUnitMapping = new StaffUserBusinessUnitMapping();
                    staffUserBusinessUnitMapping.setStaffId(finalUser2.getId());
                    staffUserBusinessUnitMapping.setCreatedOn(LocalDateTime.now());
                    staffUserBusinessUnitMapping.setLastmodifiedOn(LocalDateTime.now());
                    staffUserBusinessUnitMapping.setCreatedById(finalUser2.getId());
                    staffUserBusinessUnitMapping.setLastModifiedByName(finalUser2.getUsername());
                    staffUserBusinessUnitMapping.setLastModifiedById(finalUser2.getId());
                    staffUserBusinessUnitMapping.setCreatedOn(LocalDateTime.now());
                    staffUserBusinessUnitMapping.setCreatedByName(finalUser2.getUsername());
                    //  Long l = partner.getBuId();
                    //Long i =  partner.getBuId();
                    staffUserBusinessUnitMapping.setBusinessunitId(partner.getBuId());
                    staffUserBusinessUnitMappingRepository.save(staffUserBusinessUnitMapping);
                    ApplicationLogger.logger.info("StaffUserBusinessUnitMapping:-  " + staffUserBusinessUnitMapping.toString());
                }
            }
            List<ServiceAreaDTO> serviceAreaDTOS = user.getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            List<BusinessUnitDTO> businessUnitDTOS = user.getBusinessUnitNameList().stream().map(data -> businessUnitMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
            BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(user.getId()));
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));
            StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(user, new CycleAvoidingMappingContext());
            StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS, businessUnitDTOS);

            //messageSender.send(staffUserMessage,RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
            //kafkaMessageSender.send(new KafkaMessageData(staffUserMessage,staffUserMessage.getClass().getSimpleName()));
            //UserMessage userMessage = new UserMessage(staffUserPojo);
            //messageSender.send(userMessage, RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS);
            sharedStaffData(user, CommonConstants.OPERATION_ADD);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Page<StaffUserPojo> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, String product, Long staffId) {
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        List<StaffUserPojo> staffUsers = new ArrayList<>();
        StaffUser staff= staffUserRepository.findStaffUserStaffById(getLoggedInUserId()).orElse(null);
        if(mvnoId == 1  && staff.getDepartment()!=null){
            staffUsers = entityRepository.findAllStaffUsersByDepartment(product,staff.getDepartment());
        } else if (mvnoId == 1) {
            staffUsers = entityRepository.findAllStaffUsers(product);
        } else {
            staffUsers = entityRepository.findAllStaffUsersByRoleProduct(product, mvnoId);
        }
        List<Long> staffIds = staffUsers.stream().map(staffUser -> (staffUser.getId().longValue())).collect(Collectors.toList());
        List<StaffRoleRel> staffRoleRels = staffRoleRelRepo.findAllByStaffIdIn(staffIds);
        List<Role> roles = roleRepository.finadAllByProduct(product);
        List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings = staffUserBusinessUnitMappingRepository.findAllByBusinessunitIdIn(getBUIdsFromCurrentStaff());
        try {
            Long total = null;
            if (getLoggedInUser().getLco()) {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    if (filterList == null || 0 == filterList.size()) {
                        List<StaffUserPojo> staffUserList = new ArrayList<>();
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            staffUsers = staffUsers.stream()
                                    .filter(user -> user.getIsDelete().equals(false) && user.getLcoId() == null)
                                    .collect(Collectors.toList());
                            total = (long) staffUsers.size();
                            staffUserList = getAllLCOStaff(staffUsers, staffRoleRels, roles);
                        } else {
                            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                                total = (long) staffUsers.stream()
                                        .filter(user -> user.getIsDelete().equals(false) &&
                                                (user.getMvnoId() == getMvnoIdFromCurrentStaff() ||
                                                        user.getMvnoId() == 1) &&
                                                user.getLcoId().equals(getLoggedInUser().getPartnerId()))
                                        .collect(Collectors.toMap(
                                                staffUser -> staffUser,
                                                staffUser -> staffRoleRels.stream()
                                                        .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                                        .flatMap(staffRoleRel -> roles.stream()
                                                                .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))))).size();
                                staffUserList = getAllLCOStaffWithoutBUIDS(staffUsers, staffRoleRels, roles);
                            } else {
                                total = (long) staffUsers.stream()
                                        .filter(user -> user.getIsDelete().equals(false) &&
                                                (user.getMvnoId() == getMvnoIdFromCurrentStaff() ||
                                                        user.getMvnoId() == 1) &&
                                                user.getLcoId().equals(getLoggedInUser().getPartnerId()) &&
                                                staffUserBusinessUnitMappings.stream().anyMatch(staffUserBusinessUnitMapping -> staffUserBusinessUnitMapping.getStaffId().equals(user.getId())))
                                        .collect(Collectors.toMap(
                                                staffUser -> staffUser,
                                                staffUser -> staffRoleRels.stream()
                                                        .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                                        .flatMap(staffRoleRel -> roles.stream()
                                                                .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))))).size();
                                staffUserList = getAllLCOStaffWithBUIDS(staffUsers, staffRoleRels, roles, staffUserBusinessUnitMappings);
                            }
                        }
                        Pageable paging = PageRequest.of(pageNumber - 1, customPageSize);
                        int start = Math.min((int) paging.getOffset(), staffUserList.size());
                        int end = Math.min((start + paging.getPageSize()), staffUserList.size());
                        Page<StaffUserPojo> page = new PageImpl<>(staffUserList.subList(start, end), paging, staffUserList.size());
                        return page;
                    } else
                        return (Page<StaffUserPojo>) search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
                } else {
                    if (filterList == null || 0 == filterList.size()) {
                        List<StaffUserPojo> staffUserList = new ArrayList<>();
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            staffUsers = staffUsers.stream()
                                    .filter(user -> user.getIsDelete().equals(false) && user.getLcoId() == null)
                                    .collect(Collectors.toList());
                            total = (long) staffUsers.size();
                            staffUserList = getAllStaffUser(staffUsers, staffRoleRels, roles);
                        } else {
                            total = staffUsers.stream()
                                    .filter(user -> Boolean.FALSE.equals(user.getIsDelete()) &&
                                            (user.getMvnoId().equals(getMvnoIdFromCurrentStaff()) || user.getMvnoId() == 1) &&
                                            user.getPartnerid().equals(getLoggedInUserPartnerId()))
                                    .filter(user -> staffRoleRels.stream()
                                            .anyMatch(staffRoleRel -> staffRoleRel.getStaffId().equals(user.getId()) &&
                                                    roles.stream().anyMatch(role -> role.getId().equals(staffRoleRel.getRoleId()))))
                                    .count();
                            staffUserList = getAllStaffUsersWithPartnerId(staffUsers, staffRoleRels, roles);
                        }
                        Pageable paging = PageRequest.of(pageNumber - 1, customPageSize);
                        int start = Math.min((int) paging.getOffset(), staffUserList.size());
                        int end = Math.min((start + paging.getPageSize()), staffUserList.size());
                        Page<StaffUserPojo> page = new PageImpl<>(staffUserList.subList(start, end), paging, staffUserList.size());
                        return page;
                    } else
                        return (Page<StaffUserPojo>) search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
                }
            } else {
                if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                    if (filterList == null || 0 == filterList.size()) {
                        List<StaffUserPojo> staffUserList = new ArrayList<>();
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            staffUsers = staffUsers.stream()
                                    .filter(user -> user.getIsDelete().equals(false) && user.getLcoId() == null)
                                    .collect(Collectors.toList());
                            total = (long) staffUsers.size();
                            staffUserList = getAllStaffUser(staffUsers, staffRoleRels, roles);
                        } else {
                            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                                staffUserList = getAllStaffUsersWithoutBUIDS(staffUsers, staffRoleRels, roles);
                            } else {
                                total = (long) staffUsers.stream()
                                        .filter(user -> user.getIsDelete().equals(false) &&
                                                (user.getMvnoId() == getMvnoIdFromCurrentStaff() ||
                                                        user.getMvnoId() == 1) &&
                                                user.getLcoId() == null &&
                                                staffUserBusinessUnitMappings.stream().anyMatch(staffUserBusinessUnitMapping -> staffUserBusinessUnitMapping.getStaffId().equals(user.getId())))
                                        .collect(Collectors.toMap(
                                                staffUser -> staffUser,
                                                staffUser -> staffRoleRels.stream()
                                                        .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                                        .flatMap(staffRoleRel -> roles.stream()
                                                                .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))))).size();
                                staffUserList = getAllStaffUsersWITHBUIDS(staffUsers, staffRoleRels, roles, staffUserBusinessUnitMappings);
                            }
                        }
//                        if (product.equalsIgnoreCase("iwf")) {
//                            List<StaffUserLocationMapping> staffUserLocationMappings = staffLocationMappingRepo.findAllByStaffId(staffId);
//                            if (!staffUserLocationMappings.isEmpty()) {
//                                List<Long> locations = staffUserLocationMappings.stream()
//                                        .map(StaffUserLocationMapping::getLocationId)
//                                        .collect(Collectors.toList());
//                                List<Integer> staffIdLocations = staffLocationMappingRepo.findAllByLocationIdIn(locations).stream()
//                                        .map(StaffUserLocationMapping::getStaffId)
//                                        .map(Long::intValue)
//                                        .collect(Collectors.toList());
//                                List<StaffUser> newStaffUsers = entityRepository.findAllByIdIn(staffIdLocations).stream()
//                                        .filter(Objects::nonNull)
//                                        .collect(Collectors.toList());
//                                staffUserList.clear();
//                                newStaffUsers.sort(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()));
//                                staffUserList.addAll(newStaffUsers);
//                            }
//                        }
                        System.out.println("Pageable Start : " + LocalDateTime.now());
                        Pageable paging = PageRequest.of(pageNumber - 1, customPageSize);
                        int start = Math.min((int) paging.getOffset(), staffUserList.size());
                        int end = Math.min((start + paging.getPageSize()), staffUserList.size());
                        Page<StaffUserPojo> page = new PageImpl<>(staffUserList.subList(start, end), paging, staffUserList.size());
                        System.out.println("Pageable end : " + LocalDateTime.now());
                        return page;
                    } else
                        return (Page<StaffUserPojo>) search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
                } else {
                    if (filterList == null || 0 == filterList.size()) {
                        List<StaffUserPojo> staffUserList = new ArrayList<>();
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            staffUserList = getAllStaffUser(staffUsers, staffRoleRels, roles);
                        } else {
                            staffUserList = getAllStaffUsersWITHPartnerId(staffUsers, staffRoleRels, roles);
                        }
                        total = (long) staffUserList.size();
                        Pageable paging = PageRequest.of(pageNumber - 1, customPageSize);
                        int start = Math.min((int) paging.getOffset(), staffUserList.size());
                        int end = Math.min((start + paging.getPageSize()), staffUserList.size());
                        Page<StaffUserPojo> page = new PageImpl<>(staffUserList.subList(start, end), paging, staffUserList.size());
                        return page;
                    } else
                        return (Page<StaffUserPojo>) search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
                }
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public List<StaffUser> getAllStaffUsers(List<StaffUser> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {
        List<StaffUser> staffUserList = new ArrayList<>();
        if (getMvnoIdFromCurrentStaff() != 1) {
            Map<StaffUser, String> staffRoles = staffUsers.stream()
                    .filter(user -> user.getIsDelete().equals(false) && user.getLcoId() == null)
                    .collect(Collectors.toMap(
                            staffUser -> staffUser,
                            staffUser -> staffRoleRels.stream()
                                    .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                    .flatMap(staffRoleRel -> roles.stream()
                                            .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
                                            .map(Role::getRolename)).collect(Collectors.joining(", "))
                    ));
            staffUserList = staffRoles.keySet().stream()
                    .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            staffUserList = staffUsers.stream()
                    .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        return staffUserList;
    }

    public List<StaffUserPojo> getAllStaffUser(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {
        List<StaffUserPojo> staffUserList = new ArrayList<>();
        if (getMvnoIdFromCurrentStaff() != 1) {
            Map<StaffUserPojo, String> staffRoles = staffUsers.stream()
                    .filter(user -> user.getIsDelete().equals(false) && user.getLcoId() == null)
                    .collect(Collectors.toMap(
                            staffUser -> staffUser,
                            staffUser -> staffRoleRels.stream()
                                    .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                    .flatMap(staffRoleRel -> roles.stream()
                                            .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
                                            .map(Role::getRolename)).collect(Collectors.joining(", "))
                    ));
            staffUserList = staffRoles.keySet().stream()
                    .sorted(Comparator.comparing(StaffUserPojo::getId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            staffUserList = staffUsers.stream()
                    .sorted(Comparator.comparing(StaffUserPojo::getId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        return staffUserList;
    }

    public List<StaffUserPojo> getAllStaffUsersWITHBUIDS(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles, List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings) {
        Map<StaffUserPojo, String> staffRoles = staffUsers.stream()
                .filter(user -> user.getIsDelete().equals(false) &&
                        user.getLcoId() == null &&
                        staffUserBusinessUnitMappings.stream().anyMatch(staffUserBusinessUnitMapping -> staffUserBusinessUnitMapping.getStaffId().equals(user.getId())))
                .collect(Collectors.toMap(
                        staffUser -> staffUser,
                        staffUser -> staffRoleRels.stream()
                                .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                .flatMap(staffRoleRel -> roles.stream()
                                        .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
                                        .map(Role::getRolename)).collect(Collectors.joining(", "))
                ));
        List<StaffUserPojo> staffUserList = staffRoles.keySet().stream()
                .sorted(Comparator.comparing(StaffUserPojo::getCreatedate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return staffUserList;
    }

    public List<StaffUserPojo> getAllStaffUsersWithoutBUIDS(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {
        System.out.println("start time started : " + LocalDateTime.now());
        List<StaffUserPojo> newstaffUsers = new ArrayList<>();
        if(getLoggedInUser().getLco()){
             newstaffUsers = staffUserRepository.getFilteredStaffUsersLCO(getMvnoIdFromCurrentStaff(),getLoggedInUser().getPartnerId());
        }else{
            newstaffUsers = staffUserRepository.getFilteredStaffUsers(getMvnoIdFromCurrentStaff());
        }
        List<Long> staffIds = newstaffUsers.stream().map(staffUserPojo -> staffUserPojo.getId().longValue()).collect(Collectors.toList());
        Map<Long, List<String>> rolesMap = staffUserRepository.getRolesByStaffIds(staffIds)
                .stream()
                .collect(Collectors.groupingBy(
                        obj -> (Long) obj[0],  // staffId as key
                        Collectors.mapping(obj -> (String) obj[1], Collectors.toList()) // roleNames as value
                ));

// Assign roles to each user
        newstaffUsers.forEach(user -> user.setRoleName(rolesMap.getOrDefault(user.getId(), Collections.emptyList())));
        System.out.println("start time ended : " + LocalDateTime.now());
        return newstaffUsers;
    }

    public List<StaffUserPojo> getAllStaffUsersWITHPartnerId(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {
        Map<StaffUserPojo, String> staffRoles = staffUsers.stream()
                .filter(user -> user.getIsDelete().equals(false) &&
                        user.getPartnerid() == getLoggedInUserPartnerId())
                .collect(Collectors.toMap(
                        staffUser -> staffUser,
                        staffUser -> staffRoleRels.stream()
                                .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                .flatMap(staffRoleRel -> roles.stream()
                                        .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
                                        .map(Role::getRolename)).collect(Collectors.joining(", "))
                ));
        List<StaffUserPojo> staffUserList = staffRoles.keySet().stream()
                .sorted(Comparator.comparing(StaffUserPojo::getCreatedate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return staffUserList;
    }

//    public List<StaffUserPojo> getAllStaffUsersWithPartnerId(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {
//        Map<StaffUserPojo, String> staffRoles = staffUsers.stream()
//                .filter(user -> user.getIsDelete().equals(false) &&
//                        user.getPartnerid() == getLoggedInUserPartnerId())
//                .collect(Collectors.toMap(
//                        staffUser -> staffUser,
//                        staffUser -> staffRoleRels.stream()
//                                .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
//                                .flatMap(staffRoleRel -> roles.stream()
//                                        .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
//                                        .map(Role::getRolename)).collect(Collectors.joining(", "))
//                ));
//        List<StaffUserPojo> staffUserList = staffRoles.keySet().stream()
//                .sorted(Comparator.comparing(StaffUserPojo::getCreatedate, Comparator.reverseOrder()))
//                .collect(Collectors.toList());
//        return staffUserList;
//    }
public List<StaffUserPojo> getAllStaffUsersWithPartnerId(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {

    Map<Integer, String> staffRoles = staffUsers.stream()
            .filter(user -> Boolean.FALSE.equals(user.getIsDelete()) &&
                    user.getPartnerid() != null &&
                    user.getPartnerid().equals(getLoggedInUserPartnerId()))
            .collect(Collectors.toMap(
                    StaffUserPojo::getId,
                    staffUser -> staffRoleRels.stream()
                            .filter(staffRoleRel -> staffRoleRel.getStaffId() != null &&
                                    staffRoleRel.getStaffId().equals(staffUser.getId()))
                            .flatMap(staffRoleRel -> roles.stream()
                                    .filter(role -> role.getId() != null &&
                                            role.getId().equals(staffRoleRel.getRoleId()))
                                    .map(Role::getRolename))
                            .collect(Collectors.joining(", ")),
                    (existing, replacement) -> existing
            ));

    List<StaffUserPojo> staffUserList = staffUsers.stream()
            .filter(user -> staffRoles.containsKey(user.getId()))
            .sorted(Comparator.comparing(
                    StaffUserPojo::getCreatedate,
                    Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .collect(Collectors.toList());
    return staffUserList;
}

    public List<StaffUserPojo> getAllLCOStaff(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {
        Map<StaffUserPojo, String> staffRoles = staffUsers.stream()
                .filter(user -> user.getIsDelete().equals(false) &&
                        user.getLcoId().equals(getLoggedInUser().getPartnerId()))
                .collect(Collectors.toMap(
                        staffUser -> staffUser,
                        staffUser -> staffRoleRels.stream()
                                .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                .flatMap(staffRoleRel -> roles.stream()
                                        .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
                                        .map(Role::getRolename)).collect(Collectors.joining(", "))
                ));
        List<StaffUserPojo> staffUserList = staffRoles.keySet().stream()
                .sorted(Comparator.comparing(StaffUserPojo::getCreatedate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return staffUserList;
    }

    public List<StaffUserPojo> getAllLCOStaffWithoutBUIDS(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles) {
        Map<StaffUserPojo, String> staffRoles = staffUsers.stream()
                .filter(user -> user.getIsDelete().equals(false) &&
                        user.getLcoId().equals(getLoggedInUser().getPartnerId()))
                .collect(Collectors.toMap(
                        staffUser -> staffUser,
                        staffUser -> staffRoleRels.stream()
                                .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                .flatMap(staffRoleRel -> roles.stream()
                                        .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
                                        .map(Role::getRolename)).collect(Collectors.joining(", "))
                ));
        List<StaffUserPojo> staffUserList = staffRoles.keySet().stream()
                .sorted(Comparator.comparing(StaffUserPojo::getCreatedate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return staffUserList;
    }

    public List<StaffUserPojo> getAllLCOStaffWithBUIDS(List<StaffUserPojo> staffUsers, List<StaffRoleRel> staffRoleRels, List<Role> roles, List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings) {
        Map<StaffUserPojo, String> staffRoles = staffUsers.stream()
                .filter(user -> user.getIsDelete().equals(false) &&
                        user.getLcoId().equals(getLoggedInUser().getPartnerId()) &&
                        staffUserBusinessUnitMappings.stream().anyMatch(staffUserBusinessUnitMapping -> staffUserBusinessUnitMapping.getStaffId().equals(user.getId())))
                .collect(Collectors.toMap(
                        staffUser -> staffUser,
                        staffUser -> staffRoleRels.stream()
                                .filter(staffRoleRel -> staffRoleRel.getStaffId().equals(staffUser.getId()))
                                .flatMap(staffRoleRel -> roles.stream()
                                        .filter(role -> role.getId().equals(staffRoleRel.getRoleId()))
                                        .map(Role::getRolename)).collect(Collectors.joining(", "))
                ));
        List<StaffUserPojo> staffUserList = staffRoles.keySet().stream()
                .sorted(Comparator.comparing(StaffUserPojo::getCreatedate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        return staffUserList;
    }

    public List<StaffUser> getAllUsers() {
        return entityRepository.findAll();
    }

    //
    public void deleteStaffUser(Integer id) {
        entityRepository.deleteById(id);
        Optional<StaffUser> staffUser = entityRepository.findById(id);
        staffUser.get().setIsDelete(true);
        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser.get(), new CycleAvoidingMappingContext());

        List<ServiceAreaDTO> serviceAreaDTOS = staffUser.get().getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        List<BusinessUnitDTO> businessUnitDTOS = staffUser.get().getBusinessUnitNameList().stream().map(data -> businessUnitMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
        BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(id));
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));

        StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS, businessUnitDTOS);
        //messageSender.send(staffUserMessage,RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
        //kafkaMessageSender.send(new KafkaMessageData(staffUserMessage,staffUserMessage.getClass().getSimpleName()));
        //messageSender.send(staffUserMessage,RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE);
        createDataSharedService.deleteEntityDataForAllMicroService(staffUser);

//        messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE);


    }

    //
    public StaffUser getStaffUserForAdd() {
        return new StaffUser();
    }

    //
    public StaffUser getStaffUserForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

    public StaffUser getByUserName(String uname) throws Exception {
        StaffUser staffUser;
        staffUser = entityRepository.findStaffUserByUsername(uname);
        return staffUser;
    }

    public String forgotPass(StaffUser staffUser) throws Exception {
        Random random = new Random();
        String otp = String.format("%04d", random.nextInt(10000));
        staffUser.setOtp(otp);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        LocalDateTime validate = LocalDateTime.parse(formatDateTime, format);
        staffUser.setOtpvalidate(validate);
        update(staffUser);
        CommunicationHelper communicationHelper = new CommunicationHelper();
        Map<String, String> map = new HashMap<>();
        map.put(CommunicationConstant.DESTINATION, staffUser.getPhone());
        map.put(CommunicationConstant.EMAIL, staffUser.getEmail());
        map.put(CommunicationConstant.OTP, otp);
        communicationHelper.generateCommunicationDetails(18L, Collections.singletonList(map));

        return "Success--" + otp;
    }

    //
    public String validateForgotPassword(StaffUser staffUser, ForgotPassowrdDTO dto) throws Exception {
        String response;
        LocalDateTime time = staffUser.getOtpvalidate();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        LocalDateTime validate = LocalDateTime.parse(formatDateTime, format);
        LocalDateTime tempDateTime = LocalDateTime.from(staffUser.getOtpvalidate());
        long minutes = tempDateTime.until(validate, ChronoUnit.MINUTES);
        long min = Long.parseLong(messagesProperty.get("staffuser.validate.time"));
        if (staffUser.getOtp().equalsIgnoreCase(dto.getOtp()) && minutes <= min) {
            response = CommonConstants.FLASH_MSG_TYPE_SUCCESS;
        } else {
            response = CommonConstants.FLASH_MSG_TYPE_ERROR;
        }
        return response;
    }

    //
    public StaffUserPojo updateProfile(StaffUser staffUser, UpdateProfileDTO dto) throws Exception {
        staffUser.setFirstname(dto.getFirstname());
        staffUser.setLastname(dto.getLastname());
        staffUser.setEmail(dto.getEmail());
        staffUser.setPhone(dto.getPhone());
        update(staffUser);
        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext());
        return staffUserPojo;
    }

    //
    public StaffUser saveStaffUser(StaffUser staffUser) throws Exception {
        String SUBMODULE = MODULE + " [saveStaffUser()] ";
        try {
            if (staffUser != null) {
//                if (staffUser.getPassword() != null) {
//                    PasswordEncoder encoder = new BCryptPasswordEncoder();
//                    staffUser.setPassword(encoder.encode(staffUser.getPassword()));
////                    staffUser.setPassword(staffUser.getPassword());
//                    staffUser.setPasswordDate(LocalDateTime.now());
//                }
                if (staffUser.getPartnerid() != null) {
                    staffUser.setPartnerid(staffUser.getPartnerid());
                }

                Date currentTimestamp = new Date();
                String uuId = PropertyReaderUtil.random(currentTimestamp.getTime());
                staffUser.setUuid(uuId);

                if (getLoggedInUser().getLco())
                    staffUser.setLcoId(getLoggedInUser().getPartnerId());
                else
                    staffUser.setLcoId(null);

                StaffUser user = entityRepository.findStaffUserByUsername(staffUser.getUsername());
                if (staffUser.getId() != null) {
                    staffUser.setPasswordDate(LocalDateTime.now());
//                    List<ServiceArea> staffServiceList = new ArrayList<>();
                    List<ServiceArea> staffServiceAreaList = staffUser.getServiceAreaNameList();
                    staffUser.setServiceAreaNameList(staffServiceAreaList);

                    user = entityRepository.save(staffUser);
                    //save staff service-area mapping
                    if(staffServiceAreaList != null && !staffServiceAreaList.isEmpty()){
                        Long serviceIdStart = System.currentTimeMillis();
                        List<Long> serviceAreaIds = staffServiceAreaList.stream()
                                .map(ServiceArea::getId) // or .getServiceareaid() depending on your field
                                .collect(Collectors.toList());
                        List<StaffUserServiceAreaMapping> mappings = StaffUserServiceAreaMapping.createMappings(user.getId(), serviceAreaIds);
                        batchInsert(mappings);
                    }
                    savePasswordHistory(staffUser, staffUser.getPassword());
                } else {
                    if (user != null) staffUser.setId(user.getId());
                    staffUser.setPasswordDate(LocalDateTime.now());
                    List<ServiceArea> staffServiceList = new ArrayList<>();
                    List<ServiceArea> staffServiceAreaList = staffUser.getServiceAreaNameList();
                    staffUser.setServiceAreaNameList(staffServiceList);

                    // Save Staff User without service-area mapping
                    user = entityRepository.save(staffUser);
                    //save staff service-area mapping
                    if(staffServiceAreaList != null && !staffServiceAreaList.isEmpty()){
                        Long serviceIdStart = System.currentTimeMillis();
                        List<Long> serviceAreaIds = staffServiceAreaList.stream()
                                .map(ServiceArea::getId) // or .getServiceareaid() depending on your field
                                .collect(Collectors.toList());
                        List<StaffUserServiceAreaMapping> mappings = StaffUserServiceAreaMapping.createMappings(user.getId(), serviceAreaIds);
                        batchInsert(mappings);
                    }
                    // Save the new password in the PasswordHistory table
                    savePasswordHistory(staffUser, staffUser.getPassword());
                }
                return user;
            }
            return null;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void batchInsert(List<StaffUserServiceAreaMapping> mappings) {
        int batchSize = 500;
        int totalRecords = mappings.size();
        int batchCount = (int) Math.ceil((double) totalRecords / batchSize);

        for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
            int start = batchIndex * batchSize;
            int end = Math.min(start + batchSize, totalRecords);

            List<StaffUserServiceAreaMapping> subList = mappings.subList(start, end);
            long batchStart = System.currentTimeMillis();

            jdbcTemplate.batchUpdate(
                    "INSERT INTO tbltstaffservicearearel (staffid, serviceareaid, created_on, lastmodified_on) VALUES (?, ?, ?, ?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException, SQLException {
                            StaffUserServiceAreaMapping m = subList.get(i);
                            ps.setInt(1, m.getStaffId());
                            ps.setInt(2, m.getServiceId());
                            ps.setTimestamp(3, Timestamp.valueOf(m.getCreatedOn()));
                            ps.setTimestamp(4, Timestamp.valueOf(m.getLastmodifiedOn()));
                        }
                        public int getBatchSize() {
                            return subList.size();
                        }
                    }
            );
        }
    }

    public StaffUserPojo save(StaffUserPojo pojo, HttpServletRequest request) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
        try {
            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
            if (pojo.getBusinessUnitIdsList() != null) {
                pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
            }
            if (getLoggedInUser().getLco()) pojo.setLcoId(getLoggedInUser().getPartnerId());
            else pojo.setLcoId(null);

            //if (pojo.getServiceAreaIdsList() != null || !pojo.getBusinessUnitIdsList().isEmpty()) {
            if (pojo.getServiceAreaIdsList() != null) {
                pojo.setServiceAreaNameList(serviceAreaRepository.getLightServiceAreaFromIds(pojo.getServiceAreaIdsList()));
//                if(pojo.getBusinessUnitIdsList() != null) {
//                    pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
//                }
                if (pojo.getPassword() != null) {
                    // Delegate password validation and encoding to the service
                    validateAndEncodePasswordStaff(pojo);
                }
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);


                /** Staff Location Mapping Save for IWF */
                saveUpdateStaffLocation(pojo, obj.getId(), CommonConstants.OPERATION_ADD);
                /** Save Staff Accessible Role Mapping*/
                saveStaffAccessibleRoleMapping(pojo.getAssignableRoleIds(), obj.getId());
                pojo = convertStaffUserModelToStaffUserPojo(obj);

                if(!pojo.getServiceAreaIdsList().isEmpty()){
                    List<ServiceArea> serviceAreaList = new ArrayList<>();
                    for (Long id : pojo.getServiceAreaIdsList()) {
                        ServiceArea serviceArea = new ServiceArea();
                        serviceArea.setId(id);
                        serviceAreaList.add(serviceArea);
                    }
                    obj.setServiceAreaNameList(serviceAreaList);
                }

                sharedStaffData(obj, CommonConstants.OPERATION_ADD);
            } else {
                if (pojo.getPassword() != null) {
                    // Delegate password validation and encoding to the service
                    validateAndEncodePasswordStaff(pojo);
                }
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                /** Staff Location Mapping Save for IWF */
                saveUpdateStaffLocation(pojo, obj.getId(), CommonConstants.OPERATION_ADD);
                /** Save Staff Accessible Role Mapping*/
                saveStaffAccessibleRoleMapping(pojo.getAssignableRoleIds(), obj.getId());
                pojo = convertStaffUserModelToStaffUserPojo(obj);
                sharedStaffData(obj, CommonConstants.OPERATION_ADD);
            }

            /** Send Notification for Generate Password*/
            /**Set dynamic parameters in Map for manual mail content*/
            if (pojo.getEventName() != null) {
                Map<String, Object> obj = new ConcurrentHashMap<>();
                String baseUrl = constructBaseURL(pojo, request);
                obj.put("genPassUrl", baseUrl);
                obj.put("username", pojo.getUsername());
                /** Send Producer record dto required for notification service*/
                GeneratePasswordDto generatePasswordDto = new GeneratePasswordDto();
                generatePasswordDto.setGenPassUrl(baseUrl);
                generatePasswordDto.setUsername(pojo.getUsername());
                generatePasswordDto.setEventName(pojo.getEventName());
                generatePasswordDto.setEventId(pojo.getEventId());
                generatePasswordDto.setManualMailContent(obj);
                generatePasswordDto.setApplicationName(applicationName);
                generatePasswordDto.setEmail(pojo.getEmail());
                ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_NOTIFICATION_TOPIC, generatePasswordDto);
                kafkaProducer.send(record);
            }
            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void validateAndEncodePasswordStaff(StaffUserPojo pojo) throws CustomValidationException {
        // Retrieve password policy ID for the MVNO associated with the staff user
        Long passwordPolicyId = mvnoRepository.findPasswordPolicyIdByMvnoId(Long.valueOf(pojo.getMvnoId()))
                .orElseThrow(() -> new CustomValidationException(HttpStatus.CONFLICT.value(), "No password policy found for the given MVNO ID", null));

        // Retrieve the password policy from the repository
        PasswordPolicy passwordPolicy = passwordRepository.findById(passwordPolicyId)
                .orElseThrow(() -> new CustomValidationException(HttpStatus.CONFLICT.value(), "No password policy found for the given ID", null));

        if (pojo.getPassword() != null) {
            // Validate minimum length
            if (pojo.getPassword().length() < passwordPolicy.getMin_length()) {
                throw new CustomValidationException(HttpStatus.CONFLICT.value(), "Password is too short. Minimum length is " + passwordPolicy.getMin_length(), null);
            }

            // Validate maximum length
            if (pojo.getPassword().length() > passwordPolicy.getMax_length()) {
                throw new CustomValidationException(HttpStatus.CONFLICT.value(), "Password is too long. Maximum length is " + passwordPolicy.getMax_length(), null);
            }

            // Validate pattern
            String passwordPattern = passwordPolicy.getPattern();
            if (passwordPattern != null && !passwordPattern.isEmpty() && !pojo.getPassword().matches(passwordPattern)) {
                throw new CustomValidationException(HttpStatus.CONFLICT.value(), passwordPolicy.getPattern_description(), null);
            }

            // Optionally, you can implement password reuse prevention here by checking recent passwords.

            // Encode the password using PasswordEncoder after all validations pass
            pojo.setPassword(pojo.getPassword());
        }
    }

    /**
     * Construct origin url for generate password based on unique Uuid for every staff .
     */
    public String constructBaseURL(StaffUserPojo pojo, HttpServletRequest request) {
        String origin = request.getHeader("origin");
        String url = origin + Constants.GENERATE_PASSWORD_URL + pojo.getUuid();
        return url;
    }

    public StaffUserPojo saveWithMvno(StaffUserPojo pojo, HttpServletRequest request) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
        try {
            if (pojo.getServiceAreaIdsList() != null) {
                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                sharedStaffData(obj, CommonConstants.OPERATION_ADD);
                pojo = convertStaffUserModelToStaffUserPojo(obj);
            } else {
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                sharedStaffData(obj, CommonConstants.OPERATION_ADD);
                pojo = convertStaffUserModelToStaffUserPojo(obj);
            }
            /** Send Notification for Generate Password*/
            /**Set dynamic parameters in Map for manual mail content*/
            if (pojo.getEventName() != null) {
                Map<String, Object> obj = new ConcurrentHashMap<>();
                String baseUrl = constructBaseURL(pojo, request);
                obj.put("genPassUrl", baseUrl);
                obj.put("username", pojo.getUsername());
                /** Send Producer record dto required for notification service*/
                GeneratePasswordDto generatePasswordDto = new GeneratePasswordDto();
                generatePasswordDto.setGenPassUrl(baseUrl);
                generatePasswordDto.setUsername(pojo.getUsername());
                generatePasswordDto.setEventName(pojo.getEventName());
                generatePasswordDto.setEventId(pojo.getEventId());
                generatePasswordDto.setManualMailContent(obj);
                generatePasswordDto.setApplicationName(applicationName);
                generatePasswordDto.setEmail(pojo.getEmail());
                ProducerRecord<String, Object> record = new ProducerRecord<>(KafkaConstant.KAFKA_NOTIFICATION_TOPIC, generatePasswordDto);
                kafkaProducer.send(record);
                log.info("Successfully sent record of generated password to Notification via Kafka with username: {} and event: {}",
                        pojo.getUsername(),
                        pojo.getEventName());
            }
            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public StaffUserPojo updateWithMvno(StaffUserPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [Update()] ";
        try {
            if (pojo.getServiceAreaIdsList() != null) {
                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                sharedStaffData(obj, CommonConstants.OPERATION_ADD);
                pojo = convertStaffUserModelToStaffUserPojo(obj);
            } else {
                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
                obj = saveStaffUser(obj);
                sharedStaffData(obj, CommonConstants.OPERATION_ADD);
                pojo = convertStaffUserModelToStaffUserPojo(obj);
            }
            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public StaffUserPojo update(StaffUserPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [update()] ";
        StaffUser old = get(pojo.getId());
        try {
            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
            if (pojo.getServiceAreaIdsList() != null)
                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
            if (pojo.getBusinessUnitIdsList() != null)
                pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
            changestatus(pojo.getStatus(), pojo.getUsername());
            StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
            obj.setMvnoId(get(obj.getId()).getMvnoId());
            obj = saveStaffUser(obj);
            /** Staff Location Mapping Save for IWF */
            saveUpdateStaffLocation(pojo, obj.getId(), CommonConstants.OPERATION_UPDATE);
            /** Update Staff Accessible Role Mapping*/
            updateStaffAccessibleRoleMapping(pojo.getAssignableRoleIds(), obj.getId());
            if(!pojo.getServiceAreaIdsList().isEmpty()){
                List<ServiceArea> serviceAreaList = new ArrayList<>();
                for (Long id : pojo.getServiceAreaIdsList()) {
                    ServiceArea serviceArea = new ServiceArea();
                    serviceArea.setId(id);
                    serviceAreaList.add(serviceArea);
                }
                obj.setServiceAreaNameList(serviceAreaList);
            }
            sharedStaffData(obj, CommonConstants.OPERATION_UPDATE);
            pojo = convertStaffUserModelToStaffUserPojo(obj);
            return pojo;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    //
    public StaffUser convertStaffUserPojoToStaffUserModel(StaffUserPojo staffUserPojo) throws Exception {
        String SUBMODULE = MODULE + " [convertStaffUserPojoToStaffUserModel()] ";
        StaffUser staffUser = null;
        try {
            if (staffUserPojo != null) {
                staffUser = new StaffUser();
                if (staffUserPojo.getId() != null) {
                    staffUser = get(staffUserPojo.getId());
                    staffUser.setId(staffUserPojo.getId());
                    staffUser.setPasswordDate(LocalDateTime.now());
                }
                staffUser.setUsername(staffUserPojo.getUsername());
//                if (null == staffUserPojo.getId()) {
//                    staffUser.setPassword(staffUserPojo.getPassword());
//                }
                if (staffUserPojo.getPassword() != null && !staffUserPojo.getPassword().isEmpty()) {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    staffUser.setPassword(encoder.encode(staffUserPojo.getPassword()));
                }
                if (null != staffUserPojo.getTacacsAccessLevelGroup()) {
                    staffUser.setTacacsAccessLevelGroup(staffUserPojo.getTacacsAccessLevelGroup());
                }
                staffUser.setLcoId(staffUserPojo.getLcoId());
                staffUser.setUuid(staffUserPojo.getUuid());
//                staffUser.setEventName(staffUserPojo.getEventName());
                staffUser.setIsPasswordExpired(staffUserPojo.getIsPasswordExpired());
                staffUser.setPasswordDate(staffUserPojo.getPasswordDate());
                staffUser.setEmail(staffUserPojo.getEmail());
                staffUser.setFirstname(staffUserPojo.getFirstname());
                staffUser.setLastname(staffUserPojo.getLastname());
                staffUser.setStatus(staffUserPojo.getStatus());
                staffUser.setPhone(staffUserPojo.getPhone());
                staffUser.setCountryCode(staffUserPojo.getCountryCode());
                staffUser.setFailcount(staffUserPojo.getFailcount());
                staffUser.setCreatedate(staffUserPojo.getCreatedate());
                staffUser.setUpdatedate(staffUserPojo.getUpdatedate());
                staffUser.setLast_login_time(staffUserPojo.getLast_login_time());
                staffUser.setPartnerid(staffUserPojo.getPartnerid());
                staffUser.setFullName(staffUserPojo.getFirstname() + " " + staffUserPojo.getLastname());
                staffUser.setSysstaff(staffUserPojo.getSysstaff());
                staffUser.setServiceAreaNameList(staffUserPojo.getServiceAreaNameList());
                staffUser.setBusinessUnitNameList(staffUserPojo.getBusinessUnitNameList());
                staffUser.setStaffUserServiceMappings(staffUserPojo.getStaffUserServiceMappingList());
//                if (staffUserPojo.getMvnoId() != null) {
                staffUser.setMvnoId(staffUserPojo.getMvnoId());
//                }

                if (staffUserPojo.getMvnoId() != null) {
//                    Mvno mvno = mvnoRepository.findById(Long.valueOf(staffUserPojo.getMvnoId()))
//                            .orElseThrow(() -> new Exception("MVNO not found for mvnoId: " + staffUserPojo.getMvnoId()));
                    Mvno mvno = mvnoRepository.findEventNameAndEventIdByMvnoId(Long.valueOf(staffUserPojo.getMvnoId()))
                            .orElseThrow(() -> new Exception("MVNO not found for mvnoId: " + staffUserPojo.getMvnoId()));
                    if (staffUserPojo.getPassword() == null) {
                        staffUser.setEventName(mvno.getEventName());
                        staffUser.setEventId(mvno.getEventId());
                    }
                }
                if (staffUserPojo.getServiceAreaId() != null) {
                    ServiceArea serviceArea = serviceAreaService.getByID(staffUserPojo.getServiceAreaId());
                    staffUser.setServicearea(serviceArea);
                }

                if (staffUserPojo.getBusinessunitid() != null) {
                    BusinessUnit businessUnit = businessUnitService.getById(staffUserPojo.getBusinessunitid());
                    staffUser.setBusinessUnit(businessUnit);
                }

                if (staffUserPojo.getParentStaffId() != null) {
                    StaffUser staffUser2 = get(staffUserPojo.getParentStaffId());
                    staffUser.setStaffUserparent(staffUser2);
                }

                if (staffUserPojo.getRoleIds() != null && staffUserPojo.getRoleIds().size() > 0) {

                    List<RoleDTO> roleDTOList = roleService.getAllByIdIn(staffUserPojo.getRoleIds().stream().map(Long::longValue).collect(Collectors.toList()));

                    staffUser.getRoles().clear();
                    staffUser.getRoles().addAll(roleDTOList.stream().map(dto -> roleService.convertRolePojoToRoleModel(dto)).collect(Collectors.toSet()));
                }

                if (staffUserPojo.getTeamIds() != null && staffUserPojo.getTeamIds().size() > 0) {
                    staffUser.getTeam().clear();
                    Set<Teams> teamsSet = new HashSet<>();
                    for (Long item : staffUserPojo.getTeamIds()) {
                        Teams team = new Teams();
                        team.setId(item);
//                        Teams teamEntity = teamsRepository.findById(item).orElse(null);
                        teamsSet.add(team);
                    }
                    staffUser.setTeam(teamsSet);
                } else {
                    staffUser.getTeam().clear();
                }

                if (staffUserPojo.getBranchId() != null) {
                    staffUser.setBranchId(staffUserPojo.getBranchId());
                }

                staffUser.setHrmsId(staffUserPojo.getHrmsId());
                staffUser.setProfileImage(staffUserPojo.getProfileImage());

                if (staffUserPojo.getDepartment() != null) {
                    staffUser.setDepartment(staffUserPojo.getDepartment());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return staffUser;
    }

    //
    public StaffUserPojo convertStaffUserModelToStaffUserPojo(StaffUser staffUser) throws Exception {
        String SUBMODULE = MODULE + " [convertStaffUserModelToStaffUserPojo()] ";
        StaffUserPojo pojo = null;
        try {
            if (staffUser != null) {
                pojo = new StaffUserPojo();
                pojo.setId(staffUser.getId());
                pojo.setUsername(staffUser.getUsername());
//                if (null == pojo.getId()) {
//                    pojo.setPassword(staffUser.getPassword());
//                }
                if (staffUser.getPassword() != null) {
                    pojo.setPassword(staffUser.getPassword());
                }
                if (null != staffUser.getTacacsAccessLevelGroup())
                    pojo.setTacacsAccessLevelGroup(staffUser.getTacacsAccessLevelGroup());
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
                pojo.setLast_login_time(staffUser.getLast_login_time());
                pojo.setUuid(staffUser.getUuid());
//                pojo.setEventName(staffUser.getEventName());
                pojo.setPasswordDate(staffUser.getPasswordDate());
                pojo.setIsPasswordExpired(staffUser.getIsPasswordExpired());
                pojo.setPartnerid(staffUser.getPartnerid());
                pojo.setSysstaff(staffUser.getSysstaff());
                pojo.setFullName(staffUser.getFullName());
                pojo.setServicearea(staffUser.getServicearea());
                pojo.setBusinessUnit(staffUser.getBusinessUnit());

//                pojo.setBusinessUnitIdsList(staffUser.getBusinessUnitNameList().stream().map(BusinessUnit::getId).collect(Collectors.toList()));
                pojo.setStaffUserServiceMappingList(staffUser.getStaffUserServiceMappings());
                pojo.setMvnoId(staffUser.getMvnoId());

                if (staffUser.getMvnoId() != null) {
//                    Mvno mvno = mvnoRepository.findById(Long.valueOf(staffUser.getMvnoId()))
//                            .orElseThrow(() -> new Exception("MVNO not found for mvnoId: " + staffUser.getMvnoId()));
                    Mvno mvno = mvnoRepository.findEventNameAndEventIdByMvnoId(Long.valueOf(staffUser.getMvnoId()))
                            .orElseThrow(() -> new Exception("MVNO not found for mvnoId: " + staffUser.getMvnoId()));
                    if (staffUser.getPassword() == null) {
                        pojo.setEventName(mvno.getEventName());
                        pojo.setEventId(mvno.getEventId());
                    }
                }
                System.out.println(staffUser.getServiceAreaNameList().size());
//                if (staffUser.getServiceAreaNameList() != null && staffUser.getServiceAreaNameList().size() > 0) {
                pojo.setServiceAreaIdsList(staffUserServiceAreaMappingRepository.findServiceAreaByStaffId(staffUser.getId()).stream().map(i -> i.longValue()).collect(Collectors.toList()));
//                    if(Objects.nonNull(pojo.getServiceAreasId()) && pojo.getServiceAreasId().size()>0 ) {
                        if(Objects.nonNull(pojo.getServiceAreaIdsList()) && pojo.getServiceAreaIdsList().size()>0){
                            pojo.setServiceAreasNameList(serviceAreaRepository.findServiceAreaNameByServiceareaId(pojo.getServiceAreaIdsList()));
                        }
//                    }
//                    List<Integer> serviceAreaIds = new ArrayList<>();
//                    List<String> serviceArealist = new ArrayList<>();
//                    for (ServiceArea serviceArea : staffUser.getServiceAreaNameList()) {
//                        serviceAreaIds.add(serviceArea.getId().intValue());
//                        serviceArealist.add(serviceArea.getName());
//                    }
//                    pojo.setServiceAreasId(serviceAreaIds);
//                    pojo.setServiceAreasNameList(serviceArealist);

//                }

             pojo.setBusinessUnitIdsList(staffUserBusinessUnitMappingRepository.findBuidByStaffId(staffUser.getId()).stream().collect(Collectors.toList()));
              if(Objects.nonNull(pojo.getBusinessUnitIdsList()) &&pojo.getBusinessUnitIdsList().size()>0) {
                  pojo.setBusinessUnitNameList(businessUnitRepository.findAllByIdIn(pojo.getBusinessUnitIdsList()));
              }
//                if (staffUser.getBusinessUnitNameList() != null && staffUser.getBusinessUnitNameList().size() > 0) {
//                    pojo.setBusinessunitids(staffUser.getBusinessUnitNameList().stream()
//                            .map(businessUnit -> businessUnit.getId().intValue()).collect(Collectors.toList()));
//                    pojo.setBusinessUnitNamesList(businessUnitRepository.findAllByBusinessUnitnameById(pojo.getBusinessUnitIdsList()));
//                    List<Integer> bussinessUnitIds = new ArrayList<>();
//                    List<String> bussinessUnitNameList = new ArrayList<>();
//                    for (BusinessUnit businessUnit : staffUser.getBusinessUnitNameList()) {
//                        bussinessUnitIds.add(businessUnit.getId().intValue());
//                        bussinessUnitNameList.add(businessUnit.getBuname());
//                    }
//                    pojo.setBusinessunitids(bussinessUnitIds);
//                    pojo.setBusinessUnitNamesList(bussinessUnitNameList);

//                }

                if (staffUser.getStaffUserparent() != null)
                    pojo.setParentStaffId(staffUser.getStaffUserparent().getId());
                pojo.setRoleIds(staffRoleRelRepo.findRoleIdByStaffId(Long.valueOf(staffUser.getId())));
                if (Objects.nonNull(pojo.getRoleIds()) && pojo.getRoleIds().size() > 0) {
                    pojo.setRoleName(Arrays.asList(roleRepository.findRolenameByRoleId(pojo.getRoleIds().get(0))));
                }
//                if (staffUser.getRoles() != null && staffUser.getRoles().size() > 0) {
//                    pojo.setRoleIds(staffUser.getRoles().stream()
//                            .map(Role::getId).collect(Collectors.toList()));
//                    pojo.setRoleName(staffUser.getRoles().stream()
//                            .map(Role::getRolename).collect(Collectors.toList()));
////                    List<Long> roleIds = new ArrayList<>();
////                    List<String> roleNameList = new ArrayList<>();
////                    for (Role role : staffUser.getRoles()) {
////                        roleIds.add(role.getId());
////                        roleNameList.add(role.getRolename());
////                    }
////                    pojo.setRoleIds(roleIds);
////                    pojo.setRoleName(roleNameList);
//                }
                if (staffUser.getId() != null) {
                    List<Long> assignableRoleIds = staffAccessibleRoleMappingRepository.findAccessibleRolesByStaffId(staffUser.getId()).orElse(null);
                    pojo.setAssignableRoleIds(assignableRoleIds);
                }
                if (null != staffUser.getCreatedate()) {
                    pojo.setRegDate(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a").format(staffUser.getCreatedate()));
                }
                if (null != staffUser.getPartnerid()) {
//                    Partner partner = partnerRepository.findById(staffUser.getPartnerid()).orElse(null);
                    String partnerName = partnerRepository.findNameById(staffUser.getPartnerid());
                    if (null != partnerName) {
                        pojo.setPartnerName(partnerName);
                    } else {
                        pojo.setPartnerName("-");
                    }
                }
                Optional<StaffUser> user = staffUserRepository.findParentStaffById(staffUser.getId());
                if (user.isPresent()) {
                    if (user != null) {
                        pojo.setParentstaffname(user.get().getFirstname());
                    } else {
                        pojo.setParentstaffname("-");
                    }
                }

                LocalDateTime startdate = LocalDateTime.now();
                Set<Long> teamids = teamUserMappingsRepocitory.getTeamIds(Long.valueOf(staffUser.getId()));

                if (teamids.size() > 0) {
                    pojo.setTeamIds(teamids);
                    pojo.setTeamNameList(teamsRepository.getTeamNameListByTeamId(teamids));
//                    Set<Long> teamIds = new HashSet<>();
//                    List<String> teamNameList = new ArrayList<>();
//                    for (Teams role : staffUser.getTeam()) {
//                        teamIds.add(role.getId());
//                        teamNameList.add(role.getName());
//                    }
//                    pojo.setTeamIds(teamIds);
//                    pojo.setTeamNameList(teamNameList);
                }
                LocalDateTime endTime = LocalDateTime.now();
                Duration duration = Duration.between(startdate, endTime);
                System.out.println("total Time takken to get teamids nad name" + duration.toMillis());
                if (staffUser.getBranchId() != null) {
                    Branch branch = branchRepository.findById(Long.valueOf(staffUser.getBranchId())).orElse(null);
                    String branchName = branch.getName();
                    if (branchName != null) {
                        pojo.setBranchName(branchName);
                        pojo.setBranchId(Math.toIntExact(branch.getId()));
                    } else {
                        pojo.setBranchName("-");
                    }
                } else {
                    pojo.setBranchName("-");
                }

                pojo.setHrmsId(staffUser.getHrmsId());
                pojo.setProfileImage(staffUser.getProfileImage());
                pojo.setDisplayId(staffUser.getId());
                pojo.setDisplayName(staffUser.getFirstname());
                if (staffUser.getDepartment() != null) {
                    pojo.setDepartment(staffUser.getDepartment());
                }
                List<StaffUserLocationMapping> staffUserLocationMappings = staffLocationMappingRepo.findAllByStaffId(Long.valueOf(staffUser.getId()));
                if (!staffUserLocationMappings.isEmpty()) {
                    List<StaffUserLocationMappingDto> staffUserLocationMappingDtos = new ArrayList<>();
                    staffUserLocationMappings.stream().map(staffUserLocationMapping -> {
                        StaffUserLocationMappingDto staffUserLocationMappingDto = new StaffUserLocationMappingDto();
                        staffUserLocationMappingDto.setId(staffUserLocationMapping.getId());
                        staffUserLocationMappingDto.setStaffId(staffUserLocationMapping.getStaffId());
                        staffUserLocationMappingDto.setLocationId(staffUserLocationMapping.getLocationId());
                        staffUserLocationMappingDto.setLocationName(staffUserLocationMapping.getLocationName());
                        staffUserLocationMappingDtos.add(staffUserLocationMappingDto);
                        return staffUserLocationMappingDto;
                    });
                    pojo.setStaffUserLocationMappingDtos(staffUserLocationMappingDtos);
//                    staffUserLocationMappings.forEach(staffUserLocationMapping -> {
//                        StaffUserLocationMappingDto staffUserLocationMappingDto = new StaffUserLocationMappingDto();
//                        staffUserLocationMappingDto.setId(staffUserLocationMapping.getId());
//                        staffUserLocationMappingDto.setStaffId(staffUserLocationMapping.getStaffId());
//                        staffUserLocationMappingDto.setLocationId(staffUserLocationMapping.getLocationId());
//                        staffUserLocationMappingDto.setLocationName(staffUserLocationMapping.getLocationName());
//                        staffUserLocationMappingDtos.add(staffUserLocationMappingDto);
//                    });
//                    pojo.setStaffUserLocationMappingDtos(staffUserLocationMappingDtos);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public StaffUserPojo convertStaffUserToStaffUserPojo(StaffUserPojo staffUser) throws Exception {
        String SUBMODULE = MODULE + " [convertStaffUserModelToStaffUserPojo()] ";
        long methodStart = System.currentTimeMillis();
        ApplicationLogger.logger.info(SUBMODULE + "Method start time: " + methodStart);

        StaffUserPojo pojo = null;
        try {
            if (staffUser != null) {
                long mappingStart = System.currentTimeMillis();
                ApplicationLogger.logger.info(SUBMODULE + "Object mapping start time: " + mappingStart);
                pojo = new StaffUserPojo();
                pojo.setId(staffUser.getId());
                pojo.setUsername(staffUser.getUsername());
//                if (null == pojo.getId()) {
//                    pojo.setPassword(staffUser.getPassword());
//                }
                if (staffUser.getPassword() != null) {
                    pojo.setPassword(staffUser.getPassword());
                }
                if (null != staffUser.getTacacsAccessLevelGroup())
                    pojo.setTacacsAccessLevelGroup(staffUser.getTacacsAccessLevelGroup());
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
                pojo.setLast_login_time(staffUser.getLast_login_time());
                pojo.setUuid(staffUser.getUuid());
//                pojo.setEventName(staffUser.getEventName());
                pojo.setPasswordDate(staffUser.getPasswordDate());
                pojo.setIsPasswordExpired(staffUser.getIsPasswordExpired());
                pojo.setPartnerid(staffUser.getPartnerid());
                pojo.setSysstaff(staffUser.getSysstaff());
                pojo.setFullName(staffUser.getFullName());
                pojo.setServicearea(staffUser.getServicearea());
                pojo.setBusinessUnit(staffUser.getBusinessUnit());
//                pojo.setServiceAreaIdsList(staffUser.getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                pojo.setBusinessUnitIdsList(staffUser.getBusinessUnitNameList().stream().map(BusinessUnit::getId).collect(Collectors.toList()));
//                pojo.setStaffUserServiceMappingList(staffUser.getStaffUserServiceMappings());
                pojo.setMvnoId(staffUser.getMvnoId());

                // Fetch the list of business unit IDs associated with the given staff user ID
                List<Long> businessUnitIds = staffUserBusinessUnitMappingRepository.findBusinessUnitIdsByStaffId(staffUser.getId());

                // Check if the list is not null and contains IDs
                if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                    // Retrieve all BusinessUnit entities corresponding to the IDs
                    List<BusinessUnit> businessUnits = businessUnitRepository.findAllById(businessUnitIds);

                    // Set the entire list of BusinessUnit objects to the POJO (assumed for further use)
                    pojo.setBusinessUnitNameList(businessUnits);

                    // Create two new lists to hold business unit IDs as Integers and business unit names as Strings
                    List<Integer> buIdsAsInteger = new ArrayList<>();
                    List<String> buNames = new ArrayList<>();

                    // Loop over each BusinessUnit object
                    for (BusinessUnit bu : businessUnits) {
                        // Convert Long ID to Integer and add to the integer list
                        buIdsAsInteger.add(bu.getId().intValue());
                        // Add the business unit name to the names list
                        buNames.add(bu.getBuname());
                    }

                    // Set the converted business unit IDs as Integer list in the POJO
                    pojo.setBusinessunitids(buIdsAsInteger);
                    // Set the original Long business unit IDs list in the POJO
                    pojo.setBusinessUnitIdsList(businessUnitIds);
                    // Set the list of business unit names in the POJO
                    pojo.setBusinessUnitNamesList(buNames);
                } else {
                    // If no business unit IDs are found, set empty lists in the POJO to avoid null pointer issues
                    pojo.setBusinessunitids(new ArrayList<>());
                    pojo.setBusinessUnitIdsList(new ArrayList<>());
                    pojo.setBusinessUnitNameList(new ArrayList<>());
                    pojo.setBusinessUnitNamesList(new ArrayList<>());
                }

                if (staffUser.getMvnoId() != null && staffUser.getPassword() == null) {
                    long mvnoStart = System.currentTimeMillis();

                    Mvno mvno = mvnoRepository.findEventNameAndEventIdByMvnoId(Long.valueOf(staffUser.getMvnoId()))
                            .orElseThrow(() -> new Exception("MVNO not found for mvnoId: " + staffUser.getMvnoId()));

                    pojo.setEventName(mvno.getEventName());
                    pojo.setEventId(mvno.getEventId());

                    long mvnoEnd = System.currentTimeMillis();
                    ApplicationLogger.logger.info(SUBMODULE + "MVNO fetch time: " + (mvnoEnd - mvnoStart) + "ms");
                }

                long serviceAreaStart = System.currentTimeMillis();

                List<Integer> serviceAreaIdsInt = staffUserServiceAreaMappingRepository.findServiceAreaByStaffId(staffUser.getId());

                if (serviceAreaIdsInt != null && !serviceAreaIdsInt.isEmpty()) {
                    List<Long> serviceAreaIdsLong = serviceAreaIdsInt.stream()
                            .map(Integer::longValue)
                            .collect(Collectors.toList());

                    List<ServiceArea> serviceAreas = serviceAreaRepository.findServiceAreaIdAndNameByIdsIn(serviceAreaIdsLong);

                    if (serviceAreas != null && !serviceAreas.isEmpty()) {
                        List<Long> serviceAreaIds = serviceAreas.stream()
                                .map(ServiceArea::getId)
                                .collect(Collectors.toList());

                        List<String> serviceAreaNames = serviceAreas.stream()
                                .map(ServiceArea::getName)
                                .collect(Collectors.toList());

                        pojo.setServiceAreaIdsList(serviceAreaIds);
                        pojo.setServiceAreasNameList(serviceAreaNames);
                    }
                }

                ApplicationLogger.logger.info(SUBMODULE + "Service area mapping time: " + (System.currentTimeMillis() - serviceAreaStart) + "ms");

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

                if (staffUser.getParentStaffId() != null)
                    pojo.setParentStaffId(staffUser.getParentStaffId());

                long roleStart = System.currentTimeMillis();

                List<Long> roleIdList = staffRoleRelRepo.findRoleIdByStaffId(Long.valueOf(staffUser.getId()));
                if (roleIdList != null && !roleIdList.isEmpty()) {
                    List<Role> roles = roleRepository.findRoleIdAndNameByrolrids(roleIdList);
                    if (roles != null && !roles.isEmpty()) {
                        pojo.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
                        pojo.setRoleName(roles.stream().map(Role::getRolename).collect(Collectors.toList()));
                    }
                }

                ApplicationLogger.logger.info(SUBMODULE + "Role fetch time: " + (System.currentTimeMillis() - roleStart) + "ms");

                Set<Long> teamids = teamUserMappingsRepocitory.getTeamIds(Long.valueOf(staffUser.getId()));
                if (teamids.size() > 0) {
                    pojo.setTeamIds(teamids);
                    pojo.setTeamNameList(teamsRepository.getTeamNameListByTeamId(teamids));
                }
                if (null != staffUser.getCreatedate()) {
                    pojo.setRegDate(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a").format(staffUser.getCreatedate()));
                }
                if (null != staffUser.getPartnerid()) {
                    String partner = partnerRepository.findNameById(staffUser.getPartnerid());
                    if (null != partner) {
                        pojo.setPartnerName(null != partner ? partner : "-");
                    } else {
                        pojo.setPartnerName("-");
                    }
                }

                Optional<StaffUser> user = staffUserRepository.findParentStaffById(staffUser.getId());
                if (user.isPresent()) {
                    if (user != null) {
                        pojo.setParentstaffname(user.get().getFirstname());
                    } else {
                        pojo.setParentstaffname("-");
                    }
                }
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
                if (staffUser.getBranchId() != null) {
                    BranchNameProjection branch = branchRepository.findBranchNameById(Long.valueOf(staffUser.getBranchId()));
                    if (branch != null) {
                        pojo.setBranchName(branch.getName());
                    } else {
                        pojo.setBranchName("-");
                    }
                } else {
                    pojo.setBranchName("-");
                }

                pojo.setHrmsId(staffUser.getHrmsId());
                pojo.setProfileImage(staffUser.getProfileImage());
                pojo.setDisplayId(staffUser.getId());
                pojo.setDisplayName(staffUser.getFirstname());
                if (staffUser.getDepartment() != null) {
                    pojo.setDepartment(staffUser.getDepartment());
                }
                List<StaffUserLocationMapping> staffUserLocationMappings = staffLocationMappingRepo.findAllByStaffId(Long.valueOf(staffUser.getId()));
                if (!staffUserLocationMappings.isEmpty()) {
                    List<StaffUserLocationMappingDto> staffUserLocationMappingDtos = new ArrayList<>();
                    staffUserLocationMappings.forEach(staffUserLocationMapping -> {
                        StaffUserLocationMappingDto staffUserLocationMappingDto = new StaffUserLocationMappingDto();
                        staffUserLocationMappingDto.setId(staffUserLocationMapping.getId());
                        staffUserLocationMappingDto.setStaffId(staffUserLocationMapping.getStaffId());
                        staffUserLocationMappingDto.setLocationId(staffUserLocationMapping.getLocationId());
                        staffUserLocationMappingDto.setLocationName(staffUserLocationMapping.getLocationName());
                        staffUserLocationMappingDtos.add(staffUserLocationMappingDto);
                    });
                    pojo.setStaffUserLocationMappingDtos(staffUserLocationMappingDtos);
                }
                ApplicationLogger.logger.info(SUBMODULE + "Object mapping end time: " + System.currentTimeMillis() +
                        ", duration: " + (System.currentTimeMillis() - mappingStart) + "ms");
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        long methodEnd = System.currentTimeMillis();
        ApplicationLogger.logger.info(SUBMODULE + "Method end time: " + methodEnd +
                ", total duration: " + (methodEnd - methodStart) + "ms");
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

    public List<StaffUserPojo> convertResponseModel(List<StaffUserPojo> staffUserList) throws Exception {
        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
        List<StaffUserPojo> pojoListRes = new ArrayList<>();
        try {
            if (staffUserList != null && staffUserList.size() > 0) {
                for (StaffUserPojo staffUser : staffUserList) {
                    pojoListRes.add(convertStaffUserToStaffUserPojo(staffUser));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    //
    public void validateRequest(StaffUserPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
//            if (pojo.getPassword() == null) {
//                throw new CustomValidationException(APIConstants.FAIL, "Please Enter Password", null);
//            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.TERMINATED))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && !operation.equals(CommonConstants.OPERATION_DELETE)) {
            if (pojo.getRoleIds() == null || pojo.getRoleIds().size() == 0) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.staffuser.role.required"), null);
            }
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
        if (pojo != null && nameValidation(pojo) && !(operation.equals(CommonConstants.OPERATION_UPDATE)) && !(operation.equals(CommonConstants.OPERATION_DELETE))) {
            throw new CustomValidationException(APIConstants.FAIL, "Username is already in use.", null);
        }
    }

    //
    public boolean nameValidation(StaffUserPojo pojo) {
        List<StaffUser> staffUserList = getStaffUserFromUsername(pojo.getUsername());
        boolean result = false;
        if (staffUserList != null && staffUserList.size() > 0) {

            for (StaffUser user : staffUserList) {
                if (user.getIsDelete().equals(0)) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    //
    public StaffUser changePassword(UserPasswordChangePojo pojo) throws AlreadyExistException, CustomMessageException {
        String SUBMODULE = MODULE + "[changePassword()]";
        List<StaffUser> staffUserList = this.getStaffUserFromUsername(pojo.getUserName());
        if (staffUserList != null && staffUserList.size() > 0) {
            StaffUser staffUser = staffUserList.get(0);
            if (staffUser != null) {
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                if (Objects.nonNull(pojo.getOldPassword())) {
                    // Check if the old password matches
                    if (!encoder.matches(pojo.getOldPassword(), staffUser.getPassword())) {
                        // Throw exception if the old password doesn't match
                        throw new CustomMessageException("Old password does not match");
                    }
                }
                // Fetch the password policy for the user's mvnoId
                Long passwordPolicyId = mvnoRepository.findPasswordPolicyIdByMvnoId(Long.valueOf(staffUser.getMvnoId()))
                        .orElseThrow(() -> new CustomMessageException("Password policy not found for mvnoId: " + staffUser.getMvnoId()));

                // Fetch the PasswordPolicy from the repository
                PasswordPolicy passwordPolicy = passwordRepository.findById(passwordPolicyId)
                        .orElseThrow(() -> new CustomMessageException("Password policy not found for id: " + passwordPolicyId));

                // Password length validation
                if (pojo.getNewPassword().length() < passwordPolicy.getMin_length()) {
                    throw new CustomMessageException("Password is too short. Minimum length is " + passwordPolicy.getMin_length());
                }

                if (pojo.getNewPassword().length() > passwordPolicy.getMax_length()) {
                    throw new CustomMessageException("Password is too long. Maximum length is " + passwordPolicy.getMax_length());
                }

                // Pattern validation (if applicable)
                String passwordPattern = passwordPolicy.getPattern();
                if (passwordPattern != null && !passwordPattern.isEmpty()) {
                    StringBuilder missingRequirements = new StringBuilder("Password must include:");

                    boolean isValid = true;

                    if (passwordPattern.contains("(?=.*[A-Z])") && !pojo.getNewPassword().matches(".*[A-Z].*")) {
                        missingRequirements.append(" an uppercase letter,");
                        isValid = false;
                    }
                    if (passwordPattern.contains("(?=.*[a-z])") && !pojo.getNewPassword().matches(".*[a-z].*")) {
                        missingRequirements.append(" a lowercase letter,");
                        isValid = false;
                    }
                    if (passwordPattern.contains("(?=.*[0-9])") && !pojo.getNewPassword().matches(".*[0-9].*")) {
                        missingRequirements.append(" a digit,");
                        isValid = false;
                    }
                    if (passwordPattern.contains("(?=.*\\W)") && !pojo.getNewPassword().matches(".*\\W.*")) {
                        missingRequirements.append(" a special character,");
                        isValid = false;
                    }
                    if (passwordPattern.contains("(?!.* )") && pojo.getNewPassword().matches(".*\\s.*")) {
                        missingRequirements.append(" no spaces,");
                        isValid = false;
                    }

                    if (!isValid) {
                        missingRequirements.setLength(missingRequirements.length() - 1);
                        missingRequirements.append(".");
                        throw new CustomMessageException(missingRequirements.toString());
                    }
                }

                // Check recent password history if recycling prevention is enabled
                if (passwordPolicy.getDisable_recycling_prevention() != null && passwordPolicy.getDisable_recycling_prevention() > 0) {
                    int recentPasswordCount = passwordPolicy.getDisable_recycling_prevention().intValue();

                    List<PasswordHistory> recentPasswords = passwordHistoryRepository.findByStaffIdOrderByPasswordAttemptNumberDesc(
                            staffUser.getId(), PageRequest.of(0, recentPasswordCount));

                    // Check if the new password matches any of the recent N passwords
                    for (PasswordHistory history : recentPasswords) {
                        if (encoder.matches(pojo.getNewPassword(), history.getPassword())) {
                            throw new AlreadyExistException("The password matches one of the last " + recentPasswordCount + " passwords");
                        }
                    }
                }

                // Encode and set the new password
                String encodedNewPassword = encoder.encode(pojo.getNewPassword());
                staffUser.setNewpassword(encodedNewPassword);
                staffUser.setPassword(encodedNewPassword);
                staffUser.setPasswordDate(LocalDateTime.now());
                staffUser.setIsPasswordExpired(false);
                // Save the updated staff user with the new password
                entityRepository.save(staffUser);

                // Save the new password in the PasswordHistory table
                savePasswordHistory(staffUser, encodedNewPassword);

                return staffUser;
            }
        } else {
            // Throw exception if the user is not found
            throw new CustomValidationException(APIConstants.FAIL, "Staff User not found", null);
        }
        return null;
    }

    private void savePasswordHistory(StaffUser staffUser, String encodedNewPassword) {
        // Fetch the latest password attempt number for the staff user
        Long maxAttemptNumber = passwordHistoryRepository.findMaxPasswordAttemptNumberByStaffId(staffUser.getId());

        // Auto-increment the password attempt number
        Long newAttemptNumber = (maxAttemptNumber != null) ? maxAttemptNumber + 1 : 1L;

        // Create a new PasswordHistory entity
        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setStaffId(staffUser.getId());
        passwordHistory.setPasswordAttemptNumber(newAttemptNumber);
        passwordHistory.setPassword(encodedNewPassword);
        passwordHistory.setUuid(staffUser.getUuid());

        // Save the password history in the repository
        passwordHistoryRepository.save(passwordHistory);
    }

    //
    public List<StaffUserPojo> findStaffUserByRoleId(Long roleId) {
        String SUBMODULE = MODULE + " [findStaffUserByRoleId()] ";
        try {
            List<StaffUser> staffUserList = new ArrayList<>();
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                staffUserList = entityRepository.findStaffByRole(roleId);
            } else {
                staffUserList = entityRepository.findStaffByRoleAndPartnerid(roleId, getLoggedInUserPartnerId());
            }
            if (null != staffUserList && 0 < staffUserList.size()) {
                List<StaffUserPojo> staffUserPojos = staffUserList.stream().map(data -> staffUserMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                return staffUserPojos.stream().filter(staff -> (getMvnoIdFromCurrentStaff().intValue() == 1 || (staff.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || staff.getMvnoId().intValue() == 1))).collect(Collectors.toList());
//                return staffUserPojos.stream().filter(staff -> (staff.getMvnoId() == 1 || staff.getMvnoId() == getMvnoIdFromCurrentStaff())).collect(Collectors.toList());

            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return new ArrayList<>();
    }

    //
    public List<StaffUserPojo> searchStaff(String searchText) throws Exception {
        String SUBMODULE = MODULE + " [searchCustomersCustom()] ";
        try {
            QStaffUser staffUser = QStaffUser.staffUser;
            BooleanExpression builder = staffUser.isNotNull();
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                builder = builder.andAnyOf(staffUser.firstname.startsWithIgnoreCase(searchText), staffUser.lastname.startsWithIgnoreCase(searchText), staffUser.phone.startsWith(searchText), staffUser.email.startsWith(searchText), staffUser.username.startsWith(searchText)).and(staffUser.isDelete.isFalse()).and(staffUser.roles.any().id.in(CommonConstants.BACK_OFFICE_STAFF_ROLE_ID));
            }
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                builder = builder.andAnyOf(staffUser.firstname.startsWithIgnoreCase(searchText), staffUser.lastname.startsWithIgnoreCase(searchText), staffUser.phone.startsWith(searchText), staffUser.email.startsWith(searchText), staffUser.username.startsWith(searchText)).and(staffUser.partnerid.eq(getLoggedInUserPartnerId())).and(staffUser.isDelete.isFalse()).and(staffUser.roles.any().id.in(CommonConstants.BACK_OFFICE_STAFF_ROLE_ID));
            }

            if (getLoggedInUser().getLco()) builder = builder.and(staffUser.lcoId.eq(getLoggedInUser().getPartnerId()));
            else builder = builder.and(staffUser.lcoId.isNull());

            List<StaffUser> staffUserList = (List<StaffUser>) entityRepository.findAll((Pageable) builder);
            return convertResponseModelIntoPojo(staffUserList);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }
//
//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Staff-User");
//        List<StaffUserPojo> staffUserPojos = (entityRepository.findAll().stream().map(data -> staffUserMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//        createExcel(workbook, sheet, StaffUserPojo.class, staffUserPojos, getFields());
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        List<StaffUserPojo> staffUserPojos = entityRepository.findAll().stream().map(data -> staffUserMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        createPDF(doc, StaffUserPojo.class, staffUserPojos, getFields());
//    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{StaffUserPojo.class.getDeclaredField("id"), StaffUserPojo.class.getDeclaredField("username"), StaffUserPojo.class.getDeclaredField("fullName"), StaffUserPojo.class.getDeclaredField("email"), StaffUserPojo.class.getDeclaredField("roleName"), StaffUserPojo.class.getDeclaredField("phone"), StaffUserPojo.class.getDeclaredField("regDate"), StaffUserPojo.class.getDeclaredField("status"), StaffUserPojo.class.getDeclaredField("partnerName")};
    }
    //

    //TODO This Api modify due-to performance issues Remove All In-Memory filters,Add Saprete Query's
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String product, Long staffId) {
        String SUBMODULE = MODULE + " [search()] ";
        Sort.Direction direction = (sortOrder != null && sortOrder == 1) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageRequest = PageRequest.of(Math.max(0, page), pageSize, direction, sortBy != null ? sortBy : "id");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn() != null && searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                    String keyword = searchModel.getFilterValue();
                    List<Long> buIds = getBUIdsFromCurrentStaff();
                    Boolean isLco = getLoggedInUser().getLco();
                    List<Integer> mvnoIds = Collections.singletonList(getMvnoIdFromCurrentStaff());
                    List<StaffUserSearchDTO> staffUserList;
                    if ("iwf".equalsIgnoreCase(product)) {
                        List<StaffUserLocationMapping> staffUserLocationMappings = staffLocationMappingRepo.findAllByStaffId(staffId);
                        if (!staffUserLocationMappings.isEmpty()) {
                            List<Long> locations = staffUserLocationMappings.stream()
                                    .map(StaffUserLocationMapping::getLocationId)
                                    .collect(Collectors.toList());

                            List<Integer> allowedStaffIds = staffLocationMappingRepo.findAllByLocationIdIn(locations).stream()
                                    .map(StaffUserLocationMapping::getStaffId)
                                    .map(Long::intValue)
                                    .collect(Collectors.toList());

                            staffUserList = fetchFilteredStaffUserList(keyword, mvnoIds, buIds, isLco, allowedStaffIds,filterList);
                        } else {
                            staffUserList = Collections.emptyList();
                        }
                    } else {
                        staffUserList = fetchFilteredStaffUserList(keyword, mvnoIds, buIds, isLco, null,filterList);
                    }
                    int total = staffUserList.size();
                    int start = (int) pageRequest.getOffset();
                    int end = Math.min(start + pageRequest.getPageSize(), total);

                    List<StaffUserSearchDTO> pagedList = (start >= total)
                            ? new ArrayList<>()
                            : staffUserList.subList(start, end);

                    Page<StaffUserSearchDTO> staffUserPage = new PageImpl<>(pagedList, pageRequest, total);
                    if (!pagedList.isEmpty()) {
                        makeGenericResponseForDTO(genericDataDTO, staffUserPage);
                    }

                    return genericDataDTO;
                }
            }

            throw new RuntimeException("Please Provide Search Column!");

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }

        return genericDataDTO;
    }

    public GenericDataDTO searchStaffUserByServiceAreaWithPagination(List<GenericSearchModel> filterList, int page, int pageSize, String sortBy, Integer sortOrder, String product, Long staffId) {
        String SUBMODULE = MODULE + " [search()] ";
        Sort.Direction direction = (sortOrder != null && sortOrder == 1) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageRequest = PageRequest.of(Math.max(0, page), pageSize, direction, sortBy != null ? sortBy : "id");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn() != null && searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                    String keyword = searchModel.getFilterValue();
                    List<Long> buIds = getBUIdsFromCurrentStaff();
                    Boolean isLco = getLoggedInUser().getLco();
                    List<Integer> mvnoIds = Collections.singletonList(getMvnoIdFromCurrentStaff());
                    List<StaffUserSearchDTO> staffUserList;
                    if ("iwf".equalsIgnoreCase(product)) {
                        List<StaffUserLocationMapping> staffUserLocationMappings = staffLocationMappingRepo.findAllByStaffId(staffId);
                        if (!staffUserLocationMappings.isEmpty()) {
                            List<Long> locations = staffUserLocationMappings.stream()
                                    .map(StaffUserLocationMapping::getLocationId)
                                    .collect(Collectors.toList());

                            List<Integer> allowedStaffIds = staffLocationMappingRepo.findAllByLocationIdIn(locations).stream()
                                    .map(StaffUserLocationMapping::getStaffId)
                                    .map(Long::intValue)
                                    .collect(Collectors.toList());

                            staffUserList = fetchFilteredStaffUserList(keyword, mvnoIds, buIds, isLco, allowedStaffIds,filterList);
                        } else {
                            staffUserList = Collections.emptyList();
                        }
                    } else {
                        staffUserList = fetchFilteredStaffUserList(keyword, mvnoIds, buIds, isLco, null,filterList);
                    }
                    int total = staffUserList.size();
                    int start = (int) pageRequest.getOffset();
                    int end = Math.min(start + pageRequest.getPageSize(), total);

                    List<StaffUserSearchDTO> pagedList = (start >= total)
                            ? new ArrayList<>()
                            : staffUserList.subList(start, end);

                    Page<StaffUserSearchDTO> staffUserPage = new PageImpl<>(pagedList, pageRequest, total);
                    if (!pagedList.isEmpty()) {
                        makeGenericResponseForDTO(genericDataDTO, staffUserPage);
                    }

                    return genericDataDTO;
                }
            }

            throw new RuntimeException("Please Provide Search Column!");

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }

        return genericDataDTO;


    }

    private List<StaffUserSearchDTO> fetchFilteredStaffUserList(String keyword, List<Integer> mvnoIds, List<Long> buIds, Boolean isLco, List<Integer> allowedStaffIds,List<GenericSearchModel> filters) {
        List<StaffUserSearchDTO> results;

        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            results = staffUserRepository.searchStaffUserByKeyword(keyword, mvnoIds, null, null);
        } else {
            results = staffUserRepository.searchStaffUserByKeywordWithPartner(keyword, mvnoIds, null, null, getLoggedInUserPartnerId());
        }
        // If IWF filter is applied
        if (allowedStaffIds != null && !allowedStaffIds.isEmpty()) {
            results = results.stream()
                    .filter(dto -> allowedStaffIds.contains(dto.getId()))
                    .collect(Collectors.toList());
        }
        Optional<GenericSearchModel> statusFilter = filters.stream()
                .filter(f -> "status".equalsIgnoreCase(f.getFilterColumn()))
                .findFirst();

        if (statusFilter.isPresent()) {
            String targetStatus = statusFilter.get().getFilterValue().toLowerCase();
            results = results.stream()
                    .filter(dto -> dto.getStatus() != null && dto.getStatus().equalsIgnoreCase(targetStatus))
                    .collect(Collectors.toList());
        }
        Optional<GenericSearchModel> serviceAreaFilter = filters.stream()
                .filter(f -> "serviceArea".equalsIgnoreCase(f.getFilterColumn()))
                .findFirst();

        if (serviceAreaFilter.isPresent()) {
            List<Integer> serviceAreaIds = serviceAreaFilter.get().getFilterListValues().stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());;
            if (serviceAreaIds != null && !serviceAreaIds.isEmpty()) {
                results = results.stream()
                        .filter(dto -> staffUserServiceAreaMappingRepository
                                .existsByStaffIdAndServiceAreaIdIn(dto.getId(), serviceAreaIds))
                        .collect(Collectors.toList());
            }
        }


        return results;
    }

    public <DTO> GenericDataDTO makeGenericResponseForDTO(GenericDataDTO genericDataDTO, Page<DTO> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    //This Code Comment-out Due-to hing performance issues

//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String product, Long staffId) {
//        String SUBMODULE = MODULE + " [search()] ";
//        PageRequest pageRequest = generatePageRequest(page, pageSize, "username", sortOrder);
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (null != searchModel.getFilterColumn()) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        GenericDataDTO genericDataDTO = new GenericDataDTO();
//                        List<Integer> mvnoIds = new ArrayList<>();
//                        List<StaffUser> users = entityRepository.findAll();
//                        List<StaffRoleRel> userRoles = staffRoleRelRepo.findAll();
//                        List<Role> roles = roleRepository.findAll();
//                        List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings = staffUserBusinessUnitMappingRepository.findAllByBusinessunitIdIn(getBUIdsFromCurrentStaff());
//                        mvnoIds.add(getMvnoIdFromCurrentStaff());
//                        Page<StaffUser> staffUserPage = null;
//                        String s1 = searchModel.getFilterValue();
//                        if (product.equalsIgnoreCase("iwf")) {
//                            List<StaffUserLocationMapping> staffUserLocationMappings = staffLocationMappingRepo.findAllByStaffId(staffId);
//                            if (!staffUserLocationMappings.isEmpty()) {
//                                List<Long> locations = staffUserLocationMappings.stream()
//                                        .map(StaffUserLocationMapping::getLocationId)
//                                        .collect(Collectors.toList());
//                                List<Integer> staffIdLocations = staffLocationMappingRepo.findAllByLocationIdIn(locations).stream()
//                                        .map(StaffUserLocationMapping::getStaffId)
//                                        .map(Long::intValue)
//                                        .collect(Collectors.toList());
//                                List<StaffUser> newStaffUsers = entityRepository.findAllByIdIn(staffIdLocations).stream()
//                                        .filter(Objects::nonNull)
//                                        .collect(Collectors.toList());
//                                users.clear();
//                                users.addAll(newStaffUsers);
//                            }
//                        }
//                        List<StaffUser> staffUserList = users.stream()
//                                .filter(t -> (t.getFirstname().toLowerCase().contains(s1.toLowerCase()) ||
//                                        t.getLastname().toLowerCase().contains(s1.toLowerCase()) ||
//                                        (t.getEmail() != null && t.getEmail().toLowerCase().contains(s1.toLowerCase())) ||
//                                        t.getUsername().toLowerCase().contains(s1.toLowerCase()) ||
//                                        (userRoles.stream().anyMatch(t2 -> t2.getStaffId().equals(t.getId()) &&
//                                                roles.stream().anyMatch(t3 -> t3.getId().equals(t2.getRoleId()) &&
//                                                        t3.getRolename().toLowerCase().contains(s1.toLowerCase()))))))
//                                .collect(Collectors.toList());
//                        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                            return getStaffByNameOrUsernameOrEmailOrRoleName(genericDataDTO, searchModel.getFilterValue(), pageRequest, mvnoIds, staffUserList, staffUserBusinessUnitMappings, staffUserPage);
//                        } else {
//                            return getStaffByNameOrUsernameOrEmailOrRoleNameByPartner(genericDataDTO, searchModel.getFilterValue(), pageRequest, mvnoIds, staffUserList, staffUserBusinessUnitMappings, staffUserPage);
//                        }
//                    }
//                } else throw new RuntimeException("Please Provide Search Column!");
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//        return null;
//    }

    // These two methods Removed From use due to in-memory usage

    public GenericDataDTO getStaffByNameOrUsernameOrEmailOrRoleName(GenericDataDTO genericDataDTO, String s1, PageRequest pageRequest, List<Integer> mvnoIds, List<StaffUser> staffUserList, List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings, Page<StaffUser> staffUserPage) throws Exception {
        if (getLoggedInUser().getLco()) {
            if (getMvnoIdFromCurrentStaff() == 1) {
                staffUserList = staffUserList.stream()
                        .filter(t -> t.getIsDelete().equals(false))
                        .filter(t -> t.getLcoId() == getLoggedInUser().getPartnerId())
                        .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            } else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> t.getLcoId() == getLoggedInUser().getPartnerId())
                            .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
//                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getLoggedInUser().getPartnerId());
                } else {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> staffUserBusinessUnitMappings.stream().anyMatch(t5 -> t5.getStaffId().equals(t.getId()) &&
                                    t5.getBusinessunitId().equals(getBUIdsFromCurrentStaff())))
                            .filter(t -> t.getLcoId().equals(getLoggedInUser().getPartnerId()))
                            .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
                    //                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
                }
            }
        } else {
            if (getMvnoIdFromCurrentStaff() == 1) {
                staffUserList = staffUserList.stream()
                        .filter(t -> t.getIsDelete().equals(false))
                        .filter(t -> t.getLcoId() == null)
                        .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            } else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> t.getLcoId() == null)
                            .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
//                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds);
                } else {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> t.getLcoId() == null)
                            .filter(t -> staffUserBusinessUnitMappings.stream().anyMatch(t5 -> t5.getStaffId().equals(t.getId()) &&
                                    getBUIdsFromCurrentStaff().contains(t5.getBusinessunitId())))
                            .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
//                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getBUIdsFromCurrentStaff());
                }
            }
        }
        staffUserPage = new PageImpl<>(staffUserList, pageRequest, staffUserList.size());
        List<StaffUserPojo> staffUserPojoList = convertResponseModelIntoPojo(staffUserList);
        if (0 < staffUserPage.getSize()) {
            makeGenericResponseForStaffPojo(genericDataDTO, new PageImpl<>(staffUserPojoList, pageRequest, staffUserPojoList.size()));
        }
        return genericDataDTO;
    }

    public GenericDataDTO getStaffByNameOrUsernameOrEmailOrRoleNameByPartner(GenericDataDTO genericDataDTO, String s1, PageRequest pageRequest, List<Integer> mvnoIds, List<StaffUser> staffUserList, List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings, Page<StaffUser> staffUserPage) {
        if (getLoggedInUser().getLco()) {
            if (getMvnoIdFromCurrentStaff() == 1) {
                staffUserList = staffUserList.stream()
                        .filter(t -> t.getIsDelete().equals(false))
                        .filter(t -> t.getLcoId() == getLoggedInUser().getPartnerId())
                        .filter(t -> t.getPartnerid() == getLoggedInUserPartnerId())
                        .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            } else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> t.getLcoId() == getLoggedInUser().getPartnerId())
                            .filter(t -> t.getPartnerid() == getLoggedInUserPartnerId())
                            .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getLoggedInUser().getPartnerId());
                } else {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> t.getLcoId() == getLoggedInUser().getPartnerId())
                            .filter(t -> t.getPartnerid() == getLoggedInUserPartnerId())
                            .filter(t -> staffUserBusinessUnitMappings.stream().anyMatch(t5 -> t5.getStaffId() == t.getId() && getBUIdsFromCurrentStaff().contains(t5.getBusinessunitId())))
                            .collect(Collectors.toList());
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
                }
            }
        } else {
            if (getMvnoIdFromCurrentStaff() == 1) {
                staffUserList = staffUserList.stream()
                        .filter(t -> t.getIsDelete().equals(false))
                        .filter(t -> t.getLcoId() == null)
                        .filter(t -> t.getPartnerid() == getLoggedInUserPartnerId())
                        .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                        .collect(Collectors.toList());
            } else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> t.getPartnerid() == getLoggedInUserPartnerId())
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> t.getLcoId() == null)
                            .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds);
                } else {
                    staffUserList = staffUserList.stream()
                            .filter(t -> t.getIsDelete().equals(false))
                            .filter(t -> t.getPartnerid() == getLoggedInUserPartnerId())
                            .filter(t -> mvnoIds.contains(t.getMvnoId()))
                            .filter(t -> staffUserBusinessUnitMappings.stream().anyMatch(t5 -> t5.getStaffId() == t.getId() && getBUIdsFromCurrentStaff().contains(t5.getBusinessunitId())))
                            .filter(t -> t.getLcoId() == null)
                            .sorted(Comparator.comparing(StaffUser::getCreatedate, Comparator.reverseOrder()))
                            .collect(Collectors.toList());
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getBUIdsFromCurrentStaff());
                }
            }
        }
        staffUserPage = new PageImpl<>(staffUserList, pageRequest, staffUserList.size());
        if (0 < staffUserPage.getSize()) {
            makeGenericResponse(genericDataDTO, staffUserPage);
        }
        return genericDataDTO;
    }

//    public List<AuditForResponseModel> getStaffListForAuditFor() {
//        String SUBMODULE = MODULE + " [getStaffListForAuditFor()] ";
//        List<AuditForResponseModel> responseList = new ArrayList<>();
//        try {
//            List<StaffUser> staffUserList = getAllActiveEntities();
//            if (null != staffUserList && 0 < staffUserList.size()) {
//                for (StaffUser customers : staffUserList) {
//                    AuditForResponseModel responseModel = new AuditForResponseModel();
//                    responseModel.setId(customers.getId());
//                    responseModel.setName(customers.getFullName());
//                    responseList.add(responseModel);
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return responseList;
//    }

    public List<StaffUser> getByServiceAreaId(Integer long1) {
        return entityRepository.getByServiceAreaId(long1);
    }

    //    public List<StaffUser> getAllStaffByServiceAreaId(Integer areaid) {
//        QStaffUser qStaffUser = QStaffUser.staffUser;
//        BooleanExpression booleanExpression = qStaffUser.isNotNull();
//        booleanExpression = booleanExpression.and(qStaffUser.isDelete.eq(false));
//        List<Integer> staffIdsall = new ArrayList<>();
//        List<Integer> ids = entityRepository.findAllByServiceareaId(areaid);
//        staffIdsall.addAll(ids);
//        // booleanExpression = booleanExpression.and(qStaffUser.id.in(ids));
//        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//            QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
//            List<Integer> buids = getBUIdsFromCurrentStaff().stream().map(aLong -> aLong.intValue()).collect(Collectors.toList());
//            BooleanExpression booleanExpression1 = qStaffUserBusinessUnitMapping.businessunitId.in(buids);
//            List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappingsList = (List<StaffUserBusinessUnitMapping>) staffUserBusinessUnitMappingRepository.findAll(booleanExpression1);
//            List<Integer> staffids = staffUserBusinessUnitMappingsList.stream().map(staffUserBusinessUnitMapping -> staffUserBusinessUnitMapping.getStaffId()).collect(Collectors.toList());
//            staffIdsall.addAll(staffids);
//            booleanExpression = booleanExpression.and(qStaffUser.id.in(staffIdsall));
//        }
//        booleanExpression = booleanExpression.and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff()));
//        List<StaffUser> staffUserList = (List<StaffUser>) entityRepository.findAll(booleanExpression);
//        List<StaffUser> staffUserList1 = new ArrayList<>();
//        for (StaffUser list : staffUserList) {
//            StaffUser staffUser = new StaffUser();
//            staffUser.setId(list.getId());
//            staffUser.setFullName(list.getFullName());
//            staffUser.setPhone(list.getPhone());
//            staffUserList1.add(staffUser);
//        }
//        return staffUserList1;
//        //return staffUserList.stream().map(staffUser -> staffUser.getFullName()).collect(Collectors.toList());
//    }
//
    public List<StaffUser> getByServiceAreaIdAndTeamId(Integer serviceAreaId, Long teamId) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;


        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false).and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser).where(qStaffUser.id.in(queryForStaffService.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.eq(serviceAreaId))).and(qStaffUser.id.in(queryForStaffTeam.select(qTeamUserMapping.staffId.intValue()).from(qTeamUserMapping).where(qTeamUserMapping.teamId.eq(teamId)))).and(booleanExpression)).fetch();
        return staffUserList;
    }

    //
    public List<StaffUser> getByTeamId(Long teamId) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;

        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false).and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser).where(qStaffUser.id.in(queryForStaffTeam.select(qTeamUserMapping.staffId.intValue()).from(qTeamUserMapping).where(qTeamUserMapping.teamId.eq(teamId))).and(booleanExpression)).fetch();
        return staffUserList;
    }


    public List<StaffUser> getByTeam(Long teamId) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
//        Teams teams = teamsService.getRepository().findById(teamId).get();
        //JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;


        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false)
                .and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser)
                .where(queryForStaffTeam.in())
                .fetch();
        return staffUserList;
    }

    public StaffUser resetPassword(@Valid PasswordDto passwordDto) {
        String SUBMODULE = MODULE + " [resetPassword()] ";
        try {
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
                throw new IllegalArgumentException("Please enter valid password. New password and confirm password value must be same.");
            } else if (passwordDto.getUserName() != null) {
                List<StaffUser> staffUserList = this.getStaffUserFromUsername(passwordDto.getUserName());
                if (staffUserList != null && staffUserList.size() > 0) {
                    StaffUser staffUser = staffUserList.get(0);
                    if (staffUser != null) {
                        PasswordEncoder encoder = new BCryptPasswordEncoder();
                        staffUser.setNewpassword(encoder.encode(passwordDto.getNewPassword()));
                        staffUser.setPassword(staffUser.getNewpassword());
                        entityRepository.save(staffUser);
                        return staffUser;
                    }
                } else {
                    throw new IllegalArgumentException("Please enter valid username. No record found for this one.");
                }
            }
        } catch (Throwable e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
        return null;
    }

    //
//    @Override
    public StaffUser get(Integer id) {
        StaffUser staffUser = entityRepository.findById(id).orElse(null);
        if (getMvnoIdFromCurrentStaff() == null) return staffUser;
        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (staffUser.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || staffUser.getMvnoId().intValue() == 1))
            return staffUser;
        return null;
    }

    //
    public StaffUser getStaffForUpdateAndDelete(Integer id) {
        StaffUser staffUser = get(id);
        if (staffUser == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == staffUser.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return staffUser;
    }

    public void changestatus(String status, String username) {
        String newStatus = status;
        StaffUser staffUser = entityRepository.findStaffUserByUsername(username);
        if (staffUser != null) {
            if (!staffUser.getStatus().equals(newStatus)) {
                //Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.STAFF_STATUS_CHANGE_TEMPLATE);
//                if (optionalTemplate.isPresent()) {
                // if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                StaffStatusChangeMessage statusMessage = null;
                if (staffUser.getBusinessUnitNameList().size() >= 1) {
                    statusMessage = new StaffStatusChangeMessage(RabbitMqConstants.STAFF_STATUS_CHANGE_TEMPLATE_HEADER, null, RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, staffUser, newStatus, staffUser.getBusinessUnitNameList().get(0).getId());
                } else {
                    statusMessage = new StaffStatusChangeMessage(RabbitMqConstants.STAFF_STATUS_CHANGE_TEMPLATE_HEADER, null, RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, staffUser, newStatus, null);
                }
                statusMessage.setEmailConfigured(true);
                statusMessage.setSmsConfigured(true);
                Gson gson = new Gson();
                gson.toJson(statusMessage);
                //messageSender.send(statusMessage, RabbitMqConstants.QUEUE_STAFF_SEND_STATUS);
                kafkaMessageSender.send(new KafkaMessageData(statusMessage, statusMessage.getClass().getSimpleName()));
                //}
                //}
            }
        }
    }
//
//
//    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<WorkflowAudit> paginationList) {
//        genericDataDTO.setDataList(paginationList.getContent());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
//        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
//        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
//        genericDataDTO.setTotalPages(paginationList.getTotalPages());
//        return genericDataDTO;
//    }

    //Get All StaffUserIds By ServiceAreas
//    public List<StaffUser> getStaffUserByServiceArea() {
//        try {
//            QStaffUser qStaffUser = QStaffUser.staffUser;
//            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//            JPAQuery<?> query = new JPAQuery<>(entityManager);
//            BooleanExpression aBoolean = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false));
//            if (getLoggedInUserId() != 1) {
//                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
//                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
////                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff()))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
//                aBoolean = aBoolean.and(qStaffUser.id.in(query.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.in(serviceAreaIds))).and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff())));
//            }
//            if (getMvnoIdFromCurrentStaff() != 1) {
//                return IterableUtils.toList(entityRepository.findAll(aBoolean));
//            } else {
//                return entityRepository.findAll();
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public List<StaffUserPojo> getStaffUserByServiceAreaId(Integer serviceAreaId) {
//        List<StaffUserPojo> staffUserPojos = new ArrayList<>();
//        try {
//            QStaffUser qStaffUser = QStaffUser.staffUser;
//            QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//            JPAQuery<?> query = new JPAQuery<>(entityManager);
//            BooleanExpression aBoolean = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false));
//            if (getLoggedInUserId() != 1) {
//                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);

    /// /                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
    /// /                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff()))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
//                aBoolean = aBoolean.and(qStaffUser.id.in(query.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.eq(serviceAreaId))).and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff())));
//            }
//            if (getMvnoIdFromCurrentStaff() != 1) {
//                staffUserPojos.addAll(((List<StaffUser>) entityRepository.findAll(aBoolean)).stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//            } else {
//                staffUserPojos.addAll(entityRepository.findAll().stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
//            throw ex;
//        }
//        return staffUserPojos;
//    }

//    public List<StaffUserViewPojo> viewStaffUserByServiceArea() {
//        List<StaffUser> staffUserList = getStaffUserByServiceArea();
//        List<StaffUserViewPojo> staffUserViewPojoList = new ArrayList<>();
//        for (StaffUser staffUser : staffUserList) {
//            staffUserViewPojoList.add(dtoToViewdto(staffUser));
//
//        }
//        return staffUserViewPojoList;
//
//    }
    public StaffUserViewPojo dtoToViewdto(StaffUser staffUser) {
        StaffUserViewPojo staffUserViewPojo = new StaffUserViewPojo();
        staffUserViewPojo.setId(staffUser.getId());
        staffUserViewPojo.setFirstname(staffUser.getFirstname());
        staffUserViewPojo.setLastname(staffUser.getLastname());
        staffUserViewPojo.setUsername(staffUser.getUsername());
        return staffUserViewPojo;

    }

    @Override
    public boolean duplicateVerifyAtSave(String username) {
        boolean flag = false;
        if (username != null) {
            username = username.trim();
            Integer count;
            count = entityRepository.duplicateVerifyAtSave(username);
//            if (getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(username);
//            else
//                count = entityRepository.duplicateVerifyAtSave(username, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    //
    public StaffUserPojo findByStaffId(Integer staffId) throws Exception {
        StaffUser staffUser = getRepository().findById(staffId).orElse(null);
        if (staffUser != null) {
            return convertStaffUserModelToStaffUserPojo(staffUser);
        }
        return null;
    }

    public List<StaffUser> getAllActiveEntitiesStaff() {
        return entityRepository.findByIsDeleteIsFalseOrderByIdDesc()
                .stream().filter(x -> x.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || x.getMvnoId() == null || x.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }

    public List<StaffUserAllPojo> convertResponseModelIntoStaffUserAllPojo(List<StaffUser> staffUserList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoStaffUserAllPojo()] ";
        List<StaffUserAllPojo> staffUserListRes = new ArrayList<>();
        try {
            if (staffUserList != null && staffUserList.size() > 0) {
                for (StaffUser staffUser : staffUserList) {
                    StaffRoleRel staffRoleRel = staffRoleRelRepo.findByStaffId(staffUser.getId().longValue());
                    StaffUserAllPojo pojo = new StaffUserAllPojo();
                    pojo.setId(staffUser.getId());
                    pojo.setUsername(staffUser.getUsername());
                    pojo.setFullName(staffUser.getFirstname() + " " + staffUser.getLastname());
                    pojo.setMobileNumber(staffUser.getCountryCode() + "-" + staffUser.getPhone());
                    if (staffRoleRel != null) {
                        Optional<Role> roleOptional = roleRepository.findById(staffRoleRel.getRoleId());
                        pojo.setProduct(roleOptional.get().getProduct());
                    }
                    staffUserListRes.add(pojo);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return staffUserListRes;


    }

    public List<StaffUser> findAllByParentStaffId(Integer parentStaffId) {
        return entityRepository.findAllByParentStaffId(parentStaffId);
    }

    public void savePartnerStaff(Partner partner, String operation, Integer userId) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[savePartnerStaff()]";
        try {
            if (partner != null) {
                if ("add".equals(operation)) {
                    createPartnerUser(partner, userId);
                } else if ("edit".equalsIgnoreCase(operation)) {
                    Integer partnerId = partner.getId();
                    QStaffUser qStaffUser = QStaffUser.staffUser;
                    BooleanExpression exp = qStaffUser.isNotNull();
                    QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
                    BooleanExpression exp1 = qStaffUserServiceAreaMapping.isNotNull();
                    exp = exp.and(qStaffUser.partnerid.eq(partnerId));
                    List<StaffUser> staff = (List<StaffUser>) entityRepository.findAll(exp);
                    staff.get(0).setUsername(partner.getEmail());
                    staff.get(0).setPassword(CommonUtils.generateBcryptPassword(partner.getEmail()));
                    staff.get(0).setPartnerid(partner.getId());
                    staff.get(0).setEmail(partner.getEmail());
                    staff.get(0).setPhone(partner.getMobile());
                    staff.get(0).setFirstname(partner.getName());
                    staff.get(0).setIsDelete(partner.getIsDelete());
                    staff.get(0).setLastname(partner.getName());
                    staff.get(0).setStatus(CommonConstants.ACTIVE_STATUS);
                    staff.get(0).setMvnoId(partner.getMvnoId());
                    staff.get(0).setCreatedById(userId);
                    staff.get(0).setLastModifiedById(userId);
                    staff.get(0).setPhone(partner.getMobile());
                    if(partner.getPartnerType().equalsIgnoreCase("LCO")){
                        staff.get(0).setLcoId(partner.getId());
                    }
                    if (userId != null) {
                        staff.get(0).setCreatedByName(entityRepository.findById(userId).map(StaffUser::getCreatedByName).toString());
                        staff.get(0).setLastModifiedByName(entityRepository.findById(userId).map(StaffUser::getCreatedByName).toString());
                    }
                    StaffUser savedStaff = entityRepository.save(staff.get(0));
                    Integer staffId = staff.get(0).getId();
                    exp1 = exp1.and(qStaffUserServiceAreaMapping.staffId.eq(staffId));
                    List<StaffUserServiceAreaMapping> oldserviceAreaMappings = (List<StaffUserServiceAreaMapping>) staffUserServiceAreaMappingRepository.findAll(exp1);
                    staffUserServiceAreaMappingRepository.deleteAll(oldserviceAreaMappings);
                    //updating service area list in staffuser table
                    if (partner.getServiceAreaList().size() > 0) {
                        partner.getServiceAreaList().forEach(serviceArea -> {
                            StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                            staffUserServiceAreaMapping.setStaffId(staff.get(0).getId());
                            staffUserServiceAreaMapping.setServiceId(Math.toIntExact(serviceArea.getId()));
                            staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                            staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                            staffUserServiceAreaMapping.setCreatedById(staff.get(0).getId());
                            staffUserServiceAreaMapping.setLastModifiedById(staff.get(0).getId());
                            staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                            staffUserServiceAreaMapping.setCreatedByName(staff.get(0).getUsername());
                            staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMapping);
                        });
                    }
                    sharedStaffData(savedStaff, CommonConstants.OPERATION_UPDATE);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void sharedStaffData(StaffUser obj, Integer operation) {
        if (obj != null) {
            StaffUser staffUserEntity = obj;
            Set<Role> roleList = new HashSet<>();
            List<ServiceArea> serviceAreaList = new ArrayList<>();
            List<BusinessUnit> businessUnitList = new ArrayList<>();
            if (obj.getRoles() != null) {
                for (Role item : obj.getRoles()) {
                    Role role = new Role();
                    role.setId(item.getId());
                    roleList.add(role);
                }
                staffUserEntity.setRoles(roleList);
            }
            if (obj.getTeam() != null) {
                staffUserEntity.setTeam(obj.getTeam());
            }
            List<Pincode> pincodeList = new ArrayList<>();
            if (obj.getServiceAreaNameList() != null) {
                for (ServiceArea item : obj.getServiceAreaNameList()) {
                    ServiceArea serviceArea = new ServiceArea();
                    serviceArea.setId(item.getId());
                    serviceArea.setName(item.getName());
                    if (item.getPincodeList() != null) {
                        for (Pincode list : item.getPincodeList()) {
                            pincodeList.add(new Pincode(list));
                        }
                        serviceArea.setPincodeList(pincodeList);
                    }
                    serviceAreaList.add(serviceArea);
                }
                staffUserEntity.setServiceAreaNameList(serviceAreaList);
            }
            if (obj.getBusinessUnitNameList() != null) {
                for (BusinessUnit item : obj.getBusinessUnitNameList()) {
                    BusinessUnit businessUnit = new BusinessUnit();
                    businessUnit.setId(item.getId());
                    businessUnitList.add(businessUnit);
                }
                staffUserEntity.setBusinessUnitNameList(businessUnitList);
            }
            if (operation == CommonConstants.OPERATION_ADD) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(staffUserEntity);
            }
            if (operation == CommonConstants.OPERATION_UPDATE) {
                createDataSharedService.updateEntityDataForAllMicroService(staffUserEntity);
            }
        }
    }

    public List<StaffUserPojo> getStaffUserByServiceAreaId(Integer serviceAreaId) {
        List<StaffUserPojo> staffUserPojoList = new ArrayList<>();
        try {
            List<Integer> stafflistFromServiceArea = new ArrayList<>();
            stafflistFromServiceArea = staffUserServiceAreaMappingRepository.findStaffIdByServiceAreaId(serviceAreaId);
            if (stafflistFromServiceArea != null) {
                if (getLoggedInUserId() != 1) {
                    staffUserPojoList = staffUserRepository.findAllLightStaffUsersByStaffIds(stafflistFromServiceArea, getMvnoIdFromCurrentStaff());
                } else {
                    staffUserPojoList = staffUserRepository.findAllLightStaffUsersByStaffIdsForSuperadmin(stafflistFromServiceArea);
                }
            } else {
                ApplicationLogger.logger.warn("No staff found for the servicearea id : " + serviceAreaId + " while fetching staff list based on servicearea");
            }
            return staffUserPojoList;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error encounterd while fetching staff by servicearea : " + e.getMessage());
        }
        return null;
    }

    public StaffUser getByStaffId(Integer staffId) {
        return entityRepository.findById(staffId).get();
    }

    public List<StaffUser> getAllStaffByServiceAreaId(Integer areaid) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        BooleanExpression booleanExpression = qStaffUser.isNotNull();
        booleanExpression = booleanExpression.and(qStaffUser.isDelete.eq(false));
        List<Integer> staffIdsall = new ArrayList<>();
        List<Integer> ids = entityRepository.findAllByServiceareaId(areaid);
        staffIdsall.addAll(ids);
        // booleanExpression = booleanExpression.and(qStaffUser.id.in(ids));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
            List<Long> buids = getBUIdsFromCurrentStaff();
            BooleanExpression booleanExpression1 = qStaffUserBusinessUnitMapping.businessunitId.in(buids);
            List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappingsList = (List<StaffUserBusinessUnitMapping>) staffUserBusinessUnitMappingRepository.findAll(booleanExpression1);
            List<Integer> staffids = staffUserBusinessUnitMappingsList.stream().map(staffUserBusinessUnitMapping -> staffUserBusinessUnitMapping.getStaffId()).collect(Collectors.toList());
            staffIdsall.addAll(staffids);
            booleanExpression = booleanExpression.and(qStaffUser.id.in(staffIdsall));
        }
        booleanExpression = booleanExpression.and(qStaffUser.mvnoId.eq(getMvnoIdFromCurrentStaff()));
        //      List<StaffUser> staffUserList = (List<StaffUser>) entityRepository.findAll(booleanExpression);
        List<StaffUser> staffUsers = new JPAQuery<>(entityManager)
                .select(Projections.constructor(StaffUser.class,
                        qStaffUser.id, qStaffUser.firstname, qStaffUser.lastname, qStaffUser.phone))
                .from(qStaffUser)
                .where(booleanExpression)
                .fetch();
        return staffUsers;
        //return staffUserList.stream().map(staffUser -> staffUser.getFullName()).collect(Collectors.toList());
    }

    public Byte[] getProfilePictureByStaffId(Integer staffId) {
        Byte[] profileImage = entityRepository.getProfileImageByStaffId(staffId);
        return profileImage;
    }

    public GenericDataDTO makeGenericResponseForStaffPojo(GenericDataDTO genericDataDTO, Page<StaffUserPojo> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public String getRefreshTokenByMvno(Long mvnoId) throws JsonProcessingException {

        Mvno mvno = mvnoRepository.findById(mvnoId).orElse(null);

        if (mvno != null) {
            String loggedInUserName = mvno.getUsername();
            if (loggedInUserName != null) {
                StaffUser staffUser = entityRepository.findStaffUserByUsername(loggedInUserName);
                if (staffUser != null) {
                    Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(CommonConstants.SECRET),
                            SignatureAlgorithm.HS256.getJcaName());
                    //LoggedInUser user = (LoggedInUser) staffUser;
                    LoggedInUser loggedInUser = customUserDetailsService.getLoggedInUserRefreshDataWithStaffId(staffUser.getId());

                    String subString = new ObjectMapper().writeValueAsString(loggedInUser);
                    //Update sign with with new method
                    String token = Jwts.builder()
                            .setSubject(subString)
                            .setExpiration(new Date(System.currentTimeMillis() + CommonConstants.EXPIRATION_TIME))
                            .signWith(hmacKey)
                            .compact();
                    token = com.savbill.commonGateway.security.constants.Constants.AUTHORIZATION_TOKEN_PREFIX + " " + token;
                    return token;
                }

            }

        }
        return null;
    }


    public void UpdateStaffStatus(MvnoStatusMessage mvnoStatusMessage) {
        try {
            if (!mvnoStatusMessage.getObjectList().isEmpty()) {
                customRepository.updateMvnoStatusForStaff(mvnoStatusMessage.getObjectList(), mvnoStatusMessage.getStatus(), mvnoStatusMessage.getMvnoDeactivationFlag());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    public void saveUpdateStaffLocation(StaffUserPojo pojo, Integer staffId, Integer operation) {
        if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
            List<StaffUserLocationMapping> staffUserLocationMapping = staffLocationMappingRepo.findAllByStaffId(Long.valueOf(staffId));
            if (!staffUserLocationMapping.isEmpty()) {
                staffLocationMappingRepo.deleteAll(staffUserLocationMapping);
            }
        }
        if (!pojo.getStaffUserLocationMappingDtos().isEmpty()) {
            for (StaffUserLocationMappingDto staffUserLocationMappingDto : pojo.getStaffUserLocationMappingDtos()) {
                StaffUserLocationMapping staffUserLocationMapping = new StaffUserLocationMapping();
                staffUserLocationMapping.setLocationId(staffUserLocationMappingDto.getLocationId());
                staffUserLocationMapping.setLocationName(staffUserLocationMappingDto.getLocationName());
                staffUserLocationMapping.setStaffId(Long.valueOf(staffId));
                staffLocationMappingRepo.save(staffUserLocationMapping);
            }
        }
    }

    @Transactional
    public void saveStaffAccessibleRoleMapping(List<Long> assignableRoleIds, Integer staffId) {
        if (assignableRoleIds != null && assignableRoleIds.size() > 0) {
            List<StaffAccessibleRoleMapping> accessibleRoleMappings = assignableRoleIds.stream()
                    .map(assignableRoleId -> {
                        StaffAccessibleRoleMapping staffAccessibleRoleMapping = new StaffAccessibleRoleMapping();
                        staffAccessibleRoleMapping.setStaffId(staffId);
                        staffAccessibleRoleMapping.setStaffAccessibleRoleId(assignableRoleId);
                        return staffAccessibleRoleMapping;
                    }).collect(Collectors.toList());
            staffAccessibleRoleMappingRepository.saveAll(accessibleRoleMappings);
        }
    }

    @Transactional
    public void updateStaffAccessibleRoleMapping(List<Long> assignableRoleIds, Integer staffId) {

        List<StaffAccessibleRoleMapping> accessibleRoles = staffAccessibleRoleMappingRepository.findAllByStaffId(staffId);
        if (!accessibleRoles.isEmpty()) {
            staffAccessibleRoleMappingRepository.deleteAll(accessibleRoles);
        }
        if (assignableRoleIds != null && assignableRoleIds.size() > 0) {
            List<StaffAccessibleRoleMapping> accessibleRoleMappings = assignableRoleIds.stream()
                    .map(assignableRoleId -> {
                        StaffAccessibleRoleMapping staffAccessibleRoleMapping = new StaffAccessibleRoleMapping();
                        staffAccessibleRoleMapping.setStaffId(staffId);
                        staffAccessibleRoleMapping.setStaffAccessibleRoleId(assignableRoleId);
                        return staffAccessibleRoleMapping;
                    }).collect(Collectors.toList());
            staffAccessibleRoleMappingRepository.saveAll(accessibleRoleMappings);
        }
    }

    public List<StaffUser> getAllActiveEntitiesWithoutPagination(String product) {
        List<StaffUser> staffUsers;
        QRole role = QRole.role;
        QStaffUser qStaffUser = QStaffUser.staffUser;
        QBusinessUnit qBusinessUnit = QBusinessUnit.businessUnit;
        BooleanExpression expression = qStaffUser.isDelete.eq(false);
        if (getLoggedInUser().getMvnoId() != 1) {
            expression = expression.and(qStaffUser.mvnoId.in(getLoggedInUser().getMvnoId())
                    .and(qStaffUser.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))
                    .and(qStaffUser.roles.any().product.equalsIgnoreCase(product)));
        } else {
            expression = expression.and(qStaffUser.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))
                    .and(qStaffUser.roles.any().product.equalsIgnoreCase(product));
        }
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            expression = expression.and(qStaffUser.partnerid.eq(CommonConstants.DEFAULT_PARTNER_ID));
//            staffUsers = entityRepository.findAll(expression,pageable);
        } else {
            expression = expression.and(qStaffUser.partnerid.eq(getLoggedInUser().getPartnerId()));
            // staffUsers = entityRepository.findAll(expression,pageable);
        }
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().isEmpty()) {
//            staffUsers = IterableUtils.toList(entityRepository.findAll(expression));
            staffUsers = new JPAQuery<>(entityManager)
                    .select(Projections.constructor(StaffUser.class,
                            qStaffUser.id, qStaffUser.firstname, qStaffUser.lastname, qStaffUser.phone, qStaffUser.username))
                    .from(qStaffUser)
                    .where(expression)
                    .fetch();
        } else {
            expression = expression.and(qStaffUser.businessUnitNameList.any().in(businessUnitRepository.findAllById(getBUIdsFromCurrentStaff())));
//            staffUsers = IterableUtils.toList(entityRepository.findAll(expression));
            staffUsers = new JPAQuery<>(entityManager)
                    .select(Projections.constructor(StaffUser.class,
                            qStaffUser.id, qStaffUser.firstname, qStaffUser.lastname, qStaffUser.phone, qStaffUser.username))
                    .from(qStaffUser)
                    .where(expression)
                    .fetch();
        }
        return staffUsers;
    }

    public List<StaffUserDropdownDTO> getAllActiveEntitiesStaffForDropdown(Integer mvnoId) {
        return entityRepository.findAllStaffForDropdownByIsDeleteIsFalseAndStatusActiveOrderByIdDesc("Active",mvnoId);
    }



    public List<StaffUserDropdownDTO> getAllActiveChildStaffForDropdown(Integer parentStaffId) {
        return entityRepository.findAllChildStaffForDropdownByIsDeleteIsFalseAndStatusActiveOrderByIdDesc(parentStaffId);
    }

    public List<StaffUserPojo> findStaffIdsWithoutServiceArea(Integer serviceAreaId) {
        List<StaffUserPojo> staffUserPojoList = new ArrayList<>();
        try {
            List<StaffUserPojo> staffUserPojos = staffUserRepository.findStaffIdsWithoutServiceArea(serviceAreaId, getMvnoIdFromCurrentStaff());
            if (staffUserPojos != null) {
                return staffUserPojos;
            } else {
                ApplicationLogger.logger.warn("No staff found for the servicearea id : " + serviceAreaId + " while fetching staff list based on servicearea");
            }
            return staffUserPojoList;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error encounterd while fetching staff by servicearea : " + e.getMessage());
        }
        return null;
    }

}
