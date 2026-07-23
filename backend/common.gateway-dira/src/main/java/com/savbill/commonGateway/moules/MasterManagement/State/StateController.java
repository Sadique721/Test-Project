package com.savbill.commonGateway.moules.MasterManagement.State;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.model.StatePojo;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.savbill.commonGateway.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@JaversSpringDataAuditable
@RequestMapping(UrlConstants.BASE_API_URL)
public class StateController extends ApiBaseController {

    @Autowired
    private Tracer tracer;

    @Autowired
    CountryService countryService;

    @Autowired
    StateService stateService;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    private final Logger LOGGER = LoggerFactory.getLogger(StateController.class);
    // State
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STATE_ALL + "\",\""
//            + AclConstants.OPERATION_STATE_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.STATE + "\")")
    @PostMapping("/state/list")
    public ResponseEntity<?> getStateList(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<State> stateList = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring

        try {
            StateService stateService = SpringContext.getBean(StateService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            stateList = stateService.getList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters());
            if (null != stateList && 0 < stateList.getSize()) response.put("stateList", stateService.convertResponseModelIntoPojo(stateList.getContent()));
            else
                response.put("stateList", new ArrayList<>());
             RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All State list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All State list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch All State list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return apiResponse(RESP_CODE, response, stateList);
    }

    @GetMapping("/state/all")
    public ResponseEntity<?> getAllStateList(HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();

        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        long startTime = System.nanoTime();  // Start measuring

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            StateService stateService = SpringContext.getBean(StateService.class);
            List<StatePojo> stateList = stateService.getAllActiveEntities();
            response.put("stateList",stateList);
//            response.put("stateList", stateService.convertResponseModelIntoPojo(stateList).stream().sorted(Comparator.comparing(StatePojo::getId).reversed()).collect(Collectors.toList()));
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "fetch All State list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All State list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All State list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STATE_ALL + "\",\""+ AclConstants.OPERATION_STATE_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.STATE + "\")")
    @GetMapping("/state/{id}")
    public ResponseEntity<?> getStateById(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        long startTime = System.nanoTime();  // Start measuring
        try {
            StateService stateService = SpringContext.getBean(StateService.class);
            State state = stateService.get(id);
            if (state == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "State Not Found");
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch state" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                return apiResponse(RESP_CODE, response);
            } else {
                response.put("stateData", stateService.convertStateModelToStatePojo(state));
                RESP_CODE = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch state"+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STATE, AclConstants.OPERATION_STATE_VIEW,
//                        req.getRemoteAddr(), null, state.getId().longValue(), state.getName());

            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch state"+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch state"+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STATE_ALL + "\",\"" + AclConstants.OPERATION_STATE_ADD + "\")")

    /**
     * Create State API
     * @Author Darshan
     * @param pojo
     * @param req
     * @return
     * @throws Exception
     */
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.STATE_CREATE + "\")")
    @PostMapping("/state")
    public ResponseEntity<?> createState(@Valid @RequestBody StatePojo pojo, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Create");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        long startTime = System.nanoTime();  // Start measuring
        try {
            StateService stateService = SpringContext.getBean(StateService.class);
            stateService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            boolean flag = stateService.duplicateVerifyCountryAtSave(pojo.getName(), pojo.getCountryPojo().getId());
            boolean flag = stateService.duplicateVerification(pojo.getName(), null,pojo.getCountryPojo().getId(), CommonConstants.OPERATION_ADD);
            String url = req.getRequestURI();
            if (pojo.getName().length() > 250) {
                RESP_CODE = APIConstants.NOT_FOUND;
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create State " +LogConstants.LOG_BY_NAME+pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + " Input size is Exceeded "+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                response.put(APIConstants.ERROR_TAG, "Input size is Exceeded");
                return apiResponse(RESP_CODE, response, null);
            }
            if (flag) {
                pojo = stateService.save(pojo);
                response.put("state", pojo);
                response.put(APIConstants.MESSAGE, "Successfully Created");
                RESP_CODE = APIConstants.SUCCESS;
                req.getRequestURL();
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create State " +LogConstants.LOG_BY_NAME+pojo.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
            } else {
                RESP_CODE = APIConstants.FAIL;
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_ALREADY_EXIST_STATE_MANAGEMENT);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create State " +LogConstants.LOG_BY_NAME+pojo.getName() +  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + " State with same name already exist "+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STATE, AclConstants.OPERATION_STATE_ADD,
//                    req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getName());
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create State " +LogConstants.LOG_BY_NAME+pojo.getName()+  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {

            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create State " +LogConstants.LOG_BY_NAME+pojo.getName() +  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STATE_ALL + "\",\"" + AclConstants.OPERATION_STATE_VIEW + "\")")
    @GetMapping("/state/searchBy/{countryId}")
    public ResponseEntity<?> stateSearchByCountry(@PathVariable Integer countryId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "search");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        GenericSearchDTO genericSearchDTO = new GenericSearchDTO();
        try {
            if (countryId != null) {
                Country c = countryService.get(countryId);
                if (c != null) {
                    response.put("stateList", stateService.getStateListByCountry(c));
                    RESP_CODE = APIConstants.SUCCESS;
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search state by keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                }
            } else {
                RESP_CODE = APIConstants.FAIL;
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search state by keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
            }
            StateService stateService = SpringContext.getBean(StateService.class);

        } catch (Exception e) {
            e.printStackTrace();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search state by keyword : " + genericSearchDTO.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STATE_ALL + "\",\"" + AclConstants.OPERATION_STATE_EDIT + "\")")

    /**
     * Update State API
     * @Author Darshan
     * @param pojo
     * @param id
     * @param req
     * @return
     * @throws Exception
     */
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.STATE_EDIT + "\")")
    @PutMapping("/state/{id}")
    public ResponseEntity<?> updateState(@Valid @RequestBody StatePojo pojo, @PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        long startTime = System.nanoTime();  // Start measuring
        try {
            StateService stateService = SpringContext.getBean(StateService.class);
            pojo.setId(id);
//			State oldObj=stateService.getStateForEdit(pojo.getId());
            State oldvalue = null;
            stateService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);

//            boolean flag = stateService.duplicateVerifyCountryAtEdit(pojo.getName(), pojo.getCountryPojo().getId(),
//                    pojo.getId());
            boolean flag = stateService.duplicateVerification(pojo.getName(), pojo.getId(),pojo.getCountryPojo().getId(), CommonConstants.OPERATION_UPDATE);
            String url = req.getRequestURI();
            if (flag) {
                pojo = stateService.update(pojo, req);
                //String updatedValues = CommonUtils.getUpdatedDiff(oldObj,oldObj1);
                response.put("state", pojo);
                response.put(APIConstants.MESSAGE, "Successfully Updated");
                req.getRequestURL();
                RESP_CODE = APIConstants.SUCCESS;

            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update state :  "+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED +   LogConstants.LOG_ERROR + "Access denined for update operation "+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
//			oldObj1=null;
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STATE, AclConstants.OPERATION_STATE_EDIT,
//                    req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getName());
        } catch (CustomValidationException ce) {
            //ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, "Permission Denied. Unable to update/delete this record");
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update state :  "+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update state :  "+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STATE_ALL + "\",\"" + AclConstants.OPERATION_STATE_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.STATE_DELETE + "\")")
    @DeleteMapping("/state/{id}")
    public ResponseEntity<?> deleteState(@PathVariable Integer id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        StateService stateService = SpringContext.getBean(StateService.class);
        long startTime = System.nanoTime();  // Start measuring
        try {
            State state = stateService.getStateForUpdateAndDelete(id);
            StatePojo pojo = stateService.convertStateModelToStatePojo(state);
            String url = req.getRequestURI();
            if (state != null) {
                stateService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                stateService.deleteState(id);
                //response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.state.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
                response.put(APIConstants.MESSAGE, "Successfully Deleted");
                req.getRequestURL();
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete state by id : " + id  + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);

//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_STATE, AclConstants.OPERATION_STATE_EDIT,
//                        req.getRemoteAddr(), null, pojo.getId().longValue(), pojo.getName());
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"),
                        null);

            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, "Permission Denied. Unable to update/delete this record");
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete state by id : " + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                RESP_CODE = HttpStatus.METHOD_NOT_ALLOWED.value();
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete state by id : "+ id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
            } else {
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete state by id : "+ id +LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                ex.printStackTrace();
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            }
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STATE_ALL + "\",\"" + AclConstants.OPERATION_STATE_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.STATE + "\")")
    @PostMapping("/state/search")
    public ResponseEntity<?> searchState(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,HttpServletResponse res) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        Page<State> stateList = null;
        MDC.put("type", "Search");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search state using keyword : "+requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
                return apiResponse(RESP_CODE, response);
            }
            StateService stateService = SpringContext.getBean(StateService.class);
            stateList = stateService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer RESPONSE = 0;
            if (stateList.isEmpty()) {
                RESPONSE = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                response.put("stateList", new ArrayList<>());
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search state using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+RESPONSE);
                return apiResponse(RESPONSE, response, stateList);
            }
            if (null != stateList && 0 < stateList.getSize()) {
                response.put("stateList", stateService.convertResponseModelIntoPojo(stateList.getContent()));
            } else {
                response.put("stateList", new ArrayList<>());
            }
            RESP_CODE = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search state using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+RESP_CODE);

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search state using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (RuntimeException re) {
            re.printStackTrace();
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search state using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search state using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return apiResponse(RESP_CODE, response, stateList);
    }

    @GetMapping(value = "/state/excel")
    public void stateExcel(HttpServletResponse response) throws Exception {
        StateService service = SpringContext.getBean(StateService.class);
        exportToExcel(service, response);
    }

    @GetMapping(value = "/state/pdf")
    public void statePDF(HttpServletResponse response) throws Exception {
        StateService service = SpringContext.getBean(StateService.class);
        exportToPDF(service, response);
    }
}
