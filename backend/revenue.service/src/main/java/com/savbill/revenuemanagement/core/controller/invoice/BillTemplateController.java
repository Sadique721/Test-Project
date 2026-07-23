package com.savbill.revenuemanagement.core.controller.invoice;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.InvoiceController;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.invoice.XslManagementPojo;
import com.savbill.revenuemanagement.core.entity.debitdoc.XsltManagement;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.prepaid.XsltManagementService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.utils.CommonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class BillTemplateController  {

    private static final Logger logger = Logger.getLogger(InvoiceController.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    ClientServiceSrv clientServiceSrv;
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BILL_TEMPLATE_ALL + "\",\"" + AclConstants.OPERATION_BILL_TEMPLATE_VIEW + "\")")
   @PreAuthorize("validatePermission(\"" + MenuConstants.bill_template +  "\")")
    @PostMapping("/billTemplete/list")
    public ResponseEntity<?> getBillTemplateList(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req) throws Exception {
       TraceContext traceContext =tracer.currentSpan().context();
       MDC.put("type", "Fetch");
       MDC.put("userName", getLoggedInUser().getUsername());
       MDC.put("traceId",traceContext.traceIdString());
       MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<XsltManagement> templateList = null;
        try {
            XsltManagementService xsltManagementService = SpringContext.getBean(XsltManagementService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            templateList = xsltManagementService.getList(requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters());
            if (null != templateList && 0 < templateList.getSize()){
                for(XsltManagement xsltManagement :templateList){
                    xsltManagement.setMvnoName(mvnoRepository.getOne(xsltManagement.getMvnoId().longValue()).getName());
                }
                response.put("billRunlist", xsltManagementService.convertResponseModelIntoPojo(templateList.getContent()));
            }
            else{
                response.put("billRunlist", new ArrayList<>());
            }

            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch bill template list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"fetch bill template list"+  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"fetch bill template list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, templateList);
    }


   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BILL_TEMPLATE_ALL + "\",\"" + AclConstants.OPERATION_BILL_TEMPLATE_VIEW + "\")")
    @GetMapping("/billTempleteByType/{type}")
    public ResponseEntity<?> getBillTemplateListByType(@PathVariable String type,HttpServletRequest req) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        List<XsltManagement> templateList = null;
        try {
            XsltManagementService xsltManagementService = SpringContext.getBean(XsltManagementService.class);
            templateList = xsltManagementService.getListByType(type);
            if (!CollectionUtils.isEmpty(templateList))
                response.put("billRunlist",xsltManagementService.convertResponseModelIntoPojo(templateList));
            else
                response.put("billRunlist", new ArrayList<>());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch bill template list by type : " + type + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"fetch bill template list by type : " + type +  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"fetch bill template list by type : " + type + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    // @PreAuthorize("validatePermission(\"" +
    // AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" +
    // AclConstants.OPERATION_XSLT_MANAGEMENT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" +MenuConstants.create_bill_template +  "\")")
    @PostMapping("/billTemplete")
    public ResponseEntity<?> getCreateBillTemplet(@Valid @RequestBody XslManagementPojo pojo, HttpServletRequest req)
            throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {

            XsltManagementService xsltManagementService = SpringContext.getBean(XsltManagementService.class);
            xsltManagementService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            boolean flag = xsltManagementService.duplicateVerifyAtSave(pojo.getTemplatename());
            if (flag) {
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                    throw new CustomValidationException(APIConstants.FAIL,"Duplicate Value Exists!", null);
                if (!xsltManagementService.checkDuplicateByBuIdAndMvnoIdAndByTemplateType(pojo, false)) {
                    pojo = xsltManagementService.save(pojo);
                    response.put("xsltManage", pojo);
                    RESP_CODE = APIConstants.SUCCESS;
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch created template : " + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
                } else {
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    response.put(APIConstants.ERROR_TAG,"Given Template Type already Exists!");
                    logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch created template : " + pojo.getTemplatetype() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "template with same type already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return apiResponse(RESP_CODE, response, null);
                }

            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, "Bill Template Name Already Exist");
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetch created template : " + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "template with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Fetch created template : " + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Fetch created template : " + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    // @PreAuthorize("validatePermission(\"" +
    // AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" +
    // AclConstants.OPERATION_XSLT_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.edit_bill_template +  "\")")
    @PutMapping("/billTemplete/{id}")
    public ResponseEntity<?> updateBillTemplet(@Valid @RequestBody XslManagementPojo pojo, @PathVariable Integer id,
                                               HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {

            XsltManagementService xsltManagementService = SpringContext.getBean(XsltManagementService.class);
            pojo.setId(id);
            XsltManagement xls = xsltManagementService.get(id);
           // String updatedValues = CommonUtils.getUpdatedDiff(xsltManagementService.convertXslManagementModelToXslManagementPojo(xls), pojo);
          //  XsltManagement xsltManagement = new XsltManagement(xls);
            String updatedValues = CommonUtils.getUpdatedDiff(xsltManagementService.convertXslManagementModelToXslManagementPojo(xls), pojo);
            xsltManagementService.getEntityForUpdateAndDelete(id);
            xsltManagementService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            boolean flag = xsltManagementService.duplicateVerifyAtEdit(pojo.getTemplatename(), pojo.getId());
            if (flag) {

                if (pojo.getTemplatetype().equalsIgnoreCase(xls.getTemplatetype()) || !xsltManagementService.checkDuplicateByBuIdAndMvnoIdAndByTemplateType(pojo, true)) {
                    pojo = xsltManagementService.save(pojo);
                    response.put("xsltManage", pojo);
                    RESP_CODE = APIConstants.SUCCESS;
                    logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update bill template"+LogConstants.LOG_BY_NAME + pojo.getTemplatename()+LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ " updated bill template " + updatedValues + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                } else {
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    response.put(APIConstants.ERROR_TAG,"Given Template Type already Exists!");
                    logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update bill template"+LogConstants.LOG_BY_NAME + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return apiResponse(RESP_CODE, response, null);
                }
            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, "Unable to update");
                logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update bill template"+LogConstants.LOG_BY_NAME + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR + "Access denined for update operation " + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update bill template"+LogConstants.LOG_BY_NAME + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            //		ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update bill template"+LogConstants.LOG_BY_NAME + pojo.getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    // @PreAuthorize("validatePermission(\"" +
    // AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" +
    // AclConstants.OPERATION_XSLT_MANAGEMENT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.delete_bill_template + "\")")
    @DeleteMapping("/billTemplete/{id}")
    public ResponseEntity<?> deleteBillTemplet(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            XsltManagementService xsltManagementService = SpringContext.getBean(XsltManagementService.class);
            XsltManagement xsltManagement = xsltManagementService.getEntityForUpdateAndDelete(id);
            if (xsltManagement != null) {
                XslManagementPojo pojo = xsltManagementService
                        .convertXslManagementModelToXslManagementPojo(xsltManagement);
                xsltManagementService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                xsltManagementService.deleteXsltManagement(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, "termplate deleted Successfully");
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete bill template"+LogConstants.LOG_BY_NAME + xsltManagementService.getEntityForUpdateAndDelete(id).getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_XSLT_MANAGEMENT,
                // AclConstants.OPERATION_XSLT_MANAGEMENT_DELETE, req.getRemoteAddr(), null,
                // pojo.getId().longValue(), "");
            } else {
                logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete bill template"+LogConstants.LOG_BY_NAME + xsltManagementService.getEntityForUpdateAndDelete(id).getTemplatename() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                throw new CustomValidationException(APIConstants.FAIL, "Unable to delete bill templates with name ",
                        null);
            }

        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete bill template"+LogConstants.LOG_BY_NAME + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete bill template"+LogConstants.LOG_BY_NAME + id+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }
    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }
    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
//        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }
    public List<Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff error{},exception{}" ,APIConstants.FAIL,e.getStackTrace());
        }
        return mvnoIds;
    }
    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.bill_template +  "\")")
    @GetMapping("/billTemplete/{id}")
    public ResponseEntity<?> getBillTemplateListById(@PathVariable Integer id, HttpServletRequest req)
            throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            XsltManagementService xsltManagementService = SpringContext.getBean(XsltManagementService.class);
            XsltManagement billTemp = xsltManagementService.get(id);
            if (billTemp == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Template Not Found!");
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Bill Template by id "+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            } else {
                response.put("billRunlist",
                        xsltManagementService.convertXslManagementModelToXslManagementPojo(billTemp));
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Bill Template by id "+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_XSLT_MANAGEMENT,
            // AclConstants.OPERATION_XSLT_MANAGEMENT_VIEW, req.getRemoteAddr(), null,
            // billTemp.getId().longValue(), "");
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch Bill Template by id "+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch Bill Template by id "+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

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
