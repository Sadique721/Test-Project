//package com.savbill.ticketmanagement.core.modules.tickets.controller;
//
//
////import com.savbill.ticketmanagement.core.modules.common.LoggedInUser;
//
//import brave.Tracer;
//import brave.propagation.TraceContext;
//import com.savbill.ticketmanagement.core.constants.LogConstants;
//import com.savbill.ticketmanagement.core.constants.MenuConstants;
//import com.savbill.ticketmanagement.core.constants.MessageConstants;
//import com.savbill.ticketmanagement.core.controller.ExBaseAbstractController;
//import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
//import com.savbill.ticketmanagement.core.dto.PaginationRequestDTO;
//import com.savbill.ticketmanagement.core.exceptions.CustomValidationException;
//import com.savbill.ticketmanagement.core.exceptions.DataNotFoundException;
//import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
//import com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
//import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
//import com.savbill.ticketmanagement.core.modules.tickets.domain.Case;
//import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketReasonCategory;
//import com.savbill.ticketmanagement.core.modules.tickets.mapper.TicketReasonCategoryMapper;
//import com.savbill.ticketmanagement.core.modules.tickets.model.CaseDTO;
//import com.savbill.ticketmanagement.core.modules.tickets.model.TicketReasonCategoryDTO;
//import com.savbill.ticketmanagement.core.modules.tickets.service.TicketReasonCategoryService;
//import com.savbill.ticketmanagement.core.modules.utils.APIConstants;
//import com.savbill.ticketmanagement.core.modules.utils.Constants;
//import com.savbill.ticketmanagement.core.modules.utils.UpdateDiffFinder;
//import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
//import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
//import org.apache.log4j.Logger;
//import org.apache.log4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
////import org.springframework.security.access.prepost.PreAuthorize;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.context.SecurityContext;
////import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Objects;
//
//@RestController
//@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.TICKET_REASON_CATEGORY)
//public class TicketReasonCategoryController extends ExBaseAbstractController<TicketReasonCategoryDTO> {
//
//    @Autowired
//    TicketReasonCategoryService ticketReasonCategoryService;
//
//    @Autowired
//    TicketReasonCategoryMapper ticketReasonCategoryMapper;
//
//    //    @Autowired
////    private AuditLogService auditLogService;
//    private final Logger log = Logger.getLogger(TicketReasonCategoryController.class);
//
//    public TicketReasonCategoryController(TicketReasonCategoryService ticketReasonCategoryService) {
//        super(ticketReasonCategoryService);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "{TicketReasonSubCategoryController}";
//    }
//
//    @Autowired
//    private Tracer tracer;
//
//    @Override
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ADD + "\")")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonCategory.TICKET_REASON_CATEGORY_CREATE + "\")")
//    public GenericDataDTO save(@Valid @RequestBody TicketReasonCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Create");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//        Integer RESP_CODE = APIConstants.FAIL;
//        try {
////            entityDTO.setInwardNumber(CommonUtils.getResponse("","",null,5));
////            entityDTO.unusedQty=entityDTO.getQty();
////            entityDTO.setUsedQty(0L);
//
//            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
//            }
//            if (getMvnoIdFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
//                entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
//            }
//
//            if (getLoggedInUser().getLco())
//                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
//            else
//                entityDTO.setLcoId(null);
//            if (Objects.isNull(entityDTO.getIsDefaultProblemDomain())) {
//                entityDTO.setIsDefaultProblemDomain(false);
//            }
//            boolean flag = ticketReasonCategoryService.duplicateVerifyAtSave(entityDTO.getCategoryName());
//            if (flag) {
//                TicketReasonCategoryDTO ticketReasonCategoryDTO = ticketReasonCategoryService.saveEntity(entityDTO);
//                genericDataDTO.setData(ticketReasonCategoryDTO);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage("Success");
//                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, ticketReasonCategoryDTO.getId(), ticketReasonCategoryDTO.getCategoryName());
//                RESP_CODE = APIConstants.SUCCESS;
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "reason Category with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            }
//        } catch (Exception ex) {
//            if (ex instanceof DataNotFoundException) {
////                ApplicationLogger.logger.error(getModuleNameForLog() + "/ [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage("Not Found");
//                RESP_CODE = HttpStatus.NOT_FOUND.value();
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket reason Category " + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            } else if (ex instanceof CustomValidationException) {
////                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            } else {
////                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            }
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }
//
//    @Override
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_EDIT + "\")")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonCategory.TICKET_REASON_CATEGORY_EDIT + "\")")
//    public GenericDataDTO update(@Valid @RequestBody TicketReasonCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Update");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//        Integer RESP_CODE = APIConstants.FAIL;
//        try {
//            TicketReasonCategoryDTO dtoData = ticketReasonCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
//            boolean flag = ticketReasonCategoryService.duplicateVerifyAtEdit(entityDTO.getCategoryName(), entityDTO.getId().intValue());
//            if (flag) {
//                if (Objects.isNull(entityDTO.getIsDefaultProblemDomain())) {
//                    entityDTO.setIsDefaultProblemDomain(false);
//                }
//                TicketReasonCategory olddata = ticketReasonCategoryService.getRepository().getOne(entityDTO.getId());
//                TicketReasonCategoryDTO olddatadto = ticketReasonCategoryMapper.domainToDTO(olddata, new CycleAvoidingMappingContext());
//                genericDataDTO = super.update(entityDTO, result, authentication, req);
//                if (olddatadto != null) {
//                    log.info("TicketReasonCategory update details: " + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO));
//                }
//                RESP_CODE = APIConstants.SUCCESS;
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
//                RESP_CODE = (HttpStatus.NOT_ACCEPTABLE.value());
//                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            }
//        } catch (Exception ex) {
//            if (ex instanceof DataNotFoundException) {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage("Not Found");
//                RESP_CODE = HttpStatus.NOT_FOUND.value();
//                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            } else if (ex instanceof CustomValidationException) {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//            }
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }
//
//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_VIEW + "\")")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonCategory.TICKET_REASON_CATEGORY + "\")")
//    @PostMapping(value = "/searchAll")
//    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Search");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//
//
//        Integer RESP_CODE = APIConstants.FAIL;
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = ticketReasonCategoryService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
//            RESP_CODE = APIConstants.SUCCESS;
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Ticket reason Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Ticket reason Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Ticket reason Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }
//
//    /* this API Is not used anywhare in ticket hence commenting the api
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_VIEW + "\")")
//    @GetMapping(value = "/getReasonCategoryByCustomer")
//    public GenericDataDTO getReasonCategoryByCustomer(@RequestParam(name = "customerId") Integer customerId) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(ticketReasonCategoryService.getReasonCategoryByCustomer(customerId));
//            logger.info("Fetching all cases by resasone category by customer id "+customerId+":  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to fetch ticket category by customer   "+customerId+":  request: { From : {}}; Response : {{}{};Exception:{}}",getModuleNameForLog(), genericDataDTO.getResponseCode(),ce.getStackTrace());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to fetch ticket category by customer   "+customerId+":  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    */
//
//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_VIEW + "\")")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonCategory.TICKET_REASON_CATEGORY + "\")")
//    @GetMapping(value = "/getAllActiveReasonCatgory")
//    public GenericDataDTO getAllActiveReasonCatgory(HttpServletRequest req) {
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Fetch");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//        Integer RESP_CODE = APIConstants.FAIL;
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(ticketReasonCategoryService.getAllActiveReasonCategory());
//            RESP_CODE = APIConstants.SUCCESS;
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With name" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With name" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With name" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<TicketReasonCategoryDTO> list = ticketReasonCategoryService.getAllEntities();
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(list.size());
//
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Failed to load data");
//        }
//
//        return genericDataDTO;
//    }
//
//    public LoggedInUser getLoggedInUser() {
//        LoggedInUser loggedInUser = null;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
//            }
//        } catch (Exception e) {
//            //ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
//        }
//        return loggedInUser;
//    }
//
//   /* This API is managed from the APIGateWay side hence to reduce RQ Call will commenting the api
//
//
//    @GetMapping(value = "/getActiveServiceForSubscribers")
//    public GenericDataDTO getAllActiveServicesForCustomers(@RequestParam(name = "customerId") Integer customerId) {
//        MDC.put("type", "Fetch");
//        Integer RESP_CODE = APIConstants.FAIL;
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            //genericDataDTO.setDataList(ticketReasonCategoryService.getActiveServiceForSubscribers(customerId));
//            logger.info("Fetching all active reasone categories :  request: { From : {},}; Response : {Code:{},Message{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to get all reasone categories  :  request: { From : {}}; Response : {{}{};Exception:{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),ce.getStackTrace());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Unable to get all reasone categories :  request: { From : {},}; Response : {{}{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//  */
//
//    @PostMapping(value = "/getReasonCategoryByActiveServices")
//    public GenericDataDTO getReasonCategoryByActiveServices(@RequestBody List<Integer> serviceLists, HttpServletRequest req) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Search");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(ticketReasonCategoryService.getReasonCategoryByActiveServices(serviceLists));
//            RESP_CODE = APIConstants.SUCCESS;
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reasons Category With Active service" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            RESP_CODE = ce.getErrCode();
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With Active service" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With Active service" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonCategory.TICKET_REASON_CATEGORY_DELETE + "\")")
//    @PostMapping(value = "/delete")
//    public GenericDataDTO delete(@RequestBody TicketReasonCategoryDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        TraceContext traceContext = tracer.currentSpan().context();
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Delete");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//        Long reasone_cat_id = entityDTO.getId();
//        try {
//            TicketReasonCategoryDTO dtoData = ticketReasonCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
//            Boolean flag = ticketReasonCategoryService.getUniqueCategory(reasone_cat_id);
//            if (!flag) {
//                super.delete(entityDTO, authentication, req);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                RESP_CODE = APIConstants.SUCCESS;
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
//                RESP_CODE = HttpStatus.NOT_MODIFIED.value();
//                genericDataDTO.setResponseMessage("Problem Domain Already in use");
//                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "reason Category with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            }
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//
//        return genericDataDTO;
//    }
//
//    @GetMapping(value = "/isReasonCategoryDefault")
//    public GenericDataDTO isReasonCategoryDefault(@RequestParam Integer serviceId, HttpServletRequest req) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Fetch");
//        MDC.put("userName", getLoggedInUser().getFirstName());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
//
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        boolean flag = false;
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//
//            List<TicketReasonCategory> ticketReasonCategoryList = ticketReasonCategoryService.isReasonCategoryDefault(serviceId);
//            if (!ticketReasonCategoryList.isEmpty()) {
//                flag = true;
//            }
//
//            genericDataDTO.setData(flag);
//            genericDataDTO.setDataList(ticketReasonCategoryList);
//            RESP_CODE = APIConstants.SUCCESS;
//            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "finding given service id have defualt problem domain" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (CustomValidationException ce) {
//
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            RESP_CODE = ce.getErrCode();
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "finding given service id have defualt problem domain" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "finding given service id have defualt problem domain" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }
//
//
//}
