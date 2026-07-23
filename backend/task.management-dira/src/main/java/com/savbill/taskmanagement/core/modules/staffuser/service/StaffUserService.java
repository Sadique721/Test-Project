package com.savbill.taskmanagement.core.modules.staffuser.service;

import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.modules.Branch.domain.Branch;
import com.savbill.taskmanagement.core.modules.Branch.repository.BranchRepository;
import com.savbill.taskmanagement.core.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.taskmanagement.core.modules.Partner.domain.Partner;
import com.savbill.taskmanagement.core.modules.Partner.service.PartnerService;
import com.savbill.taskmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.savbill.taskmanagement.core.modules.Teams.domain.TeamUserMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamUserMappingsRepository;
import com.savbill.taskmanagement.core.modules.role.domain.Role;
import com.savbill.taskmanagement.core.modules.role.repository.RoleRepository;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUserServiceAreaMapping;
import com.savbill.taskmanagement.core.modules.staffuser.dto.StaffUserPojo;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserServiceAreaMappingRepository;
import com.savbill.taskmanagement.core.service.AbstractService;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.SaveStaffUserSharedDataMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.UpdateStaffUserSharedDataMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    PartnerService partnerService;

    @Autowired
    TeamUserMappingsRepository teamUserMappingsRepository;

    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;


//    @Autowired
//    private StaffUserServiceRepository staffUserServiceRepository;
    @Autowired
    private StaffUserRepository entityRepository;



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
            staffUser.setRoles(message.getRoles());
            staffUser.setIsDelete(message.getIsDelete());
            staffUser.setEmail(message.getEmail());
            staffUser.setPhone(message.getPhone());
            staffUser.setParentStaffId(message.getParentStaffId());
            staffUser.setLcoId(message.getLcoId());
//            if(!message.getLast_login_time().equalsIgnoreCase("null") ) {
//                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
//            } else {
//                staffUser.setLast_login_time(null);
//            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setBranchId(message.getBranchId());
//            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
            if(message.getServiceAreaNameList()!=null){
                for(ServiceArea serviceArea : message.getServiceAreaNameList()){
                    StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMapping.setStaffId(message.getId());
                    staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
                    staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMapping);
                }
            }
            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            if(message.getTeamsList().size()>0){
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    teamUserMappingsRepository.save(teamUserMapping);
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

            staffUser.setId(message.getId());
            staffUser.setUsername(message.getUsername());
            staffUser.setPassword(message.getPassword());
            staffUser.setFirstname(message.getFirstname());
            staffUser.setLastname(message.getLastname());
            staffUser.setStatus(message.getStatus());
            staffUser.setLcoId(message.getLcoId());
            staffUser.setParentStaffId(message.getParentStaffId());
            staffUser.setPhone(message.getPhone());
            staffUser.setEmail(message.getEmail());
            if(!message.getLast_login_time().equalsIgnoreCase("null") ) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setPartnerid(message.getPartnerid());
            staffUser.setRoles(message.getRoles());
            staffUser.setIsDelete(message.getIsDelete());
            if(message.getTeamsList().size()>0){
                List<TeamUserMapping> oldteamMapping = new ArrayList<>();
                oldteamMapping = teamUserMappingsRepository.findAllByStaffId(staffUser.getId().longValue());
                teamUserMappingsRepository.deleteAll(oldteamMapping);
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    teamUserMappingsRepository.save(teamUserMapping);
                }
            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setBranchId(message.getBranchId());
//            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
            if(message.getServiceAreaNameList()!=null){
                StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                List<StaffUserServiceAreaMapping> oldStaffserviceareaMapping = new ArrayList<>();
                oldStaffserviceareaMapping = staffUserServiceAreaMappingRepository.findAllByStaffId(staffUser.getId());
                staffUserServiceAreaMappingRepository.deleteAll(oldStaffserviceareaMapping);

                for(ServiceArea serviceArea : message.getServiceAreaNameList()){
                    StaffUserServiceAreaMapping staffUserServiceAreaMappings = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMappings.setStaffId(message.getId());
                    staffUserServiceAreaMappings.setServiceId(serviceArea.getId().intValue());
                    staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMappings);
                }
            }
            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            entityRepository.save(staffUser);
            ApplicationLogger.logger.info("Staff User created successfully with name " + message.getUsername());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create staff user with name " + message.getUsername(), e.getMessage());
        }
    }
