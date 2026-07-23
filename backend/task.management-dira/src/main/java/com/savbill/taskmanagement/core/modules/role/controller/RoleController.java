package com.savbill.taskmanagement.core.modules.role.controller;


//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;


//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ROLE)
public class RoleController  {
//
//    @Autowired
//    AuditLogService auditLogService;
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    private StaffRolRelRepo staffRoleRelRepo;
//
//    public RoleController(RoleService service) {
//        super(service);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[RoleController]";
//    }
//    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = super.getEntityById(id, req);
//        RoleDTO role = (RoleDTO) genericDataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId().longValue(), role.getRolename());
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//
//        if(getLoggedInUser().getLco())
//            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
//        else
//            entityDTO.setLcoId(null);
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        boolean flag = roleService.duplicateVerifyAtSave(entityDTO.getRolename());
//        if (flag) {
//            genericDataDTO = super.save(entityDTO, result, authentication, req);
//            RoleDTO role = (RoleDTO) genericDataDTO.getData();
//            //send message
//            RoleMessage roleMessage = new RoleMessage();
//            if(role != null) {
//                roleMessage.setId(role.getId());
//                roleMessage.setRolename(role.getRolename());
//                roleMessage.setStatus(role.getStatus());
//                roleMessage.setIsDelete(role.getIsDelete());
//                roleMessage.setSysRole(role.getSysRole());
//                roleMessage.setMvnoId(role.getMvnoId());
//                roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
//                messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                        AclConstants.OPERATION_ROLE_VIEW, req.getRemoteAddr(), null, role.getId(), role.getRolename());
//            }
//
//        } else {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
//            logger.error("Unable to Create  Role With name "+entityDTO.getRolename()+"    :  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody RoleDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            if(getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//
//            if(getLoggedInUser().getLco())
//                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
//            else
//                entityDTO.setLcoId(null);
//
//            boolean flag = roleService.duplicateVerifyAtEdit(entityDTO.getRolename(), entityDTO.getId());
//            if (flag) {
//                Integer staffRoleCount = staffRoleRelRepo.findByRoleId(entityDTO.getId()).size();
//                if(staffRoleCount == 0 || (staffRoleCount > 0 && entityDTO.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS))) {
//                    genericDataDTO = super.update(entityDTO, result, authentication, req);
//                    if (genericDataDTO.getResponseCode() != 200) {
//                        return genericDataDTO;
//                    }
//                    RoleDTO role = (RoleDTO) genericDataDTO.getData();
//                    //send message
//                    RoleMessage roleMessage = new RoleMessage();
//                    roleMessage.setId(role.getId());
//                    roleMessage.setRolename(role.getRolename());
//                    roleMessage.setStatus(role.getStatus());
//                    roleMessage.setIsDelete(role.getIsDelete());
//                    roleMessage.setSysRole(role.getSysRole());
//                    roleMessage.setMvnoId(role.getMvnoId());
//                    roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
//                    messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                            AclConstants.OPERATION_ROLE_EDIT, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
//                } else {
//                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    genericDataDTO.setResponseMessage(MessageConstants.ROLE_IN_USE);
//                }
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.ROLE_NAME_EXITS);
//                logger.error("Unable to Update Role With "+roleService.getEntityById(entityDTO.getId())+" to "+entityDTO.getRolename()+"   Role With name "+entityDTO.getRolename()+"    :  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//
//
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody RoleDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = super.delete(entityDTO, authentication, req);
//        RoleDTO role = (RoleDTO) genericDataDTO.getData();
//        //send message
//        RoleMessage roleMessage = new RoleMessage();
//        roleMessage.setId(role.getId());
//        roleMessage.setRolename(role.getRolename());
//        roleMessage.setStatus(role.getStatus());
//        roleMessage.setIsDelete(true);
//        roleMessage.setSysRole(role.getSysRole());
//        roleMessage.setMvnoId(role.getMvnoId());
//        roleMessage.setAclEntryDTOList(role.getAclEntryPojoList());
//        messageSender.send(roleMessage, RabbitMqConstants.QUEUE_ROLE);
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_ROLE,
//                AclConstants.OPERATION_ROLE_DELETE, req.getRemoteAddr(), null, entityDTO.getId().longValue(), entityDTO.getRolename());
//        return genericDataDTO;
//
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<RoleDTO> list = roleService.getAllEntities();
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(list.size());
//            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Failed to load data");
//            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//
//        }
//
//        return genericDataDTO;
//    }
//
////   // @Deprecated
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
////    @Override
////    public GenericDataDTO search(Integer page, Integer pageSize, Integer sortOrder, String sortBy, GenericSearchDTO filter) {
////        return super.search(page, pageSize, sortOrder, sortBy, filter);
////    }
//
//  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
//  @PostMapping("/searchrole")
//  public GenericDataDTO search(@RequestBody PaginationRequestDTO requestDTO) {
//            GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
//            genericSearchDTO.setFilter(requestDTO.getFilters());
//            return super.search(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortOrder(), requestDTO.getSortBy(), genericSearchDTO);
//  }
//
//
//
//
////    @GetMapping("/role")
////    public GenericDataDTO getAllActiveRole() {
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////
////        try {
////            RoleService roleService = SpringContext.getBean(RoleService.class);
////            List<RoleDTO> roleList = roleService.convertResponseModelIntoPojo(roleService.getAllActiveEntities());
////            genericDataDTO = new GenericDataDTO();
////            genericDataDTO.setDataList(roleList);
////            genericDataDTO.setTotalRecords(roleList.size());
////            genericDataDTO.setResponseCode(HttpStatus.OK.value());
////            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
////            genericDataDTO.setPageRecords(0);
////            genericDataDTO.setCurrentPageNumber(1);
////            genericDataDTO.setTotalPages(1);
////        } catch (CustomValidationException ce) {
////            ce.printStackTrace();
////            genericDataDTO = new GenericDataDTO();
////            ApplicationLogger.logger.error(ce.getMessage(), ce);
////            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
////            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
////            genericDataDTO.setTotalRecords(0);
////        }
////        return genericDataDTO;
////    }
//
////    @PostMapping("/save")
////    public GenericDataDTO createRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        try {
////            RoleService roleService = SpringContext.getBean(RoleService.class);
////            roleService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
////            pojo = roleService.saveEntity(pojo);
////            genericDataDTO = new GenericDataDTO();
////            genericDataDTO.setData(pojo);
////            genericDataDTO.setResponseCode(HttpStatus.OK.value());
////            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
////            genericDataDTO.setTotalRecords(1);
////            genericDataDTO.setPageRecords(0);
////            genericDataDTO.setCurrentPageNumber(1);
////            genericDataDTO.setTotalPages(1);
////        } catch (CustomValidationException ce) {
////            ce.printStackTrace();
////            genericDataDTO = new GenericDataDTO();
////            ApplicationLogger.logger.error(ce.getMessage(), ce);
////            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
////            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
////            genericDataDTO.setTotalRecords(0);
////        }
////        return genericDataDTO;
////    }
//
////    @GetMapping("/role/{id}")
////    public GenericDataDTO getRole(@PathVariable Integer id) throws Exception {
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        try {
////            RoleService roleService = SpringContext.getBean(RoleService.class);
////            RoleDTO pojo = roleService.convertRoleModelToRolePojo(roleService.get(id));
////            genericDataDTO = new GenericDataDTO();
////            genericDataDTO.setData(pojo);
////            genericDataDTO.setResponseCode(HttpStatus.OK.value());
////            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
////            genericDataDTO.setTotalRecords(1);
////            genericDataDTO.setPageRecords(0);
////            genericDataDTO.setCurrentPageNumber(1);
////            genericDataDTO.setTotalPages(1);
////            ;
////        } catch (CustomValidationException ce) {
////            ce.printStackTrace();
////            genericDataDTO = new GenericDataDTO();
////            ApplicationLogger.logger.error(ce.getMessage(), ce);
////            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
////            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
////            genericDataDTO.setTotalRecords(0);
////        }
////        return genericDataDTO;
////    }
//////
////    @PostMapping("/update")
////    public GenericDataDTO updateRole(@Valid @RequestBody RoleDTO pojo) throws Exception {
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        try {
////            RoleService roleService = SpringContext.getBean(RoleService.class);
////            roleService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
////            pojo = roleService.updateEntity(pojo);
////            genericDataDTO.setData(pojo);
////            genericDataDTO.setResponseCode(HttpStatus.OK.value());
////            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
////            genericDataDTO.setTotalRecords(1);
////            genericDataDTO.setPageRecords(0);
////            genericDataDTO.setCurrentPageNumber(1);
////            genericDataDTO.setTotalPages(1);
////        } catch (CustomValidationException ce) {
////            ce.printStackTrace();
////            genericDataDTO = new GenericDataDTO();
////            ApplicationLogger.logger.error(ce.getMessage(), ce);
////            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
////            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
////            genericDataDTO.setTotalRecords(0);
////        }
////        return genericDataDTO;
////    }
////
////    @PostMapping("/delete")
////    public GenericDataDTO deleteRole(@RequestBody RoleDTO pojo) throws Exception {
////        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        try {
////            RoleService roleService = SpringContext.getBean(RoleService.class);
////            roleService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
////            roleService.deleteEntity(pojo);
////            genericDataDTO.setData(pojo);
////            genericDataDTO.setResponseCode(HttpStatus.OK.value());
////            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
////            genericDataDTO.setTotalRecords(1);
////            genericDataDTO.setPageRecords(0);
////            genericDataDTO.setCurrentPageNumber(1);
////            genericDataDTO.setTotalPages(1);
////        } catch (CustomValidationException ce) {
////            ce.printStackTrace();
////            genericDataDTO = new GenericDataDTO();
////            ApplicationLogger.logger.error(ce.getMessage(), ce);
////            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
////            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
////            genericDataDTO.setTotalRecords(0);
////        }
////        return genericDataDTO;
////    }
////
////
////    @GetMapping(value = "/role/excel")
////    public void roleExcel(HttpServletResponse response) throws Exception {
////        RoleService service = SpringContext.getBean(RoleService.class);
////        exportToExcel(service, response);
////    }
////
////    @GetMapping(value = "/role/pdf")
////    public void rolePDF(HttpServletResponse response) throws Exception {
////        RoleService service = SpringContext.getBean(RoleService.class);
////        exportToPDF(service, response);
////    }
////
//    public LoggedInUser getLoggedInUser() {
//        LoggedInUser user = null;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
//            }
//        } catch (Exception e) {
//            user = null;
//        }
//        return user;
//    }
}
