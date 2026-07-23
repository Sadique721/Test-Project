package com.savbill.taskmanagement.core.modules.ServiceArea.controller;


//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;

//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SERVICE_AREA)
public class ServiceAreaController { //extends ExBaseAbstractController<ServiceAreaDTO> {
//    public ServiceAreaController(ServiceAreaService service) {
//        super(service);
//    }
//
//    private static String MODULE = " [ServiceAreaController] ";
//    private static final Logger logger= LoggerFactory.getLogger(ServiceAreaController.class);
//    @Autowired
//    private ServiceAreaService serviceAreaService;
//    @Autowired
//    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
//
//    @Autowired
//    private AuditLogService auditLogService;
//    @Autowired
//    private MessageSender messageSender;
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[ServiceAreaController]";
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.getAllEntities());
//            genericDataDTO.setTotalRecords(serviceAreaService.getAllEntities().size());
//            logger.info("Fetching Sevice area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to fetch data without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @GetMapping("/all/byreasonconfig/{caseReasonId}")
//    public GenericDataDTO getAllServiceAreaForCaseReasonConfig(@PathVariable Long caseReasonId,HttpServletRequest req) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllServiceAreaForCaseReasonConfig()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return GenericDataDTO.getGenericDataDTO(serviceAreaService.getAllServiceAreaForCaseReasonConfig(caseReasonId));
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//
//        }
//        return genericDataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody ServiceAreaDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Delete");
//        try {
//            serviceAreaService.validateServiceAreaInventory(entityDTO);
//            boolean flag = serviceAreaService.deleteVerification(entityDTO.getId().intValue());
//            if (flag) {
//                dataDTO = super.delete(entityDTO, authentication, req);
//                ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//                if (serviceArea != null) {
//                    //send message
//                    ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
//                    serviceAreaMessage.setId(serviceArea.getId());
//                    serviceAreaMessage.setName(serviceArea.getName());
//                    serviceAreaMessage.setStatus(serviceArea.getStatus());
//                    serviceAreaMessage.setIsDeleted(true);
//                    serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
//                    serviceAreaMessage.setLatitude(serviceArea.getLatitude());
//                    serviceAreaMessage.setLongitude(serviceArea.getLongitude());
//                    serviceAreaMessage.setAreaId(serviceArea.getAreaid());
//                    this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA);
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_DELETE, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//                }
//                dataDTO.setResponseMessage("Successfully Deleted");
//                logger.info("Service Area With name " + serviceArea.getName() + " Deleted  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//
//            } else {
//                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//                dataDTO.setResponseMessage(DeleteContant.SERVICE_AREA_DELETE_EXIST);
//
//                logger.error("Unable to Delete Service Area With name " + entityDTO.getName() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), HttpStatus.METHOD_NOT_ALLOWED.value(), DeleteContant.SERVICE_AREA_DELETE_EXIST);
//
//            }
//        } catch (CustomValidationException e) {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(e.getMessage());
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ServiceAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        boolean flag = serviceAreaService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
//        ServiceArea oldname=serviceAreaService.getByID(entityDTO.getId());
//        if (flag) {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            String updatedValues = CommonUtils.getUpdatedDiff(oldname,entityDTO);
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//            if(serviceArea != null)
//            {
//            	 //send message
//            	  ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
//                  serviceAreaMessage.setId(serviceArea.getId());
//                  serviceAreaMessage.setName(serviceArea.getName());
//                  serviceAreaMessage.setStatus(serviceArea.getStatus());
//                  serviceAreaMessage.setIsDeleted(serviceArea.getIsDeleted());
//                  serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
//                  serviceAreaMessage.setLatitude(serviceArea.getLatitude());
//                  serviceAreaMessage.setLongitude(serviceArea.getLongitude());
//                  serviceAreaMessage.setAreaId(serviceArea.getAreaid());
//                  this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA);
//            	auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_EDIT, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//            }
//            dataDTO.setResponseMessage("Successfully Updated");
//            logger.info("Sevice Area With oldname "+updatedValues+" :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(MessageConstants.SERVICE_AREA_NAME_EXITS);
//            logger.error("Unable to Update Service Area With oldname "+oldname+":  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.SERVICE_AREA_NAME_EXITS);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody ServiceAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        boolean flag = serviceAreaService.duplicateVerifyAtSave(entityDTO.getName());
//        if (flag) {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            dataDTO = super.save(entityDTO, result, authentication, req);
//            ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//
//            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();
//
//            StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
//            staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
//            staffUserServiceAreaMapping.setStaffId(serviceAreaService.getLoggedInUserId());
//            staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
//            staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
//            staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping);
//
//            if (serviceAreaService.getLoggedInUserId() != 1) {
//                StaffUserServiceAreaMapping staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                staffUserServiceAreaMapping1.setServiceId(serviceArea.getId().intValue());
//                staffUserServiceAreaMapping1.setStaffId(1);
//                staffUserServiceAreaMapping1.setCreatedOn(LocalDateTime.now());
//                staffUserServiceAreaMapping1.setLastmodifiedOn(LocalDateTime.now());
//                staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping1);
//            }
//            staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
//
//            //send message
//            ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
//            serviceAreaMessage.setId(serviceArea.getId());
//            serviceAreaMessage.setName(serviceArea.getName());
//            serviceAreaMessage.setStatus(serviceArea.getStatus());
//            serviceAreaMessage.setIsDeleted(serviceArea.getIsDeleted());
//            serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
//            serviceAreaMessage.setLatitude(serviceArea.getLatitude());
//            serviceAreaMessage.setLongitude(serviceArea.getLongitude());
//            serviceAreaMessage.setAreaId(serviceArea.getAreaid());
//            this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_ADD, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//            dataDTO.setResponseMessage("Successfully Created");
//            logger.info("Service Area is created with name "+ entityDTO.getName()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(MessageConstants.SERVICE_AREA_NAME_EXITS);
//            logger.error("Unable to Create Service Area With name " +entityDTO.getName() +" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.SERVICE_AREA_NAME_EXITS);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        MDC.put("type", "Fetch");
//        ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_VIEW, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//        logger.info("Service  Search with Name "+serviceArea.getName()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        MDC.remove("type");
//        return dataDTO;
//
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    // Get All Service Area List By UserStaff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @GetMapping("/getAllServiceAreaByStaff")
//    public GenericDataDTO getAllServiceAreaByStaff(){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.getAllServiceAreaByStaffId());
//            logger.info("Fetching Service area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Service area list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//
//    //Get StaffIds by ServiceAreas
//    @GetMapping("/getStaffUserByServiceArea")
//    public GenericDataDTO getStaffUserByServiceArea() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            genericDataDTO.setDataList(staffUserService
//                    .getStaffUserByServiceArea());
//            genericDataDTO.setTotalRecords(staffUserService
//                    .getStaffUserByServiceArea().size());
//            logger.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//
//    @GetMapping("/viewStaffUserByServiceArea")
//    public GenericDataDTO viewStaffUserByServiceArea() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            genericDataDTO.setDataList(staffUserService
//                    .viewStaffUserByServiceArea());
//            genericDataDTO.setTotalRecords(staffUserService
//                    .viewStaffUserByServiceArea().size());
//            logger.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//
//
//    @GetMapping("/getPincodefromCity")
//    public GenericDataDTO getpincodefromcity(@RequestParam("id") Integer id) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.getPincodefromcity(id));
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//    @PostMapping("/getAllServicesByServiceAreaId")
//    public GenericDataDTO getAllServicesByServiceAreaId(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.getAllServicebyServiceAreaId(serviceAreaId));
//            logger.info("Fetching Service area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Service area list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//    @GetMapping("/serviceAreaListWhereBranchIsNotBind")
//    public GenericDataDTO getAllserviceAreaListWhereBranchIsNotBind(){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.serviceAreaIdListWhereBranchIsNotBind());
//            genericDataDTO.setTotalRecords(serviceAreaService.serviceAreaIdListWhereBranchIsNotBind().size());
//            logger.info("Fetching Service area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Service area list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
}