//
//    @Autowired
//    private MessagesPropertyConfig messagesProperty;
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private TeamsService teamsService;
//
//    @Autowired
//    private TeamsMapper teamsMapper;
//
//    @Autowired
//    private PartnerService partnerService;
//
//    @Autowired
//    private StaffUserMapper staffUserMapper;
//
//    @Autowired
//    private ServiceAreaService serviceAreaService;
//
//    @Autowired
//    private BusinessUnitService businessUnitService;
//
//    @Autowired
//    private ServiceAreaRepository serviceAreaRepository;
//
//    @Autowired
//    private BusinessUnitRepository businessUnitRepository;
//
//    @Autowired
//    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
//
//
//    @Autowired
//    private StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;
//    @Autowired
//    MessageSender messageSender;
//
//
//    @Autowired
//    MessageReceiver messageReceiver;
//
//    @Autowired
//    NotificationTemplateRepository templateRepository;
//
//
//    @Autowired
//    private ServiceAreaMapper serviceAreaMapper;
//
//    @Autowired
//    private BusinessUnitMapper businessUnitMapper;
//
//    @PersistenceContext
//    EntityManager entityManager;
//
//    @Autowired
//    CustomersService customersService;
//
//    @Autowired
//    CreditDocService creditDocService;
//
//    @Autowired
//    CustomerCafAssignmentService customerCafAssignmentService;
//
//    @Autowired
//    HierarchyService hierarchyService;
//
//    private static String MODULE = " [StaffUserService] ";
//    private final BranchRepository branchRepository;
//    private final RoleRepository roleRepository;
//
//    public String getModuleNameForLog() {
//        return "[StaffUserService]";
//    }
//
////    @Autowired
////    CustomerCafAssignmentRepository customerCafAssignmentRepository;
//
//    //private static String MODULE = " [StaffUserService] ";
//
//    @Override
//    public JpaRepository<StaffUser, Integer> getRepository() {
//        return entityRepository;
//    }
//
//    public Page<StaffUser> searchEntity(String searchText, Integer pageNumber, int pageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
//        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//            return entityRepository.searchEntity(searchText, pageRequest);
//        } else {
//            return entityRepository.searchEntity(searchText, pageRequest, getLoggedInUserPartnerId());
//        }
//
//    }
//
//    public List<StaffUser> getAllActiveEntities() {
//        List<StaffUser> staffUsers = new ArrayList<>();
//        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//            staffUsers = entityRepository.findByStatusAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS);
//        } else {
//            staffUsers = entityRepository.findByStatusAndPartneridAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS, getLoggedInUserPartnerId());
//        }
//        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
//            //staffUsers.stream().filter(staff -> (staff.getMvnoId() == getMvnoIdFromCurrentStaff() && staff.getMvnoId() != 1));
//            staffUsers = entityRepository.findAllUsername(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            return staffUsers;
//        } else {
//            staffUsers = entityRepository.findAllUsername(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff());
//            return staffUsers;
//        }
//    }
//
//    public List<StaffUser> getStaffUserFromUsername(String username) {
//        return entityRepository.findByUsername(username);
//    }
//
//    public List<StaffUser> getActiveStaffUserFromUsername(String username) {
//        return entityRepository.findByUsernameAndStatusAndIsDeleteIsFalse(username, CommonConstants.ACTIVE_STATUS);
//    }
//
//    public List<StaffUserPojo> searchUserCustom(String searchText) throws Exception {
//        List<StaffUser> list = entityRepository.findAllUsername(searchText);
//        return convertResponseModelIntoPojo(list);
//    }
//
//    public void increaseFailAttempts(String username) {
//        List<StaffUser> userList = entityRepository.findByUsername(username);
//        if (userList != null && userList.size() > 0) {
//            StaffUser user = userList.get(0);
//            user.setFailcount(user.getFailcount() + 1);
//            entityRepository.save(user);
//        }
//    }
//
//    public void resetFailAttempts(String username) {
//        String SUBMODULE = MODULE + "[resetFailAttempts()]";
//        List<StaffUser> userList = entityRepository.findByUsername(username);
//        try {
//            if (userList != null && userList.size() > 0) {
//                StaffUser user = userList.get(0);
//                user.setLast_login_time(LocalDateTime.now());
//                user.setFailcount(0);
//                entityRepository.save(user);
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    @Transactional
//    public void createPartnerUser(Partner partner) throws Exception {
//        String SUBMODULE = MODULE + "[createPartnerUser()]";
//        StaffUser user = new StaffUser();
//        try {
//            user.setUsername(partner.getEmail());
//            user.setPassword(CommonUtils.generateBcryptPassword(partner.getEmail()));
//            user.setPartnerid(partner.getId());
//            user.setEmail(partner.getEmail());
//            user.setPhone(partner.getMobile());
//            user.setFirstname(partner.getName());
//            user.setIsDelete(partner.getIsDelete());
//            user.setLastname(partner.getName());
//            user.setStatus(CommonConstants.ACTIVE_STATUS);
//            user.setMvnoId(partner.getMvnoId());
//            // user.getBusinessUnit().setId(partner.getBuId());
//
//            HashSet<Role> roles = new HashSet<>();
//            //Add default role
//            String roleId = getLoggedInUser().getRolesList();
//            Role role = null;
//            if(partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO)) {
//                String roleName = CommonUtils.getPartnerRoleName();
//                if (!roleName.isEmpty()) {
//                    List<Role> roleList = roleRepository.findAllByRolename(roleName);
//                    if(!CollectionUtils.isEmpty(roleList))
//                        role = roleService.convertRolePojoToRoleModel(roleService.getEntityById(roleList.get(0).getId()));
//                }
//
//            } else {
//                role = roleService.convertRolePojoToRoleModel(roleService.getEntityById(CommonUtils.getPartnerRoleId().longValue()));
//            }
//            if(role == null) {
//                role = roleService.convertRolePojoToRoleModel(roleService.getEntityById(Long.valueOf(roleId)));
//            }
//            roles.add(role);
//            user.setRoles(roles);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        user = save(user);
//        if (partner.getServiceAreaList().size() > 0) {
//            StaffUser finalUser = user;
//            partner.getServiceAreaList().forEach(serviceArea -> {
//                StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
//                staffUserServiceAreaMapping.setStaffId(finalUser.getId());
//                staffUserServiceAreaMapping.setServiceId(Math.toIntExact(serviceArea.getId()));
//                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
//                staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
//                staffUserServiceAreaMapping.setCreatedById(finalUser.getId());
//                staffUserServiceAreaMapping.setLastModifiedByName(finalUser.getUsername());
//                staffUserServiceAreaMapping.setLastModifiedById(finalUser.getId());
//                staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
//                staffUserServiceAreaMapping.setCreatedByName(finalUser.getUsername());
//                staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMapping);
//            });
//            if (partner.getId() > 0 && getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
//                StaffUser finalUser2 = user;
//                StaffUserBusinessUnitMapping staffUserBusinessUnitMapping = new StaffUserBusinessUnitMapping();
//                staffUserBusinessUnitMapping.setStaffId(finalUser2.getId());
//                staffUserBusinessUnitMapping.setCreatedOn(LocalDateTime.now());
//                staffUserBusinessUnitMapping.setLastmodifiedOn(LocalDateTime.now());
//                staffUserBusinessUnitMapping.setCreatedById(finalUser2.getId());
//                staffUserBusinessUnitMapping.setLastModifiedByName(finalUser2.getUsername());
//                staffUserBusinessUnitMapping.setLastModifiedById(finalUser2.getId());
//                staffUserBusinessUnitMapping.setCreatedOn(LocalDateTime.now());
//                staffUserBusinessUnitMapping.setCreatedByName(finalUser2.getUsername());
//                Long l = getBUIdsFromCurrentStaff().get(0);
//                Integer i = l.intValue();
//                staffUserBusinessUnitMapping.setBusinessunitId(i);
//
//                staffUserBusinessUnitMappingRepository.save(staffUserBusinessUnitMapping);
//            }
//
//        }
//        List<ServiceAreaDTO> serviceAreaDTOS = user.getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//        BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(user.getId()));
//        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));
//        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(user, new CycleAvoidingMappingContext());
//        StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS);
//        messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS, RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
//        UserMessage userMessage = new UserMessage(staffUserPojo);
//        messageSender.send(userMessage, RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS);
//
//
//    }
//
//    public Page<StaffUser> getList(Integer pageNumber) {
//        return getList(pageNumber, CommonConstants.DB_PAGE_SIZE);
//    }
//
//    @Override
//    public Page<StaffUser> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//        if (getLoggedInUser().getLco()) {
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                if (filterList == null || 0 == filterList.size()) {
//                    if (getMvnoIdFromCurrentStaff() == 1)
//                        return entityRepository.findAll(pageRequest, getLoggedInUser().getPartnerId());
//                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
//                        return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getLoggedInUser().getPartnerId());
//                    } else {
//                        return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
//                    }
//                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//            } else {
//                if (filterList == null || 0 == filterList.size()) {
//                    if (getMvnoIdFromCurrentStaff() == 1) return entityRepository.findAll(pageRequest);
//                    return entityRepository.findByPartneridAndIsDeleteIsFalse(getLoggedInUserPartnerId(), pageRequest, getMvnoIdFromCurrentStaff());
//                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//            }
//        } else {
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                if (filterList == null || 0 == filterList.size()) {
//                    if (getMvnoIdFromCurrentStaff() == 1) return entityRepository.findAll(pageRequest);
//                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
//                        return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                    } else {
//                        return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), getBUIdsFromCurrentStaff());
//                    }
//                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//            } else {
//                if (filterList == null || 0 == filterList.size()) {
//                    if (getMvnoIdFromCurrentStaff() == 1) return entityRepository.findAll(pageRequest);
//                    return entityRepository.findByPartneridAndIsDeleteIsFalse(getLoggedInUserPartnerId(), pageRequest, getMvnoIdFromCurrentStaff());
//                } else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//            }
//        }
//    }
//
//    public Page<StaffUser> getList(Integer pageNumber, int customPageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, customPageSize);
//        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//            return getRepository().findAll(pageRequest);
//        } else {
//            return entityRepository.findByPartneridAndIsDeleteIsFalse(getLoggedInUserPartnerId(), pageRequest, getMvnoIdFromCurrentStaff());
//        }
//    }
//
//    public List<StaffUser> getAllUsers() {
//        return entityRepository.findAll();
//    }
//
//    public void deleteStaffUser(Integer id) {
//        entityRepository.deleteById(id);
//        Optional<StaffUser> staffUser = entityRepository.findById(id);
//        staffUser.get().setIsDelete(true);
//        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser.get(), new CycleAvoidingMappingContext());
//
//        List<ServiceAreaDTO> serviceAreaDTOS = staffUser.get().getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//
//        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//        BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(id));
//        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));
//
//        StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS);
//        messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS, RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
//        messageSender.send(staffUserMessage,RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE);
//
////        messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_DELETE);
//
//
//    }
//
//    public StaffUser getStaffUserForAdd() {
//        return new StaffUser();
//    }
//
//    public StaffUser getStaffUserForEdit(Integer id) throws Exception {
//        return entityRepository.getOne(id);
//    }
//
//    public StaffUser getByUserName(String uname) throws Exception {
//        StaffUser staffUser;
//        staffUser = entityRepository.findUsername(uname);
//        return staffUser;
//    }
//
//    public String forgotPass(StaffUser staffUser) throws Exception {
//        Random random = new Random();
//        String otp = String.format("%04d", random.nextInt(10000));
//        staffUser.setOtp(otp);
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        String formatDateTime = now.format(format);
//        LocalDateTime validate = LocalDateTime.parse(formatDateTime, format);
//        staffUser.setOtpvalidate(validate);
//        update(staffUser);
//        CommunicationHelper communicationHelper = new CommunicationHelper();
//        Map<String, String> map = new HashMap<>();
//        map.put(CommunicationConstant.DESTINATION, staffUser.getPhone());
//        map.put(CommunicationConstant.EMAIL, staffUser.getEmail());
//        map.put(CommunicationConstant.OTP, otp);
//        communicationHelper.generateCommunicationDetails(18L, Collections.singletonList(map));
//
//        return "Success--" + otp;
//    }
//
//    public String validateForgotPassword(StaffUser staffUser, ForgotPassowrdDTO dto) throws Exception {
//        String response;
//        LocalDateTime time = staffUser.getOtpvalidate();
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        String formatDateTime = now.format(format);
//        LocalDateTime validate = LocalDateTime.parse(formatDateTime, format);
//        LocalDateTime tempDateTime = LocalDateTime.from(staffUser.getOtpvalidate());
//        long minutes = tempDateTime.until(validate, ChronoUnit.MINUTES);
//        long min = Long.parseLong(messagesProperty.get("staffuser.validate.time"));
//        if (staffUser.getOtp().equalsIgnoreCase(dto.getOtp()) && minutes <= min) {
//            response = CommonConstants.FLASH_MSG_TYPE_SUCCESS;
//        } else {
//            response = CommonConstants.FLASH_MSG_TYPE_ERROR;
//        }
//        return response;
//    }
//
//    public StaffUserPojo updateProfile(StaffUser staffUser, UpdateProfileDTO dto) throws Exception {
//        staffUser.setFirstname(dto.getFirstname());
//        staffUser.setLastname(dto.getLastname());
//        staffUser.setEmail(dto.getEmail());
//        staffUser.setPhone(dto.getPhone());
//        update(staffUser);
//        StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext());
//        return staffUserPojo;
//    }
//
//    public StaffUser saveStaffUser(StaffUser staffUser) throws Exception {
//        String SUBMODULE = MODULE + " [saveStaffUser()] ";
//        try {
//            if (staffUser != null) {
//                if (staffUser.getId() == null) {
//                    PasswordEncoder encoder = new BCryptPasswordEncoder();
//                    staffUser.setPassword(encoder.encode(staffUser.getPassword()));
//                }
//                if (staffUser.getPartnerid() != null) {
//                    staffUser.setPartnerid(staffUser.getPartnerid());
//                }
//
//                if (getLoggedInUser().getLco())
//                    staffUser.setLcoId(getLoggedInUser().getPartnerId());
//                else
//                    staffUser.setLcoId(null);
//
//                StaffUser user = entityRepository.findStaffUserByUsername(staffUser.getUsername());
//                if (staffUser.getId() != null) {
//                    QStaffUserServiceAreaMapping qstaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//                    BooleanExpression booleanExpression = qstaffUserServiceAreaMapping.isNotNull().and(qstaffUserServiceAreaMapping.staffId.eq(staffUser.getId()));
//                    List<StaffUserServiceAreaMapping> oldstaffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));
//                    user = entityRepository.save(staffUser);
//                    QStaffUserServiceAreaMapping qstaffUserServiceAreaMapping11 = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//                    BooleanExpression booleanExpression11 = qstaffUserServiceAreaMapping.isNotNull().and(qstaffUserServiceAreaMapping.staffId.eq(staffUser.getId()));
//                    List<StaffUserServiceAreaMapping> newstaffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression11));
//
//                    QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
//                    BooleanExpression booleanExpression2 = qStaffUserBusinessUnitMapping.isNotNull().and(qStaffUserBusinessUnitMapping.staffId.eq(staffUser.getId()));
//                    List<StaffUserBusinessUnitMapping> oldstaffUserBusinessUnitMappings = IterableUtils.toList(staffUserBusinessUnitMappingRepository.findAll(booleanExpression2));
//                    QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping1 = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
//                    BooleanExpression booleanExpression22 = qStaffUserBusinessUnitMapping.isNotNull().and(qStaffUserBusinessUnitMapping.staffId.eq(staffUser.getId()));
//                    List<StaffUserBusinessUnitMapping> newstaffUserBusinessUnitMappings = IterableUtils.toList(staffUserBusinessUnitMappingRepository.findAll(booleanExpression22));
//
//                    //user = entityRepository.save(staffUser);
//
//                    if (oldstaffUserServiceAreaMappings != newstaffUserServiceAreaMappings) {
//                        boolean flag = true;
//                    }
//
//                    if (oldstaffUserBusinessUnitMappings != newstaffUserBusinessUnitMappings) {
//                        boolean flag = true;
//                    }
//                } else {
//                    if (user != null) staffUser.setId(user.getId());
//                    user = entityRepository.save(staffUser);
//                }
//                StaffUserPojo staffUserPojo = staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext());
//                QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//                BooleanExpression booleanExpression = qStaffUserServiceAreaMapping.isNotNull().and(qStaffUserServiceAreaMapping.staffId.eq(user.getId()));
//                List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = IterableUtils.toList(staffUserServiceAreaMappingRepository.findAll(booleanExpression));
//
//                QStaffUserBusinessUnitMapping qStaffUserBusinessUnitMapping = QStaffUserBusinessUnitMapping.staffUserBusinessUnitMapping;
//                BooleanExpression booleanExpression1 = qStaffUserBusinessUnitMapping.isNotNull().and(qStaffUserBusinessUnitMapping.staffId.eq(user.getId()));
//                List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappings = IterableUtils.toList(staffUserBusinessUnitMappingRepository.findAll(booleanExpression1));
//
//                //  Set<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = (Set<StaffUserServiceAreaMapping>) staffUserServiceAreaMappingRepository.findAll(booleanExpression);
//
//                //  Set<Integer> users= staffUserServiceAreaMappings.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toSet());
//                // Set<StaffUserServiceAreaMapping> staffUserServiceAreaMappings1=staffUserServiceAreaMappingRepository.findAllById(user.getId());
//
//                List<ServiceAreaDTO> serviceAreaDTOS = staffUser.getServiceAreaNameList().stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//                StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, serviceAreaDTOS);
//                //List<BusinessUnitDTO> businessUnitDTOS = staffUser.getBusinessUnitNameList().stream().map(data -> businessUnitMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//                //StaffUserMessage staffUserMessage = new StaffUserMessage(staffUserPojo, staffUserServiceAreaMappings, staffUserBusinessUnitMappings, serviceAreaDTOS, businessUnitDTOS);
//                messageSender.send(staffUserMessage, RabbitMqConstants.QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS, RabbitMqConstants.QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS);
//                UserMessage userMessage = new UserMessage();
//                userMessage.setId(user.getId());
//                userMessage.setUsername(user.getUsername());
//                userMessage.setPassword(user.getPassword());
//                userMessage.setFirstname(user.getFirstname());
//                userMessage.setLastname(user.getLastname());
//                userMessage.setEmail(user.getEmail());
//                if (user.getRoles() != null) {
//                    Set<RoleMessage> roleMessageList = new HashSet<>();
//                    for (Role role : user.getRoles()) {
//                        roleMessageList.add(new RoleMessage(role));
//                    }
//                    userMessage.setRoles(roleMessageList);
//                }
//                userMessage.setPhone(user.getPhone());
//                userMessage.setFailcount(user.getFailcount());
//                userMessage.setStatus(user.getStatus());
//                if (user.getLast_login_time() != null)
//                    userMessage.setLast_login_time(user.getLast_login_time().toString());
//                if (user.getCreatedate() != null) userMessage.setCreatedate(user.getCreatedate().toString());
//                if (user.getUpdatedate() != null) userMessage.setUpdatedate(user.getUpdatedate().toString());
//                userMessage.setPartnerid(user.getPartnerid());
//                userMessage.setOtp(user.getOtp());
//                if (user.getOtpvalidate() != null) userMessage.setOtpvalidate(user.getOtpvalidate().toString());
//                userMessage.setIsDelete(user.getIsDelete());
//                userMessage.setCountryCode(user.getCountryCode());
//                userMessage.setSysstaff(user.getSysstaff());
//                if (user.getServicearea() != null) userMessage.setServiceareaId(user.getServicearea().getId());
//                if (user.getBusinessUnit() != null) userMessage.setBusinessunitid(user.getBusinessUnit().getId());
//                if (user.getStaffUserparent() != null)
//                    userMessage.setStaffUserparentId(user.getStaffUserparent().getId());
//                userMessage.setMvnoId(user.getMvnoId());
//                userMessage.setBranchId(user.getBranchId());
//                if (user.getBusinessUnitNameList() != null && user.getBusinessUnitNameList().size() > 0) {
//                    Set<BusinessUnitMessage> businessUnitMessageList = new HashSet<BusinessUnitMessage>();
//                    for (BusinessUnit businessUnit : user.getBusinessUnitNameList()) {
//                        businessUnitMessageList.add(new BusinessUnitMessage(businessUnit));
//                    }
//                    userMessage.setBusinessUnitMessageList(businessUnitMessageList);
//                }
//                if (user.getTeam() != null && user.getTeam().size() > 0) {
//                    Set<TeamsMessage> teamsMessageList = new HashSet<TeamsMessage>();
//                    for (Teams teams : user.getTeam()) {
//                        teamsMessageList.add(new TeamsMessage(teams));
//                    }
//                    userMessage.setTeamMessageList(teamsMessageList);
//                }
//
//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_USER);
//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS);
//
//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_RESPONSE_TO_SAVE_STAFFUSER_FROM_GATEWAY);
//
//                messageSender.send(userMessage, RabbitMqConstants.QUEUE_STAFF_SAVE_USER_SEND);
//                log.info("{} Queue staff user",userMessage);
//
//
//                return user;
//            }
//            return null;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public StaffUserPojo save(StaffUserPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [save()] ";
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            if (pojo.getBusinessUnitIdsList() != null) {
//                pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
//            }
//
//            if (getLoggedInUser().getLco()) pojo.setLcoId(getLoggedInUser().getPartnerId());
//            else pojo.setLcoId(null);
//
//            //if (pojo.getServiceAreaIdsList() != null || !pojo.getBusinessUnitIdsList().isEmpty()) {
//            if (pojo.getServiceAreaIdsList() != null) {
//                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
////                if(pojo.getBusinessUnitIdsList() != null) {
////                    pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
////                }
//                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
//                obj = saveStaffUser(obj);
//                pojo = convertStaffUserModelToStaffUserPojo(obj);
//            } else {
//                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
//                obj = saveStaffUser(obj);
//                pojo = convertStaffUserModelToStaffUserPojo(obj);
//            }
//            return pojo;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public StaffUserPojo saveWithMvno(StaffUserPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [save()] ";
//        try {
//            if (pojo.getServiceAreaIdsList() != null) {
//                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
//                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
//                obj = saveStaffUser(obj);
//                pojo = convertStaffUserModelToStaffUserPojo(obj);
//            } else {
//                StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
//                obj = saveStaffUser(obj);
//                pojo = convertStaffUserModelToStaffUserPojo(obj);
//            }
//            return pojo;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//
//    public StaffUserPojo update(StaffUserPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [update()] ";
//
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            if (pojo.getServiceAreaIdsList() != null)
//                pojo.setServiceAreaNameList(serviceAreaRepository.findAllById(pojo.getServiceAreaIdsList()));
//            if (pojo.getBusinessUnitIdsList() != null)
//                pojo.setBusinessUnitNameList(businessUnitRepository.findAllById(pojo.getBusinessUnitIdsList()));
//            changestatus(pojo.getStatus(), pojo.getUsername());
//            StaffUser obj = convertStaffUserPojoToStaffUserModel(pojo);
//            obj.setMvnoId(get(obj.getId()).getMvnoId());
//            obj = saveStaffUser(obj);
//            pojo = convertStaffUserModelToStaffUserPojo(obj);
//            return pojo;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public StaffUser convertStaffUserPojoToStaffUserModel(StaffUserPojo staffUserPojo) throws Exception {
//        String SUBMODULE = MODULE + " [convertStaffUserPojoToStaffUserModel()] ";
//        StaffUser staffUser = null;
//        try {
//            if (staffUserPojo != null) {
//                staffUser = new StaffUser();
//                if (staffUserPojo.getId() != null) {
//                    staffUser = get(staffUserPojo.getId());
//                    staffUser.setId(staffUserPojo.getId());
//                }
//                staffUser.setUsername(staffUserPojo.getUsername());
//                if (null == staffUserPojo.getId()) {
//                    staffUser.setPassword(staffUserPojo.getPassword());
//                }
//                staffUser.setLcoId(staffUserPojo.getLcoId());
//                staffUser.setEmail(staffUserPojo.getEmail());
//                staffUser.setFirstname(staffUserPojo.getFirstname());
//                staffUser.setLastname(staffUserPojo.getLastname());
//                staffUser.setStatus(staffUserPojo.getStatus());
//                staffUser.setPhone(staffUserPojo.getPhone());
//                staffUser.setCountryCode(staffUserPojo.getCountryCode());
//                staffUser.setFailcount(staffUserPojo.getFailcount());
//                staffUser.setCreatedate(staffUserPojo.getCreatedate());
//                staffUser.setUpdatedate(staffUserPojo.getUpdatedate());
//                staffUser.setLast_login_time(staffUserPojo.getLast_login_time());
//                staffUser.setPartnerid(staffUserPojo.getPartnerid());
//                staffUser.setFullName(staffUserPojo.getFirstname() + " " + staffUserPojo.getLastname());
//                staffUser.setSysstaff(staffUserPojo.getSysstaff());
//                staffUser.setServiceAreaNameList(staffUserPojo.getServiceAreaNameList());
//                staffUser.setBusinessUnitNameList(staffUserPojo.getBusinessUnitNameList());
//                staffUser.setStaffUserServiceMappings(staffUserPojo.getStaffUserServiceMappingList());
////                if (staffUserPojo.getMvnoId() != null) {
//                staffUser.setMvnoId(staffUserPojo.getMvnoId());
////                }
//                if (staffUserPojo.getServiceAreaId() != null) {
//                    ServiceArea serviceArea = serviceAreaService.getByID(staffUserPojo.getServiceAreaId());
//                    staffUser.setServicearea(serviceArea);
//                }
//
//                if (staffUserPojo.getBusinessunitid() != null) {
//                    BusinessUnit businessUnit = businessUnitService.getById(staffUserPojo.getBusinessunitid());
//                    staffUser.setBusinessUnit(businessUnit);
//                }
//
//                if (staffUserPojo.getParentStaffId() != null) {
//                    StaffUser staffUser2 = get(staffUserPojo.getParentStaffId());
//                    staffUser.setStaffUserparent(staffUser2);
//                }
//
//                if (staffUserPojo.getRoleIds() != null && staffUserPojo.getRoleIds().size() > 0) {
//
//                    List<RoleDTO> roleDTOList = roleService.getAllByIdIn(staffUserPojo.getRoleIds().stream().map(Integer::longValue).collect(Collectors.toList()));
//
//                    staffUser.getRoles().clear();
//                    staffUser.getRoles().addAll(roleDTOList.stream().map(dto -> roleService.convertRolePojoToRoleModel(dto)).collect(Collectors.toSet()));
//                }
//
//                if (staffUserPojo.getTeamIds() != null && staffUserPojo.getTeamIds().size() > 0) {
//                    Set<Teams> teamList = teamsService.getAllByIdIn(new ArrayList<>(staffUserPojo.getTeamIds())).stream().map(dto -> teamsMapper.dtoToDomain(dto, new CycleAvoidingMappingContext())).collect(Collectors.toSet());
//                    staffUser.getTeam().clear();
//                    staffUser.getTeam().addAll(teamList);
//                }
//                if (staffUserPojo.getBranchId() != null) {
//                    staffUser.setBranchId(staffUserPojo.getBranchId());
//                }
//
//                staffUser.setHrmsId(staffUserPojo.getHrmsId());
//                staffUser.setProfileImage(staffUserPojo.getProfileImage());
//
//                if(staffUserPojo.getDepartment()!=null) {
//                    staffUser.setDepartment(staffUserPojo.getDepartment());
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return staffUser;
//    }
//
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
                pojo.setFullName(staffUser.getFullName());
                pojo.setServicearea(staffUser.getServicearea());
                pojo.setBusinessUnit(staffUser.getBusinessUnit());
                pojo.setServiceAreaIdsList(staffUser.getServiceAreaNameList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                pojo.setBusinessUnitIdsList(staffUser.getBusinessUnitNameList().stream().map(BusinessUnit::getId).collect(Collectors.toList()));
                pojo.setStaffUserServiceMappingList(staffUser.getStaffUserServiceMappings());
                //                if (staffUser.getMvnoId() != null) {
                pojo.setMvnoId(staffUser.getMvnoId());
//                }
//                if (staffUser.getServicearea() != null) pojo.setServiceAreaId(staffUser.getServicearea().getId());
//                if (staffUser.getBusinessUnit() != null) pojo.setBusinessunitid(staffUser.getBusinessUnit().getId());

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

                if (staffUser.getParentStaffId() != null)
                    pojo.setParentStaffId(staffUser.getParentStaffId());

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
                    Partner partner = partnerService.get(staffUser.getPartnerid());
                    if (null != partner) {
                        pojo.setPartnerName(null != partner.getName() ? partner.getName() : "-");
                    } else {
                        pojo.setPartnerName("-");
                    }
                }

                if (null != staffUser.getParentStaffId()) {
                    Optional<StaffUser> parent = entityRepository.findById(staffUser.getParentStaffId());
                    if (parent.isPresent()) {
                        pojo.setParentstaffname(parent.get().getUsername());
                    } else {
                        pojo.setParentstaffname("-");
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
                    Branch branch = branchRepository.findById(Long.valueOf(staffUser.getBranchId())).orElse(null);
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
//
//    public void validateRequest(StaffUserPojo pojo, Integer operation) {
//
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
//            if (pojo.getId() != null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//            }
//            if (pojo.getPassword() == null) {
//                throw new CustomValidationException(APIConstants.FAIL, "Please Enter Password", null);
//            }
//        }
//        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.TERMINATED))) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
//        }
//        if (pojo != null && !operation.equals(CommonConstants.OPERATION_DELETE)) {
//            if (pojo.getRoleIds() == null || pojo.getRoleIds().size() == 0) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.staffuser.role.required"), null);
//            }
//        }
//        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//        if (pojo != null && nameValidation(pojo) && !(operation.equals(CommonConstants.OPERATION_UPDATE)) && !(operation.equals(CommonConstants.OPERATION_DELETE))) {
//            throw new CustomValidationException(APIConstants.FAIL, "Username is already in use.", null);
//        }
//    }
//
//    public boolean nameValidation(StaffUserPojo pojo) {
//        List<StaffUser> staffUserList = getStaffUserFromUsername(pojo.getUsername());
//        boolean result = false;
//        if (staffUserList != null && staffUserList.size() > 0) {
//
//            for (StaffUser user : staffUserList) {
//                if (user.getIsDelete().equals(0)) {
//                    result = true;
//                } else {
//                    result = false;
//                }
//            }
//        } else {
//            result = false;
//        }
//        return result;
//    }
//
//    public StaffUser changePassword(UserPasswordChangePojo pojo) {
//        String SUBMODULE = MODULE + "[changePassword()]";
//        List<StaffUser> staffUserList = this.getStaffUserFromUsername(pojo.getUserName());
//        if (staffUserList != null && staffUserList.size() > 0) {
//            StaffUser staffUser = staffUserList.get(0);
//            if (staffUser != null) {
//                PasswordEncoder encoder = new BCryptPasswordEncoder();
//                //if (encoder.matches(pojo.getOldPassword(), staffUser.getPassword())) {
//                staffUser.setNewpassword(encoder.encode(pojo.getNewPassword()));
//                staffUser.setPassword(staffUser.getNewpassword());
//                entityRepository.save(staffUser);
//                return staffUser;
//                //} else {
//                //throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.staffuser.oldpassword.mismatch"), null);
//                //}
//            }
//        } else {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.staffuser.not.found"), null);
//        }
//        return null;
//    }
//
//    public List<StaffUserPojo> findStaffUserByRoleId(Long roleId) {
//        String SUBMODULE = MODULE + " [findStaffUserByRoleId()] ";
//        try {
//            List<StaffUser> staffUserList = new ArrayList<>();
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                staffUserList = entityRepository.findStaffByRole(roleId);
//            } else {
//                staffUserList = entityRepository.findStaffByRoleAndPartnerid(roleId, getLoggedInUserPartnerId());
//            }
//            if (null != staffUserList && 0 < staffUserList.size()) {
//                List<StaffUserPojo> staffUserPojos = staffUserList.stream().map(data -> staffUserMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//                return staffUserPojos.stream().filter(staff -> (getMvnoIdFromCurrentStaff().intValue() == 1 || (staff.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || staff.getMvnoId().intValue() == 1))).collect(Collectors.toList());
////                return staffUserPojos.stream().filter(staff -> (staff.getMvnoId() == 1 || staff.getMvnoId() == getMvnoIdFromCurrentStaff())).collect(Collectors.toList());
//
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return new ArrayList<>();
//    }
//
//    public List<StaffUserPojo> searchStaff(String searchText) throws Exception {
//        String SUBMODULE = MODULE + " [searchCustomersCustom()] ";
//        try {
//            QStaffUser staffUser = QStaffUser.staffUser;
//            BooleanExpression builder = staffUser.isNotNull();
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                builder = builder.andAnyOf(staffUser.firstname.startsWithIgnoreCase(searchText), staffUser.lastname.startsWithIgnoreCase(searchText), staffUser.phone.startsWith(searchText), staffUser.email.startsWith(searchText), staffUser.username.startsWith(searchText)).and(staffUser.isDelete.isFalse()).and(staffUser.roles.any().id.in(CommonConstants.BACK_OFFICE_STAFF_ROLE_ID));
//            }
//            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                builder = builder.andAnyOf(staffUser.firstname.startsWithIgnoreCase(searchText), staffUser.lastname.startsWithIgnoreCase(searchText), staffUser.phone.startsWith(searchText), staffUser.email.startsWith(searchText), staffUser.username.startsWith(searchText)).and(staffUser.partnerid.eq(getLoggedInUserPartnerId())).and(staffUser.isDelete.isFalse()).and(staffUser.roles.any().id.in(CommonConstants.BACK_OFFICE_STAFF_ROLE_ID));
//            }
//
//            if (getLoggedInUser().getLco()) builder = builder.and(staffUser.lcoId.eq(getLoggedInUser().getPartnerId()));
//            else builder = builder.and(staffUser.lcoId.isNull());
//
//            List<StaffUser> staffUserList = (List<StaffUser>) entityRepository.findAll(builder);
//            return convertResponseModelIntoPojo(staffUserList);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
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
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{StaffUserPojo.class.getDeclaredField("id"), StaffUserPojo.class.getDeclaredField("username"), StaffUserPojo.class.getDeclaredField("fullName"), StaffUserPojo.class.getDeclaredField("email"), StaffUserPojo.class.getDeclaredField("roleName"), StaffUserPojo.class.getDeclaredField("phone"), StaffUserPojo.class.getDeclaredField("regDate"), StaffUserPojo.class.getDeclaredField("status"), StaffUserPojo.class.getDeclaredField("partnerName")};
//    }
//
//    @Override
//    public Page<StaffUser> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = MODULE + " [search()] ";
//        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (null != searchModel.getFilterColumn()) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID)
//                            return getStaffByNameOrUsernameOrEmailOrRoleName(searchModel.getFilterValue(), pageRequest);
//                        else
//                            return getStaffByNameOrUsernameOrEmailOrRoleNameByPartner(searchModel.getFilterValue(), pageRequest);
//                    }
//                } else throw new RuntimeException("Please Provide Search Column!");
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }
//
//    public Page<StaffUser> getStaffByNameOrUsernameOrEmailOrRoleName(String s1, PageRequest pageRequest) {
//        List<Integer> mvnoIds = new ArrayList<>();
//        mvnoIds.add(1);
//        mvnoIds.add(getMvnoIdFromCurrentStaff());
//        if (getLoggedInUser().getLco()) {
//            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getLoggedInUser().getPartnerId());
//            else
//                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
//        } else {
//            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds);
//            else
//                return entityRepository.findAllByNameOrEmailOrRole(pageRequest, s1, s1, s1, s1, s1, mvnoIds, getBUIdsFromCurrentStaff());
//        }
//
//    }
//
//    public Page<StaffUser> getStaffByNameOrUsernameOrEmailOrRoleNameByPartner(String s1, PageRequest pageRequest) {
//        List<Integer> mvnoIds = new ArrayList<>();
//        mvnoIds.add(1);
//        mvnoIds.add(getMvnoIdFromCurrentStaff());
//        if (getLoggedInUser().getLco()) {
//            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getLoggedInUser().getPartnerId());
//            else
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getBUIdsFromCurrentStaff(), getLoggedInUser().getPartnerId());
//
//        } else {
//            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds);
//            else
//                return entityRepository.findAllByNameOrEmailOrRoleByPartner(pageRequest, s1, s1, s1, s1, s1, getLoggedInUserPartnerId(), mvnoIds, getBUIdsFromCurrentStaff());
//        }
//    }
//
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
//
//    public List<StaffUser> getByServiceAreaId(Integer long1) {
//        return entityRepository.getByServiceAreaId(long1);
//    }
//
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
//    public List<StaffUser> getByServiceAreaIdAndTeamId(Integer serviceAreaId, Long teamId) {
//        QStaffUser qStaffUser = QStaffUser.staffUser;
//        JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
//        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
//        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
//        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
//        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//
//
//        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false).and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
//        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser).where(qStaffUser.id.in(queryForStaffService.select(qStaffUserServiceAreaMapping.staffId).from(qStaffUserServiceAreaMapping).where(qStaffUserServiceAreaMapping.serviceId.eq(serviceAreaId))).and(qStaffUser.id.in(queryForStaffTeam.select(qTeamUserMapping.staffId.intValue()).from(qTeamUserMapping).where(qTeamUserMapping.teamId.eq(teamId)))).and(booleanExpression)).fetch();
//        return staffUserList;
//    }
//
//    public List<StaffUser> getByTeamId(Long teamId) {
//        QStaffUser qStaffUser = QStaffUser.staffUser;
//        JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
//        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
//        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
//        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
//
//        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false).and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
//        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser).where(qStaffUser.id.in(queryForStaffTeam.select(qTeamUserMapping.staffId.intValue()).from(qTeamUserMapping).where(qTeamUserMapping.teamId.eq(teamId))).and(booleanExpression)).fetch();
//        return staffUserList;
//    }
//
//
////    public List<StaffUser> getByTeam(Long teamId) {
////        QStaffUser qStaffUser = QStaffUser.staffUser;
////        Teams teams = teamsService.getRepository().findById(teamId).get();
////        //JPAQuery<StaffUserServiceAreaMapping> queryForStaffService = new JPAQuery<>(entityManager);
////        JPAQuery<TeamUserMapping> queryForStaffTeam = new JPAQuery<>(entityManager);
////        JPAQuery<StaffUser> queryForStaff = new JPAQuery<>(entityManager);
////        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
////        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
////
////
////        BooleanExpression booleanExpression = qStaffUser.isDelete.eq(false)
////                .and(qStaffUser.isNotNull()).and(qStaffUser.status.eq(CommonConstants.ACTIVE_STATUS));
////        List<StaffUser> staffUserList = queryForStaff.select(qStaffUser).from(qStaffUser)
////                .where(queryForStaffTeam.in())
////                .fetch();
////        return staffUserList;
////    }
//
//    public StaffUser resetPassword(@Valid PasswordDto passwordDto) {
//        String SUBMODULE = MODULE + " [resetPassword()] ";
//        try {
//            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
//                throw new IllegalArgumentException("Please enter valid password. New password and confirm password value must be same.");
//            } else if (passwordDto.getUserName() != null) {
//                List<StaffUser> staffUserList = this.getStaffUserFromUsername(passwordDto.getUserName());
//                if (staffUserList != null && staffUserList.size() > 0) {
//                    StaffUser staffUser = staffUserList.get(0);
//                    if (staffUser != null) {
//                        PasswordEncoder encoder = new BCryptPasswordEncoder();
//                        staffUser.setNewpassword(encoder.encode(passwordDto.getNewPassword()));
//                        staffUser.setPassword(staffUser.getNewpassword());
//                        entityRepository.save(staffUser);
//                        return staffUser;
//                    }
//                } else {
//                    throw new IllegalArgumentException("Please enter valid username. No record found for this one.");
//                }
//            }
//        } catch (Throwable e) {
//            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
//            throw e;
//        }
//        return null;
//    }
//
//    @Override
//    public StaffUser get(Integer id) {
//        StaffUser staffUser = super.get(id);
//        if (getMvnoIdFromCurrentStaff() == null) return staffUser;
//        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (staffUser.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || staffUser.getMvnoId().intValue() == 1))
//            return staffUser;
//        return null;
//    }
//
//    public StaffUser getStaffForUpdateAndDelete(Integer id) {
//        StaffUser staffUser = get(id);
//        if (staffUser == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == staffUser.getMvnoId().intValue()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return staffUser;
//    }
//
//    public void changestatus(String status, String username) {
//        String newStatus = status;
//        StaffUser staffUser = entityRepository.findStaffUserByUsername(username);
//        if (staffUser != null) {
//            if (!staffUser.getStatus().equals(newStatus)) {
//                Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.STAFF_STATUS_CHANGE_TEMPLATE);
//                if (optionalTemplate.isPresent()) {
//                    if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                        StaffStatusChangeMessage statusMessage = new StaffStatusChangeMessage(RabbitMqConstants.STAFF_STATUS_CHANGE_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, staffUser, newStatus);
//                        statusMessage.setEmailConfigured(true);
//                        statusMessage.setSmsConfigured(true);
//                        Gson gson = new Gson();
//                        gson.toJson(statusMessage);
//                        messageSender.send(statusMessage, RabbitMqConstants.QUEUE_STAFF_SEND_STATUS);
//                    }
//                }
//            }
//        }
//    }
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
//
//    //Get All StaffUserIds By ServiceAreas
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
////                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
////                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff()))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
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
//
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
//
//    public StaffUserViewPojo dtoToViewdto(StaffUser staffUser) {
//        StaffUserViewPojo staffUserViewPojo = new StaffUserViewPojo();
//        staffUserViewPojo.setId(staffUser.getId());
//        staffUserViewPojo.setFirstname(staffUser.getFirstname());
//        staffUserViewPojo.setLastname(staffUser.getLastname());
//        staffUserViewPojo.setUsername(staffUser.getUsername());
//        return staffUserViewPojo;
//
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String username) {
//        boolean flag = false;
//        if (username != null) {
//            username = username.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(username);
//            else
//                count = entityRepository.duplicateVerifyAtSave(username, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public StaffUserPojo findByStaffId(Integer staffId) throws Exception {
//        StaffUser staffUser = getRepository().findById(staffId).orElse(null);
//        if (staffUser != null) {
//            return convertStaffUserModelToStaffUserPojo(staffUser);
//        }
//        return null;
//    }
//
//    public List<StaffUser> getAllActiveEntitiesStaff() {
//        return entityRepository.findByIsDeleteIsFalseOrderByIdDesc()
//                .stream().filter(x -> x.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || x.getMvnoId() == null || x.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
//    }
//
//    public List<StaffUserAllPojo> convertResponseModelIntoStaffUserAllPojo(List<StaffUser> staffUserList) throws Exception {
//        String SUBMODULE = MODULE + " [convertResponseModelIntoStaffUserAllPojo()] ";
//        List<StaffUserAllPojo> staffUserListRes = new ArrayList<>();
//        try {
//            if (staffUserList != null && staffUserList.size() > 0) {
//                for (StaffUser staffUser : staffUserList) {
//                    StaffUserAllPojo pojo = new StaffUserAllPojo();
//                    pojo.setId(staffUser.getId());
//                    pojo.setUsername(staffUser.getUsername());
//                    staffUserListRes.add(pojo);
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return staffUserListRes;
//
//
//    }
//
//    public List<StaffUser> findAllByParentStaffId(Integer parentStaffId) {
//        return entityRepository.findAllByParentStaffId(parentStaffId);
//    }

    @Override
    protected JpaRepository<StaffUser, Integer> getRepository() {
        return null;
    }

    @Override
    public StaffUser get(Integer id) {
        StaffUser staffUser = entityRepository.findById(id).orElse(null);
        if (getMvnoIdFromCurrentStaff() == null) return staffUser;
        if (getMvnoIdFromCurrentStaff().intValue() == 1 || (staffUser.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || staffUser.getMvnoId().intValue() == 1))
            return staffUser;
        return null;
    }

    public void saveStaffuserData(ConsumerRecord<String, Object> records) {
        ObjectMapper objectMapper = new ObjectMapper();
        String recordsInJson = (String) records.value();
        try {
            SaveStaffUserSharedDataMessage message = objectMapper.readValue(recordsInJson, SaveStaffUserSharedDataMessage.class);
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
            staffUser.setEmail(message.getEmail());
            staffUser.setPhone(message.getPhone());
            staffUser.setParentStaffId(message.getParentStaffId());
            staffUser.setLcoId(message.getLcoId());
            if(!message.getLast_login_time().equalsIgnoreCase("null") ) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setBranchId(message.getBranchId());
//            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
            if(message.getServiceAreaNameList()!=null){
                for(ServiceArea serviceArea : message.getServiceAreaNameList()){
                    StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                    staffUserServiceAreaMapping.setStaffId(message.getId());
                    staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
                    staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMapping);
                }
            }
            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            if(message.getTeamsList().size()>0){
                for (Teams item : message.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId(message.getId().longValue());
                    teamUserMappingsRepository.save(teamUserMapping);
                }
            }
            entityRepository.save(staffUser);
           log.info("Staff User created successfully with name " + message.getUsername());

        }catch (Exception e){
            log.error("Error message : "+e.getMessage());
        }
    }

    public void updateStaffUserData(ConsumerRecord<String, Object> records) {
        ObjectMapper objectMapper = new ObjectMapper();
        String recordsInJson = (String) records.value();
        try {
            UpdateStaffUserSharedDataMessage message = objectMapper.readValue(recordsInJson, UpdateStaffUserSharedDataMessage.class);
            StaffUser staffUser =entityRepository.findById(message.getId()).orElse(null);
            if(staffUser != null){
                staffUser.setId(message.getId());
                staffUser.setUsername(message.getUsername());
                staffUser.setPassword(message.getPassword());
                staffUser.setFirstname(message.getFirstname());
                staffUser.setLastname(message.getLastname());
                staffUser.setStatus(message.getStatus());
                staffUser.setLcoId(message.getLcoId());
                staffUser.setParentStaffId(message.getParentStaffId());
                staffUser.setPhone(message.getPhone());
                staffUser.setEmail(message.getEmail());
                if(!message.getLast_login_time().equalsIgnoreCase("null") ) {
                    staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
                } else {
                    staffUser.setLast_login_time(null);
                }
                staffUser.setPartnerid(message.getPartnerid());
                staffUser.setRoles(message.getRoles());
                staffUser.setIsDelete(message.getIsDelete());
                if(message.getTeamsList().size()>0){
                    List<TeamUserMapping> oldteamMapping = new ArrayList<>();
                    oldteamMapping = teamUserMappingsRepository.findAllByStaffId(staffUser.getId().longValue());
                    teamUserMappingsRepository.deleteAll(oldteamMapping);
                    for (Teams item : message.getTeamsList()) {
                        TeamUserMapping teamUserMapping = new TeamUserMapping();
                        teamUserMapping.setTeamId(item.getId());
                        teamUserMapping.setStaffId(message.getId().longValue());
                        teamUserMappingsRepository.save(teamUserMapping);
                    }
                }
                staffUser.setMvnoId(message.getMvnoId());
                staffUser.setBranchId(message.getBranchId());
//            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
                if(message.getServiceAreaNameList()!=null){
                    StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
                    List<StaffUserServiceAreaMapping> oldStaffserviceareaMapping = new ArrayList<>();
                    oldStaffserviceareaMapping = staffUserServiceAreaMappingRepository.findAllByStaffId(staffUser.getId());
                    staffUserServiceAreaMappingRepository.deleteAll(oldStaffserviceareaMapping);

                    for(ServiceArea serviceArea : message.getServiceAreaNameList()){
                        StaffUserServiceAreaMapping staffUserServiceAreaMappings = new StaffUserServiceAreaMapping();
                        staffUserServiceAreaMappings.setStaffId(message.getId());
                        staffUserServiceAreaMappings.setServiceId(serviceArea.getId().intValue());
                        staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMappings);
                    }
                }
                staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
                entityRepository.save(staffUser);
                ApplicationLogger.logger.info("Staff User created successfully with name " + message.getUsername());
            }
        }catch (Exception e){
            log.error("Error message : " + e.getMessage());
        }
    }


    public List<StaffUser> findAllStaffUserByEmail(String email){
        List<StaffUser> staffUserList = entityRepository.findAllByEmailAndIsDeleteFalse(email);
        return staffUserList;
    }
}
