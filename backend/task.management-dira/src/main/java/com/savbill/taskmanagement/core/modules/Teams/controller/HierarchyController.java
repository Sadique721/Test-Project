package com.savbill.taskmanagement.core.modules.Teams.controller;



import com.savbill.taskmanagement.core.controller.ExBaseAbstractController;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Teams.model.HierarchyDTO;
import com.savbill.taskmanagement.core.modules.Teams.service.HierarchyService;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.TEAMS_HIERARCHY)
public class HierarchyController extends ExBaseAbstractController<HierarchyDTO> {

    @Autowired
    HierarchyService hierarchyService;

//    @Autowired
//    AuditLogService auditLogService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private CaseService caseService;


    public HierarchyController(HierarchyService service) {
        super(service);
    }


    @Override
    public String getModuleNameForLog() {
        return "[Teams Hierarchy]";
    }

    private static final Logger logger = LoggerFactory.getLogger(HierarchyController.class);


//    public GenericDataDTO save(@Valid @RequestBody HierarchyDTO entityDTO, BindingResult bindingResult, Authentication authentication, HttpServletRequest httpServletRequest)throws Exception{
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//       try{
//        HierarchyDTO hierarchyDTO = hierarchyService.saveEntity(entityDTO);
//        genericDataDTO.setData(hierarchyDTO);
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");}
//       catch (Exception ex){
//           ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//           genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//           genericDataDTO.setResponseMessage(ex.getMessage());
//       }
//       return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")
//    @GetMapping("/hierarchy/all")
//    public GenericDataDTO getAllHierarchy() throws Exception {
//        MDC.put("type", "Fetch");
//        String SUBMODULE = " [getAllHierarchy()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(hierarchyService.getAllHierarchy());
//            logger.info("Fetching All Hierarchy :  request: { From : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable fetch heirarchy :  request: { From : {}}; Response : {{}{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_ADD + "\")")
//    @Override
//    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public GenericDataDTO save(@Valid @RequestBody HierarchyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() > 1) {
//            throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
//        }
//
//        if (getLoggedInUser().getLco())
//            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
//        else
//            entityDTO.setLcoId(null);
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            if (result.hasErrors()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
//                logger.error("Unable to create Heirarchy with name " + entityDTO.getHierarchyName() + "  :  request: { From : {}}; Response : {{};}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            ValidationData validation = validateSave(entityDTO);
//            if (!validation.isValid()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(validation.getMessage());
//                logger.error("Unable to create Heirarchy with name " + entityDTO.getHierarchyName() + " :  request: { From : {}}; Response : {{};}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            //           ApplicationLogger.logger.info(getModuleNameForLog() + " entityDto : " + entityDTO);
//            boolean flag = hierarchyService.duplicateVerifyAtSave(entityDTO.getEventName());
//            if (!flag) {
//                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
//                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
//                HierarchyDTO dtoData = hierarchyService.saveEntity(entityDTO);
//                genericDataDTO.setData(dtoData);
//                genericDataDTO.setTotalRecords(1);
//                logger.info("Creating to create Heirarchy with name " + entityDTO.getHierarchyName() + ":  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                //response.put(APIConstants.ERROR_TAG, MessageConstants.TAX_NAME_EXITS);
//                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
//                    genericDataDTO.setResponseMessage("User with multiple BU access is restricted from SAVE operations !!");
//                } else {
//                    genericDataDTO.setResponseMessage("Duplicate Entry already Exist!!");
//                }
//                //return apiResponse(RESP_CODE, response, null);
//                logger.error("Unable to create Heirarchy with name " + entityDTO.getHierarchyName() + ":  request: { From : {}, }; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
//            logger.error("Unable to create Heirarchy with name " + entityDTO.getHierarchyName() + "  :  request: { From : {}, }; Response : {{};Exception:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }


//    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public GenericDataDTO update(@Valid @RequestBody HierarchyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            if (result.hasErrors()) {
////               ApplicationLogger.logger.debug("Base Controller Error"+result.getFieldErrors());
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
//                return genericDataDTO;
//            }
//            ValidationData validation = validateUpdate(entityDTO);
//            if (!validation.isValid()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(validation.getMessage());
//                return genericDataDTO;
//            }
//
//            HierarchyDTO dtoData = hierarchyService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
////            entityDTO.setMvnoId(dtoData.getMvnoId());
//            genericDataDTO.setData(hierarchyService.updateEntity(entityDTO));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setTotalRecords(1);
//        } catch (Exception ex) {
//            if (ex instanceof DataNotFoundException) {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage("Not Found");
//            } else if (ex instanceof CustomValidationException){
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//            } else {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
//            }
//        }
//        return genericDataDTO;
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")
//    @PostMapping(value = "/search")
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        MDC.put("type", "Fetch");
//        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
//        try {
//
//            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please provide search criteria!");
//                logger.error("Unable to search heirarchy  :  request: { From : {},}; Response : {{}{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            if (null != pageSize && pageSize > MAX_PAGE_SIZE)
//                pageSize = MAX_PAGE_SIZE;
//            genericDataDTO = hierarchyService.search(filter.getFilter(), page, pageSize, sortBy, sortOrder);
//
//            if (null != genericDataDTO) {
//
//                if (genericDataDTO.getDataList().isEmpty()) {
//                    genericDataDTO = new GenericDataDTO();
//                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
//                    genericDataDTO.setResponseMessage("No Record Found!");
//                    genericDataDTO.setDataList(new ArrayList<>());
//                    genericDataDTO.setTotalRecords(0);
//                    genericDataDTO.setPageRecords(0);
//                    genericDataDTO.setCurrentPageNumber(1);
//                    genericDataDTO.setTotalPages(1);
//                    logger.info("Searching Heirarcht :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//                }
//                return genericDataDTO;
//
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
//                genericDataDTO.setResponseMessage("No Record Found!");
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Search Heirarchy  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = super.getEntityById(id, req);
//        HierarchyDTO hierarchyDTO = (HierarchyDTO) genericDataDTO.getData();
//        return genericDataDTO;
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")
    @GetMapping("/assignFromStaffList")
    public GenericDataDTO assignFromStaffList(@RequestParam(name = "nextAssignStaff") Integer nextAssignStaff, @RequestParam(name = "eventName") String eventName, @RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Assigned to next staff");
            //hierarchyService.assignFromStaffList(nextAssignStaff, eventName, entityId, isApproveRequest);
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setPageRecords(0);
            genericDataDTO.setCurrentPageNumber(1);
            genericDataDTO.setTotalPages(1);
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }

//    @PutMapping("/approveLead")
//    public GenericDataDTO approveLead(@RequestBody LeadReasonMgmtWfDTO leadReasonMgmtWfDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
//            genericDataDTO.setResponseMessage("Assigned to next staff");
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//            LeadMgmtWfDTO leadMgmtWfDTO = hierarchyService.convertLeadReasonMgmtWfDTOToLeadMgmtWfDTO(leadReasonMgmtWfDTO);
//            return hierarchyService.approveLead(leadMgmtWfDTO, genericDataDTO);
//
//
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }

//    @PostMapping("/assignFromStaffListForLead")
//    public GenericDataDTO assignFromStaffListForLead(@RequestParam(name = "nextAssignStaff") Integer nextAssignStaff, @RequestParam(name = "eventName") String eventName, @RequestBody LeadMgmtWfDTO leadMgmtWfDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
//            genericDataDTO.setResponseMessage("Assigned to next staff");
//            genericDataDTO.setData(hierarchyService.assignFromStaffListForLead(nextAssignStaff, eventName, leadMgmtWfDTO));
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setPageRecords(0);
//            genericDataDTO.setCurrentPageNumber(1);
//            genericDataDTO.setTotalPages(1);
//
//
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }


    @GetMapping("/getApprovalProgress")
    public GenericDataDTO getApprovalProgress(@RequestParam(name = "entityId") Long entityId, @RequestParam(name = "eventName") String eventName) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        MDC.put("type", "Fetch");
