package com.savbill.commonGateway.moules.MasterManagement.Department.controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.Department.domain.Department;
import com.savbill.commonGateway.moules.MasterManagement.Department.dto.DepartmentPojo;
import com.savbill.commonGateway.moules.MasterManagement.Department.service.DepartmentService;
//import liquibase.pro.packaged.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.DEPARTMENT)
public class DepartmentController extends ApiBaseController {
    private static String MODULE = " [DepartmentController] ";

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    DepartmentService departmentService;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private Tracer tracer;

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.DEPARTMENT + "\")")
    @PostMapping("/list")
    public ResponseEntity<?> getDepartmentList(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<Department> departmentList = null;
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());

        long startTime = System.nanoTime();  // Start measuring

        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            departmentList = departmentService.getList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters());
            if (null != departmentList && 0 < departmentList.getSize())
                response.put("departmentList", departmentService.convertResponseModelIntoPojo(departmentList.getContent()));
            else response.put("departmentList", new ArrayList<>());
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetch Department List"+ LogConstants.REQUEST_BY + departmentService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + respCode );
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch Department List"+ LogConstants.REQUEST_BY + departmentService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+ LogConstants.LOG_ERROR + ce.getMessage() +LogConstants.LOG_STATUS_CODE+ respCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch All Department list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response, departmentList);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.DEPARTMENT + "\")")
    @PostMapping("/search")
    public ResponseEntity<?> searchDepartment(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<Department> departmentList = null;
        MDC.put("type", "serach");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Department using keyword : "+requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
                return apiResponse(respCode, response);
            }
            departmentList = departmentService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (departmentList.isEmpty()) {
                Response = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");

                response.put("departmentList", new ArrayList<>());

                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Department using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+Response);
                return apiResponse(Response, response, departmentList);

            }
            if (null != departmentList && 0 < departmentList.getSize()) {
                response.put("departmentList", departmentService.convertResponseModelIntoPojo(departmentList.getContent()));
            } else {
                response.put("departmentList", new ArrayList<>());
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Department using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
            respCode = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            respCode = ce.getErrCode();

            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search Department using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (RuntimeException re) {
            re.printStackTrace();
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Department using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception e) {
            e.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search Department using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response, departmentList);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDepartmentList(HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<Department> departmentList = departmentService.getAllActiveEntities();
            response.put("departmentList", departmentService.convertResponseModelIntoPojo(departmentList).stream()
                    .sorted(Comparator.comparing(DepartmentPojo::getId).reversed()).collect(Collectors.toList()));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetch All Department list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Department list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch All Department list"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.DEPARTMENT + "\")")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            Department department = departmentService.get(id);
            if (department == null) {
                respCode = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Department Not Found!");
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch Department" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+respCode);
                return apiResponse(respCode, response);
            } else {
                response.put("departmentData", departmentService.convertDepartmentModelToDepartmentPojo(department));
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Department" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);

            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetch Department "+LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Departemnt" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.DEPARTMENT_CREATE + "\")")
    @PostMapping("/save")
    public ResponseEntity<?> createDepartment(@Valid @RequestBody DepartmentPojo pojo, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;

        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            departmentService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            boolean flag = departmentService.duplicateVerifyAtSave(pojo.getName());
            String url = req.getRequestURI();
            if (pojo.getName().length() > 250) {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Department" +LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Input size is Exceeded"+LogConstants.LOG_STATUS_CODE+respCode);
                response.put(APIConstants.ERROR_TAG, "Input size is Exceeded");
                return apiResponse(respCode, response, null);
            } else {
                if (flag) {
                    pojo = departmentService.save(pojo);
                    response.put("department", pojo);
                    response.put(APIConstants.MESSAGE, "Successfully Created");
                    respCode = APIConstants.SUCCESS;
                    createDataSharedService.sendEntitySaveDataForAllMicroService(pojo);
                    req.getRequestURL();
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create Department" +LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
                }else {
                    respCode = HttpStatus.NOT_ACCEPTABLE.value();
                    //logger.warn("Country Already Exists"+pojo);
                    response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_DEPARTMENT_AVAILABLE);
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create Department" +LogConstants.LOG_BY_NAME+pojo.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE+respCode);
                    return apiResponse(respCode, response, null);
//				logger.error("Already Exists: Data={}; request: { From{}}; Response : {{}}",pojo, req.getHeader("requestFrom"),respCode);
//
                }
                  }
        } catch (DataIntegrityViolationException exc) {
            exc.printStackTrace();
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, "Input Size Exceeded");
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create Department"  +LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + exc.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create Department" +LogConstants.LOG_BY_NAME+pojo.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create Department"  +LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\""
//            + AclConstants.OPERATION_COUNTRY_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.DEPARTMENT_EDIT+ "\")")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@Valid @RequestBody DepartmentPojo pojo, @PathVariable Integer id,
                                              HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            pojo.setId(id);
            departmentService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            boolean flag = departmentService.duplicateVerifyAtEdit(pojo.getName(), pojo.getId());
            if (pojo.getName().length() > 250) {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, "Input size is Exceeded");
                return apiResponse(respCode, response, null);
            } else {
                if (flag) {
                    pojo = departmentService.update(pojo, req);
                    createDataSharedService.updateEntityDataForAllMicroService(pojo);
                    response.put("department", pojo);
                    response.put(APIConstants.MESSAGE, "Successfully Updated");
                    respCode = APIConstants.SUCCESS;
                } else {
                        respCode = HttpStatus.NOT_ACCEPTABLE.value();
                        response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_DEPARTMENT_AVAILABLE);
                        LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Department"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
                        return apiResponse(respCode, response, null);
                }
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Department"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update country"+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.DEPARTMENT_DELETE + "\")")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {

        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            Department department = departmentService.getDepartmentForUpdateAndDelete(id);
            if (department != null) {
                DepartmentPojo pojo = departmentService.convertDepartmentModelToDepartmentPojo(department);
                departmentService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                departmentService.deleteDepartment(id);
                createDataSharedService.updateEntityDataForAllMicroService(pojo);
                response.put(APIConstants.MESSAGE, "Successfully deleted");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Department"+LogConstants.LOG_BY_NAME+department.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
            } else {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Department"+LogConstants.LOG_BY_NAME+department.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Department By Id : "+id+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                respCode = HttpStatus.METHOD_NOT_ALLOWED.value();
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Department By Id :"+id+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
            } else {
                ex.printStackTrace();
                respCode = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Department By Id : "+id +LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(respCode, response);
    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
        ValidationData validationData = new ValidationData();
        if (null == filterList || 0 < filterList.size()) {
            validationData.setValid(false);
            validationData.setMessage("Please Provide Search Criteria");
            return validationData;
        }
        validationData.setValid(true);
        return validationData;
    }
}
