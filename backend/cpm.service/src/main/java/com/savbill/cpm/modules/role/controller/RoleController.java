package com.savbill.cpm.modules.role.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.modules.role.domain.Role;
import com.savbill.cpm.modules.role.mapper.RoleMapper;
import com.savbill.cpm.repository.common.StaffRolRelRepo;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.cpm.constants.MessageConstants;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.modules.role.model.RoleDTO;
import com.savbill.cpm.modules.role.service.RoleService;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.RoleMessage;

import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ROLE)
public class RoleController extends ExBaseAbstractController<RoleDTO> {

    @Autowired
    AuditLogService auditLogService;
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private StaffRolRelRepo staffRoleRelRepo;
    @Autowired
    CreateDataSharedService createDataSharedService;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    public RoleController(RoleService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RoleController]";
    }
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        return super.getAll(requestDTO, req);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = super.getEntityById(id, req);
        RoleDTO role = (RoleDTO) genericDataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId().longValue(), role.getRolename());
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        if(getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }

        if(getLoggedInUser().getLco())
            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
        else
            entityDTO.setLcoId(null);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean flag = roleService.duplicateVerifyAtSave(entityDTO.getRolename());
        if (flag) {
            genericDataDTO = super.save(entityDTO, result, authentication, req);
            RoleDTO role = (RoleDTO) genericDataDTO.getData();
            //send message
            RoleMessage roleMessage = new RoleMessage();
            if(role != null) {
                roleMessage.setId(role.getId());
                roleMessage.setRolename(role.getRolename());
                roleMessage.setStatus(role.getStatus());
                roleMessage.setIsDelete(role.getIsDelete());
                roleMessage.setSysRole(role.getSysRole());
                roleMessage.setMvnoId(role.getMvnoId());
                roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
                kafkaMessageSender.send(new KafkaMessageData(roleMessage,RoleMessage.class.getSimpleName() ));
//                messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
                Role roleEntity = roleService.convertRolePojoToRoleModel(role);
                createDataSharedService.sendEntitySaveDataForAllMicroService(roleEntity);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                        AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId(), role.getRolename());
            }

        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
            logger.error("Unable to Create  Role With name "+entityDTO.getRolename()+"    :  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            if(getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }

            if(getLoggedInUser().getLco())
                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
            else
                entityDTO.setLcoId(null);

            boolean flag = roleService.duplicateVerifyAtEdit(entityDTO.getRolename(), entityDTO.getId());
            if (flag) {
                Integer staffRoleCount = staffRoleRelRepo.findByRoleId(entityDTO.getId()).size();
                if(staffRoleCount == 0 || (staffRoleCount > 0 && entityDTO.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))) {
                    genericDataDTO = super.update(entityDTO, result, authentication, req);
                    if (genericDataDTO.getResponseCode() != 200) {
                        return genericDataDTO;
                    }
                    RoleDTO role = (RoleDTO) genericDataDTO.getData();
                    //send message
                    RoleMessage roleMessage = new RoleMessage();
                    roleMessage.setId(role.getId());
                    roleMessage.setRolename(role.getRolename());
                    roleMessage.setStatus(role.getStatus());
                    roleMessage.setIsDelete(role.getIsDelete());
                    roleMessage.setSysRole(role.getSysRole());
                    roleMessage.setMvnoId(role.getMvnoId());
                    roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
//                    messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
                    kafkaMessageSender.send(new KafkaMessageData(roleMessage,RoleMessage.class.getSimpleName() ));
                    Role roleEntity = roleService.convertRolePojoToRoleModel(role);
                    createDataSharedService.updateEntityDataForAllMicroService(roleEntity);
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                            AclConstants.OPERATION_ROLE_EDIT, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
                } else {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.ROLE_IN_USE);
                }
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
                logger.error("Unable to Update Role With "+roleService.getEntityById(entityDTO.getId())+" to "+entityDTO.getRolename()+"   Role With name "+entityDTO.getRolename()+"    :  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        
        
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody RoleDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = super.delete(entityDTO, authentication, req);
        RoleDTO role = (RoleDTO) genericDataDTO.getData();
        //send message
        RoleMessage roleMessage = new RoleMessage();
        roleMessage.setId(role.getId());
        roleMessage.setRolename(role.getRolename());
        roleMessage.setStatus(role.getStatus());
        roleMessage.setIsDelete(true);
        roleMessage.setSysRole(role.getSysRole());
        roleMessage.setMvnoId(role.getMvnoId());
        roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
//        messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
        kafkaMessageSender.send(new KafkaMessageData(roleMessage,RoleMessage.class.getSimpleName() ));
        Role roleEntity = roleService.convertRolePojoToRoleModel(role);
        createDataSharedService.deleteEntityDataForAllMicroService(roleEntity);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
                AclConstants.OPERATION_ROLE_DELETE, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
        return genericDataDTO;

    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<RoleDTO> list = roleService.getAllEntities();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }

        return genericDataDTO;
    }

//   // @Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(Integer page, Integer pageSize, Integer sortOrder, String sortBy, GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
    
  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
  @PostMapping("/searchrole")
  public GenericDataDTO search(@RequestBody PaginationRequestDTO requestDTO , HttpServletRequest req) {
            GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
            genericSearchDTO.setFilter(requestDTO.getFilters());
            return super.search(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortOrder(), requestDTO.getSortBy(), genericSearchDTO , req);
  }
    
    


//    @GetMapping("/role")
//    public GenericDataDTO getAllActiveRole() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            List<RoleDTO> roleList = roleService.convertResponseModelIntoPojo(roleService.getAllActiveEntities());
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setDataList(roleList);
//            genericDataDTO.setTotalRecords(roleList.size());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/save")
//    public GenericDataDTO createRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            pojo = roleService.saveEntity(pojo);
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/role/{id}")
//    public GenericDataDTO getRole(@PathVariable Integer id) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            RoleDTO pojo = roleService.convertRoleModelToRolePojo(roleService.get(id));
//            genericDataDTO = new GenericDataDTO();
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//            ;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
////
//    @PostMapping("/update")
//    public GenericDataDTO updateRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            pojo = roleService.updateEntity(pojo);
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//    @PostMapping("/delete")
//    public GenericDataDTO deleteRole(@RequestBody RoleDTO pojo) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            RoleService roleService = SpringContext.getBean(RoleService.class);
//            roleService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//            roleService.deleteEntity(pojo);
//            genericDataDTO.setData(pojo);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//
//    @GetMapping(value = "/role/excel")
//    public void roleExcel(HttpServletResponse response) throws Exception {
//        RoleService service = SpringContext.getBean(RoleService.class);
//        exportToExcel(service, response);
//    }
//
//    @GetMapping(value = "/role/pdf")
//    public void rolePDF(HttpServletResponse response) throws Exception {
//        RoleService service = SpringContext.getBean(RoleService.class);
//        exportToPDF(service, response);
//    }
//
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
}