//        String staffname=hierarchyService.getApproveProgress(eventName,entityId);
        try {
            //genericDataDTO.setDataList(hierarchyService.getApproveProgress(eventName, entityId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());

            logger.info("Getting Approval Progress is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Approval Progress :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;

    }


//    @GetMapping("/getApprovalProgressForLead")
//    public GenericDataDTO getApprovalProgressForLead(@RequestParam(name = "mvnoId") Integer mvnoId, @RequestParam(name = "buId") Long buId, @RequestParam(name = "nextTeamHierarchyMappingId") Integer nextTeamHierarchyMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
////        String staffname=hierarchyService.getApproveProgress(eventName,entityId);
//        try {
//            genericDataDTO.setDataList(hierarchyService.getApprovalProgressForLead(mvnoId, buId, nextTeamHierarchyMappingId));
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
//            logger.info("Getting Approval Progress is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Approval Progress :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getStackTrace(), e);
        }
        return loggedInUser;
    }










//    @GetMapping("/reassignWorkflowGetStaffList")
//    public GenericDataDTO reassignWorkflowFetchDataList(@RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "eventName") String eventName) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("ReAssign Staff from  with id  " + entityId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return hierarchyService.reassignWorkflowGetStaffList(entityId, eventName);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to ReAssign Staff  with " + entityId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @GetMapping("/reassignWorkflow")
//    public GenericDataDTO reassignWorkflow(@RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "eventName") String eventName, @RequestParam(name = "assignToStaffId") Integer assignToStaffId, @RequestParam(name = "remark", required = false) String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("ReAssign Staff from  with id  " + entityId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return hierarchyService.reassignWorkflow(entityId, eventName, assignToStaffId, remark);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to ReAssign Staff  with " + entityId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }



}
