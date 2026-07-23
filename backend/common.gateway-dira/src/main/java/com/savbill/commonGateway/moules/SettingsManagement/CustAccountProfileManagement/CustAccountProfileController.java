package com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.exceptions.AlreadyExistException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.CUST_ACCOUNT_PROFILE)
public class CustAccountProfileController extends ApiBaseController {


    @Autowired
    private Tracer tracer;

    @Autowired
    CustAccountProfileService custAccountProfileService;


    @PostMapping("/create")
    @Operation(
            operationId = "createCustAccountProfile",
            summary = "To simply add CustAccountProfile, Call this API",
            description = "createCustAccountProfile method is HTTP POST mapping so put some CustAccountProfile details to save the CustAccountProfile object."
    )
    public ResponseEntity<?> createCustAccountProfile(@Valid @RequestBody CustAccountProfileDTO custAccountProfileDTO, HttpServletRequest req) throws Exception, AlreadyExistException {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        HashMap<String, Object> response = new HashMap<>();
        try {
            custAccountProfileDTO = custAccountProfileService.save(custAccountProfileDTO);
            response.put("custAccountProfile", custAccountProfileDTO);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create CustAccount" + LogConstants.LOG_BY_NAME + custAccountProfileDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create CustAccountProfile" + LogConstants.LOG_BY_NAME + custAccountProfileDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (DataIntegrityViolationException exc) {
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exc.getMessage());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create CustAccountProfile" + LogConstants.LOG_BY_NAME + custAccountProfileDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exc.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (AlreadyExistException e) {
            RESP_CODE = HttpStatus.CONFLICT.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create CustAccountProfile" + LogConstants.LOG_BY_NAME + custAccountProfileDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create CustAccountProfile" + LogConstants.LOG_BY_NAME + custAccountProfileDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/update/{id}")
    @Operation(
            operationId = "updateCustAccountProfile",
            summary = "To simply update CustAccountProfile, Call this API",
            description = "updateCustAccountProfile method is HTTP PUT mapping so put some CustAccountProfile details to save the CustAccountProfile object."
    )
    public ResponseEntity<?> updateCustAccountProfile(@Valid @RequestBody CustAccountProfileDTO custAccountProfileDTO, @PathVariable Long id, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            custAccountProfileDTO.setId(id);
            custAccountProfileDTO = custAccountProfileService.update(custAccountProfileDTO, req);
            response.put("custAccountProfile", custAccountProfileDTO);
            response.put(APIConstants.MESSAGE, "Successfully Updated");
            RESP_CODE = APIConstants.SUCCESS;

        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update CustAccountProfile" + LogConstants.LOG_BY_NAME + getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {

            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update CustAccountProfile" + LogConstants.LOG_BY_NAME + getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    /**
     * @param id Long representing the ID of the CustAccountProfile to be deleted.
     * @return ResponseEntity<Object> containing the response data.
     * @API: Delete CustAccountProfile By ID
     * Deletes a CustAccountProfile based on the provided ID.
     */
    @DeleteMapping("/delete/{id}")
    @Operation(
            operationId = "deleteCustAccountProfile",
            summary = "To simply deleted CustAccountProfile by id, Call this API",
            description = "deleteCustAccountProfile method is HTTP DELETE mapping so put some CustAccountProfile objects with id."
    )
    public ResponseEntity<?> deleteCustAccountProfile(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();

        try {
            custAccountProfileService.deleteCustAccountProfile(id);
            response.put(APIConstants.MESSAGE, "Successfully deleted");
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete CustAccountProfile" + LogConstants.LOG_BY_NAME + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (CustomValidationException ce) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete CustAccountProfile" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                RESP_CODE = HttpStatus.METHOD_NOT_ALLOWED.value();
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete CustAccountProfile By Id : " + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete CustAccountProfile By Id : " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    /**
     * @param requestDTO PaginationRequestDTO object containing the search criteria.
     * @return ResponseEntity<Object> containing the list of CustAccountProfile matching the search criteria.
     * @API: Get CustAccountProfile By Name
     * Retrieves CustAccountProfile based on the provided PaginationRequestDTO object.
     */
    @PostMapping("/search")
    @Operation(
            operationId = "searchCustAccountProfile",
            summary = "To simply fetch search by CustAccountProfile name, Call this API",
            description = "searchCustAccountProfile method is HTTP GET mapping so get list of CustAccountProfile object."
    )
    public ResponseEntity<?> searchCustAccountProfile(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Page<CustAccountProfile> custAccountProfiles = null;
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            }
            custAccountProfiles = custAccountProfileService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (custAccountProfiles.isEmpty()) {
                // Set the response code to 204 No Content
                RESP_CODE = HttpStatus.NO_CONTENT.value();

                // Set the response body to indicate no records found
                response.put(APIConstants.MESSAGE, "No Records Found!");
                response.put("CustAccountProfileList", new ArrayList<>());
                response.put("statusCode", RESP_CODE);

                // Log the event as successful with no content
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword: " +
                        requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                        LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND +
                        LogConstants.LOG_STATUS_CODE + RESP_CODE);

                // Return the response with 200 OK status, but indicate no content in the body
                return apiResponse(HttpStatus.OK.value(), response, custAccountProfiles);
            }

            if (0 < custAccountProfiles.getSize()) {
                response.put("CustAccountProfile", custAccountProfileService.convertResponseModelIntoPojo(custAccountProfiles.getContent()));
            } else {
                response.put("CustAccountProfileList", new ArrayList<>());
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (RuntimeException re) {
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search CustAccountProfile using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, custAccountProfiles);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/getAllWithPagination", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "getAllCustAccountProfiles",
            summary = "To simply get CustAccountProfile with Pagination, Call this API",
            description = "getAllCustAccountProfile method is HTTP POST mapping so put some Pagination details to get the CustAccountProfile object."
    )
    public ResponseEntity<?> getAllCustAccountProfiles(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<CustAccountProfile> custAccountProfiles = null;
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            custAccountProfiles = custAccountProfileService.getAllCustAccountProfileList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(),
                    requestDTO.getSortOrder(), requestDTO.getFilters());
            if (null != custAccountProfiles && 0 < custAccountProfiles.getSize())
                response.put("custAccountProfilesList", custAccountProfileService.convertResponseModelIntoPojo(custAccountProfiles.getContent()));
            else
                response.put("custAccountProfilesList", new ArrayList<>());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All CustAccountProfile list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All CustAccountProfile list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All CustAccountProfile list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, custAccountProfiles);
    }

    /**
     * @return ResponseEntity<Object> containing the list of all CustAccountProfile.
     * @API: Get All CustAccountProfile Without Pagination
     * Retrieves all CustAccountProfile without pagination.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/all")
    @Operation(
            operationId = "getAllCustAccountProfileList",
            summary = "To simply get CustAccountProfile, Call this API",
            description = "getAllCustAccountProfileList method is HTTP GET mapping so get the CustAccountProfile object."
    )
    public ResponseEntity<?>getAllCustAccountProfilesList(HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<CustAccountProfile> custAccountProfiles = custAccountProfileService.getAllActiveEntities();
            response.put("custAccountProfilesList", custAccountProfileService.convertResponseModelIntoPojo(custAccountProfiles).stream()
                    .sorted(Comparator.comparing(CustAccountProfileDTO::getId).reversed()).collect(Collectors.toList()));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All CustAccountProfile list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All CustAccountProfile list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All CustAccountProfile list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/get/{id}")
    @Operation(
            operationId = "getCustAccountProfilesById",
            summary = "To simply get CustAccountProfile by id, Call this API",
            description = "getCustAccountProfileById method is HTTP Get mapping so put some CustAccountProfile objects with id."
    )
    public ResponseEntity<?> getCustAccountProfilesById(@PathVariable Long id, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
       try {
            CustAccountProfile custAccountProfile = custAccountProfileService.getCustAccountProfilesById(id);
            if (custAccountProfile == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "CustAccountProfile Not Found!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch CustAccountProfile" + LogConstants.LOG_BY_NAME + getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            } else {
                response.put("custAccountProfilesList", custAccountProfileService.convertModelToPojo(custAccountProfile));
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch CustAccountProfile" + LogConstants.LOG_BY_NAME + getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch CustAccountProfile by" + LogConstants.LOG_BY_NAME + getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch CustAccountProfile" + LogConstants.LOG_BY_NAME + getLoggedInUser().getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }
}
